import * as d3 from "https://cdn.jsdelivr.net/npm/d3@7/+esm";

export function drawCandlestickChart(data, signals) {
    const margin = { top: 20, right: 20, bottom: 30, left: 40 };
    const width = 800 - margin.left - margin.right;
    const height = 400 - margin.top - margin.bottom;

    // Clear existing chart
    d3.select('#chart').selectAll('*').remove();

    const svg = d3.select('#chart')
        .append('svg')
        .attr('width', width + margin.left + margin.right)
        .attr('height', height + margin.top + margin.bottom)
        .append('g')
        .attr('transform', `translate(${margin.left},${margin.top})`);

    const xScale = d3.scaleTime()
        .domain(d3.extent(data, d => d.date))
        .range([0, width]);

    const yScale = d3.scaleLinear()
        .domain([d3.min(data, d => d.low), d3.max(data, d => d.high)])
        .range([height, 0]);

    // Draw candlesticks
    svg.selectAll('rect')
        .data(data)
        .enter()
        .append('rect')
        .attr('x', d => xScale(d.date) - 2)
        .attr('y', d => yScale(Math.max(d.open, d.close)))
        .attr('width', 4)
        .attr('height', d => Math.abs(yScale(d.open) - yScale(d.close)))
        .attr('fill', d => d.close > d.open ? 'green' : 'red');

    // Draw high-low lines
    svg.selectAll('line')
        .data(data)
        .enter()
        .append('line')
        .attr('x1', d => xScale(d.date))
        .attr('y1', d => yScale(d.high))
        .attr('x2', d => xScale(d.date))
        .attr('y2', d => yScale(d.low))
        .attr('stroke', 'black');

    // Add signal markers
    svg.selectAll('.signal-marker')
        .data(signals)
        .enter()
        .append('path')
        .attr('class', 'signal-marker')
        .attr('d', function(d, i) {
            const date = data[i].date;
            const price = d === 'BUY' ?
                Math.min(data[i].low * 0.99, data[i].close * 0.98) :
                Math.max(data[i].high * 1.01, data[i].close * 1.02);

            return d === 'BUY' ?
                `M ${xScale(date)-5},${yScale(price)} l 10,-5 l 5,5 z` :
                `M ${xScale(date)-5},${yScale(price)} l 10,5 l 5,-5 z`;
        })
        .attr('fill', d => d === 'BUY' ? '#4CAF50' : '#F44336')
        .attr('stroke', d => d === 'BUY' ? '#2E7D32' : '#C62828')
        .style('opacity', 0.7);

    // Add axes
    svg.append('g')
        .attr('transform', `translate(0,${height})`)
        .call(d3.axisBottom(xScale).tickFormat(d3.timeFormat('%Y-%m-%d')));

    svg.append('g')
        .call(d3.axisLeft(yScale));
}