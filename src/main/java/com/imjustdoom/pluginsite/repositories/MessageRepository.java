package com.imjustdoom.pluginsite.repositories;

import com.imjustdoom.pluginsite.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MessageRepository extends JpaRepository<Message, Integer> {
}
