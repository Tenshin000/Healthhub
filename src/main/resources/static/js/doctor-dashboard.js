document.addEventListener('DOMContentLoaded', () => {
    const doctorId = document.getElementById('doctor-info').getAttribute('data-doctor-id');

    // Get the earnings title element
    const earningsTitleElement = document.getElementById('earningsTitle');

    // Get the current year
    const currentYear = new Date().getFullYear();

    // Update the title with the current year
    earningsTitleElement.textContent = 'Earnings ' + currentYear;


    // CHARTS
    const visitsCanvas = document.getElementById('visitsChart').getContext('2d');
    let visitsChartData = {
        type: 'pie',
        data: {
            labels: ['New Patients', 'Old Patients', 'Total Patients'],
            datasets: [{
                data: [30, 50, 80],
            }]
        },
        options: {
            responsive: true,
            plugins: {
                legend: {
                    position: 'bottom',
                },
                title: {
                    display: false,
                    text: 'Patients Summary December 2021'
                }
            }
        },
    };

    const earningsCanvas = document.getElementById('earningsChart').getContext('2d');
    let earningsChartData = {
        type: 'line',
            data: {
        labels: ['January', 'February', 'March', 'April', 'May', 'June'],
            datasets: [{
            label: 'Earnings',
            data: [1000, 1200, 1500, 1700, 1600, 1900],
            backgroundColor: 'rgba(75, 192, 192, 0.2)',
            borderColor: 'rgba(75, 192, 192, 1)',
            borderWidth: 1,
            tension: 0.3,
            fill: true,
        }]
    },
        options: {
            plugins: {
                legend: {
                    position: 'top',
                        display: false
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                        title: {
                        display: true,
                            text: 'Earnings (€)'
                    }
                },
                x: {
                    title: {
                        display: true,
                            text: 'Month'
                    }
                }
            }
        },
    };

    async function fetchVisitsData() {
        try {
            const response = await fetch('/api/doctor/analytics/visits');
            if (!response.ok) {
                throw new Error('Network response was not ok ' + response.statusText);
            }
            // Suppose the data is an array of numbers, one for each month
            return await response.json();
        } catch (error) {
            console.error('Errore nel recupero dei dati:', error);
            return null;
        }
    }

    async function fetchEarningsData() {
        try {
            const response = await fetch('/api/doctor/analytics/earnings');
            if (!response.ok) {
                throw new Error('Network response was not ok ' + response.statusText);
            }
            // Suppose the data is an array of numbers, one for each month
            return await response.json();
        } catch (error) {
            console.error('Errore nel recupero dei dati:', error);
            return null;
        }
    }

    fetchVisitsData().then(visitsData => {
        if (visitsData) {
            visitsChartData.data.datasets[0].data = Object.values(visitsData);
            visitsChartData.data.labels = Object.keys(visitsData);
            const visitsChart = new Chart(visitsCanvas, visitsChartData);
        }
    });

    fetchEarningsData().then(earningsData => {
        let months = ['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December'];
        if (earningsData) {
            earningsData = {...earningsData};
            let orderedData = [];
            for (let i = 0; i < months.length; i++) {
                let month = months[i].toLowerCase()
                if (earningsData[month]) {
                    orderedData.push(earningsData[month]);
                }
                else {
                    orderedData.push(0);
                }
            }
            earningsChartData.data.datasets[0].data = orderedData
            earningsChartData.data.labels = months;
            const earningsChart = new Chart(earningsCanvas, earningsChartData);
        }
    });

    // ENDORSEMENT DISPLAY CODE
    let endorsementCount = 0;

    // Function to get the state of endorsements
    async function fetchEndorsementState() {
        try {
            const response = await fetch(`/api/doctors/${doctorId}/endorsements`);
            if (!response.ok) {
                throw new Error(`HTTP error! Status: ${response.status}`);
            }
            const data = await response.json();
            endorsementCount = data.endorsementCount;
        } catch (error) {
            console.error('Error fetching initial endorsement state:', error);
            // Handle errors appropriately, e.g., by showing a message to the user
        }
    }
    fetchEndorsementState();

    // NEW PATIENTS BY MONTH DISPLAY
    let patientCount = 0;
    const patientCountElement = document.getElementById('patientCount');

    // Function to get the new Patients of the currenth Month
    async function fetchNewPatients() {
        try {
            const response = await fetch(`/api/doctor/analytics/newPatients`);
            if (!response.ok) {
                throw new Error(`HTTP error! Status: ${response.status}`);
            }
            const data = await response.json();
            patientCount = data;
            patientCountElement.textContent = patientCount;
        } catch (error) {
            console.error('Error fetching the number of patients', error);
            // Handle errors appropriately, e.g., by showing a message to the user
        }
    }

    fetchNewPatients();

    // DISPLAY TODAY VISITS
    const appointmentList = document.getElementById('appointments-list');
    let patientToday = 0;
    const patientTodayElement = document.getElementById('patientToday');
    const patientInfo = document.getElementById('patient-info');

    async function fetchAppointments() {
        const date = formatDate(new Date());
        const now = new Date();

        try {
            const response = await fetch(`/api/doctor/appointments?date=${date}`);
            const appointments = await response.json();

            appointmentList.innerHTML = '';
            let appointmentCount = 0;

            let nextAppointment = null;

            appointments.sort((a, b) => new Date(a.date) - new Date(b.date));

            appointments.forEach(appointment => {
                const li = document.createElement('li');
                const div = document.createElement('div');
                div.className = 'appointment-detail';

                const name = document.createElement('p');
                name.className = 'appointment-name';
                name.textContent = appointment.patient.name;

                const time = document.createElement('p');
                time.className = 'appointment-time';
                const appointmentDate = new Date(appointment.date);
                const formattedTime = appointmentDate.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
                time.textContent = formattedTime;

                const type = document.createElement('p');
                type.className = 'appointment-type';
                type.textContent = appointment.visitType;

                div.appendChild(name);
                div.appendChild(time);
                div.appendChild(type);
                li.appendChild(div);
                appointmentList.appendChild(li);

                appointmentCount++;

                // Find the first appointment that is now or after now
                if(!nextAppointment && appointmentDate >= now){
                    nextAppointment = {
                        userId: appointment.patient.id,
                        visitType: appointment.visitType,
                        patientNotes: appointment.patientNotes
                    };
                }
            });

            if(appointmentCount === 0){
                const li = document.createElement('li');
                const p = document.createElement('p');
                p.textContent = "There are no appointments scheduled for today.";
                li.appendChild(p);
                appointmentList.appendChild(li);
            }

            patientToday = appointmentCount;
            patientTodayElement.textContent = patientToday;

            patientInfo.innerHTML = '';

            // If we find the next appointment, we load the user details
            if(nextAppointment){
                fetchUserDetails(nextAppointment.userId, nextAppointment.visitType, nextAppointment.patientNotes);
            }
            else{
                const p = document.createElement('p');
                p.textContent = "There are no more appointments today.";
                patientInfo.appendChild(p);
            }

        } catch (error) {
            console.error('Error retrieving appointments:', error);
        }
    }

    function formatDate(date) {
        const year = date.getFullYear();
        const month = (date.getMonth() + 1).toString().padStart(2, '0'); // Add zero if month is less than 10
        const day = date.getDate().toString().padStart(2, '0'); // Add zero if day is less than 10
        return `${year}-${month}-${day}`;
    }

    async function fetchUserDetails(userId, visitType, patientNotes) {
        try {
            const response = await fetch(`/api/users/${userId}`);
            if (!response.ok) {
                throw new Error('Failed to fetch user details');
            }
            const user = await response.json();

            // Clear the contents first
            patientInfo.innerHTML = '';

            // Create the data to insert
            const fields = [
                { label: 'Name', value: user.name },
                { label: 'Type of Visit', value: visitType },
                { label: 'Notes', value: patientNotes },
                { label: 'D.O.B', value: user.dob },
                { label: 'Sex', value: user.gender },
                { label: 'Phone Number', value: user.personalNumber },
                { label: 'Email', value: user.email }
            ];

            // Dynamically build content
            fields.forEach(field => {
                const row = document.createElement('div');
                row.className = 'patient-row';

                const label = document.createElement('span');
                label.className = 'patient-label';
                label.textContent = `${field.label}:`;

                const data = document.createElement('span');
                data.className = 'patient-data';
                data.textContent = field.value ? field.value : '-';

                row.appendChild(label);
                row.appendChild(data);
                patientInfo.appendChild(row);
            });

        } catch (error) {
            console.error('Error retrieving user details:', error);
        }
    }

    fetchAppointments();
});


