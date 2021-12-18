package com.imjustdoom.pluginsite.repositories;

import com.imjustdoom.pluginsite.model.Blog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlogRepository extends JpaRepository<Blog, Integer> {

}