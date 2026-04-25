package com.assignment.virality_engine.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Data
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long postId;
    private Long authorId;
    private String authorType; // "USER" or "BOT"
    
    @Column(columnDefinition = "TEXT")
    private String content;

    private Integer depthLevel;
    private LocalDateTime createdAt;
    
    @PrePersist
    public void prePersist() {
        if(this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }
}
