package com.krishimitra.market.dto.response;

import com.krishimitra.market.enums.MarketDataSource;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record MarketPriceResponse(

        String id,

        String state,

        String district,

        String market,

        String commodity,

        String variety,

        String grade,

        LocalDate arrivalDate,

        BigDecimal minPrice,

        BigDecimal maxPrice,

        BigDecimal modalPrice,

        String priceUnit,

        MarketDataSource source,

        Instant fetchedAt

) {
}