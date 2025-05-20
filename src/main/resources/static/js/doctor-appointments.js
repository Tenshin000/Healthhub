let distributionChart = null;

$(document).ready(() => {
    const modalOverlay = document.getElementById('user-modal-overlay');
    const modalCloseBtn = modalOverlay.querySelector('.modal-close');
    const modalContent  = modalOverlay.querySelector('.modal-content');

    // Initialization of the datepicker with jQuery UI
    $("#datepicker").datepicker({
        firstDay: 1,
        onSelect: (dateText) => {
            fetchAppointments(dateText).then((appointments) => renderAppointments(appointments));
            fetchAppointmentsAnalytics(dateText).then((appointmentDistribution) => renderAppointmentsAnalytics(appointmentDistribution));
        }
    });

    // Initial call to fetch appointments for the current date
    fetchAppointments().then((appointments) => renderAppointments(appointments));
    fetchAppointmentsAnalytics().then((appointmentDistribution) => renderAppointmentsAnalytics(appointmentDistribution));
});

function getWeekNumber(date) {
    let d = new Date(Date.UTC(date.getFullYear(), date.getMonth(), date.getDate()));
    d.setUTCDate(d.getUTCDate() + 6 - d.getUTCDay());
    let yearStart = new Date(Date.UTC(d.getUTCFullYear(), 0, 1));
    yearStart.setUTCDate(yearStart.getUTCDate() - yearStart.getUTCDay());
    return Math.ceil((d - yearStart) / 6.048e8);
}

async function fetchAppointments(dateJQuery = null) {
    const date = dateJQuery ? new Date(dateJQuery) : new Date();
    const dateStr = formatDate(date);

    try {
        const response = await fetch(`/api/doctor/appointments?date=${dateStr}`);
        return await response.json();
    } catch (error) {
        console.error('Error fetching appointments:', error);
    }
}

function formatDate(date) {
    const year = date.getFullYear();
    const month = (date.getMonth() + 1).toString().padStart(2, '0');
    const day = date.getDate().toString().padStart(2, '0');
    return `${year}-${month}-${day}`;
}

function renderAppointments(appointments) {
    const appointmentsContainer = document.getElementById('appointments');
    appointmentsContainer.innerHTML = '';

    const dayTitle = document.createElement('h2');
    dayTitle.textContent = 'Appointments for ' + formatDate($('#datepicker').datepicker('getDate'));
    appointmentsContainer.appendChild(dayTitle);

    appointments.sort((a, b) => {
        const timeA = a.date.split('T')[1].slice(0, 5);
        const timeB = b.date.split('T')[1].slice(0, 5);
        return timeA.localeCompare(timeB);
    });

    appointments.forEach(appointment => {
        console.log('DEBUG appointment.patient →', appointment.patient);
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
    h3.textContent = appointment.patient.name;

    const pType = document.createElement('p');
    pType.textContent = appointment.visitType;

    const pTime = document.createElement('p');
    pTime.textContent = appointment.date.split('T')[1].slice(0, 5);

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
    viewButton.setAttribute('data-email', appointment.patient.email);
    viewButton.addEventListener('click', () => {
        const userEmail = viewButton.dataset.email;
        showUserDetails(userEmail);
    });

    const deleteButton = document.createElement('button');
    deleteButton.onclick = () => deleteAppointment(appointment.id);
    deleteButton.textContent = 'Delete';

    actionsDiv.appendChild(viewButton);
    actionsDiv.appendChild(deleteButton);

    div.appendChild(headerDiv);
    div.appendChild(notesP);
    div.appendChild(actionsDiv);

    return div;
}

async function deleteAppointment(appointmentId) {
    try {
        const response = await fetch(`/api/doctor/appointments/${appointmentId}`, {
            method: 'DELETE'
        });

        if (response.ok) {
            const date = $('#datepicker').datepicker('getDate');
            fetchAppointments(date).then((appointments) => renderAppointments(appointments));
            fetchAppointmentsAnalytics(date).then((appointmentDistribution) => renderAppointmentsAnalytics(appointmentDistribution));
        }
    } catch (error) {
        console.error('Error deleting appointment:', error);
    }
}

function startOfWeek(d) {
    d = new Date(d);
    let day = d.getDay(),
        diff = d.getDate() - day + (day === 0 ? -6 : 1);
    return new Date(d.setDate(diff));
}

async function fetchAppointmentsAnalytics(dayText = null) {
    try {
        const params = new URLSearchParams();
        if (dayText !== null) {
            const date = new Date(dayText);
            const week = getWeekNumber(startOfWeek(date));
            const year = date.getFullYear();

            params.append('year', year);
            params.append('week', week);
        }

        const queryString = params.toString();
        const url = queryString ? `/api/doctor/analytics/visits/distribution?${queryString}` : '/api/doctor/analytics/visits/distribution';

        const response = await fetch(url);
        if (!response.ok) {
            throw new Error('Network response was not ok ' + response.statusText);
        }

        return await response.json();
    } catch (error) {
        console.error('Error fetching data:', error);
        return null;
    }
}

let appDistrChartData = {
    type: 'bar',
    data: {
        labels: ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday'],
        datasets: [{
            label: 'Appointments',
            data: [12, 19, 3, 5, 2, 0], // Placeholder iniziale per sabato
            backgroundColor: ['rgba(54, 162, 235, 0.2)'],
            borderColor: ['rgba(54, 162, 235, 1)'],
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
};

function renderAppointmentsAnalytics(appointmentDistribution) {
    if (distributionChart) {
        distributionChart.destroy();
    }

    const ctx = document.getElementById('app-distr-chart').getContext('2d');

    if (appointmentDistribution) {
        let days = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday'];
        appointmentDistribution = { ...appointmentDistribution };
        let orderedData = [];

        for (let i = 0; i < days.length; i++) {
            let dayKey = days[i].toLowerCase();
            orderedData.push(appointmentDistribution[dayKey] || 0);
        }

        console.log(orderedData);
        appDistrChartData.data.datasets[0].data = orderedData;
        appDistrChartData.data.labels = days;
        distributionChart = new Chart(ctx, appDistrChartData);
    }
}

function createAppointmentElement(appointment) {
    const div = document.createElement('div');
    div.className = 'appointment';

    const headerDiv = document.createElement('div');
    headerDiv.className = 'appointment-header';

    const detailsDiv = document.createElement('div');

    const h3 = document.createElement('h3');
    h3.textContent = appointment.patient.name;

    const emailEl = document.createElement('p');
    const emailId = `email-${appointment.id}`;
    emailEl.id = emailId;
    emailEl.textContent = appointment.patient.email;
    emailEl.className = 'hidden';

    const pType = document.createElement('p');
    pType.textContent = appointment.visitType;

    const pTime = document.createElement('p');
    pTime.textContent = appointment.date.split('T')[1].slice(0, 5);

    detailsDiv.appendChild(h3);
    detailsDiv.appendChild(emailEl);
    detailsDiv.appendChild(pType);

    headerDiv.appendChild(detailsDiv);
    headerDiv.appendChild(pTime);

    const notesP = document.createElement('p');
    notesP.className = 'appointment-notes';
    notesP.textContent = `Notes: ${appointment.patientNotes}`;

    const actionsDiv = document.createElement('div');
    actionsDiv.className = 'appointment-actions';

    const viewButton = document.createElement('button');
    viewButton.dataset.email = appointment.patient.email || '';
    viewButton.textContent = 'View';
    viewButton.onclick = () => {
        const userEmail = viewButton.dataset.email;
        console.log('DEBUG: viewBtn.dataset.email =', userEmail);
        showUserDetails(userEmail);
    };

    const deleteButton = document.createElement('button');
    deleteButton.onclick = () => deleteAppointment(appointment.id);
    deleteButton.textContent = 'Delete';

    actionsDiv.appendChild(viewButton);
    actionsDiv.appendChild(deleteButton);

    div.appendChild(headerDiv);
    div.appendChild(notesP);
    div.appendChild(actionsDiv);

    return div;
}


// Function for fetch + rendering the modal
async function showUserDetails(email) {
    try {
        console.log(email);
        const response = await fetch(
            `/api/user/details/view?email=${encodeURIComponent(email)}`
        );
        if (!response.ok)
            throw new Error(response.statusText);

        const userDetails = await response.json();

        // Popola il modal
        const body = document.querySelector('#user-modal-overlay .modal-body');
        body.innerHTML = `
            <h3>${userDetails.name}</h3>
            <p><strong>Fiscal Code:</strong> ${userDetails.fiscalCode}</p>
            <p><strong>Birthdate:</strong> ${userDetails.birthDate || 'N/D'}</p>
            <p><strong>Gender:</strong> ${userDetails.gender}</p>
            <p><strong>Email:</strong> ${userDetails.email}</p>
            <p><strong>Personal Phone:</strong> ${userDetails.phoneNumber || 'N/A'}</p>
        `;

        // Show overlay
        document.getElementById('user-modal-overlay').classList.remove('hidden');
    } catch (err) {
        console.error('Errore nel fetch dei dettagli utente:', err);
        alert('Impossibile caricare i dettagli utente.');
    }
}

// Close modal
document.addEventListener('DOMContentLoaded', () => {
    const overlay = document.getElementById('user-modal-overlay');
    const closeBtn = overlay.querySelector('.modal-close');

    closeBtn.onclick = () => {
        overlay.classList.add('hidden');
    };
});
