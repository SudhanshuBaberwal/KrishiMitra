package com.krishimitra.schedule.service;

import com.krishimitra.schedule.dto.request.CreateScheduleRequest;
import com.krishimitra.schedule.dto.request.UpdateScheduleRequest;
import com.krishimitra.schedule.dto.request.UpdateScheduleStatusRequest;
import com.krishimitra.schedule.dto.response.CropScheduleResponse;
import com.krishimitra.schedule.enums.ScheduleStatus;

import java.time.LocalDate;
import java.util.List;

public interface CropScheduleService {

    CropScheduleResponse createSchedule(
            String cropId,
            CreateScheduleRequest request,
            String userId
    );

    List<CropScheduleResponse> getSchedules(
            String userId,
            ScheduleStatus status,
            LocalDate fromDate,
            LocalDate toDate
    );

    List<CropScheduleResponse> getUpcomingSchedules(String userId);

    CropScheduleResponse getScheduleById(String id, String userId);

    CropScheduleResponse updateSchedule(
            String id,
            UpdateScheduleRequest request,
            String userId
    );

    CropScheduleResponse updateScheduleStatus(
            String id,
            UpdateScheduleStatusRequest request,
            String userId
    );

    void deleteSchedule(String id, String userId);
}