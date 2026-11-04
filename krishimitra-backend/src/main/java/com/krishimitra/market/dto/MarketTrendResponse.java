package com.krishimitra.market.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record MarketTrendResponse(

        String commodity,

        String district,

        String market,

        String priceUnit,

        List<TrendPoint> trends

) {

    public record TrendPoint(
            LocalDate arrivalDate,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            BigDecimal modalPrice
    ) {
    }
}