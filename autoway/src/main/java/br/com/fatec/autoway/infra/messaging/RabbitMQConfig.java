package br.com.fatec.autoway.infra.messaging;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "boletos.exchange";
    public static final String ROUTING_KEY = "boleto.gerado";
    public static final String QUEUE_NAME = "boletos.queue";

    @Bean
    public TopicExchange boletosExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue boletosQueue() {
        return new Queue(QUEUE_NAME, true);
    }

    @Bean
    public Binding boletosBinding(Queue boletosQueue, TopicExchange boletosExchange) {
        return BindingBuilder.bind(boletosQueue)
                .to(boletosExchange)
                .with(ROUTING_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
