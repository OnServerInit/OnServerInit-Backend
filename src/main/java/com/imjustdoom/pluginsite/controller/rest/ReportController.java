package com.imjustdoom.pluginsite.controller.rest;

import com.imjustdoom.pluginsite.dtos.in.CreateReportRequest;
import com.imjustdoom.pluginsite.model.Account;
import com.imjustdoom.pluginsite.service.rest.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/report")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    // todo should a DTO be returned instead of the raw Report here?
    @PostMapping
    public void createReport(Account account, @RequestBody CreateReportRequest request) {
        this.reportService.createReport(account, request);
    }
}
