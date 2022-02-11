package com.imjustdoom.pluginsite.service.rest;

import com.imjustdoom.pluginsite.config.exception.RestErrorCode;
import com.imjustdoom.pluginsite.config.exception.RestException;
import com.imjustdoom.pluginsite.model.Report;
import com.imjustdoom.pluginsite.repositories.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final ReportRepository reportRepository;

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
