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

// Add event listeners for interval buttons
document.querySelectorAll('#intervalButtons button').forEach(button => {
    button.addEventListener('click', () => {
        const interval = button.textContent === '1D' ? 'daily' :
            button.textContent === '1H' ? 'hourly' : '5min';
        updateChartForInterval(interval, { dailyTimeSeries, intradayTimeSeries, hourlyTimeSeries });
    });
});

// Rest of the code remains the same...
function fetchStockData(ticker) {
    showLoading();
    fetch(`/stocks/${ticker}`)
        .then(response => {
            if (!response.ok) {
                throw new Error(`Failed to fetch stock data for symbol: ${ticker}. HTTP status: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            console.log(data);
            formatAndDisplayStockData(data);
        })
        .catch(error => {
            console.error('Error fetching stock data:', error);
            displayErrorMessage('An error occurred while fetching stock data. Please try again.');
        })
        .finally(() => {
            hideLoading();
        });
}

function formatAndDisplayStockData(data) {
    if (!data || typeof data !== 'object') {
        console.error('No data found or data is not in the correct format');
        displayErrorMessage('No stock data found for the selected ticker.');
        return;
    }

    const { dailyTimeSeries, intradayTimeSeries, hourlyTimeSeries } = data;

    if (!dailyTimeSeries || typeof dailyTimeSeries !== 'object') {
        console.error('Invalid dailyTimeSeries data structure:', dailyTimeSeries);
        displayErrorMessage('No daily time series data found for the stock.');
        return;
    }

    // Default to rendering the daily chart
    updateChartForInterval('daily', { dailyTimeSeries, intradayTimeSeries, hourlyTimeSeries });
}

function updateChartForInterval(interval, seriesData) {
    const { dailyTimeSeries, intradayTimeSeries, hourlyTimeSeries } = seriesData;
    let chartData = [];

    if (interval === 'daily') {
        chartData = formatTimeSeriesData(dailyTimeSeries);
    } else if (interval === 'hourly') {
        chartData = formatTimeSeriesData(hourlyTimeSeries);
    } else if (interval === '5min') {
        chartData = formatTimeSeriesData(intradayTimeSeries, true); // Filter for 5-min intervals
    }

    if (chartData.length > 0) {
        clearChartContainer(); // Clear any existing chart
        drawCandlestickChart(chartData); // Redraw the chart
    } else {
        displayErrorMessage(`No data available for the selected interval: ${interval}`);
    }
}

function formatTimeSeriesData(timeSeries, isFiveMinute = false) {
    if (!timeSeries || typeof timeSeries !== 'object') {
        return [];
    }

    // Optionally filter for 5-minute intervals
    const filteredTimeSeries = isFiveMinute
        ? Object.fromEntries(
            Object.entries(timeSeries).filter(([key]) => {
                const minutes = new Date(key).getMinutes();
                return minutes % 5 === 0; // Only include 5-minute intervals
            })
        )
        : timeSeries;

    // Map the time series to chart-friendly format
    return Object.entries(filteredTimeSeries).map(([timestamp, values]) => ({
        date: new Date(timestamp),
        open: parseFloat(values.open),
        high: parseFloat(values.high),
        low: parseFloat(values.low),
        close: parseFloat(values.close),
        volume: parseInt(values.volume, 10),
    }));
}

function clearChartContainer() {
    const chartContainer = document.getElementById('chartContainer');
    if (chartContainer) {
        chartContainer.innerHTML = ''; // Clear the container
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
        ['latestDailySignal', 'latestIntradaySignal', 'latestHourlySignal'].every(key => key in analysis);
}

function displayAnalysis(analysis) {
    function setupSignalElement(element, signal) {
        element.classList.remove('buy', 'sell', 'neutral');
        element.classList.add('signal-card');
        element.classList.add(signal.toLowerCase());
        element.innerHTML = '';
        const signalTextElement = document.createElement('p');
        signalTextElement.innerText = signal.toUpperCase();
        element.appendChild(signalTextElement);
    }

    const dailySignalElement = document.getElementById('dailySignal');
    const intradaySignalElement = document.getElementById('intradaySignal');
    const hourlySignalElement = document.getElementById('hourlySignal');

    setupSignalElement(dailySignalElement, analysis.latestDailySignal);
    setupSignalElement(intradaySignalElement, analysis.latestIntradaySignal);
    setupSignalElement(hourlySignalElement, analysis.latestHourlySignal);
}

function showLoading() {
    const loadingOverlay = document.getElementById('loadingOverlay');
    loadingOverlay.style.display = 'flex';
}

function hideLoading() {
    const loadingOverlay = document.getElementById('loadingOverlay');
    loadingOverlay.style.display = 'none';
}

function displayErrorMessage(message) {
    alert(message);
}
document.querySelectorAll('#intervalButtons button').forEach(button => {
    button.addEventListener('click', () => {
        const interval = button.textContent === '1D' ? 'daily' :
            button.textContent === '1H' ? 'hourly' : '5min';
        updateChartForInterval(interval, { dailyTimeSeries, intradayTimeSeries, hourlyTimeSeries });
    });
});