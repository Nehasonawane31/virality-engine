package com.assignment.virality_engine.repository;

import com.assignment.virality_engine.entity.Bot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BotRepository extends JpaRepository<Bot, Long> {
}
