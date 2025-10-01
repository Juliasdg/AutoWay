package br.com.fatec.autoway.infra.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitTemplateConfig {

    private final RabbitTemplate rabbitTemplate;
    private final Jackson2JsonMessageConverter messageConverter;

    public RabbitTemplateConfig(RabbitTemplate rabbitTemplate,
                                Jackson2JsonMessageConverter messageConverter) {
        this.rabbitTemplate = rabbitTemplate;
        this.messageConverter = messageConverter;
        this.rabbitTemplate.setMessageConverter(messageConverter);
    }
}
