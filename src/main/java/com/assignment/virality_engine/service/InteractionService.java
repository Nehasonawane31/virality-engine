package com.assignment.virality_engine.service;

import com.assignment.virality_engine.entity.Comment;
import com.assignment.virality_engine.entity.Post;
import com.assignment.virality_engine.exception.RateLimitException;
import com.assignment.virality_engine.repository.CommentRepository;
import com.assignment.virality_engine.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class InteractionService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final StringRedisTemplate redisTemplate;

    @Transactional
    public Post createPost(Post post) {
        return postRepository.save(post);
    }

    @Transactional
    public Comment addComment(Long postId, Comment comment) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new IllegalArgumentException("Post not found"));
        
        // Vertical Cap
        if (comment.getDepthLevel() != null && comment.getDepthLevel() > 20) {
            throw new RateLimitException("Comment thread cannot go deeper than 20 levels");
        }

        boolean isBot = "BOT".equalsIgnoreCase(comment.getAuthorType());
        boolean isHuman = "USER".equalsIgnoreCase(comment.getAuthorType());

        if (isBot) {
            // Horizontal Cap
            Long count = redisTemplate.opsForValue().increment("post:" + postId + ":bot_count");
            if (count != null && count > 100) {
                // If it exceeds 100, we strictly reject it.
                throw new RateLimitException("Post cannot have more than 100 bot replies total");
            }

            // Cooldown Cap
            if ("USER".equalsIgnoreCase(post.getAuthorType())) {
                String cooldownKey = "cooldown:bot_" + comment.getAuthorId() + ":human_" + post.getAuthorId();
                Boolean allowed = redisTemplate.opsForValue().setIfAbsent(cooldownKey, "1", Duration.ofMinutes(10));
                if (Boolean.FALSE.equals(allowed)) {
                    throw new RateLimitException("Bot is in cooldown for this human");
                }
                
                // Notification Engine (Phase 3)
                handleNotification(post.getAuthorId(), "Bot " + comment.getAuthorId() + " replied to your post");
            }
            
            // Virality Score
            updateViralityScore(postId, 1);
        } else if (isHuman) {
            updateViralityScore(postId, 50);
        }

        comment.setPostId(postId);
        return commentRepository.save(comment);
    }

    @Transactional
    public void likePost(Long postId, String authorType, Long authorId) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new IllegalArgumentException("Post not found"));
        
        if ("USER".equalsIgnoreCase(authorType)) {
            updateViralityScore(postId, 20);
        }
    }

    private void updateViralityScore(Long postId, int points) {
        redisTemplate.opsForValue().increment("post:" + postId + ":virality_score", points);
    }

    private void handleNotification(Long userId, String message) {
        String notifCooldownKey = "user:" + userId + ":notif_cooldown";
        Boolean isCooldown = redisTemplate.hasKey(notifCooldownKey);

        if (Boolean.TRUE.equals(isCooldown)) {
            redisTemplate.opsForList().rightPush("user:" + userId + ":pending_notifs", message);
        } else {
            log.info("Push Notification Sent to User: {}", message);
            redisTemplate.opsForValue().set(notifCooldownKey, "1", Duration.ofMinutes(15));
        }
    }
}
