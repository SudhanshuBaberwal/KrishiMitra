package com.krishimitra.soil.repository;

import com.krishimitra.soil.model.SoilRecord;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface SoilRecordRepository
        extends MongoRepository<SoilRecord, String> {

    List<SoilRecord> findByFarmerIdAndActiveTrueOrderBySampleDateDesc(
            String farmerId
    );

    Optional<SoilRecord> findByIdAndUserIdAndActiveTrue(
            String id,
            String userId
    );
}