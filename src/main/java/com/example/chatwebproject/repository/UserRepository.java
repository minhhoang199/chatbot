package com.example.chatwebproject.repository;

import com.example.chatwebproject.model.entity.User;
import com.example.chatwebproject.model.enums.UserStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u " +
            " WHERE u.email = :email " +
            " AND (u.delFlag IS NULL OR u.delFlag = false) " +
            " AND u.status = 'ACTIVE'")
//    @EntityGraph(attributePaths = {"role"})
    Optional<User> findByEmailAndDelFlg(@Param("email") String email);

    @Query("SELECT u FROM User u " +
            " WHERE u.email = :email " +
            " AND (u.delFlag IS NULL OR u.delFlag = false) " +
            " AND u.status IN :statusList")
//    @EntityGraph(attributePaths = {"role"})
    Optional<User> findByEmailAndDelFlgAndStatus(@Param("email") String email, @Param("statusList") List<UserStatus> statusList);


    @Query("SELECT u FROM User u " +
            " WHERE u.email LIKE %:email% " +
            " AND (u.delFlag IS NULL OR u.delFlag = false)")
    List<User> searchByEmail(@Param("email") String email);

    Optional<User> findByUsername(String username);

    @Query(" SELECT DISTINCT u " +
            " FROM User u " +
            " JOIN Friendship fs " +
            "  ON (u.email = fs.acceptedUserEmail OR u.email = fs.requestUserEmail) " +
            " WHERE " +
            "  (fs.acceptedUserEmail = :currentEmail OR fs.requestUserEmail = :currentEmail) " +
            "  AND u.email <> :currentEmail " +
            " AND fs.status = 'ACCEPTED'")
    List<User> getFriends(String currentEmail);
}
