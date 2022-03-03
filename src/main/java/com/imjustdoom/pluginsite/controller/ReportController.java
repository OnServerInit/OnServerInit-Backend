package com.imjustdoom.pluginsite.controller;

import com.imjustdoom.pluginsite.dtos.in.CreateReportRequest;
import com.imjustdoom.pluginsite.model.Account;
import com.imjustdoom.pluginsite.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/report")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public void createReport(Account account, @RequestBody CreateReportRequest request) {
        this.reportService.createReport(account, request);
    }
}
