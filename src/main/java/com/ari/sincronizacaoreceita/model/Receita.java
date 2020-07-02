package com.ari.sincronizacaoreceita.model;

import com.ari.sincronizacaoreceita.enums.StatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class Receita implements Serializable {
	private String agencia;
	private String conta;
	private BigDecimal saldo;
	private StatusEnum status;
	private Boolean resultado;

	public String toCSVLine() {
		String separator = ";";
		return this.agencia + separator
				+ this.conta + separator
				+ this.saldo.toString() + separator
				+ this.status.toString() + separator
				+ this.resultado.toString() + "\n";
	}
}
