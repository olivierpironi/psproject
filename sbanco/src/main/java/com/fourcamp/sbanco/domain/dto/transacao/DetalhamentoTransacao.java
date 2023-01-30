package com.fourcamp.sbanco.domain.dto.transacao;

import java.time.format.DateTimeFormatter;

public record DetalhamentoTransacao(String tipoTransacao, String sinalTransacao, String valorTransacao,
		String dataTransacao) {
	public DetalhamentoTransacao(TransacaoDTO transacao) {
		this(
				transacao.getEnumTransacao().getTipo(), 
				transacao.getEnumTransacao().getSinal(),transacao.getValor().toString(), 
				transacao.getDataDaTransacao().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
				);
	}
}
	