package com.imjustdoom.pluginsite.repositories;

import com.imjustdoom.pluginsite.model.QResource;
import com.imjustdoom.pluginsite.model.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Integer>, QuerydslPredicateExecutor<Resource>, QuerydslBinderCustomizer<QResource> {

    @Override
    default void customize(QuerydslBindings bindings, QResource root) {
        bindings.including(
            root.name,
            root.category,
            root.author.username,
            root.author.id
        );
        bindings.excludeUnlistedProperties(true);
    }

    boolean existsByNameEqualsIgnoreCase(String name);

    @Query("SELECT COUNT(resource)>0 FROM Resource resource WHERE NOT resource.id = ?1 AND resource.name = ?2")
    boolean existsByNameEqualsIgnoreCaseAndIdEqualsNot(int id, String name);

    List<Resource> findAllByStatusEqualsIgnoreCase(String status, Pageable pageable);

    List<Resource> findAllByStatusEqualsIgnoreCase(String status, Sort sort);

    List<Resource> findAllByStatusEqualsIgnoreCase(String status);

    @Modifying
    @Transactional
    @Query("UPDATE Resource resource SET resource.name = ?2, resource.blurb = ?3, resource.description = ?4, " +
            "resource.donation = ?5, resource.source = ?6, resource.support = ?7, resource.category = ?8 WHERE resource.id = ?1")
    void setInfo(int id, String name, String blurb, String description, String donation, String source, String support, String category);

    @Query("SELECT COUNT(resource) FROM Resource resource WHERE resource.created > CURDATE() - HOUR(1) AND resource.author.id = ?1")
    int getResourcesCreateLastHour(int authorId);

    List<Resource> findAllByAuthorId(int authorId, Pageable pageable);

    List<Resource> findAllByCategoryEqualsAndStatusEquals(String category, String status, Pageable pageable);

    Optional<Resource> findByNameEqualsIgnoreCase(String name);

    @Modifying
    @Transactional
    @Query("UPDATE Resource resource SET resource.status = ?2 WHERE resource.id = ?1")
    void updateStatusById(int id, String status);

    @Modifying
    @Transactional
    @Query("UPDATE Resource resource SET resource.logo = ?2 WHERE resource.id = ?1")
    void updateLogoById(int id, byte[] logo);

    @Query("SELECT logo FROM Resource WHERE id = ?1")
    byte[] findResourceLogo(int id);
}