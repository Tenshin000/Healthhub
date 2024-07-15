let distributionChart = null;
$(document).ready(() => {
    // Inizializzazione del datepicker con jQuery UI
    $("#datepicker").datepicker({
        firstDay: 1,
        onSelect: (dateText) => {
            fetchAppointments(dateText).then((appointments) => renderAppointments(appointments));
            fetchAppointmentsAnalytics(dateText).then((appointmentDistribution) => renderAppointmentsAnalytics(appointmentDistribution));
        }
    });
    // Chiamata iniziale per recuperare gli appuntamenti della data corrente
    fetchAppointments().then((appointments) => renderAppointments(appointments));
    fetchAppointmentsAnalytics().then((appointmentDistribution) => renderAppointmentsAnalytics(appointmentDistribution));

});

function getWeekNumber(date) {
    // Copy date as UTC to avoid DST
    let d = new Date(Date.UTC(date.getFullYear(), date.getMonth(), date.getDate()));
    // Shift to the following Saturday to get the year
    d.setUTCDate(d.getUTCDate() + 6 - d.getUTCDay());
    // Get the first day of the year
    let yearStart = new Date(Date.UTC(d.getUTCFullYear(), 0, 1));
    yearStart.setUTCDate(yearStart.getUTCDate() - yearStart.getUTCDay());
    // Get difference between yearStart and d in milliseconds
    // Reduce to whole weeks
    return (Math.ceil((d - yearStart) / 6.048e8));
}

async function fetchAppointments(dateJQuery = null) {
    const date = dateJQuery ? new Date(dateJQuery) : new Date();
    const dateStr = formatDate(date);

    try {
        const response = await fetch(`/api/doctor/appointments?date=${dateStr}`);
        return await response.json();
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

    const dayTitle = document.createElement('h2');
    dayTitle.textContent = 'Appointments for ' + formatDate($('#datepicker').datepicker('getDate'));
    appointmentsContainer.appendChild(dayTitle);

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
        const response = await fetch(`/api/doctor/appointments/${appointmentId}`, {
            method: 'DELETE'
        });

        if (response.ok) {
            // Recupera di nuovo gli appuntamenti per aggiornare la lista
            const date = $('#datepicker').datepicker('getDate');
            //const formattedDate = formatDate(date);
            fetchAppointments(date).then((appointments) => renderAppointments(appointments));
            fetchAppointmentsAnalytics(date).then((appointmentDistribution) => renderAppointmentsAnalytics(appointmentDistribution));
        }
    } catch (error) {
        console.error('Errore nella cancellazione dell\'appuntamento:', error);
    }
}

function startOfWeek(d) {
    d = new Date(d);
    let day = d.getDay(),
        diff = d.getDate() - day + (day === 0 ? -6 : 1); // adjust when day is sunday
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
        console.error('Errore nel recupero dei dati:', error);
        return null;
    }
}


let appDistrChartData = {
    type: 'bar',
    data: {
        labels: ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday'],
        datasets: [{
            label: 'Appointments',
            data: [12, 19, 3, 5, 2],
            backgroundColor: ['rgba(54, 162, 235, 0.2)',],
            borderColor: ['rgba(54, 162, 235, 1)',],
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
        let days = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday'];
        appointmentDistribution = {...appointmentDistribution};
        let orderedData = [];
        for (let i = 0; i < days.length; i++) {
            let month = days[i].toLowerCase()
            if (appointmentDistribution[month]) {
                orderedData.push(appointmentDistribution[month]);
            }
            else {
                orderedData.push(0);
            }
        }
        console.log(orderedData);
        appDistrChartData.data.datasets[0].data = orderedData
        appDistrChartData.data.labels = days;
        distributionChart = new Chart(ctx, appDistrChartData);
    }
}
