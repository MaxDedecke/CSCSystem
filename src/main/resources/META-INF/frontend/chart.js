const script = document.createElement('script');
script.src = 'https://cdn.jsdelivr.net/npm/chart.js';
document.head.appendChild(script);

script.onload = function() {
    window.drawChart = function() {
		const container = document.getElementById('chart-container');
		container.innerHTML = '<canvas id="myChart"></canvas>';
		
        const ctx = document.getElementById('myChart').getContext('2d');
        new Chart(ctx, {
            type: 'bar',
            data: {
                labels: ['Januar', 'Februar', 'März', 'April', 'Mai', 'Juni'],
                datasets: [{
                    label: 'Umsatz',
                    data: [12, 19, 3, 5, 2, 3],
                    backgroundColor: 'rgba(75, 192, 192, 0.2)',
                    borderColor: 'rgba(75, 192, 192, 1)',
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
    };
};