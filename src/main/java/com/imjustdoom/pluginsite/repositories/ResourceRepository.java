package com.imjustdoom.pluginsite.repositories;

import com.imjustdoom.pluginsite.model.Resource;
import lombok.Getter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Integer> {

    @Modifying
    @Transactional
    @Query("UPDATE Resource resource SET resource.download = ?2 WHERE resource.id = ?1")
    void setDownload(int id, String download);

    @Modifying
    @Transactional
    @Query("UPDATE Resource resource SET resource.name = ?2, resource.blurb = ?3, resource.description = ?4, " +
            "resource.donation = ?5, resource.source = ?6, resource.support = ?7 WHERE resource.id = ?1")
    void setInfo(int id, String name, String blurb, String description, String donation, String source, String support);

    @Query("SELECT COUNT(r) FROM Resource r WHERE r.created > CURDATE() - HOUR(1) AND r.author.id = ?1")
    int getLastHour(int authorId);

    List<Resource> findAllByAuthorId(int authorId, Pageable pageable);

    Optional<Resource> findByNameEqualsIgnoreCase(String name);
}