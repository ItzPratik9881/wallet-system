package com.pratik.walletsystem.controller;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.pratik.walletsystem.dto.ApiResponse;
import com.pratik.walletsystem.dto.DashboardResponse;
import com.pratik.walletsystem.service.DashboardService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Endpoints for user financial overview and analytics")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    @Operation(summary = "Get user dashboard", description = "Returns financial overview, wallet balance, and recent transactions for authenticated user")
    public ResponseEntity<ApiResponse<DashboardResponse>> getDashboard(Principal principal) {
        String email = principal.getName();
        return ResponseEntity.ok(ApiResponse.success(dashboardService.getDashboard(email)));
    }

    @GetMapping("/{email}")
    @Operation(summary = "Get dashboard by email", description = "Returns dashboard ensuring request is authenticated to user's own identity")
    public ResponseEntity<ApiResponse<DashboardResponse>> getDashboardByEmail(
            Principal principal,
            @PathVariable String email) {
        String authenticatedEmail = principal != null ? principal.getName() : email;
        return ResponseEntity.ok(ApiResponse.success(dashboardService.getDashboard(authenticatedEmail)));
    }
}
