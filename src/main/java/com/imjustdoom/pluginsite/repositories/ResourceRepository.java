package com.imjustdoom.pluginsite.repositories;

import com.imjustdoom.pluginsite.model.Account;
import com.imjustdoom.pluginsite.model.Resource;
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

    List<Resource> findAllByAuthorId(int authorId, Pageable pageable);

    Optional<Resource> findByNameEqualsIgnoreCase(String name);
}