package com.imjustdoom.pluginsite.repositories;

import com.imjustdoom.pluginsite.model.Message;
import com.imjustdoom.pluginsite.model.MessageGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Integer> {

    Page<Message> findByGroup(MessageGroup group, Pageable pageable);
}
