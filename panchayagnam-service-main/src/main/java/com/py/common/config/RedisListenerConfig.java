//
//package com.py.common.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.connection.RedisConnectionFactory;
//import org.springframework.data.redis.listener.PatternTopic;
//import org.springframework.data.redis.listener.RedisMessageListenerContainer;
//import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
//
//@Configuration
//public class RedisListenerConfig {
//
//    @Bean
//    public RedisMessageListenerContainer container(
//            RedisConnectionFactory connectionFactory,
//            MessageListenerAdapter adapter) {
//
//        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
//        container.setConnectionFactory(connectionFactory);
//
//        // listen all accounts
//        container.addMessageListener(adapter, new PatternTopic("LIVE_FEED_*"));
//
//        return container;
//    }
//
//    @Bean
//    public MessageListenerAdapter listenerAdapter(LiveFeedSubscriber subscriber) {
//        return new MessageListenerAdapter(subscriber, "onMessage");
//    }
//}
