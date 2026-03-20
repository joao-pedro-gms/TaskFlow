package com.taskflow.config;

import jakarta.jms.Queue;
import jakarta.jms.Topic;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

import jakarta.jms.ConnectionFactory;

@Configuration
@EnableJms
public class ActiveMQConfig {

    public static final String DEADLINE_REMINDERS_QUEUE = "taskflow.deadline-reminders";
    public static final String NOTIFICATIONS_TOPIC = "taskflow.notifications";

    @Bean
    public Queue deadlineRemindersQueue() {
        return new ActiveMQQueue(DEADLINE_REMINDERS_QUEUE);
    }

    @Bean
    public Topic notificationsTopic() {
        return new ActiveMQTopic(NOTIFICATIONS_TOPIC);
    }

    @Bean
    public MessageConverter jacksonJmsMessageConverter() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");
        return converter;
    }

    @Bean
    public JmsListenerContainerFactory<?> jmsListenerContainerFactory(
            ConnectionFactory connectionFactory) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jacksonJmsMessageConverter());
        factory.setErrorHandler(t -> System.err.println("JMS error: " + t.getMessage()));
        return factory;
    }
}
