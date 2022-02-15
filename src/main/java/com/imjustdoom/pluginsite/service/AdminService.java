package com.imjustdoom.pluginsite.service;

import com.imjustdoom.pluginsite.repositories.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final ReportRepository reportRepository;
}
