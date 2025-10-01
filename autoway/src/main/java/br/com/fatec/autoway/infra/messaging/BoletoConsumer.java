package br.com.fatec.autoway.infra.messaging;

import br.com.fatec.autoway.domain.model.Boleto;
import br.com.fatec.autoway.infra.orm.BoletoOrm;
import br.com.fatec.autoway.infra.repository.jpa.BoletoRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class BoletoConsumer {

    private final BoletoRepository boletoRepository;
    private final ObjectMapper objectMapper;

    public BoletoConsumer(BoletoRepository boletoRepository, ObjectMapper objectMapper) {
        this.boletoRepository = boletoRepository;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = "boletos.queue")
    public void receberBoleto(Boleto boleto) {
        System.out.println("📩 Boleto recebido da fila: " + boleto);

        try {
            BoletoOrm boletoOrm = new BoletoOrm();
            boletoOrm.setIdBoleto(boleto.idBoleto());
            boletoOrm.setIdPessoa(boleto.idPessoa());
            boletoOrm.setValorTotal(boleto.valorTotal());
            boletoOrm.setDataInicio(boleto.dataInicio());
            boletoOrm.setDataFim(boleto.dataFim());
            boletoOrm.setPassagens(objectMapper.writeValueAsString(boleto.passagens()));
            boletoOrm.setStatusPagamento(boleto.statusPagamento());
            boletoOrm.setDataEmissao(boleto.dataEmissao());
            boletoOrm.setDataVencimento(boleto.dataVencimento());

            boletoRepository.save(boletoOrm);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Erro ao converter passagens para JSON", e);
        }
    }
}
