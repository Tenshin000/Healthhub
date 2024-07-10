// script.js

let templates = [];

// Function to add a time slot input field for a specific day
function addSlot(dayId, startTime = '', endTime = '') {
    const dayContainer = document.getElementById(dayId);
    const slotDiv = document.createElement('div');
    slotDiv.className = 'slot';
    slotDiv.innerHTML = `
        <input type="time" name="${dayId}-start" value="${startTime}" placeholder="Start Time">
        <input type="time" name="${dayId}-end" value="${endTime}" placeholder="End Time">
        <button type="button" class="remove-slot-btn" onclick="removeSlot(this)">Remove Slot</button>
    `;
    dayContainer.appendChild(slotDiv);
}

// Function to remove a specific time slot
function removeSlot(button) {
    const slotDiv = button.parentElement;
    slotDiv.remove();
}

// Function to save template data
function saveTemplate() {
    const templateName = document.getElementById('template-name').textContent;
    const form = document.getElementById('template-form');
    const templateData = {
        name: templateName,
        slots: {}
    };

    disableForm();

    let slots = form.querySelectorAll('div.slot');
    slots.forEach(slot => {
        const startTime = slot.querySelector('input[name$="-start"]').value;
        const endTime = slot.querySelector('input[name$="-end"]').value;
        const day = slot.parentElement.id;
        if (!templateData.slots[day]) {
            templateData.slots[day] = [];
        }
        templateData.slots[day].push({ start: startTime, end: endTime });
    });

    const templateId = templates.find(template => template.name === templateName).id;

    fetch(`/api/doctors/templates/${templateId}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(templateData)
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to save template: ' + response.statusText);
            }
        })
        .then(savedTemplate => {
            console.log('Template saved successfully:', savedTemplate);

            // Seconda richiesta GET per ottenere i template aggiornati
            fetchUpdatedTemplates();
        })
        .catch(error => {
            console.error('Error saving template:', error);
        });
}

function fetchUpdatedTemplates() {
    fetch('/api/doctors/templates')
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to fetch templates: ' + response.statusText);
            }
            return response.json();
        })
        .then(updatedTemplates => {
            templates = updatedTemplates;
            renderTemplates();
            clearForm();
        })
        .catch(error => {
            console.error('Error fetching updated templates:', error);
        });
}

// Function to render templates list
function renderTemplates() {
    const templateList = document.getElementById('template-list');
    templateList.innerHTML = '';

    templates.forEach((template, index) => {
        const li = document.createElement('li');
        li.innerHTML = `
            ${template.name}
            <div>
                <button type="button" class="edit-btn" onclick="editTemplate(${index})">Edit</button>
                <button type="button" class="delete-btn" onclick="deleteTemplate(${index})">Delete</button>
            </div>
        `;
        templateList.appendChild(li);
    });
}

// Function to edit template
function editTemplate(index) {
    const template = templates[index];
    document.getElementById('template-name').textContent = template.name;

    // Clear existing slots
    document.querySelectorAll('.day-slots .slot').forEach(slot => slot.remove());

    // Populate slots
    Object.keys(template.slots).forEach(day => {
        template.slots[day].forEach(slot => {
            addSlot(day, slot.start, slot.end);
        });
    });

    enableForm();
}

// Function to delete template
function deleteTemplate(index) {
    // Send delete request to the server and update the local template list
    fetch(`/api/doctors/templates/${templates[index].id}`, {
        method: 'DELETE'
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to delete template: ' + response.statusText);
            }
        })
        .then(() => {
            fetchUpdatedTemplates();
        })
        .catch(error => {
            console.error('Error deleting template:', error);
        });
}

function newTemplate() {
    const templateName = document.getElementById('new-template-name').value;
    const templateData = {
        name: templateName,
        slots: {}
    };

    fetch('/api/doctors/templates', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(templateData)
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to save template: ' + response.statusText);
            }
            return response.json();
        })
        .then(savedTemplate => {
            console.log('Template saved successfully:', savedTemplate);

            // Seconda richiesta GET per ottenere i template aggiornati
            fetchUpdatedTemplates();
        })
        .catch(error => {
            console.error('Error saving template:', error);
        });

}

// Function to disable all elements in a form
function disableForm() {
    const form = document.getElementById('template-form');
    const elements = form.elements;
    for (let i = 0; i < elements.length; i++) {
        elements[i].disabled = true;
    }
}

// Function to enable all elements in a form
function enableForm() {
    const form = document.getElementById('template-form');
    const elements = form.elements;
    for (let i = 0; i < elements.length; i++) {
        elements[i].disabled = false;
    }
}

// Function to clear the form after saving
function clearForm() {
    document.getElementById('template-form').reset();
    document.getElementById('template-name').textContent = '';
    document.querySelectorAll('.day-slots .slot').forEach(slot => slot.remove());
}

document.getElementById('save-template').addEventListener('click', saveTemplate);
fetchUpdatedTemplates();
disableForm();