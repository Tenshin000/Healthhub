document.addEventListener('DOMContentLoaded', () => {

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
        labels: ['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'Dicember'],
            datasets: [{
            label: 'Earnings',
            data: [1000, 1200, 1500, 1700, 1600, 1900, 2000, 2100, 1800, 2200, 2400, 2500],
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
            // Supponiamo che i dati siano un array di numeri, uno per ogni mese
            return await response.json();
        } catch (error) {
            console.error('Errore nel recupero dei dati:', error);
            return null;
        }
    }

    function addData(chart, label, newData) {
        chart.data.labels.push(label);
        chart.data.datasets.forEach((dataset) => {
            dataset.data.push(newData);
        });
        chart.update();
    }

    function removeData(chart) {
        chart.data.labels.pop();
        chart.data.datasets.forEach((dataset) => {
            dataset.data.pop();
        });
        chart.update();
    }


    fetchVisitsData().then(visitsData => {
        if (visitsData) {
            console.log(visitsChartData.data.datasets[0].data);
            console.log(visitsChartData.data.labels);

            visitsChartData.data.datasets[0].data = Object.values(visitsData);
            visitsChartData.data.labels = Object.keys(visitsData);

            console.log(visitsChartData.data.datasets[0].data);
            console.log(visitsChartData.data.labels);

            const visitsChart = new Chart(visitsCanvas, visitsChartData);
        }

    });
    const earningsChart = new Chart(earningsCanvas, earningsChartData);

});


