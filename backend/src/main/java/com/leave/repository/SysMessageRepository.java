package com.leave.repository;

import com.leave.entity.SysMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SysMessageRepository extends JpaRepository<SysMessage, Long> {

    @Query("SELECT m FROM SysMessage m WHERE m.receiver.id = :userId ORDER BY m.createdAt DESC")
    Page<SysMessage> findByReceiverId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT COUNT(m) FROM SysMessage m WHERE m.receiver.id = :userId AND m.isRead = 0")
    Long countUnreadByReceiverId(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE SysMessage m SET m.isRead = 1 WHERE m.receiver.id = :userId AND m.isRead = 0")
    void markAllAsRead(@Param("userId") Long userId);
}
