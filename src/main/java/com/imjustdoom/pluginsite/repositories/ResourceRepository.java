package com.imjustdoom.pluginsite.repositories;

import com.imjustdoom.pluginsite.model.Account;
import com.imjustdoom.pluginsite.model.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Integer> {

    @Modifying
    @Transactional
    @Query("UPDATE Resource resource SET resource.download = ?2 WHERE resource.id = ?1")
    void setDownload(int id, String download);
}