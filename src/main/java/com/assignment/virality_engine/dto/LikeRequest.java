package com.assignment.virality_engine.dto;

import lombok.Data;

@Data
public class LikeRequest {
    private Long authorId;
    private String authorType;
}
