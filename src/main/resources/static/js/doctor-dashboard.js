document.addEventListener('DOMContentLoaded', () => {
    // Get doctor ID and key DOM elements
    const doctorId = document.getElementById('doctor-info')?.getAttribute('data-doctor-id');
    const earningsTitleElement = document.getElementById('earningsTitle');
    const currentYear = new Date().getFullYear();
    earningsTitleElement.textContent = `Earnings ${currentYear}`;

    // Canvas contexts for charts
    const visitsCtx = document.getElementById('visitsChart')?.getContext('2d');
    const earningsCtx = document.getElementById('earningsChart')?.getContext('2d');

    // Other DOM Elements
    const endorsementCountElement = document.getElementById('endorsementCount');
    const patientCountElement = document.getElementById('patientCount');
    const patientTodayElement = document.getElementById('patientToday');
    const appointmentList = document.getElementById('appointments-list');
    const patientInfo = document.getElementById('patient-info');
    const weeklyReviewsElement = document.getElementById('weeklyReviews');

    // Chart templates configuration
    const visitsChartConfig = {
        type: 'pie',
        data: { labels: [], datasets: [{ data: [] }] },
        options: {
            responsive: true,
            plugins: {
                legend: { position: 'bottom' },
                title: { display: false }
            }
        }
    };

    const earningsChartConfig = {
        type: 'line',
        data: { labels: [], datasets: [{
                label: 'Earnings',
                data: [],
                backgroundColor: 'rgba(75, 192, 192, 0.2)',
                borderColor: 'rgba(75, 192, 192, 1)',
                borderWidth: 1,
                tension: 0.3,
                fill: true,
            }] },
        options: {
            responsive: true,
            plugins: {
                legend: { display: false }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    title: { display: true, text: 'Earnings (€)' }
                },
                x: {
                    title: { display: true, text: 'Month' }
                }
            }
        }
    };

    // Generic function to fetch JSON from a given URL
    const fetchJSON = async (url) => {
        try{
            const response = await fetch(url);
            if (!response.ok) throw new Error(`HTTP error: ${response.status}`);
            return await response.json();
        }
        catch(error){
            console.error(`Fetch error (${url}):`, error);
            return null;
        }
    };

    // Update visits chart with data from server
    const updateVisitsChart = async () => {
        const data = await fetchJSON('/api/doctor/analytics/visits');
        if(data){
            visitsChartConfig.data.labels = Object.keys(data);
            visitsChartConfig.data.datasets[0].data = Object.values(data);
            new Chart(visitsCtx, visitsChartConfig);
        }
    };

    // Update earnings chart with data from server
    const updateEarningsChart = async () => {
        const data = await fetchJSON('/api/doctor/analytics/earnings');
        if(data){
            const months = ['January', 'February', 'March', 'April', 'May', 'June',
                'July', 'August', 'September', 'October', 'November', 'December'];
            const orderedData = months.map(month => data[month.toLowerCase()] || 0);
            earningsChartConfig.data.labels = months;
            earningsChartConfig.data.datasets[0].data = orderedData;
            new Chart(earningsCtx, earningsChartConfig);
        }
    };

    // Update the number of endorsements
    const updateEndorsements = async () => {
        const data = await fetchJSON(`/api/doctors/${doctorId}/endorsements`);
        if(data && endorsementCountElement){
            endorsementCountElement.textContent = data.endorsementCount;
        }
    };

    // Update the count of new patients this month
    const updateNewPatients = async () => {
        const data = await fetchJSON('/api/doctor/analytics/newPatients');
        if(data !== null && patientCountElement){
            patientCountElement.textContent = data;
        }
    };

    // Update the count of weekly reviews
    const updateWeeklyReviews = async () => {
        const data = await fetchJSON(`/api/doctors/${doctorId}/reviews/week/count`);
        if(data !== null && weeklyReviewsElement){
            weeklyReviewsElement.textContent = data;
        }
    };

    // Format a date into YYYY-MM-DD
    const formatDate = (date) => {
        const year = date.getFullYear();
        const month = (date.getMonth() + 1).toString().padStart(2, '0');
        const day = date.getDate().toString().padStart(2, '0');
        return `${year}-${month}-${day}`;
    };

    // Fetch and display user details for the next appointment
    const fetchUserDetails = async (userId, visitType, patientNotes) => {
        const user = await fetchJSON(`/api/users/${userId}`);
        if(!user)
            return;

        patientInfo.innerHTML = '';
        const fields = [
            { label: 'Name', value: user.name },
            { label: 'Type of Visit', value: visitType },
            { label: 'Notes', value: patientNotes },
            { label: 'D.O.B', value: user.dob },
            { label: 'Sex', value: user.gender },
            { label: 'Phone Number', value: user.personalNumber },
            { label: 'Email', value: user.email }
        ];

        fields.forEach(({ label, value }) => {
            const row = document.createElement('div');
            row.className = 'patient-row';

            const labelEl = document.createElement('span');
            labelEl.className = 'patient-label';
            labelEl.textContent = `${label}:`;

            const valueEl = document.createElement('span');
            valueEl.className = 'patient-data';
            valueEl.textContent = value || '-';

            row.append(labelEl, valueEl);
            patientInfo.appendChild(row);
        });
    };

    // Fetch and display today's appointments
    const updateAppointments = async () => {
        const today = formatDate(new Date());
        const now = new Date();
        const appointments = await fetchJSON(`/api/doctor/appointments?date=${today}`);

        appointmentList.innerHTML = '';
        let appointmentCount = 0;
        let nextAppointment = null;

        if(appointments && appointments.length){
            appointments.sort((a, b) => new Date(a.date) - new Date(b.date));

            appointments.forEach(({ patient, date, visitType, patientNotes }) => {
                const appointmentDate = new Date(date);
                const li = document.createElement('li');
                const detail = document.createElement('div');
                detail.className = 'appointment-detail';

                detail.innerHTML = `
                    <p class="appointment-name">${patient.name}</p>
                    <p class="appointment-time">${appointmentDate.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}</p>
                    <p class="appointment-type">${visitType}</p>
                `;

                li.appendChild(detail);
                appointmentList.appendChild(li);
                appointmentCount++;

                // Set next upcoming appointment
                if (!nextAppointment && appointmentDate >= now) {
                    nextAppointment = { userId: patient.id, visitType, patientNotes };
                }
            });
        }
        else{
            const li = document.createElement('li');
            li.innerHTML = `<p>There are no appointments scheduled for today.</p>`;
            appointmentList.appendChild(li);
        }

        // Update today's patient count
        patientTodayElement.textContent = appointmentCount;
        patientInfo.innerHTML = '';

        // Display next appointment details if available
        if(nextAppointment){
            fetchUserDetails(nextAppointment.userId, nextAppointment.visitType, nextAppointment.patientNotes);
        }
        else{
            patientInfo.innerHTML = `<p>There are no more appointments today.</p>`;
        }
    };

    // Initialize dashboard by loading all data
    updateVisitsChart();
    updateEarningsChart();
    updateEndorsements();
    updateNewPatients();
    updateAppointments();
    updateWeeklyReviews();
});
