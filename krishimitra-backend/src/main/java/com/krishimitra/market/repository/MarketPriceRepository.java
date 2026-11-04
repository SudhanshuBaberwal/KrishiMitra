package com.krishimitra.market.repository;

import com.krishimitra.market.model.MarketPrice;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MarketPriceRepository
        extends MongoRepository<MarketPrice, String> {

    List<MarketPrice>
    findByStateIgnoreCaseAndDistrictIgnoreCaseOrderByArrivalDateDesc(
            String state,
            String district
    );

    List<MarketPrice>
    findByStateIgnoreCaseAndDistrictIgnoreCaseAndCommodityIgnoreCaseOrderByArrivalDateDesc(
            String state,
            String district,
            String commodity
    );

    List<MarketPrice>
    findByStateIgnoreCaseAndDistrictIgnoreCaseAndArrivalDateBetweenOrderByArrivalDateAsc(
            String state,
            String district,
            LocalDate fromDate,
            LocalDate toDate
    );

    List<MarketPrice>
    findByStateIgnoreCaseAndDistrictIgnoreCaseAndCommodityIgnoreCaseAndArrivalDateBetweenOrderByArrivalDateAsc(
            String state,
            String district,
            String commodity,
            LocalDate fromDate,
            LocalDate toDate
    );
}