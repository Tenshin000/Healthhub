// script.js

// Function to save each field independently
function saveField(fieldName, value) {
    if (value !== '') {
        // Simulate saving to backend (replace with actual backend integration)
        console.log(`Saved ${fieldName}: ${value}`);
    }
}

// Event listener for saving address
const saveAddressButton = document.querySelector('button[data-field="address"]');
saveAddressButton.addEventListener('click', () => {
    const streetValue = document.getElementById('street').value.trim();
    const cityValue = document.getElementById('city').value.trim();
    const stateValue = document.getElementById('state').value.trim();
    const zipValue = document.getElementById('zip').value.trim();

    const fullAddress = `${streetValue}, ${cityValue}, ${stateValue} ${zipValue}`;
    saveField('address', fullAddress);
});

// Function to add phone number
function addPhoneNumber() {
    const phoneInput = document.getElementById('phone');
    const phoneNumber = phoneInput.value.trim();

    if (phoneNumber !== '') {
        const phoneList = document.getElementById('phone-list');
        const phoneTag = document.createElement('div');
        phoneTag.classList.add('phone-number');
        phoneTag.textContent = phoneNumber;
        phoneTag.addEventListener('click', () => {
            phoneTag.remove();
        });
        phoneList.appendChild(phoneTag);

        // Clear input
        phoneInput.value = '';
    }
}

// Event listener for adding phone number
const addPhoneButton = document.getElementById('add-phone');
addPhoneButton.addEventListener('click', addPhoneNumber);

// Function to add specialization tag
function addSpecialization() {
    const specializationInput = document.getElementById('specialization-input');
    const specializationValue = specializationInput.value.trim();

    if (specializationValue !== '') {
        const specializationsContainer = document.getElementById('specializations');
        const specializationTag = document.createElement('div');
        specializationTag.classList.add('specialization');
        specializationTag.textContent = specializationValue;
        specializationTag.addEventListener('click', () => {
            specializationTag.remove();
        });
        specializationsContainer.appendChild(specializationTag);

        // Clear input
        specializationInput.value = '';
    }
}

// Event listener for adding specialization
const addSpecializationButton = document.getElementById('add-specialization');
addSpecializationButton.addEventListener('click', addSpecialization);

// Function to add visit type card
function addVisitType() {
    const visitTypesContainer = document.getElementById('visit-types');
    const visitTypeTemplate = `
        <div class="visit-type">
            <input type="text" placeholder="Visit type">
            <input type="text" placeholder="Price">
            <button class="edit-visit-type">Edit</button>
            <button class="save-visit-type">Save</button>
            <button class="remove-visit-type">Remove</button>
        </div>
    `;
    const visitTypeElement = document.createElement('div');
    visitTypeElement.innerHTML = visitTypeTemplate.trim();

    // Add event listener to remove visit type
    const removeButton = visitTypeElement.querySelector('.remove-visit-type');
    removeButton.addEventListener('click', () => {
        visitTypeElement.remove();
    });

    // Add event listener to save visit type
    const saveButton = visitTypeElement.querySelector('.save-visit-type');
    saveButton.addEventListener('click', () => {
        const visitTypeInput = visitTypeElement.querySelector('input[type="text"]:first-of-type').value.trim();
        const priceInput = visitTypeElement.querySelector('input[type="text"]:last-of-type').value.trim();

        if (visitTypeInput !== '' && priceInput !== '') {
            saveField('visit type', `${visitTypeInput} - ${priceInput}`);
        }
    });

    // Add event listener to edit visit type
    const editButton = visitTypeElement.querySelector('.edit-visit-type');
    editButton.addEventListener('click', () => {
        visitTypeElement.querySelector('input[type="text"]:first-of-type').disabled = false;
        visitTypeElement.querySelector('input[type="text"]:last-of-type').disabled = false;
    });

    visitTypesContainer.appendChild(visitTypeElement);
}

// Event listener for adding visit type
const addVisitTypeButton = document.getElementById('add-visit-type');
addVisitTypeButton.addEventListener('click', addVisitType);
