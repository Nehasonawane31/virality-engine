package com.assignment.virality_engine.dto;

import lombok.Data;

@Data
public class PostCreateRequest {
    private Long authorId;
    private String authorType; // "USER" or "BOT"
    private String content;
}
