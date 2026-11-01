package com.krishimitra.crop.repository;

import com.krishimitra.crop.entity.FarmerCrop;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FarmerCropRepository
        extends MongoRepository<FarmerCrop, String> {

    List<FarmerCrop> findByFarmerIdOrderByCreatedAtDesc(
            String farmerId
    );

    List<FarmerCrop> findByFarmerIdAndActiveTrueOrderByCreatedAtDesc(
            String farmerId
    );

    Optional<FarmerCrop> findByIdAndUserId(
            String id,
            String userId
    );

    boolean existsByFarmerIdAndCropCodeAndVarietyAndSowingDateAndActiveTrue(
            String farmerId,
            String cropCode,
            String variety,
            java.time.LocalDate sowingDate
    );
}