package com.leave.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "sys_message")
public class SysMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private SysUser receiver;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "msg_type", columnDefinition = "TINYINT DEFAULT 0")
    private Integer msgType = 0;

    @Column(name = "related_id")
    private Long relatedId;

    @Column(name = "is_read", columnDefinition = "TINYINT DEFAULT 0")
    private Integer isRead = 0;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
