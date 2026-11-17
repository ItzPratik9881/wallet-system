package com.pratik.walletsystem.service;

import com.pratik.walletsystem.dto.DashboardResponse;

public interface DashboardService {

    DashboardResponse getDashboard(String email);

}