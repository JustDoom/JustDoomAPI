package com.imjustdoom.justdoomapi.repository;

import com.imjustdoom.justdoomapi.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {

//    boolean existsByUsernameEqualsIgnoreCaseOrEmailEqualsIgnoreCase(String username, String email);
//
//    boolean existsByUsernameEqualsIgnoreCase(String username);
//
//    boolean existsByEmailEqualsIgnoreCase(String email);
//
    Optional<Account> findByUsernameEqualsIgnoreCase(String username);

    Optional<Account> findByEmailEqualsIgnoreCase(String email);
//
//    Optional<Account> findByUsernameEquals(String username);
//
//    @Modifying
//    @Transactional
//    @Query("UPDATE Account account SET account.username = ?2 WHERE account.id = ?1")
//    void setUsernameById(int id, String username);
//
//    @Modifying
//    @Transactional
//    @Query("UPDATE Account account SET account.email = ?2 WHERE account.id = ?1")
//    void setEmailById(int id, String email);
//
//    @Modifying
//    @Transactional
//    @Query("UPDATE Account account SET account.password = ?2 WHERE account.id = ?1")
//    void setPasswordById(int id, String password);
//
//    @Modifying
//    @Transactional
//    @Query("UPDATE Account account SET account.role = ?2 WHERE account.id = ?1")
//    void setRoleById(int id, String role);
//
//    @Modifying
//    @Transactional
//    @Query("UPDATE Account account SET account.profilePicture = ?2 WHERE account.id = ?1")
//    void updateProfilePictureById(int id, byte[] profilePicture);
//
//    @Query("SELECT profilePicture FROM Account WHERE id = ?1")
//    byte[] findAccountProfilePicture(int id);
}