package com.example.chatwebproject.repository;

import com.example.chatwebproject.model.entity.Friendship;
import com.example.chatwebproject.model.enums.FriendshipStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    @Query("SELECT f FROM Friendship f Where ((f.requestUserEmail = :email1 AND f.acceptedUserEmail = :email2) " +
            " OR (f.acceptedUserEmail = :email1 AND f.requestUserEmail = :email2)) " +
            " AND f.status IN :statuses " +
            " AND f.delFlag = false ")
    List<Friendship> findByUsersAndStatus(@Param("email1") String email1,
                                              @Param("email2")  String email2,
                                              @Param("statuses") List<FriendshipStatus> statuses);

    @Query("SELECT f FROM Friendship f Where f.id = :id " +
            " AND f.delFlag = false ")
    Optional<Friendship> findByIdAndDelFlag(@Param("id") Long id);

    @Query("SELECT f FROM Friendship f Where f.acceptedUserEmail LIKE %:email% " +
            " AND f.status = 'PENDING' " +
            " AND f.delFlag = false ")
    List<Friendship> getIncomingRequest(@Param("email") String email);

    @Query("SELECT f FROM Friendship f Where f.requestUserEmail LIKE %:email% " +
            " AND f.status = 'PENDING' " +
            " AND f.delFlag = false ")
    List<Friendship> getOutgoingRequest(@Param("email") String email);

    @Query("SELECT f FROM Friendship f Where (f.requestUserEmail = :currentEmail OR f.acceptedUserEmail = :currentEmail) " +
            " AND (:findingEmail IS NULL OR (f.requestUserEmail LIKE %:findingEmail% OR f.acceptedUserEmail LIKE %:findingEmail%)) " +
            " AND f.status = 'ACCEPTED' " +
            " AND f.delFlag = false ")
    List<Friendship> getAcceptedFriend(@Param("currentEmail") String currentEmail,
                                       @Param("findingEmail") String findingEmail);

    @Query("SELECT f FROM Friendship f Where f.blockUserEmail = :currentEmail " +
            " AND (:findingEmail IS NULL OR (f.requestUserEmail LIKE %:findingEmail% OR f.acceptedUserEmail LIKE %:findingEmail%)) " +
            " AND f.status = 'BLOCKED' " +
            " AND f.delFlag = false ")
    List<Friendship> getBlockedFriend(@Param("currentEmail") String currentEmail,
                                       @Param("findingEmail") String findingEmail);
}
