package com.imjustdoom.pluginsite.repositories;

import com.imjustdoom.pluginsite.model.Report;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface ReportRepository extends PagingAndSortingRepository<Report, Integer> {

    @Modifying
    @Transactional
    @Query("UPDATE Report report SET report.actionTaken = ?2 WHERE report.id = ?1")
    Report updateActionTakenById(int id, String actionTaken);
}
