package com.ari.sincronizacaoreceita.service;

import com.ari.sincronizacaoreceita.exception.BusinessException;
import com.ari.sincronizacaoreceita.model.Receita;
import com.ari.sincronizacaoreceita.validator.ReceitaValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReceitaService {
	private static final Logger LOGGER = LoggerFactory.getLogger(ReceitaService.class);
	private final ReceitaValidator validator;

	@Autowired
	public ReceitaService(ReceitaValidator validator) {
		this.validator = validator;
	}

	/**
	 * Atualiza a conta de acordo com a receita enviada
	 * @param receita
	 * @return
	 * @throws BusinessException
	 * @throws InterruptedException
	 */
	public Boolean atualizarConta(Receita receita) throws BusinessException, InterruptedException {
		if (validator.accept(receita)){
			LOGGER.info("Atualizando a {}", receita.toString());
			return enviarReceita();
		}
		return false;
	}

	/**
	 * Simula tempo de resposta do serviço (entre 1 e 5 segundos)
	 * @throws  InterruptedException
	 */
	private Boolean enviarReceita() throws InterruptedException {
		long wait = Math.round(Math.random() * 4000) + 1000;
		Thread.sleep(wait);
		return validarFalhas();
	}

	/**
	 * Simula cenario de erro no serviço (0,1% de erro)
	 */
	private Boolean validarFalhas() {
		long randomError = Math.round(Math.random() * 1000);
		if (randomError == 500) {
			throw new BusinessException("Falha ao enviar dados ao Banco Central!");
		}
		return true;
	}
}
