package com.imjustdoom.pluginsite.repositories;

import com.imjustdoom.pluginsite.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepository extends JpaRepository<Report, Integer> {
}
