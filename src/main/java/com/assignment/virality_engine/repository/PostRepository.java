package com.assignment.virality_engine.repository;

import com.assignment.virality_engine.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
