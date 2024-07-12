$(document).ready(() => {
    // Inizializzazione del datepicker con jQuery UI
    $("#datepicker").datepicker({
        onSelect: (dateText) => {
            fetchAppointments(dateText);
        }
    });

    // Chiamata iniziale per recuperare gli appuntamenti della data corrente
    fetchAppointments();
});

document.addEventListener('DOMContentLoaded', () => {
    const ctx = document.getElementById('app-distr-chart').getContext('2d');
    const myChart = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday'],
            datasets: [{
                label: 'Appointments',
                data: [12, 19, 3, 5, 2],
                backgroundColor: 'rgba(54, 162, 235, 0.2)',
                borderColor: 'rgba(54, 162, 235, 1)',
                borderWidth: 1
            }]
        },
        options: {
            scales: {
                y: {
                    beginAtZero: true
                }
            }
        }
    });
});

async function fetchAppointments(dateJQuery = null) {
    const date = dateJQuery ? new Date(dateJQuery) : new Date();
    const dateStr = formatDate(date);

    try {
        const response = await fetch(`/api/doctors/appointments?date=${dateStr}`);
        const data = await response.json();
        console.log(JSON.stringify(data));
        renderAppointments(data);
    } catch (error) {
        console.error('Errore nel recupero degli appuntamenti:', error);
    }
}

function formatDate(date) {
    const year = date.getFullYear();
    const month = (date.getMonth() + 1).toString().padStart(2, '0'); // Aggiunge lo zero se il mese è inferiore a 10
    const day = date.getDate().toString().padStart(2, '0'); // Aggiunge lo zero se il giorno è inferiore a 10
    return `${year}-${month}-${day}`;
}

function renderAppointments(appointments) {
    const appointmentsContainer = document.getElementById('appointments');
    appointmentsContainer.innerHTML = '';
    appointments.forEach(appointment => {
        const appointmentElement = createAppointmentElement(appointment);
        appointmentsContainer.appendChild(appointmentElement);
    });
}

function createAppointmentElement(appointment) {
    const div = document.createElement('div');
    div.className = 'appointment';

    const headerDiv = document.createElement('div');
    headerDiv.className = 'appointment-header';

    const detailsDiv = document.createElement('div');

    const h3 = document.createElement('h3');
    h3.textContent = appointment.patientInfo.patientName;

    const pType = document.createElement('p');
    pType.textContent = appointment.visitType;

    const pTime = document.createElement('p');
    pTime.textContent = appointment.appointmentDateTime.split('T')[1].slice(0, 5);

    detailsDiv.appendChild(h3);
    detailsDiv.appendChild(pType);

    headerDiv.appendChild(detailsDiv);
    headerDiv.appendChild(pTime);

    const notesP = document.createElement('p');
    notesP.className = 'appointment-notes';
    notesP.textContent = `Notes: ${appointment.patientNotes}`;

    const actionsDiv = document.createElement('div');
    actionsDiv.className = 'appointment-actions';

    const viewButton = document.createElement('button');
    viewButton.textContent = 'View';

    const cancelButton = document.createElement('button');
    cancelButton.onclick = () => deleteAppointment(appointment.id);
    cancelButton.textContent = 'Cancel';

    actionsDiv.appendChild(viewButton);
    actionsDiv.appendChild(cancelButton);

    div.appendChild(headerDiv);
    div.appendChild(notesP);
    div.appendChild(actionsDiv);

    return div;
}

async function deleteAppointment(appointmentId) {
    try {
        const response = await fetch(`/api/doctors/appointments/${appointmentId}`, {
            method: 'DELETE'
        });

        if (response.ok) {
            // Recupera di nuovo gli appuntamenti per aggiornare la lista
            const date = $('#datepicker').datepicker('getDate');
            const formattedDate = formatDate(date);
            fetchAppointments(formattedDate);
        }
    } catch (error) {
        console.error('Errore nella cancellazione dell\'appuntamento:', error);
    }
}
