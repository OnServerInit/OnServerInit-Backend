package com.imjustdoom.pluginsite.repositories;

import com.imjustdoom.pluginsite.model.Resource;
import com.imjustdoom.pluginsite.model.Update;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface UpdateRepository extends JpaRepository<Update, Integer> {

    List<Update> findAllByResourceId(int resourceId, Sort sort);

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
}