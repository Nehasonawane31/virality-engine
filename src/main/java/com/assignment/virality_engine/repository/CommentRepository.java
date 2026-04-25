package com.assignment.virality_engine.repository;

import com.assignment.virality_engine.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
