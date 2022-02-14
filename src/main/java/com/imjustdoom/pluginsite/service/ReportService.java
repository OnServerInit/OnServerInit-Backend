package com.imjustdoom.pluginsite.service;

import com.imjustdoom.pluginsite.config.exception.RestErrorCode;
import com.imjustdoom.pluginsite.config.exception.RestException;
import com.imjustdoom.pluginsite.dtos.in.CreateReportRequest;
import com.imjustdoom.pluginsite.model.Account;
import com.imjustdoom.pluginsite.model.Report;
import com.imjustdoom.pluginsite.repositories.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;

    public void createReport(Account reporter, CreateReportRequest request) {
        Report report = new Report(reporter, request.getReportingObject(), request.getReportingId(), request.getReport(), request.getReason());
        this.reportRepository.save(report);
    }

    public Page<Report> getReportPage(Pageable pageable) {
        return this.reportRepository.findAll(pageable);
    }

    public Report getReport(int id) throws RestException {
        return this.reportRepository.findById(id).orElseThrow(() -> new RestException(RestErrorCode.REPORT_NOT_FOUND));
    }

    public Report updateActionTaken(int id, String actionTaken) {
        return this.reportRepository.updateActionTakenById(id, actionTaken);
    }
}
