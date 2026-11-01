package com.krishimitra.auth.repository;

import com.krishimitra.auth.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    // Find user using email
    Optional<User> findByEmail(String email);

    // Check whether email already exists
    boolean existsByEmail(String email);

}