package com.imjustdoom.pluginsite.repositories;

import com.imjustdoom.pluginsite.model.MessageGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MessageGroupRepository extends JpaRepository<MessageGroup, Integer> {

    // get all message groups that an account is in
//    @Query("select mg from MessageGroup mg where mg.account.id = ?1")
//    Iterable<MessageGroup> findAllByAccountId(int accountId);
}
