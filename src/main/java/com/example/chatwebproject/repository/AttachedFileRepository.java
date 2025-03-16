package com.example.chatwebproject.repository;

import com.example.chatwebproject.model.entity.AttachedFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface AttachedFileRepository extends JpaRepository<AttachedFile, Long> {
    @Query("SELECT af FROM AttachedFile af " +
            " WHERE (:listIds IS NULL OR af.id IN :listIds) " +
            " AND (af.delFlag IS NULL OR af.delFlag = false)")
    Set<AttachedFile> findAllByIdAndDelFlag(@Param("listIds") List<Long> listIds);
}
