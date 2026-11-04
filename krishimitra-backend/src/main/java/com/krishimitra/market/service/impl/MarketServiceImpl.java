package com.krishimitra.market.service.impl;

import com.krishimitra.market.client.DataGovClient;
import com.krishimitra.market.dto.request.MarketFilterRequest;
import com.krishimitra.market.dto.response.MarketPriceResponse;
import com.krishimitra.market.dto.response.MarketSummaryResponse;
import com.krishimitra.market.dto.response.MarketSyncResult;
import com.krishimitra.market.dto.response.MarketTrendResponse;
import com.krishimitra.market.mapper.MarketPriceMapper;
import com.krishimitra.market.model.MarketPrice;
import com.krishimitra.market.repository.MarketPriceRepository;
import com.krishimitra.market.service.MarketPricePersistenceService;
import com.krishimitra.market.service.MarketService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class MarketServiceImpl implements MarketService {

    private final MarketPriceRepository marketPriceRepository;
    private final MongoTemplate mongoTemplate;
    private final MarketPriceMapper marketPriceMapper;
    private final DataGovClient dataGovClient;
    private final MarketPricePersistenceService persistenceService;

    public MarketServiceImpl(
            MarketPriceRepository marketPriceRepository,
            MongoTemplate mongoTemplate,
            MarketPriceMapper marketPriceMapper, DataGovClient dataGovClient, MarketPricePersistenceService persistenceService
    ) {
        this.marketPriceRepository = marketPriceRepository;
        this.mongoTemplate = mongoTemplate;
        this.marketPriceMapper = marketPriceMapper;
        this.dataGovClient = dataGovClient;
        this.persistenceService = persistenceService;
    }

    @Override
    public Page<MarketPriceResponse> getPrices(
            MarketFilterRequest filters
    ) {
        if (filters == null) {
            throw new IllegalArgumentException("Market filters are required");
        }

        validateDateRange(filters.fromDate(), filters.toDate());

        int page = filters.page();
        int size = filters.size();

        Query query = buildFilterQuery(
                filters.state(),
                filters.district(),
                filters.market(),
                filters.commodity(),
                filters.fromDate(),
                filters.toDate()
        );

        long total = mongoTemplate.count(
                query,
                MarketPrice.class
        );

        PageRequest pageable = PageRequest.of(
                page,
                size,
                Sort.by(
                        Sort.Direction.DESC,
                        "arrivalDate"
                )
        );

        List<MarketPriceResponse> content = mongoTemplate
                .find(query.with(pageable), MarketPrice.class)
                .stream()
                .map(marketPriceMapper::toResponse)
                .toList();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public MarketSyncResult syncPrices(String state, String district) {
        requireText(state, "State");
        requireText(district, "District");

        var records = dataGovClient.fetchAll(state, district);

        return persistenceService.saveRecords(records);
    }

    @Override
    public MarketPriceResponse getPriceById(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException(
                    "Market price ID is required"
            );
        }

        MarketPrice price = marketPriceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Market price record not found: " + id
                ));

        return marketPriceMapper.toResponse(price);
    }

    @Override
    public List<String> getCommodities(
            String state,
            String district
    ) {
        requireText(state, "State");
        requireText(district, "District");

        Query query = buildFilterQuery(
                state,
                district,
                null,
                null,
                null,
                null
        );

        return mongoTemplate.findDistinct(
                        query,
                        "commodity",
                        MarketPrice.class,
                        String.class
                )
                .stream()
                .filter(value -> value != null && !value.isBlank())
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .toList();
    }

    @Override
    public List<String> getMarkets(
            String state,
            String district,
            String commodity
    ) {
        requireText(state, "State");
        requireText(district, "District");

        Query query = buildFilterQuery(
                state,
                district,
                null,
                commodity,
                null,
                null
        );

        return mongoTemplate.findDistinct(
                        query,
                        "market",
                        MarketPrice.class,
                        String.class
                )
                .stream()
                .filter(value -> value != null && !value.isBlank())
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .toList();
    }

    @Override
    public MarketTrendResponse getTrends(
            String state,
            String district,
            String commodity,
            String market,
            LocalDate fromDate,
            LocalDate toDate
    ) {
        requireText(state, "State");
        requireText(district, "District");
        requireText(commodity, "Commodity");
        validateDateRange(fromDate, toDate);

        if (fromDate == null || toDate == null) {
            throw new IllegalArgumentException(
                    "Both fromDate and toDate are required for trends"
            );
        }

        Query query = buildFilterQuery(
                state,
                district,
                market,
                commodity,
                fromDate,
                toDate
        );

        query.with(Sort.by(Sort.Direction.ASC, "arrivalDate"));

        List<MarketTrendResponse.TrendPoint> points =
                mongoTemplate.find(query, MarketPrice.class)
                        .stream()
                        .map(price -> new MarketTrendResponse.TrendPoint(
                                price.getArrivalDate(),
                                price.getMinPrice(),
                                price.getMaxPrice(),
                                price.getModalPrice()
                        ))
                        .toList();

        return new MarketTrendResponse(
                commodity,
                district,
                market,
                "INR/QUINTAL",
                points
        );
    }

    @Override
    public MarketSummaryResponse getSummary(
            String state,
            String district,
            String commodity
    ) {
        requireText(state, "State");
        requireText(district, "District");

        Query query = buildFilterQuery(
                state,
                district,
                null,
                commodity,
                null,
                null
        );

        List<MarketPrice> records = mongoTemplate.find(
                query,
                MarketPrice.class
        );

        if (records.isEmpty()) {
            return new MarketSummaryResponse(
                    state,
                    district,
                    commodity,
                    0,
                    null,
                    null,
                    null,
                    "INR/QUINTAL",
                    null
            );
        }

        BigDecimal minimum = records.stream()
                .map(MarketPrice::getMinPrice)
                .filter(value -> value != null)
                .min(BigDecimal::compareTo)
                .orElse(null);

        BigDecimal maximum = records.stream()
                .map(MarketPrice::getMaxPrice)
                .filter(value -> value != null)
                .max(BigDecimal::compareTo)
                .orElse(null);

        List<BigDecimal> modalPrices = records.stream()
                .map(MarketPrice::getModalPrice)
                .filter(value -> value != null)
                .toList();

        BigDecimal averageModal = modalPrices.isEmpty()
                ? null
                : modalPrices.stream()
                  .reduce(BigDecimal.ZERO, BigDecimal::add)
                  .divide(
                          BigDecimal.valueOf(modalPrices.size()),
                          2,
                          RoundingMode.HALF_UP
                  );

        Instant lastUpdated = records.stream()
                .map(MarketPrice::getFetchedAt)
                .filter(value -> value != null)
                .max(Instant::compareTo)
                .orElse(null);

        return new MarketSummaryResponse(
                state,
                district,
                commodity,
                records.size(),
                minimum,
                maximum,
                averageModal,
                "INR/QUINTAL",
                lastUpdated
        );
    }

    private Query buildFilterQuery(
            String state,
            String district,
            String market,
            String commodity,
            LocalDate fromDate,
            LocalDate toDate
    ) {
        Query query = new Query();

        addTextFilter(query, "state", state);
        addTextFilter(query, "district", district);
        addTextFilter(query, "market", market);
        addTextFilter(query, "commodity", commodity);

        if (fromDate != null && toDate != null) {
            query.addCriteria(
                    Criteria.where("arrivalDate")
                            .gte(fromDate)
                            .lte(toDate)
            );
        }

        return query;
    }

    private void addTextFilter(
            Query query,
            String field,
            String value
    ) {
        if (value != null && !value.isBlank()) {
            String normalized = Pattern.quote(value.trim());

            query.addCriteria(
                    Criteria.where(field)
                            .regex("^" + normalized + "$", "i")
            );
        }
    }

    private void validateDateRange(
            LocalDate fromDate,
            LocalDate toDate
    ) {
        if ((fromDate == null) != (toDate == null)) {
            throw new IllegalArgumentException(
                    "Both fromDate and toDate must be provided together"
            );
        }

        if (fromDate != null && fromDate.isAfter(toDate)) {
            throw new IllegalArgumentException(
                    "fromDate cannot be after toDate"
            );
        }
    }

    private void requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    field + " is required"
            );
        }
    }
}