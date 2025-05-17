// Event listener for saving address
const saveAddressButton = document.querySelector('button[data-field="address"]');
saveAddressButton.addEventListener('click', () => {
    const streetValue = document.getElementById('street').value.trim();
    const cityValue = document.getElementById('city').value.trim();
    const stateValue = document.getElementById('state').value.trim();
    const zipValue = document.getElementById('zip').value.trim();
    const province = document.getElementById('province').value.trim();

    const addressData = {
        street: streetValue,
        city: cityValue,
        country: stateValue,
        postalCode: zipValue,
        province: province
    };

    fetch('/api/doctor/address', {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(addressData)
    }).then(response => {
        if (!response.ok) {
            throw new Error('Network response was not ok');
        }
        return response.json();
    }).then(data => {
        console.log('Address updated successfully:', data);
        // Execute other actions after the address has been updated
    }).catch(error => {
        console.error('Error updating address:', error);
        // Handle the error, e.g., show a message to the user
    });
});

// Event listener for saving personal information
const savePersonalInfoButton = document.querySelector('button[data-field="personal-info"]');
savePersonalInfoButton.addEventListener('click', () => {
    const fullName = document.getElementById('full-name').value.trim();
    const orderRegistrationNumber = document.getElementById('orderRegistrationNumber').value.trim();
    const fiscalCode = document.getElementById('fiscalCode').value.trim();
    const email = document.getElementById('email').value.trim();
    const birthDate = document.getElementById('birthdate').value.trim();
    const gender = document.getElementById('gender').value;

    const doctorDetails = {
        fullName: fullName,
        orderRegistrationNumber: orderRegistrationNumber,
        fiscalCode: fiscalCode,
        email: email,
        birthDate: birthDate,
        gender: gender
    };

    fetch('/api/doctor/details', {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(doctorDetails)
    }).then(response => {
        if (!response.ok) {
            throw new Error('Network response was not ok');
        }
        return response.json();
    }).then(data => {
        console.log('User details updated successfully:', data);
        // Execute other actions after personal info update, if needed
    }).catch(error => {
        console.error('Error updating user details:', error);
        // Handle the error, e.g., show a message to the user
    });
});

// Function to add phone number
function addPhoneNumber() {
    const phoneInput = document.getElementById('phone');
    const phoneNumber = phoneInput.value.trim();

    if (phoneNumber !== '') {
        fetch('/api/doctor/phones', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ phoneNumber })
        })
            .then(res => {
                if (!res.ok) throw new Error('Failed to add phone number');
                return res.json();
            })
            .then(data => {
                const phoneList = document.getElementById('phone-list');
                const phoneTag = createPhoneTag(data.phoneNumber, data.index);
                phoneList.appendChild(phoneTag);
            })
            .catch(console.error);

        phoneInput.value = '';
    }
}

function createPhoneTag(phoneNumber, index) {
    const container = document.createElement('div');
    container.classList.add('phone-number');
    container.setAttribute('data-index', index);

    const span = document.createElement('span');
    span.classList.add('phone-text');
    span.textContent = phoneNumber;
    container.appendChild(span);

    const icon = document.createElement('i');
    icon.classList.add('fa-solid', 'fa-xmark', 'remove-icon');
    icon.setAttribute('data-index', index);
    icon.addEventListener('click', () => removePhoneNumber(container));
    container.appendChild(icon);

    return container;
}

// External function to handle click on phoneTag and remove phone number
function handlePhoneTagClick(event) {
    const icon = event.target;
    const phoneTag = icon.closest('.phone-number');
    removePhoneNumber(phoneTag);
}

// Function to remove phone number by calling the server
function removePhoneNumber(phoneTag) {
    const index = phoneTag.getAttribute('data-index');
    const phoneList = document.getElementById('phone-list');

    // If only one remains, block
    if (phoneList.querySelectorAll('.phone-number').length <= 1) {
        alert("You cannot remove the last phone number.");
        return;
    }

    fetch(`/api/doctor/phones/${index}`, {
        method: 'DELETE',
        headers: {
            'Content-Type': 'application/json',
        }
    }).then(response => {
        if (!response.ok) {
            throw new Error('Failed to delete phone number');
        }
        return fetchUpdatedPhoneNumbers(); // Call to get updated list
    }).then(updatedPhoneNumbers => {
        renderPhoneNumbersList(updatedPhoneNumbers);
        console.log('Phone number deleted successfully');
    }).catch(error => {
        console.error('Error deleting phone number:', error);
        // Handle the error, e.g., show a message to the user
    });
}

function fetchUpdatedPhoneNumbers() {
    return fetch('/api/doctor/phones')
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to fetch updated phone numbers');
            }
            return response.json(); // Extract JSON data from response
        })
        .then(data => {
            console.log('Phone numbers fetched successfully:', data);
            return data;
        });
}

function renderPhoneNumbersList(phoneNumbers) {
    const phoneList = document.getElementById('phone-list');
    phoneList.innerHTML = ''; // Clear previous list

    console.log('Rendering phone numbers:', phoneNumbers);
    phoneNumbers.forEach(phone => {
        const phoneTag = createPhoneTag(phone.phoneNumber, phone.index);
        phoneList.appendChild(phoneTag);
    });
}

// Event listener for adding phone number
const addPhoneButton = document.getElementById('add-phone');
addPhoneButton.addEventListener('click', addPhoneNumber);

function addSpecialization() {
    const input = document.getElementById('specialization-input');
    const val = input.value.trim();
    if (!val) return;

    fetch('/api/doctor/specializations', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ specialization: val })
    })
        .then(res => {
            if (!res.ok) throw new Error('Failed to add specialization');
            return res.json();
        })
        .then(data => {
            const cont = document.getElementById('specializations');
            cont.appendChild(createSpecializationTag(data.specialization, data.index));
        })
        .catch(console.error);

    input.value = '';
}

function createSpecializationTag(specialization, index) {
    const container = document.createElement('div');
    container.classList.add('specialization');
    container.setAttribute('data-index', index);

    const span = document.createElement('span');
    span.classList.add('specialization-text');
    span.textContent = specialization;
    container.appendChild(span);

    const icon = document.createElement('i');
    icon.classList.add('fa-solid', 'fa-xmark', 'remove-icon');
    icon.setAttribute('data-index', index);
    icon.addEventListener('click', event => handleSpecializationTagClick(event));
    container.appendChild(icon);

    return container;
}

// External function to handle click on specialization tag and remove it
function handleSpecializationTagClick(event) {
    const icon = event.target;
    const specializationTag = icon.closest('.specialization');
    removeSpecialization(specializationTag);
}

// Function to remove specialization by calling the server
function removeSpecialization(specializationTag) {
    const index = specializationTag.getAttribute('data-index');
    const specializationList = document.getElementById('specializations');

    // If only one remains, block
    if (specializationList.querySelectorAll('.specialization').length <= 1) {
        alert("You cannot remove the last specialization.");
        return;
    }

    fetch(`/api/doctor/specializations/${index}`, {
        method: 'DELETE',
        headers: {
            'Content-Type': 'application/json',
        }
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to delete specialization');
            }
            return fetchUpdatedSpecializations(); // Get updated list
        })
        .then(updatedSpecializations => {
            renderSpecializationsList(updatedSpecializations);
            console.log('Specialization deleted successfully');
        })
        .catch(error => {
            console.error('Error deleting specialization:', error);
            // Handle the error, e.g., show a message to the user
        });
}

function fetchUpdatedSpecializations() {
    return fetch('/api/doctor/specializations')
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to fetch updated specializations');
            }
            return response.json();
        })
        .then(data => {
            console.log('Specializations fetched successfully:', data);
            return data;
        });
}

function renderSpecializationsList(specializations) {
    const specializationList = document.getElementById('specializations');
    specializationList.innerHTML = ''; // Clear previous list

    console.log('Rendering specializations:', specializations);
    specializations.forEach(item => {
        const specializationTag = createSpecializationTag(item.specialization, item.index)
        specializationList.appendChild(specializationTag);
    });
}

// Event listener for adding specialization
const addSpecializationButton = document.getElementById('add-specialization');
addSpecializationButton.addEventListener('click', addSpecialization);

function saveNewService(visitTypeInput, priceInput, serviceCardElement) {
    const visitData = {
        service: visitTypeInput,
        price: priceInput
    };

    fetch('/api/doctor/services', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(visitData)
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to save new service');
            }
            return response.json();
        })
        .then(data => {
            console.log('New service saved successfully:', data);
            serviceCardElement.setAttribute('data-index', data.index);
        })
        .catch(error => {
            console.error('Error saving new service:', error);
            throw error; // Re-throw error for higher-level handling if needed
        });
}

function updateService(dataIndex, visitTypeInput, priceInput) {
    const visitData = {
        service: visitTypeInput,
        price: priceInput
    };

    // Call backend to update service type
    fetch(`/api/doctor/services/${dataIndex}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(visitData)
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to update service');
            }
            console.log('Service updated successfully');
        })
        .catch(error => {
            console.error('Error updating service:', error);
            // Handle the error, e.g., show a message to the user
        });
}

function saveServiceHandler(event) {
    const serviceCardElement = event.target.closest('.visit-type');
    const visitTypeInput = serviceCardElement.querySelector('input[type="text"]:first-of-type').value.trim();
    const priceInput = serviceCardElement.querySelector('input[type="text"]:last-of-type').value.trim();
    const dataIndex = serviceCardElement.getAttribute('data-index');

    if (dataIndex === '-1' || !dataIndex) {
        saveNewService(visitTypeInput, priceInput, serviceCardElement);
    } else {
        updateService(dataIndex, visitTypeInput, priceInput);
    }
}

function removeServiceHandler(event) {
    const serviceCardElement = event.target.closest('.visit-type');
    const dataIndex = serviceCardElement.getAttribute('data-index');

    // Example request to server to remove service
    fetch(`/api/doctor/services/${dataIndex}`, {
        method: 'DELETE',
        headers: {
            'Content-Type': 'application/json',
        }
    })
        .then(response => {
            if (!response.ok) {
                console.log('Failed to delete service');
                throw new Error('Failed to delete service');
            }
            return fetchUpdatedServices();
        }).then(updatedServices => {
        renderServicesList(updatedServices);
        console.log('Service deleted successfully');
    })
        .catch(error => {
            console.error('Error deleting service:', error);
            // Handle the error, e.g., show a message to the user
        });
}

function fetchUpdatedServices() {
    return fetch('/api/doctor/services')
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to fetch updated services');
            }
            return response.json();
        })
        .then(data => {
            console.log('Services fetched successfully:', data);
            return data;
        });
}

function renderServicesList(services) {
    const serviceList = document.getElementById('visit-types');
    serviceList.innerHTML = ''; // Clear previous list

    services.forEach(item => {
        const serviceCard = createServiceCard(item)
        serviceList.appendChild(serviceCard);
    });
}

// Function to add visit type card
function createServiceCard(initialData = null) {
    const serviceTemplate = `
        <div class="visit-type" data-index="-1">
            <input type="text" placeholder="Visit type">
            <input type="text" placeholder="Price">
            <button class="save-visit-type" type="button">Save</button>
            <button class="remove-visit-type" type="button">Remove</button>
        </div>
    `;
    const serviceElement = document.createElement('div');
    serviceElement.innerHTML = serviceTemplate.trim();

    if (initialData) {
        // If initialData is provided, fill input fields with initial values
        const visitTypeInput = serviceElement.querySelector('input[type="text"]:first-of-type');
        const priceInput = serviceElement.querySelector('input[type="text"]:last-of-type');
        visitTypeInput.value = initialData.service || '';
        priceInput.value = initialData.price || '';
        serviceElement.setAttribute('data-index', initialData.index);
    }

    // Add event listener to remove service type
    const removeButton = serviceElement.querySelector('.remove-visit-type');
    removeButton.addEventListener('click', removeServiceHandler);

    // Add event listener to save service type
    const saveButton = serviceElement.querySelector('.save-visit-type');
    saveButton.addEventListener('click', saveServiceHandler);

    return serviceElement;
}

function addServiceCardToContainer(serviceElement) {
    const serviceContainer = document.getElementById('visit-types');
    serviceContainer.appendChild(serviceElement);
}

function newEmptyService() {
    const serviceCard = createServiceCard();
    addServiceCardToContainer(serviceCard, 'specializations');
}

// Function to update password
function updatePassword(event){
    event.preventDefault();

    const currentPwd = document.getElementById('currentPassword').value;
    const newPwd     = document.getElementById('newPassword').value;
    const confirmPwd = document.getElementById('confirmPassword').value;

    if (newPwd !== confirmPwd) {
        alert('The new password and confirmation do not match.');
        return;
    }

    const payload = {
        currentPassword: currentPwd,
        newPassword: newPwd
    };

    fetch('/api/doctor/password', {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
    })
        .then(response => {
            if (!response.ok) throw new Error('Network error during update');
        })
        .then(data => {
            console.log('Password updated successfully', data);
            alert('Password updated successfully!');
            document.getElementById('currentPassword').value = '';
            document.getElementById('newPassword').value     = '';
            document.getElementById('confirmPassword').value = '';
        })
        .catch(error => {
            console.error('Error updating password:', error);
            alert('An error occurred: ' + error.message);
        });
}

// Add event listener to the security form submit
document.getElementById('update-password')
    .closest('form')
    .addEventListener('submit', updatePassword);

// Event listener for adding visit type
const addVisitTypeButton = document.getElementById('add-visit-type');
addVisitTypeButton.addEventListener('click', newEmptyService);
