package com.krishimitra.market.dto.response;

import java.math.BigDecimal;
import java.time.Instant;

public record MarketSummaryResponse(

        String state,

        String district,

        String commodity,

        long totalRecords,

        BigDecimal minimumPrice,

        BigDecimal maximumPrice,

        BigDecimal averageModalPrice,

        String priceUnit,

        Instant lastUpdated

) {
}