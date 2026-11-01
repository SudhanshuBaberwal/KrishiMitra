package com.krishimitra.schedule.dto.response;

import com.krishimitra.schedule.enums.ScheduleActivityType;
import com.krishimitra.schedule.enums.ScheduleStatus;

import java.time.Instant;
import java.time.LocalDate;

public record CropScheduleResponse(

        String id,
        String farmerId,
        String cropId,
        String cropName,
        String title,
        String description,
        ScheduleActivityType activityType,
        LocalDate scheduledDate,
        ScheduleStatus status,
        LocalDate completedDate,
        String notes,
        Instant createdAt,
        Instant updatedAt

) {}