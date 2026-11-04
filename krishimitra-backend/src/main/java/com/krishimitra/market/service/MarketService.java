package com.krishimitra.market.service;

import com.krishimitra.market.dto.request.MarketFilterRequest;
import com.krishimitra.market.dto.response.MarketPriceResponse;
import com.krishimitra.market.dto.response.MarketSummaryResponse;
import com.krishimitra.market.dto.response.MarketTrendResponse;
import com.krishimitra.market.dto.response.MarketSyncResult;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;

public interface MarketService {

    Page<MarketPriceResponse> getPrices(MarketFilterRequest filters);

    MarketPriceResponse getPriceById(String id);

    List<String> getCommodities(String state, String district);

    List<String> getMarkets(
            String state,
            String district,
            String commodity
    );

    MarketTrendResponse getTrends(
            String state,
            String district,
            String commodity,
            String market,
            LocalDate fromDate,
            LocalDate toDate
    );

    MarketSummaryResponse getSummary(
            String state,
            String district,
            String commodity
    );

    MarketSyncResult syncPrices(String state, String district);
}