package com.krishimitra.schedule.dto.request;

import com.krishimitra.schedule.enums.ScheduleActivityType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UpdateScheduleRequest(

        @NotBlank
        @Size(max = 120)
        String title,

        @Size(max = 1000)
        String description,

        @NotNull
        ScheduleActivityType activityType,

        @NotNull
        LocalDate scheduledDate,

        @Size(max = 1000)
        String notes

) {}