package com.imjustdoom.pluginsite.controller;

import com.imjustdoom.pluginsite.dtos.in.CreateReportRequest;
import com.imjustdoom.pluginsite.model.Account;
import com.imjustdoom.pluginsite.model.Report;
import com.imjustdoom.pluginsite.repositories.ReportRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@AllArgsConstructor
public class ReportController {

    private final ReportRepository reportRepository;

    @GetMapping("/report")
    public String report(Account account, Model model) {
        model.addAttribute("report", new CreateReportRequest());
        model.addAttribute("account", account);
        return "report";
    }

    @PostMapping("/report")
    public void sendReport(Account account, @ModelAttribute CreateReportRequest reportRequest) {
        Report report = new Report(account, reportRequest.getReportingObject(), reportRequest.getReportingId(),
                reportRequest.getReport(), reportRequest.getReason());
        reportRepository.save(report);
    }
}
