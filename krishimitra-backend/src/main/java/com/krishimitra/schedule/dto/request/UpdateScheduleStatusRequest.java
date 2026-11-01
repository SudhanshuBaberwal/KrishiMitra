package com.krishimitra.schedule.dto.request;

import com.krishimitra.schedule.enums.ScheduleStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateScheduleStatusRequest(

        @NotNull
        ScheduleStatus status

) {}