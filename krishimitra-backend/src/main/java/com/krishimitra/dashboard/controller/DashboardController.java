package com.krishimitra.dashboard.controller;

import com.krishimitra.auth.security.UserPrincipal;
import com.krishimitra.common.ApiResponse;
import com.krishimitra.dashboard.dto.response.DashboardOverviewResponse;
import com.krishimitra.dashboard.service.DashboardService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(
            DashboardService dashboardService
    ) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/overview")
    public ResponseEntity<ApiResponse<DashboardOverviewResponse>>
    getDashboardOverview(
            @AuthenticationPrincipal UserPrincipal principal
    ) {

        DashboardOverviewResponse response =
                dashboardService.getDashboardOverview(
                        principal.getId()
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Dashboard overview fetched successfully",
                        response
                )
        );
    }
}