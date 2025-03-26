package com.learn.WeatherApp.utils;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.learn.WeatherApp.models.WeatherResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public RedisTemplate<String, WeatherResponse> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, WeatherResponse> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);

        // JSON Serializer
        ObjectMapper objectMapper = new ObjectMapper();
        // decides which fields are included in JSON serialization
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.PUBLIC_ONLY);
        // pretty print
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        // doesn't throw errors if it doesn't recognize a field, skips
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        // helps when storing complex object hierarchies in Redis
        objectMapper.activateDefaultTyping(objectMapper.getPolymorphicTypeValidator(), ObjectMapper.DefaultTyping.NON_FINAL);

        Jackson2JsonRedisSerializer<WeatherResponse> serializer = new Jackson2JsonRedisSerializer<>(objectMapper, WeatherResponse.class);

        // Set serializers for key and value
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);

        template.afterPropertiesSet();
        return template;
    }
}
