package com.krishimitra.market.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.time.LocalDate;

public record MarketFilterRequest(

        String state,

        String district,

        String market,

        String commodity,

        LocalDate fromDate,

        LocalDate toDate,

        @Min(0)
        Integer page,

        @Min(1)
        @Max(100)
        Integer size

) {
    public MarketFilterRequest {
        page = page == null ? 0 : page;
        size = size == null ? 20 : size;
    }
}