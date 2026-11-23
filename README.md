# DIANS Project

> This project was developed as a **FINKI course project** (Faculty of Computer Science and Engineering, Ss. Cyril and Methodius University in Skopje).

A Spring Boot application that fetches stock market data, stores it in PostgreSQL, analyses it with technical indicators, and generates BUY / SELL signals using a Simple Moving Average (SMA) crossover strategy. Results are visualised in a D3.js web interface.

## How it works

1. **Ingestion** (`service/ingest`): `DataIngestService` uses a `RestTemplate` to request price data for a symbol from the Twelve Data API. Daily, hourly and five-minute data are retrieved and cached in the database.
2. **Storage** (`service/storage`): `DataStorageService` saves and loads stock data through `StockRepository`. If data for a symbol is already stored, it is reused and the API is not called again.
3. **Processing** (`service/processing`):
   - `SignalGenerationService` converts each data series into `Bar` objects (open, high, low, close, volume) and applies the SMA crossover rules.
   - `AnalysisService` coordinates the workflow: it checks storage first, falls back to ingestion when needed, and returns the generated signals.
4. **Presentation**: a static front end (`static/`) displays the price charts and signals.

### Signal rules

- **BUY** when the short-term SMA crosses above the long-term SMA.
- **SELL** when the short-term SMA crosses below the long-term SMA.

These signals are for educational purposes and are not financial advice.

## Getting started

### Prerequisites

- Java 17
- PostgreSQL (or Docker)
- A [Twelve Data](https://twelvedata.com/) API key

### Configuration

Set your database connection and API key without committing them to the repository. Spring Boot reads the properties below from `application.properties` or from environment variables:

| Property | Environment variable |
|---|---|
| `twelvedata.api_key` | `TWELVEDATA_API_KEY` |
| `spring.datasource.url` | `SPRING_DATASOURCE_URL` |
| `spring.datasource.username` | `SPRING_DATASOURCE_USERNAME` |
| `spring.datasource.password` | `SPRING_DATASOURCE_PASSWORD` |

### Run locally

```bash
./mvnw spring-boot:run
```

Then open http://localhost:8080.

### Run with Docker Compose

```bash
docker compose up --build
```

This starts the application together with a PostgreSQL container.

## Tech stack

Java 17 · Spring Boot 3.4 · Spring Data JPA · PostgreSQL · ta4j (technical analysis) · Twelve Data API · D3.js · Docker

## Author

**Kristijan Zafirovski**
