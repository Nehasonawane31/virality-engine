package com.assignment.virality_engine.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationSweeper {

    private final StringRedisTemplate redisTemplate;

    @Scheduled(fixedRate = 300000) // 5 minutes
    public void sweepNotifications() {
        Set<String> keys = redisTemplate.keys("user:*:pending_notifs");
        if (keys != null && !keys.isEmpty()) {
            for (String key : keys) {
                Long size = redisTemplate.opsForList().size(key);
                if (size != null && size > 0) {
                    List<String> messages = redisTemplate.opsForList().range(key, 0, -1);
                    redisTemplate.delete(key);
                    
                    if (messages != null && !messages.isEmpty()) {
                        String firstMsg = messages.get(0); 
                        String botName = firstMsg.contains("Bot ") ? firstMsg.split(" ")[1] : "X";
                        long others = size - 1;
                        
                        log.info("Summarized Push Notification: Bot {} and {} others interacted with your posts.", botName, others);
                    }
                }
            }
        }
    }
}
