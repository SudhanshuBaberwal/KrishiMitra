package com.krishimitra.market.service;

import com.krishimitra.market.client.DataGovMarketRecord;
import com.krishimitra.market.dto.response.MarketSyncResult;
import com.krishimitra.market.mapper.MarketPriceMapper;
import com.krishimitra.market.model.MarketPrice;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class MarketPricePersistenceService {

    private final MongoTemplate mongoTemplate;
    private final MarketPriceMapper marketPriceMapper;

    public MarketPricePersistenceService(
            MongoTemplate mongoTemplate,
            MarketPriceMapper marketPriceMapper
    ) {
        this.mongoTemplate = mongoTemplate;
        this.marketPriceMapper = marketPriceMapper;
    }

    public MarketSyncResult saveRecords(
            List<DataGovMarketRecord> records
    ) {
        if (records == null || records.isEmpty()) {
            return new MarketSyncResult(0, 0, 0);
        }

        int processed = 0;
        int rejected = 0;

        for (DataGovMarketRecord record : records) {
            try {
                MarketPrice price = marketPriceMapper.toEntity(record);

                upsertRecord(price);

                processed++;

            } catch (IllegalArgumentException exception) {
                rejected++;
            }
        }

        return new MarketSyncResult(
                records.size(),
                processed,
                rejected
        );
    }

    private void upsertRecord(MarketPrice price) {
        Query query = new Query(
                Criteria.where("state").is(price.getState())
                        .and("district").is(price.getDistrict())
                        .and("market").is(price.getMarket())
                        .and("commodity").is(price.getCommodity())
                        .and("variety").is(price.getVariety())
                        .and("grade").is(price.getGrade())
                        .and("arrivalDate").is(price.getArrivalDate())
        );

        Instant now = Instant.now();

        Update update = new Update()
                .set("state", price.getState())
                .set("district", price.getDistrict())
                .set("market", price.getMarket())
                .set("commodity", price.getCommodity())
                .set("variety", price.getVariety())
                .set("grade", price.getGrade())
                .set("arrivalDate", price.getArrivalDate())
                .set("minPrice", price.getMinPrice())
                .set("maxPrice", price.getMaxPrice())
                .set("modalPrice", price.getModalPrice())
                .set("priceUnit", price.getPriceUnit())
                .set("source", price.getSource())
                .set("fetchedAt", now)
                .set("updatedAt", now)
                .setOnInsert("createdAt", now);

        mongoTemplate.upsert(query, update, MarketPrice.class);
    }
}