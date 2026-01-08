package com.req2res.actionarybe.domain.aisummary.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.req2res.actionarybe.domain.aisummary.dto.AiSummaryResponseDataDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AiSummaryRedisRepository {

    private static final String JOB_KEY_PREFIX = "ai:summary:job:";
    private static final String QUEUE_KEY = "ai:summary:queue";

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void saveJob(String jobId, AiSummaryResponseDataDTO data, Duration ttl) {
        try {
            redisTemplate.opsForValue().set(JOB_KEY_PREFIX + jobId, objectMapper.writeValueAsString(data), ttl);
        } catch (Exception e) {
            throw new RuntimeException("Redis saveJob 실패", e);
        }
    }

    public Optional<AiSummaryResponseDataDTO> findJob(String jobId) {
        String json = redisTemplate.opsForValue().get(JOB_KEY_PREFIX + jobId);
        if (json == null) return Optional.empty();
        try {
            return Optional.of(objectMapper.readValue(json, AiSummaryResponseDataDTO.class));
        } catch (Exception e) {
            throw new RuntimeException("Redis findJob 실패", e);
        }
    }

    public void enqueue(String jobId) {
        redisTemplate.opsForList().leftPush(QUEUE_KEY, jobId);
    }

    public String popNextJobId() {
        return redisTemplate.opsForList().rightPop(QUEUE_KEY);
    }
}
