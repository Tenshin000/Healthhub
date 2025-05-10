// Function to fetch personal information from the backend
function fetchPersonalInfo() {
    fetch('/api/user/details')
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(data => {
            renderPersonalInfo(data);
        })
        .catch(error => {
            console.error('Error fetching personal info:', error);
        });
}

// Function to populate input fields with personal info
function renderPersonalInfo(personalInfo) {
    document.getElementById('fullName').value     = personalInfo.fullName || '';
    document.getElementById('fiscalCode').value   = personalInfo.fiscalCode || '';
    document.getElementById('birthDate').value    = personalInfo.birthDate || '';
    document.getElementById('gender').value       = personalInfo.gender || 'male';
}

// Function to save personal info from the form
function savePersonalInfo() {
    const fullName    = document.getElementById('fullName').value;
    const fiscalCode  = document.getElementById('fiscalCode').value;
    const birthDate   = document.getElementById('birthDate').value;
    const gender      = document.getElementById('gender').value;

    const data = { fullName, fiscalCode, birthDate, gender };

    fetch('/api/user/details', {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
    })
        .then(response => {
            if (!response.ok) throw new Error('Network response was not ok');
            return response.json();
        })
        .then(data => {
            console.log('Success:', data);
            fetchPersonalInfo();
        })
        .catch(error => {
            console.error('Error saving personal info:', error);
        });
}

// Add event listener to the personal info save button
document.getElementById('save-personal-info')
    .addEventListener('click', savePersonalInfo);

// Function to fetch contact information from the backend
function fetchContactInfo() {
    fetch('/api/user/contacts')
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(data => {
            renderContactInfo(data);
        })
        .catch(error => {
            console.error('Error fetching contact info:', error);
        });
}

// Function to populate input fields with contact info
function renderContactInfo(contactInfo) {
    document.getElementById('email').value    = contactInfo.email || '';
    document.getElementById('phone').value    = contactInfo.phoneNumber || '';
    document.getElementById('street').value   = contactInfo.street || '';
    document.getElementById('city').value     = contactInfo.city || '';
    document.getElementById('province').value = contactInfo.province || '';
    document.getElementById('CAP').value      = contactInfo.postalCode || '';
    document.getElementById('country').value  = contactInfo.country || '';
}

// Function to save contact information from the form
function saveContactInfo() {
    const email      = document.getElementById('email').value;
    const phone      = document.getElementById('phone').value;
    const street     = document.getElementById('street').value;
    const city       = document.getElementById('city').value;
    const province   = document.getElementById('province').value;
    const postalCode = document.getElementById('CAP').value;
    const country    = document.getElementById('country').value;

    const data = {
        email,
        phoneNumber: phone,
        street,
        city,
        province,
        postalCode,
        country
    };

    fetch('/api/user/contacts', {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
    })
        .then(response => {
            if (!response.ok) throw new Error('Network response was not ok');
            return response.json();
        })
        .then(data => {
            console.log('Success:', data);
            fetchContactInfo();
        })
        .catch(error => {
            console.error('Error saving contact info:', error);
        });
}

// Add event listener to the contact info save button
document.getElementById('save-contact-info')
    .addEventListener('click', saveContactInfo);

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

    fetch('/api/user/password', {
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

// On DOM ready, fetch initial data
document.addEventListener('DOMContentLoaded', () => {
    fetchPersonalInfo();
    fetchContactInfo();
});
