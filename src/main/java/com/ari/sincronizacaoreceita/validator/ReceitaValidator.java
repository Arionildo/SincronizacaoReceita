package com.ari.sincronizacaoreceita.validator;

import com.ari.sincronizacaoreceita.exception.BusinessException;
import com.ari.sincronizacaoreceita.model.Receita;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component
public class ReceitaValidator implements Validator<Boolean, Receita> {

	@Override
	public Boolean accept(Receita receita) {
		if (isNull(receita)) {
			throw new BusinessException("Receita inexistente!");
		}
		if (isNull(receita.getAgencia())
				|| receita.getAgencia().length() != 4) {
			throw new BusinessException("Receita não possui uma AGÊNCIA válida!");
		}
		if (isNull(receita.getConta())
				|| receita.getConta().replace("-", "").length() != 6) {
			throw new BusinessException("Receita não possui uma CONTA válida!");
		}
		if (isNull(receita.getSaldo())) {
			throw new BusinessException("Receita não possui um SALDO válido!");
		}
		if (isNull(receita.getStatus())) {
			throw new BusinessException("Receita não possui um STATUS válido!");
		}
		return true;
	}
}
