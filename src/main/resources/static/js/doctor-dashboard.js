const ctx = document.getElementById('patientsChart').getContext('2d');
const patientsChart = new Chart(ctx, {
    type: 'pie',
    data: {
        labels: ['New Patients', 'Old Patients', 'Total Patients'],
        datasets: [{
            data: [30, 50, 80],
            backgroundColor: ['#4CAF50', '#FFC107', '#00ACC1'],
        }]
    },
    options: {
        responsive: true,
        plugins: {
            legend: {
                position: 'top',
            },
            title: {
                display: true,
                text: 'Patients Summary December 2021'
            }
        }
    },
});
