
package com.krishimitra.farmer.repository;

import com.krishimitra.farmer.model.FarmerProfile;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface FarmerRepository
        extends MongoRepository<FarmerProfile, String> {

    Optional<FarmerProfile> findByUserId(String userId);

    boolean existsByUserId(String userId);
}
