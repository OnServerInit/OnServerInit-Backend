package com.imjustdoom.pluginsite.repositories;

import com.imjustdoom.pluginsite.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {

    boolean existsByUsernameEqualsIgnoreCaseOrEmailEqualsIgnoreCase(String username, String email);

    boolean existsByUsernameEqualsIgnoreCase(String username);
    boolean existsByEmailEqualsIgnoreCase(String email);

    Optional<Account> findByUsernameEqualsIgnoreCase(String username);
}