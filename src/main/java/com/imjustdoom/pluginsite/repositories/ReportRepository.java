package com.imjustdoom.pluginsite.repositories;

import com.imjustdoom.pluginsite.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface ReportRepository extends JpaRepository<Report, Integer> {

    @Modifying
    @Transactional
    @Query("UPDATE Report report SET report.actionTaken = ?2 WHERE report.id = ?1")
    void updateActionTakenById(int id, String actionTaken);
}
