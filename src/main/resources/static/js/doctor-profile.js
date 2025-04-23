// script.js

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
            // Esegui altre azioni dopo l'aggiornamento dell'indirizzo
    }).catch(error => {
            console.error('Error updating address:', error);
            // Gestisci l'errore, ad esempio mostrando un messaggio all'utente
    });
});

const savePersonalInfoButton = document.querySelector('button[data-field="personal-info"]');
savePersonalInfoButton.addEventListener('click', () => {
    const fullName = document.getElementById('full-name').value.trim();
    const birthDate = document.getElementById('birthdate').value.trim();
    const gender = document.getElementById('gender').value;

    const userDetails = {
        fullName: fullName,
        birthDate: birthDate,
        gender: gender
    };

    fetch('/api/doctor/details', {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(userDetails)
    }).then(response => {
        if (!response.ok) {
            throw new Error('Network response was not ok');
        }
        return response.json();
    }).then(data => {
        console.log('User details updated successfully:', data);
        // Esegui altre azioni dopo l'aggiornamento dell'indirizzo
    }).catch(error => {
        console.error('Error updating user details:', error);
        // Gestisci l'errore, ad esempio mostrando un messaggio all'utente
    });

});

// Function to add phone number
function addPhoneNumber() {
    const phoneInput = document.getElementById('phone');
    const phoneNumber = phoneInput.value.trim();

    if (phoneNumber !== '') {
        // Chiamata al backend per salvare il numero di telefono
        fetch('/api/doctor/phones', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ phoneNumber: phoneNumber })
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Failed to add phone number');
                }
                return response.json(); // Estrai dati JSON dalla risposta
            })
            .then(data => {
                // Aggiungi il numero di telefono all'interfaccia
                const phoneList = document.getElementById('phone-list');
                const phoneTag = createPhoneTag(data.phoneNumber, data.index);
                phoneList.appendChild(phoneTag);

                console.log('Phone number added successfully:', data.phoneNumber);
            })
            .catch(error => {
                console.error('Error adding phone number:', error);
                // Gestisci l'errore, ad esempio mostrando un messaggio all'utente
            });

        // Clear input
        phoneInput.value = '';
    }
}

function createPhoneTag(phoneNumber, index) {
    const phoneTag = document.createElement('div');
    phoneTag.classList.add('phone-number');
    phoneTag.setAttribute('data-index', index); // Aggiungi attributo data-index
    phoneTag.textContent = phoneNumber; // Usiamo data.phoneNumber se il backend restituisce il numero aggiunto
    phoneTag.addEventListener('click', handlePhoneTagClick);
    return phoneTag;
}

// Funzione esterna per gestire il click su phoneTag e rimuovere il numero di telefono
function handlePhoneTagClick(event) {
    const phoneTag = event.target; // Ottieni l'elemento su cui è stato cliccato
    removePhoneNumber(phoneTag); // Chiamata alla funzione removePhoneNumber
}

// Funzione per rimuovere il numero di telefono chiamando il server
function removePhoneNumber(phoneTag) {
    const index = phoneTag.getAttribute('data-index');

    fetch(`/api/doctor/phones/${index}`, {
        method: 'DELETE',
        headers: {
            'Content-Type': 'application/json',
        }
    }).then(response => {
            if (!response.ok) {
                throw new Error('Failed to delete phone number');
            }
            return fetchUpdatedPhoneNumbers(); // Chiamata per ottenere la lista aggiornata
    }).then(updatedPhoneNumbers => {
            renderPhoneNumbersList(updatedPhoneNumbers); // Funzione per renderizzare la lista aggiornata
            console.log('Phone number deleted successfully');
    }).catch(error => {
            console.error('Error deleting phone number:', error);
            // Gestisci l'errore, ad esempio mostrando un messaggio all'utente
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
            return data; // Return the array of updated phone numbers
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
    const specializationInput = document.getElementById('specialization-input');
    const specializationValue = specializationInput.value.trim();

    if (specializationValue !== '') {
        // Chiamata al backend per salvare il numero di telefono
        fetch('/api/doctor/specializations', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ specialization: specializationValue })
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Failed to add specialization');
                }
                return response.json(); // Estrai dati JSON dalla risposta
            })
            .then(data => {
                // Aggiungi la specializzazione all'interfaccia
                const specializationsContainer = document.getElementById('specializations');
                const specializationTag = createSpecializationTag(data.specialization, data.index);
                specializationsContainer.appendChild(specializationTag);

                console.log('Phone number added successfully:', data.phoneNumber);
            })
            .catch(error => {
                console.error('Error adding phone number:', error);
                // Gestisci l'errore, ad esempio mostrando un messaggio all'utente
            });

        // Clear input
        specializationInput.value = '';
    }
}

function createSpecializationTag(specialization, index) {
    const specializationTag = document.createElement('div');
    specializationTag.classList.add('specialization');
    specializationTag.setAttribute('data-index', index); // Aggiungi attributo data-index
    specializationTag.textContent = specialization; // Usiamo data.phoneNumber se il backend restituisce il numero aggiunto
    specializationTag.addEventListener('click', handleSpecializationTagClick);
    return specializationTag;
}

// Funzione esterna per gestire il click su phoneTag e rimuovere il numero di telefono
function handleSpecializationTagClick(event) {
    const specializationTag = event.target; // Ottieni l'elemento su cui è stato cliccato
    removeSpecialization(specializationTag); // Chiamata alla funzione removePhoneNumber
}

// Funzione per rimuovere il numero di telefono chiamando il server
function removeSpecialization(specializationTag) {
    const index = specializationTag.getAttribute('data-index');

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
            return fetchUpdatedSpecializations(); // Chiamata per ottenere la lista aggiornata
        })
        .then(updatedSpecializations => {
            renderSpecializationsList(updatedSpecializations); // Funzione per renderizzare la lista aggiornata
            console.log('Specialization deleted successfully');
        })
        .catch(error => {
            console.error('Error deleting specialization:', error);
            // Gestisci l'errore, ad esempio mostrando un messaggio all'utente
        });
}

function fetchUpdatedSpecializations() {
    return fetch('/api/doctor/specializations')
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to fetch updated specializations');
            }
            return response.json(); // Estrai dati JSON dalla risposta
        })
        .then(data => {
            console.log('Specializations fetched successfully:', data);
            return data; // Ritorna l'array delle specializzazioni aggiornate
        });
}

function renderSpecializationsList(specializations) {
    const specializationList = document.getElementById('specializations');
    specializationList.innerHTML = ''; // Svuota la lista precedente

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
            return response.json(); // Estrai dati JSON dalla risposta
        })
        .then(data => {
            console.log('New service saved successfully:', data);
            serviceCardElement.setAttribute('data-index', data.index);
        })
        .catch(error => {
            console.error('Error saving new service:', error);
            throw error; // Rilancia l'errore per gestione superiore, se necessario
        });
}

function updateService(dataIndex, visitTypeInput, priceInput) {
    const visitData = {
        service: visitTypeInput,
        price: priceInput
    };

    // Effettua la richiesta al backend per aggiornare il tipo di visita
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
            // Esegui altre azioni dopo aver aggiornato il tipo di visita, se necessario
        })
        .catch(error => {
            console.error('Error updating service:', error);
            // Gestisci l'errore, ad esempio mostrando un messaggio all'utente
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

    // Esempio di richiesta al server per rimuovere il servizio
    fetch(`/api/doctor/services/${dataIndex}`, {
        method: 'DELETE',
        headers: {
            'Content-Type': 'application/json',
        }
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to delete service');
            }
            return fetchUpdatedServices(); // Chiamata per ottenere la lista aggiornata
        }).then(updatedServices => {
        renderServicesList(updatedServices); // Funzione per renderizzare la lista aggiornata
        console.log('Service deleted successfully');
        })
        .catch(error => {
            console.error('Error deleting service:', error);
            // Gestisci l'errore, ad esempio mostrando un messaggio all'utente
        });
}

function fetchUpdatedServices() {
    return fetch('/api/doctor/services')
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to fetch updated services');
            }
            return response.json(); // Estrai dati JSON dalla risposta
        })
        .then(data => {
            console.log('Services fetched successfully:', data);
            return data; // Ritorna l'array delle specializzazioni aggiornate
        });
}

function renderServicesList(services) {
    const serviceList = document.getElementById('visit-types');
    serviceList.innerHTML = ''; // Svuota la lista precedente

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
        // Se initialData è fornito, popola i campi input con i valori iniziali
        const visitTypeInput = serviceElement.querySelector('input[type="text"]:first-of-type');
        const priceInput = serviceElement.querySelector('input[type="text"]:last-of-type');
        visitTypeInput.value = initialData.service || '';
        priceInput.value = initialData.price || '';
        serviceElement.setAttribute('data-index', initialData.index);
    }

    // Aggiungi event listener per rimuovere il tipo di visita
    const removeButton = serviceElement.querySelector('.remove-visit-type');
    removeButton.addEventListener('click', removeServiceHandler);

    // Aggiungi event listener per salvare il tipo di visita
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



// Event listener for adding visit type
const addVisitTypeButton = document.getElementById('add-visit-type');
addVisitTypeButton.addEventListener('click', newEmptyService);
