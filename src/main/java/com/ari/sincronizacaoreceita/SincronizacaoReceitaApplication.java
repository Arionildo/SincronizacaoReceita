package com.ari.sincronizacaoreceita;

import com.ari.sincronizacaoreceita.service.ArquivoService;
import com.ari.sincronizacaoreceita.service.ReceitaService;
import com.ari.sincronizacaoreceita.validator.ReceitaValidator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SincronizacaoReceitaApplication {

	public static void main(String[] args) {
		SpringApplication.run(SincronizacaoReceitaApplication.class, args);
		ReceitaValidator receitaValidator = new ReceitaValidator();
		ReceitaService receitaService = new ReceitaService(receitaValidator);
		ArquivoService arquivoService = new ArquivoService(receitaService);
		arquivoService.ler(args);
	}

}
