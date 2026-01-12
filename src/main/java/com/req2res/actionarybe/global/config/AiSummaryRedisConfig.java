package com.req2res.actionarybe.global.config;

import com.req2res.actionarybe.domain.aisummary.dto.AiSummaryResponseDataDTO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class AiSummaryRedisConfig {

    @Bean
    public RedisTemplate<String, AiSummaryResponseDataDTO> aiSummaryRedisTemplate(
            RedisConnectionFactory connectionFactory
    ) {
        RedisTemplate<String, AiSummaryResponseDataDTO> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // key: String
        template.setKeySerializer(new StringRedisSerializer());

        // value: JSON 직렬화
        Jackson2JsonRedisSerializer<AiSummaryResponseDataDTO> serializer =
                new Jackson2JsonRedisSerializer<>(AiSummaryResponseDataDTO.class);

        template.setValueSerializer(serializer);

        return template;
    }
}
