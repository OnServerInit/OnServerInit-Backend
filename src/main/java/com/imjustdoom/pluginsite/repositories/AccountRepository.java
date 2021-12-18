package com.imjustdoom.pluginsite.repositories;

import com.imjustdoom.pluginsite.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {

    boolean existsByUsernameEqualsIgnoreCaseOrEmailEqualsIgnoreCase(String username, String email);

    boolean existsByUsernameEqualsIgnoreCase(String username);
    boolean existsByEmailEqualsIgnoreCase(String email);

    Optional<Account> findByUsernameEqualsIgnoreCase(String username);

    @Modifying
    @Transactional
    @Query("UPDATE Account account SET account.username = ?2 WHERE account.id = ?1")
    void setUsernameById(int id, String username);

    @Modifying
    @Transactional
    @Query("UPDATE Account account SET account.email = ?2 WHERE account.id = ?1")
    void setEmailById(int id, String email);

    @Modifying
    @Transactional
    @Query("UPDATE Account account SET account.password = ?2 WHERE account.id = ?1")
    void setPasswordById(int id, String password);

    @Modifying
    @Transactional
    @Query("UPDATE Account account SET account.role = ?2 WHERE account.id = ?1")
    void setRoleById(int id, String role);
}