package com.krishimitra.dashboard.service;

import com.krishimitra.dashboard.dto.response.DashboardOverviewResponse;

public interface DashboardService {

    DashboardOverviewResponse getDashboardOverview(String userId);
}