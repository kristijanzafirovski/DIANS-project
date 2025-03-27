import { drawCandlestickChart } from "./D3.js";

document.getElementById('searchButton').addEventListener('click', () => {
    const ticker = document.getElementById('ticker').value.trim();
    if (ticker) {
        fetchStockData(ticker);
        fetchAnalysis(ticker);
    } else {
        console.error('Ticker symbol is required!');
        displayErrorMessage('Please enter a valid ticker symbol.');
    }
});

function fetchStockData(ticker) {
    fetch(`/stocks/${ticker}`)
        .then(response => {
            if (!response.ok) {
                throw new Error(`Failed to fetch stock data for symbol: ${ticker}. HTTP status: ${response.status}`);
            }
            return response.json();
        })
        .then(data => formatAndDisplayStockData(data))
        .catch(error => {
            console.error('Error fetching stock data:', error);
            displayErrorMessage('An error occurred while fetching stock data. Please try again.');
        });
}

function formatAndDisplayStockData(data) {
    if (!data || typeof data !== 'object') {
        console.error('No data found or data is not in the correct format');
        displayErrorMessage('No stock data found for the selected ticker.');
        return;
    }

    const { dailyTimeSeries, intradayTimeSeries } = data;

    if (!dailyTimeSeries || typeof dailyTimeSeries !== 'object') {
        console.error('Invalid dailyTimeSeries data structure:', dailyTimeSeries);
        displayErrorMessage('No daily time series data found for the stock.');
        return;
    }

    // Populate the dropdown for dates (from dailyTimeSeries)
    const dateSelect = document.getElementById('dateSelect');
    dateSelect.innerHTML = '<option value="">Select Date</option>';
    Object.keys(dailyTimeSeries).forEach(date => {
        const option = document.createElement('option');
        option.value = date;
        option.text = date;
        dateSelect.appendChild(option);
    });

    // Update UI for selected date
    dateSelect.addEventListener('change', () => {
        const selectedDate = dateSelect.value;

        const selectedDateData = document.getElementById('selectedDateData');
        const sName = document.getElementById('sname');
        const sTime = document.getElementById('stime');

        selectedDateData.innerHTML = '';
        if (selectedDate && dailyTimeSeries[selectedDate]) {
            const dailyData = dailyTimeSeries[selectedDate];
            sName.innerText = `Stock Symbol: ${ticker}`;
            sTime.innerText = `Selected date: ${selectedDate}`;
            const dataElement = document.createElement('p');
            dataElement.innerText = `Open: ${dailyData.open}, High: ${dailyData.high}, Low: ${dailyData.low}, Close: ${dailyData.close}, Volume: ${dailyData.volume}`;
            selectedDateData.appendChild(dataElement);
        }
    });

    // Prepare and display the candlestick chart (e.g., using dailyTimeSeries)
    const chartData = Object.entries(dailyTimeSeries).map(([date, values]) => ({
        date: new Date(date),
        open: parseFloat(values.open),
        high: parseFloat(values.high),
        low: parseFloat(values.low),
        close: parseFloat(values.close),
        volume: parseInt(values.volume, 10),
    }));

    if (typeof drawCandlestickChart === 'function') {
        drawCandlestickChart(chartData);
    } else {
        console.error('drawCandlestickChart is not defined.');
    }
}

function fetchAnalysis(ticker) {
    fetch(`/analyze/${ticker}`)
        .then(response => {
            if (!response.ok) {
                throw new Error(`Failed to fetch analysis for symbol: ${ticker}. HTTP status: ${response.status}`);
            }
            return response.json();
        })
        .then(analysis => {
            if (validateAnalysisResponse(analysis)) {
                displayAnalysis(analysis);
            } else {
                console.error('Invalid analysis response format:', analysis);
                displayErrorMessage('Analysis data is not in the expected format.');
            }
        })
        .catch(error => {
            console.error('Error fetching analysis:', error);
            displayErrorMessage(`An error occurred while fetching analysis. ${error.message}`);
        });
}

function validateAnalysisResponse(analysis) {
    return analysis && typeof analysis === 'object' &&
        ['dailySignal', 'intradaySignal', 'hourlySignal'].every(key => key in analysis);
}

function displayAnalysis(analysis) {
    const analysisResults = document.getElementById('analysisResults');
    analysisResults.innerHTML = `
        <p>Daily Signal: ${analysis.dailySignal}</p>
        <p>Intraday Signal: ${analysis.intradaySignal}</p>
        <p>Hourly Signal: ${analysis.hourlySignal}</p>
    `;
}

function displayErrorMessage(message) {
    alert(message);
}