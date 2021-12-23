package com.imjustdoom.pluginsite.repositories;

import com.imjustdoom.pluginsite.model.Update;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface UpdateRepository extends JpaRepository<Update, Integer> {

    List<Update> findAllByResourceId(int resourceId, Sort sort);

    List<Update> findAllByResourceIdAndStatusEquals(int resourceId, String status, Sort sort);

    @Modifying
    @Transactional
    @Query("UPDATE Update updates SET updates.download = ?2 WHERE updates.id = ?1")
    void setDownload(int id, String download);

    @Modifying
    @Transactional
    @Query("UPDATE Update updates SET updates.downloads = updates.downloads + 1 WHERE updates.id = ?1")
    void addDownload(int id);

    @Query("SELECT SUM(updates.downloads) FROM Update updates WHERE updates.resource.id = ?1")
    Integer getTotalDownloads(int id);

    @Modifying
    @Transactional
    @Query("UPDATE Update updates SET updates.name = ?2, updates.description = ?3, updates.version = ?4 WHERE updates.id = ?1")
    void setInfo(int id, String name, String description, String version);

    @Query("SELECT COUNT(updates) FROM Update updates WHERE updates.uploaded > CURDATE() - HOUR(1) AND updates.resource.author.id = ?1")
    int getUpdatesCreateLastHour(int authorId);

    @Modifying
    @Transactional
    @Query("UPDATE Update updates SET updates.status = ?2 WHERE updates.id = ?1")
    void updateStatusById(int id, String status);
}