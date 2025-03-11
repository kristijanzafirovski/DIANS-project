async function searchTicker() {
    const ticker = document.getElementById('tickerSearch').value.toUpperCase();
    if (!ticker) return;

    try {
        // Update UI with loading state
        document.querySelectorAll('.chart').forEach(el =>
            el.innerText = 'Loading...'
        );

        // Fetch historical data
        const historyResponse = await fetch(
            `/api/stocks/${ticker}/history?from=2024-01-01&to=2025-03-11`
        );
        const historyData = await historyResponse.json();

        // Fetch dividend data
        const dividendResponse = await fetch(
            `/api/stocks/${ticker}/dividends?from=2020-01-01&to=2025-03-11`
        );
        const dividendData = await dividendResponse.json();

        // Update UI with real data
        updateDashboard(ticker, historyData, dividendData);

    } catch (error) {
        console.error('Error fetching data:', error);
        document.querySelectorAll('.chart').forEach(el =>
            el.innerText = 'Error loading data'
        );
    }
}

function updateDashboard(ticker, history, dividends) {
    // Update ticker names
    document.querySelectorAll('[id^="tickerName"]').forEach(el =>
        el.innerText = ticker
    );

    // Example metrics calculation
    const latestPrice = history[history.length - 1].getClose();
    const dividendSum = Array.from(dividends.values())
        .reduce((a, b) => a.add(b), BigDecimal.ZERO);

    // Update sections
    document.getElementById('keyMetrics').innerText =
        `Latest Price: $${latestPrice}`;

    document.getElementById('summaryCharts').innerText =
        `52-Week Range: $${getPriceRange(history)}`;

    document.getElementById('statisticalAnalysis').innerText =
        `Total Dividends (5Y): $${dividendSum}`;
}

function getPriceRange(history) {
    const prices = history.map(q => q.getClose());
    return `${Math.min(...prices).toFixed(2)} - $${Math.max(...prices).toFixed(2)}`;
}
