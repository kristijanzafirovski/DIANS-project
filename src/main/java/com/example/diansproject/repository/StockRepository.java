package com.example.diansproject.repository;

import com.crazzyghost.alphavantage.timeseries.response.StockUnit;
import com.example.diansproject.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {
    List<Stock> findBySymbol(String symbol);

    List<Stock> getUnitsBySymbol(@Param("symbol") String symbol);
    boolean existsStockBySymbol(String ticker);
}
