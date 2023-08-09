package com.card.mscard.infra.mqueue;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.card.mscard.entity.Card;
import com.card.mscard.entity.CustomerCard;
import com.card.mscard.entity.DadosSolicitacaoEmissaoCartao;
import com.card.mscard.repository.CardRepository;
import com.card.mscard.repository.CustomerCardRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class EmissaoCartaoSubscriber {

	@Autowired private CardRepository cardRepository;
	@Autowired private CustomerCardRepository ccRepository;
	
	@RabbitListener(queues = "${mq.queues.emissao-cartoes}")
	public void receberSolicitacaoEmissao(@Payload String payload) {
		
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			
			DadosSolicitacaoEmissaoCartao dados = mapper.readValue(payload, DadosSolicitacaoEmissaoCartao.class);
			
			Card cardEntity = cardRepository.findById(dados.getIdCard()).get();
			log.info("Result: {}", payload);
			CustomerCard cc = new CustomerCard();
			cc.setCpf(dados.getCpf());
			cc.setCard(cardEntity);
			cc.setBasicLimit(dados.getBasicLimit());
			
			ccRepository.save(cc);
			
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}
}
