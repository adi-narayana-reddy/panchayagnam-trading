//package com.py.common.config;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.redis.core.StringRedisTemplate;
//import org.springframework.stereotype.Component;
//
//@Component
//public class RedisPublisher {
//
//    private static StringRedisTemplate redisTemplate;
//
//    @Autowired
//    public void setRedisTemplate(StringRedisTemplate template) {
//        RedisPublisher.redisTemplate = template;
//    }
//
//    public static void publish(String channel, String message) {
//        if (redisTemplate != null) {
//            redisTemplate.convertAndSend(channel, message);
//        }
//    }
//}
