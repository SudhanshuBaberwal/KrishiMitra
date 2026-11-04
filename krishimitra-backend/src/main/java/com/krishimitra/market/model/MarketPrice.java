package com.krishimitra.market.model;

import com.krishimitra.market.enums.MarketDataSource;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Document(collection = "market_prices")
@CompoundIndex(
        name = "market_price_unique_idx",
        def = "{'state': 1, 'district': 1, 'market': 1, " +
                "'commodity': 1, 'variety': 1, 'grade': 1, " +
                "'arrivalDate': 1}",
        unique = true
)
@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class MarketPrice {

    @Id
    private String id;

    private String state;
    private String district;
    private String market;
    private String commodity;
    private String variety;
    private String grade;

    private LocalDate arrivalDate;

    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private BigDecimal modalPrice;

    private String priceUnit;
    private MarketDataSource source;

    private Instant fetchedAt;
    private Instant createdAt;
    private Instant updatedAt;


}