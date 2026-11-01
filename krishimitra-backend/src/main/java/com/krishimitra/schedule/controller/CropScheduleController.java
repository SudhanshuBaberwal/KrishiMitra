
package com.krishimitra.schedule.controller;

import com.krishimitra.auth.security.UserPrincipal;
import com.krishimitra.common.ApiResponse;
import com.krishimitra.schedule.dto.request.CreateScheduleRequest;
import com.krishimitra.schedule.dto.request.UpdateScheduleRequest;
import com.krishimitra.schedule.dto.request.UpdateScheduleStatusRequest;
import com.krishimitra.schedule.dto.response.CropScheduleResponse;
import com.krishimitra.schedule.enums.ScheduleStatus;
import com.krishimitra.schedule.service.CropScheduleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/farmers/me")
public class CropScheduleController {

    private final CropScheduleService scheduleService;

    public CropScheduleController(
            CropScheduleService scheduleService
    ) {
        this.scheduleService = scheduleService;
    }

    @PostMapping("/crops/{cropId}/schedule")
    public ResponseEntity<ApiResponse<CropScheduleResponse>> createSchedule(
            @PathVariable String cropId,
            @Valid @RequestBody CreateScheduleRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        CropScheduleResponse response =
                scheduleService.createSchedule(
                        cropId,
                        request,
                        principal.getId()
                );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Crop schedule created successfully",
                        response
                ));
    }

    @GetMapping("/schedules")
    public ResponseEntity<ApiResponse<List<CropScheduleResponse>>> getSchedules(
            @RequestParam(required = false) ScheduleStatus status,
            @RequestParam(required = false) LocalDate fromDate,
            @RequestParam(required = false) LocalDate toDate,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        List<CropScheduleResponse> response =
                scheduleService.getSchedules(
                        principal.getId(),
                        status,
                        fromDate,
                        toDate
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Crop schedules fetched successfully",
                        response
                )
        );
    }

    @GetMapping("/schedules/upcoming")
    public ResponseEntity<ApiResponse<List<CropScheduleResponse>>> getUpcomingSchedules(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        List<CropScheduleResponse> response =
                scheduleService.getUpcomingSchedules(
                        principal.getId()
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Upcoming crop schedules fetched successfully",
                        response
                )
        );
    }

    @GetMapping("/crops/{cropId}/schedules")
    public ResponseEntity<ApiResponse<List<CropScheduleResponse>>> getSchedulesByCrop(
            @PathVariable String cropId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        List<CropScheduleResponse> response =
                scheduleService.getSchedulesByCropId(
                        cropId,
                        principal.getId()
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Crop schedules fetched successfully",
                        response
                )
        );
    }

    @GetMapping("/schedules/{id}")
    public ResponseEntity<ApiResponse<CropScheduleResponse>> getScheduleById(
            @PathVariable String id,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        CropScheduleResponse response =
                scheduleService.getScheduleById(
                        id,
                        principal.getId()
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Schedule fetched successfully",
                        response
                )
        );
    }

    @PutMapping("/schedules/{id}")
    public ResponseEntity<ApiResponse<CropScheduleResponse>> updateSchedule(
            @PathVariable String id,
            @Valid @RequestBody UpdateScheduleRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        CropScheduleResponse response =
                scheduleService.updateSchedule(
                        id,
                        request,
                        principal.getId()
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Schedule updated successfully",
                        response
                )
        );
    }

    @PatchMapping("/schedules/{id}/status")
    public ResponseEntity<ApiResponse<CropScheduleResponse>> updateStatus(
            @PathVariable String id,
            @Valid @RequestBody UpdateScheduleStatusRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        CropScheduleResponse response =
                scheduleService.updateScheduleStatus(
                        id,
                        request,
                        principal.getId()
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Schedule status updated successfully",
                        response
                )
        );
    }

    @DeleteMapping("/schedules/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteSchedule(
            @PathVariable String id,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        scheduleService.deleteSchedule(
                id,
                principal.getId()
        );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Schedule deleted successfully",
                        null
                )
        );
    }
}
