function fetchAppointments() {
    fetch('/api/user/appointments/upcoming')  // URL dell'endpoint API
        .then(response => response.json())
        .then(data => renderAppointments(data))
        .catch(error => console.error('Error fetching appointments:', error));
}

function renderAppointments(appointments) {
    const appointmentsList = document.getElementById('upcoming-appointments');
    appointmentsList.innerHTML = '';  // Svuota la lista degli appuntamenti

    appointments.forEach(appointment => {
        const listItem = document.createElement('li');
        listItem.className = 'appointment-card';

        const appointmentInfo = document.createElement('div');
        appointmentInfo.className = 'appointment-info';

        const date = appointment.date.split('T')[0];
        const slot = appointment.date.split('T')[1].slice(0, 5);

        appointmentInfo.innerHTML = `
            <h3>Dr. ${appointment.doctor.name}</h3>
            <p><strong>Data:</strong> ${date}</p>
            <p><strong>Ora:</strong> ${slot}</p>
            <p><strong>Servizio:</strong> ${appointment.visitType}</p>
            <p><strong>Indirizzo:</strong> ${appointment.address}</p>
        `;

        const cancelButton = document.createElement('button');
        cancelButton.className = 'cancel-button';
        cancelButton.textContent = 'Cancella';
        cancelButton.addEventListener('click', () => cancelAppointment(appointment.id));

        listItem.appendChild(appointmentInfo);
        listItem.appendChild(cancelButton);
        appointmentsList.appendChild(listItem);
    });
}

function cancelAppointment(appointmentId) {
    fetch(`/api/user/appointments/${appointmentId}`, {
        method: 'DELETE'
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            // Ricarica gli appuntamenti dopo la cancellazione
            fetchAppointments();
        })
        .catch(error => console.error('Error cancelling appointment:', error));
}

function fetchPastAppointments() {
    fetch('/api/user/appointments/past')  // URL dell'endpoint API per appuntamenti passati
        .then(response => response.json())
        .then(data => renderPastAppointments(data))
        .catch(error => console.error('Error fetching past appointments:', error));
}

function renderPastAppointments(appointments) {
    const pastAppointmentsList = document.getElementById('past-appointments');
    pastAppointmentsList.innerHTML = '';  // Svuota la lista degli appuntamenti

    appointments.forEach(appointment => {
        const listItem = document.createElement('li');
        listItem.className = 'appointment-card';

        const appointmentInfo = document.createElement('div');
        appointmentInfo.className = 'appointment-info';

        const date = appointment.date.split('T')[0];
        const slot = appointment.date.split('T')[1].slice(0, 5);


        appointmentInfo.innerHTML = `
            <h3>Dr. ${appointment.doctor.name}</h3>
            <p><strong>Data:</strong> ${date}</p>
            <p><strong>Ora:</strong> ${slot}</p>
            <p><strong>Servizio:</strong> ${appointment.visitType}</p>
            <p><strong>Indirizzo:</strong> ${appointment.address}</p>
        `;

        listItem.appendChild(appointmentInfo);
        pastAppointmentsList.appendChild(listItem);
    });
}

function cancelPastAppointment(appointmentId) {
    // Placeholder function - in realtà non si dovrebbe cancellare un appuntamento passato
    console.warn('Cannot cancel past appointments');
}

document.addEventListener('DOMContentLoaded', () => {
    fetchAppointments();
    fetchPastAppointments();
});
