function fetchAppointments() {
    fetch('/api/user/appointments/upcoming')  // API endpoint URL
        .then(response => response.json())
        .then(data => renderAppointments(data))
        .catch(error => console.error('Error fetching appointments:', error));
}

function renderAppointments(appointments) {
    const appointmentsList = document.getElementById('upcoming-appointments');
    appointmentsList.innerHTML = '';  // Clear the appointment list

    appointments.forEach(appointment => {
        const listItem = document.createElement('li');
        listItem.className = 'appointment-card';

        const appointmentInfo = document.createElement('div');
        appointmentInfo.className = 'appointment-info';

        const date = appointment.date.split('T')[0];
        const slot = appointment.date.split('T')[1].slice(0, 5);

        appointmentInfo.innerHTML = `
            <h3>Dr. ${appointment.doctor.name}</h3>
            <p><strong>Date:</strong> ${date}</p>
            <p><strong>Time:</strong> ${slot}</p>
            <p><strong>Service:</strong> ${appointment.visitType}</p>
            <p><strong>Address:</strong> ${appointment.address?.toString() ?? 'Undefined'}</p>
        `;

        const cancelButton = document.createElement('button');
        cancelButton.className = 'cancel-button';
        cancelButton.textContent = 'Cancel';
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
            // Reload appointments after deletion
            fetchAppointments();
        })
        .catch(error => console.error('Error cancelling appointment:', error));
}

function fetchPastAppointments() {
    fetch('/api/user/appointments/past')  // API endpoint URL for past appointments
        .then(response => response.json())
        .then(data => renderPastAppointments(data))
        .catch(error => console.error('Error fetching past appointments:', error));
}

function renderPastAppointments(appointments) {
    const pastAppointmentsList = document.getElementById('past-appointments');
    pastAppointmentsList.innerHTML = '';  // Clear the list of past appointments

    appointments.forEach(appointment => {
        const listItem = document.createElement('li');
        listItem.className = 'appointment-card';

        const appointmentInfo = document.createElement('div');
        appointmentInfo.className = 'appointment-info';

        const date = appointment.date.split('T')[0];
        const slot = appointment.date.split('T')[1].slice(0, 5);

        appointmentInfo.innerHTML = `
            <h3>Dr. ${appointment.doctor.name}</h3>
            <p><strong>Date:</strong> ${date}</p>
            <p><strong>Time:</strong> ${slot}</p>
            <p><strong>Service:</strong> ${appointment.visitType}</p>
            <p><strong>Address:</strong> ${appointment.address}</p>
        `;

        listItem.appendChild(appointmentInfo);
        pastAppointmentsList.appendChild(listItem);
    });
}

function cancelPastAppointment(appointmentId) {
    // Placeholder function - you shouldn't be able to cancel a past appointment
    console.warn('Cannot cancel past appointments');
}

document.addEventListener('DOMContentLoaded', () => {
    fetchAppointments();
    fetchPastAppointments();
});