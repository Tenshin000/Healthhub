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

// Validation of the slot
function isValidSlot(startTime, endTime) {
    const minTime = "07:00";
    const maxTime = "19:00";

    // Check bounds
    if (startTime < minTime || endTime > maxTime) {
        return false;
    }

    // Check logical order
    if (endTime <= startTime) {
        return false;
    }

    return true;
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

        if (!isValidSlot(startTime, endTime)) {
            alert(`Invalid time slot on ${day}: ${startTime} - ${endTime}. Make sure times are between 07:00 and 19:00 and end is after start.`);
            enableForm();
            throw new Error(`Invalid slot: ${startTime} - ${endTime}`);
        }

        if (!templateData.slots[day]) {
            templateData.slots[day] = [];
        }
        templateData.slots[day].push({ start: startTime, end: endTime });
    });

    const templateId = templates.find(template => template.name === templateName).id;

    fetch(`/api/doctor/templates/${templateId}`, {
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

            // Second GET request to get the updated templates
            fetchUpdatedTemplates();
        })
        .catch(error => {
            console.error('Error saving template:', error);
        });
}

function fetchUpdatedTemplates() {
    fetch('/api/doctor/templates')
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

    // Populate slots in chronological order
    Object.keys(template.slots).forEach(day => {
        // Sort by start time
        template.slots[day]
            .sort((a, b) => a.start.localeCompare(b.start))
            .forEach(slot => {
                addSlot(day, slot.start, slot.end);
            });
    });

    enableForm();
}

// Function to delete template
function deleteTemplate(index) {
    // Send delete request to the server and update the local template list
    fetch(`/api/doctor/templates/${templates[index].id}`, {
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

    if(templateName == null || templateName === "")
        return;

    const templateData = {
        name: templateName,
        slots: {}
    };

    fetch('/api/doctor/templates', {
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

            // Second GET request to get the updated templates
            fetchUpdatedTemplates();
        })
        .catch(error => {
            alert("Template creation failed. You have used the same name of another template?");
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