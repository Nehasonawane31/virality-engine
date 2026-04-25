package com.assignment.virality_engine.dto;

import lombok.Data;

@Data
public class CommentCreateRequest {
    private Long authorId;
    private String authorType; // "USER" or "BOT"
    private String content;
    private Integer depthLevel;
}
