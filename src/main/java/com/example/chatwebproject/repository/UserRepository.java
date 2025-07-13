package com.example.chatwebproject.repository;

import com.example.chatwebproject.model.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u " +
            " WHERE u.email = :email " +
            " AND (u.delFlag IS NULL OR u.delFlag = false)")
//    @EntityGraph(attributePaths = {"role"})
    Optional<User> findByEmailAndDelFlg(@Param("email") String email);
    Optional<User> findByUsername(String username);
}
