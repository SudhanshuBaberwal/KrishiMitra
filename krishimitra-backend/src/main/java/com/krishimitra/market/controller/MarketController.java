package com.krishimitra.market.controller;

import com.krishimitra.common.ApiResponse;
import com.krishimitra.market.dto.request.MarketFilterRequest;
import com.krishimitra.market.dto.response.MarketPriceResponse;
import com.krishimitra.market.dto.response.MarketSummaryResponse;
import com.krishimitra.market.dto.response.MarketSyncResult;
import com.krishimitra.market.dto.response.MarketTrendResponse;
import com.krishimitra.market.service.MarketService;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/market")
@Validated
public class MarketController {

    private final MarketService marketService;
    private final String ingestionKey;

    public MarketController(
            MarketService marketService,
            @Value("${app.market.ingestion-key}") String ingestionKey
    ) {
        this.marketService = marketService;
        this.ingestionKey = ingestionKey;
    }

    /**
     * Get filtered and paginated market prices.
     */
    @GetMapping("/prices")
    public ResponseEntity<ApiResponse<Page<MarketPriceResponse>>> getPrices(

            @RequestParam(required = false) String state,

            @RequestParam(required = false) String district,

            @RequestParam(required = false) String market,

            @RequestParam(required = false) String commodity,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fromDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate toDate,

            @RequestParam(defaultValue = "0")
            @Min(0) Integer page,

            @RequestParam(defaultValue = "20")
            @Min(1) @Max(100) Integer size
    ) {

        MarketFilterRequest filters = new MarketFilterRequest(
                state,
                district,
                market,
                commodity,
                fromDate,
                toDate,
                page,
                size
        );

        Page<MarketPriceResponse> response =
                marketService.getPrices(filters);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Market prices fetched successfully",
                        response
                )
        );
    }

    /**
     * Get one market price record.
     */
    @GetMapping("/prices/{id}")
    public ResponseEntity<ApiResponse<MarketPriceResponse>> getPriceById(
            @PathVariable String id
    ) {

        MarketPriceResponse response =
                marketService.getPriceById(id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Market price fetched successfully",
                        response
                )
        );
    }

    /**
     * Get available commodities.
     */
    @GetMapping("/commodities")
    public ResponseEntity<ApiResponse<List<String>>> getCommodities(

            @RequestParam(defaultValue = "Karnataka") String state,

            @RequestParam(defaultValue = "Dharwad") String district
    ) {

        List<String> response =
                marketService.getCommodities(state, district);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Commodities fetched successfully",
                        response
                )
        );
    }

    /**
     * Get available markets.
     */
    @GetMapping("/markets")
    public ResponseEntity<ApiResponse<List<String>>> getMarkets(

            @RequestParam(defaultValue = "Karnataka") String state,

            @RequestParam(defaultValue = "Dharwad") String district,

            @RequestParam(required = false) String commodity
    ) {

        List<String> response =
                marketService.getMarkets(state, district, commodity);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Markets fetched successfully",
                        response
                )
        );
    }

    /**
     * Get historical market price trends.
     */
    @GetMapping("/trends")
    public ResponseEntity<ApiResponse<MarketTrendResponse>> getTrends(

            @RequestParam(defaultValue = "Karnataka") String state,

            @RequestParam(defaultValue = "Dharwad") String district,

            @RequestParam String commodity,

            @RequestParam(required = false) String market,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fromDate,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate toDate
    ) {

        MarketTrendResponse response = marketService.getTrends(
                state,
                district,
                commodity,
                market,
                fromDate,
                toDate
        );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Market trends fetched successfully",
                        response
                )
        );
    }

    /**
     * Get market summary.
     */
    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<MarketSummaryResponse>> getSummary(

            @RequestParam(defaultValue = "Karnataka") String state,

            @RequestParam(defaultValue = "Dharwad") String district,

            @RequestParam(required = false) String commodity
    ) {

        MarketSummaryResponse response =
                marketService.getSummary(state, district, commodity);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Market summary fetched successfully",
                        response
                )
        );
    }

    /**
     * Protected endpoint to synchronize external market data.
     */
    @PostMapping("/admin/sync")
    public ResponseEntity<ApiResponse<MarketSyncResult>> syncPrices(

            @RequestHeader("X-MARKET-INGEST-KEY") String suppliedKey,

            @RequestParam(defaultValue = "Karnataka") String state,

            @RequestParam(defaultValue = "Dharwad") String district
    ) {

        if (!isValidIngestionKey(suppliedKey)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error(
                            "Invalid market ingestion key",
                            null
                    ));
        }

        MarketSyncResult response =
                marketService.syncPrices(state, district);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Market prices synchronized successfully",
                        response
                )
        );
    }

    private boolean isValidIngestionKey(String suppliedKey) {

        if (suppliedKey == null
                || ingestionKey == null
                || ingestionKey.isBlank()) {
            return false;
        }

        return MessageDigest.isEqual(
                ingestionKey.getBytes(StandardCharsets.UTF_8),
                suppliedKey.getBytes(StandardCharsets.UTF_8)
        );
    }
}