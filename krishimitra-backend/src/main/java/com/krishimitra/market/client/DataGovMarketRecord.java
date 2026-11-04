package com.krishimitra.market.client;

public record DataGovMarketRecord(
        String state,
        String district,
        String market,
        String commodity,
        String variety,
        String grade,
        String arrivalDate,
        String minPrice,
        String maxPrice,
        String modalPrice
) {
}