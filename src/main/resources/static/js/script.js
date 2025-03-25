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


        const dateSelect = document.getElementById('dateSelect');
        dateSelect.innerHTML = '<option value="">Select Date</option>';
        Object.keys(stock.timeSeries).forEach(date => {
            const option = document.createElement('option');
            option.value = date;
            option.text = date;
            dateSelect.appendChild(option);
        });

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


        const chartData = Object.entries(stock.timeSeries).map(([date, values]) => ({
            date: new Date(date),
            open: values.open,
            high: values.high,
            low: values.low,
            close: values.close,
            volume: values.volume
        }));

        if (typeof drawCandlestickChart === 'function') {
            drawCandlestickChart(chartData);
        } else {
            console.error('drawCandlestickChart is not defined.');
        }
    } else {
        document.getElementById('tickerTitle').innerText = 'No data found';
    }
}

function fetchAnalysis(symbol) {
    fetch(`/analyze/${symbol}`)
        .then(response => response.json())
        .then(analysis => {
            displayAnalysis(analysis);
        })
        .catch(error => console.error('Error fetching analysis:', error));
}

function displayAnalysis(analysis) {
    const analysisDiv = document.getElementById('analysisResults');
    analysisDiv.innerHTML = `
        <h4>Signals</h4>
        <div class="card">
            <div class="card-body">
                <h5 class="card-title">Intraday Signal</h5>
                <p>${analysis.intradaySignal}</p>
            </div>
        </div>
        <div class="card">
            <div class="card-body">
                <h5 class="card-title">Hourly Signal</h5>
                <p>${analysis.hourlySignal}</p>
            </div>
        </div>
        <div class="card">
            <div class="card-body">
                <h5 class="card-title">Daily Signal</h5>
                <p>${analysis.dailySignal}</p>
            </div>
        </div>
    `;
}
