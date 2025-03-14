document.getElementById('searchButton').addEventListener('click', function () {
    const ticker = document.getElementById('ticker').value;
    fetchStockData(ticker);
});

function fetchStockData(ticker) {
    fetch(`/stocks/${ticker}`)
        .then(response => response.json())
        .then(data => displayStockData(data))
        .catch(error => console.error('Error fetching stock data:', error));

    document.getElementById('resultContainer').style.display = 'block';
}

function displayStockData(data) {
    if (data.length > 0) {
        const stock = data[0];
        document.getElementById('tickerTitle').innerText = stock.symbol;
        document.getElementById('tickerSymbol').innerText = `Symbol: ${stock.symbol}`;
        document.getElementById('lastRefreshed').innerText = `Last Refreshed: ${stock.lastRefreshed}`;
        document.getElementById('timeZone').innerText = `Time Zone: ${stock.timeZone}`;

        const timeSeriesData = document.getElementById('timeSeriesData');
        timeSeriesData.innerHTML = ''; // Clear existing data

        Object.keys(stock.timeSeries).forEach(date => {
            const dailyData = stock.timeSeries[date];
            const dataElement = document.createElement('p');
            dataElement.innerText = `${date}: Open=${dailyData.open}, High=${dailyData.high}, Low=${dailyData.low}, Close=${dailyData.close}, Volume=${dailyData.volume}`;
            timeSeriesData.appendChild(dataElement);
        });
    } else {
        document.getElementById('tickerTitle').innerText = 'No data found';
    }
}
