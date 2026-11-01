package com.krishimitra.schedule.repository;

import com.krishimitra.schedule.entity.CropSchedule;
import com.krishimitra.schedule.enums.ScheduleStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CropScheduleRepository
        extends MongoRepository<CropSchedule, String> {

    List<CropSchedule> findByUserIdAndActiveTrueOrderByScheduledDateAsc(
            String userId
    );

    List<CropSchedule> findByUserIdAndActiveTrueAndStatusOrderByScheduledDateAsc(
            String userId,
            ScheduleStatus status
    );

    List<CropSchedule> findByUserIdAndActiveTrueAndScheduledDateBetweenOrderByScheduledDateAsc(
            String userId,
            LocalDate fromDate,
            LocalDate toDate
    );

    List<CropSchedule> findByUserIdAndActiveTrueAndStatusAndScheduledDateBetweenOrderByScheduledDateAsc(
            String userId,
            ScheduleStatus status,
            LocalDate fromDate,
            LocalDate toDate
    );

    List<CropSchedule> findByUserIdAndCropIdAndActiveTrueOrderByScheduledDateAsc(
            String userId,
            String cropId
    );

    Optional<CropSchedule> findByIdAndUserIdAndActiveTrue(
            String id,
            String userId
    );
}