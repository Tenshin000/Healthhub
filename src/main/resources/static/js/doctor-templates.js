// script.js

// Function to add a time slot input field for a specific day
function addSlot(dayId) {
    const dayContainer = document.getElementById(dayId);
    const slotDiv = document.createElement('div');
    slotDiv.className = 'slot';
    slotDiv.innerHTML = `
        <input type="time" name="${dayId}-start[]" placeholder="Start Time">
        <input type="time" name="${dayId}-end[]" placeholder="End Time">
        <button type="button" class="remove-slot-btn" onclick="removeSlot(this)">Remove Slot</button>
    `;
    dayContainer.appendChild(slotDiv);
}

// Function to remove a specific time slot
function removeSlot(button) {
    const slotDiv = button.parentElement;
    slotDiv.remove();
}

// Sample function to save template data (to be implemented with backend integration)
function saveTemplate() {
    const templateName = document.getElementById('template-name').value;
    const formData = new FormData(document.getElementById('template-form'));
    console.log('Template Name:', templateName);
    for (let [key, value] of formData.entries()) {
        console.log(`${key}: ${value}`);
    }
    // Add your AJAX call or fetch API call here to save the data
}

// Sample function to assign a template to a week (to be implemented with backend integration)
function assignTemplate() {
    const template = document.getElementById('select-template').value;
    const week = document.getElementById('select-week').value;
    console.log('Assign Template:', template, 'to Week:', week);
    // Add your AJAX call or fetch API call here to save the data
}

document.getElementById('save-template').addEventListener('click', saveTemplate);
document.getElementById('assign-template').addEventListener('click', assignTemplate);
