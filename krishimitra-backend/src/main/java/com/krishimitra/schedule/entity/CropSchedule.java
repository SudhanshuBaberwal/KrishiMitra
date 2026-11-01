package com.krishimitra.schedule.entity;

import com.krishimitra.schedule.enums.ScheduleActivityType;
import com.krishimitra.schedule.enums.ScheduleStatus;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.LocalDate;

@Data
@Document(collection = "crop_schedules")
public class CropSchedule {

    @Id
    private String id;

    @Indexed
    private String farmerId;

    @Indexed
    private String userId;

    @Indexed
    private String cropId;

    private String cropName;

    private String title;

    private String description;

    private ScheduleActivityType activityType;

    @Indexed
    private LocalDate scheduledDate;

    private ScheduleStatus status;

    private LocalDate completedDate;

    private String notes;

    private boolean active;

    private Instant createdAt;

    private Instant updatedAt;
}