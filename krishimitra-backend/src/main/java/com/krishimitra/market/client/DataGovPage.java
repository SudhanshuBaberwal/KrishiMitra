package com.krishimitra.market.client;

import java.util.List;

public record DataGovPage(
        List<DataGovMarketRecord> records,
        long totalRecords,
        int offset,
        int limit
) {
}