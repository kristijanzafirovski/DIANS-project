import { drawCandlestickChart } from "./D3.js";

document.getElementById('searchButton').addEventListener('click', function () {
    const ticker = document.getElementById('ticker').value;
    fetchStockData(ticker);
    fetchAnalysis(ticker);
});

function fetchStockData(ticker) {
    fetch(`/stocks/${ticker}`)
        .then(response => response.json())
        .then(data => formatAndDisplayStockData(data))
        .catch(error => console.error('Error fetching stock data:', error));

}

function formatAndDisplayStockData(data) {
    if (data.length > 0) {
        const stock = data[0];

        // Populate the dropdown with dates
        const dateSelect = document.getElementById('dateSelect');
        dateSelect.innerHTML = '<option value="">Select Date</option>';
        Object.keys(stock.timeSeries).forEach(date => {
            const option = document.createElement('option');
            option.value = date;
            option.text = date;
            dateSelect.appendChild(option);
        });

        // Add event listener to display data for selected date
        dateSelect.addEventListener('change', function () {
            const selectedDate = dateSelect.value;
            if (selectedDate !== '') {
                const dailyData = stock.timeSeries[selectedDate];
                const selectedDateData = document.getElementById('selectedDateData');
                selectedDateData.innerHTML = '';
                const dataElement = document.createElement('p');
                const sName = document.getElementById("sname");
                const sTime = document.getElementById("stime");
                sName.innerText = '';
                sTime.innerText = '';
                sName.innerText = "Stock Symbol: " + stock.symbol;
                sTime.innerText = "Last refresh time: " + stock.lastRefreshed;
                dataElement.innerText = `Open: ${dailyData.open}, High: ${dailyData.high}, Low: ${dailyData.low}, Close: ${dailyData.close}, Volume: ${dailyData.volume}`;
                selectedDateData.appendChild(dataElement);
            }
        });

        // Transform the data into an array of objects for the chart
        const chartData = Object.entries(stock.timeSeries).map(([date, values]) => ({
            date: new Date(date),
            open: values.open,
            high: values.high,
            low: values.low,
            close: values.close,
            volume: values.volume
        }));

        drawCandlestickChart(chartData);
    } else {
        document.getElementById('tickerTitle').innerText = 'No data found';
    }
}

function fetchAnalysis(symbol) {
    fetch(`/analyze/${symbol}`)
        .then(response => response.json())
        .then(analysis => {
            displayAnalysis(analysis);
            drawChartWithSignals(analysis);
        });
}

function drawChartWithSignals(analysis) {
    const chartData = Object.entries(stock.timeSeries).map(([date, values]) => ({
        date: new Date(date),
        open: values.open,
        high: values.high,
        low: values.low,
        close: values.close,
        volume: values.volume
    }));

    drawCandlestickChart(chartData, analysis.dailySignals);
}

function displayAnalysis(analysis) {
    const analysisDiv = document.getElementById('analysisResults');

    const intradayList = analysis.intradaySignals.map(s => `<li>${s}</li>`).join('');
    const dailyList = analysis.dailySignals.map(s => `<li>${s}</li>`).join('');

    analysisDiv.innerHTML = `
        <h4>Intraday Signals:</h4>
        <ul>${intradayList}</ul>
        <h4>Daily Signals:</h4>
        <ul>${dailyList}</ul>
    `;
}
