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

    // Add axes
    svg.append('g')
        .attr('transform', `translate(0,${height})`)
        .call(d3.axisBottom(xScale).tickFormat(d3.timeFormat('%Y-%m-%d')));

    svg.append('g')
        .call(d3.axisLeft(yScale));
}