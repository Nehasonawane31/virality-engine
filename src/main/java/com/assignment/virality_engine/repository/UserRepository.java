package com.assignment.virality_engine.repository;

import com.assignment.virality_engine.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
