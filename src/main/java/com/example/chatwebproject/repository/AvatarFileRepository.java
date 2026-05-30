package com.example.chatwebproject.repository;

import com.example.chatwebproject.model.entity.AttachedFile;
import com.example.chatwebproject.model.entity.AvatarFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface AvatarFileRepository extends JpaRepository<AvatarFile, Long> {
    @Query("SELECT af FROM AvatarFile af " +
            " WHERE af.id = :fileId " +
            " AND af.userId = :userId ")
    Optional<AvatarFile> findByUserIdAndFileId(@Param("userId")Long userId, @Param("fileId") Long fileId);
}
