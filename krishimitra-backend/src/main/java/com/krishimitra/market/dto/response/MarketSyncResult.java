package com.krishimitra.market.dto.response;

public record MarketSyncResult(
        int receivedRecords,
        int processedRecords,
        int rejectedRecords
) {
}