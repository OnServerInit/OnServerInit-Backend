package com.imjustdoom.pluginsite.repositories;

import com.imjustdoom.pluginsite.model.Account;
import com.imjustdoom.pluginsite.model.MessageGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface MessageGroupRepository extends PagingAndSortingRepository<MessageGroup, Integer> {

    Page<MessageGroup> findAllByMembersContains(Account account, Pageable pageable);

}
