package br.com.fatec.autoway.infra.messaging;

import br.com.fatec.autoway.domain.model.Boleto;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import static br.com.fatec.autoway.infra.messaging.RabbitMQConfig.*;

@Component
public class BoletoProducer {

    private final RabbitTemplate rabbitTemplate;

    public BoletoProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void enviarBoletoGerado(Boleto boleto) {
        rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY, boleto);
    }
}
