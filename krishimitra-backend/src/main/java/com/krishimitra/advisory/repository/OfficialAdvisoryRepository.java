package com.krishimitra.advisory.repository;

import com.krishimitra.advisory.entity.OfficialAdvisory;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface OfficialAdvisoryRepository
        extends MongoRepository<OfficialAdvisory, String> {

    List<OfficialAdvisory>
    findByActiveTrueAndDistrictIgnoreCaseAndStateIgnoreCaseAndValidUntilGreaterThanEqualOrderByPublishedAtDesc(
            String district,
            String state,
            LocalDate today
    );

    Optional<OfficialAdvisory> findByIdAndActiveTrue(String id);
}