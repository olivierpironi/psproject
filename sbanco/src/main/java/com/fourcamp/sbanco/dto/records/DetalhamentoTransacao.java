package com.fourcamp.sbanco.dto.records;

import com.fourcamp.sbanco.dto.TransacaoDTO;

public record DetalhamentoTransacao(String tipoTransacao, String sinalTransacao, String valorTransacao,
		String dataTransacao) {
	public DetalhamentoTransacao(TransacaoDTO transacao) {
		this(transacao.getEnumTransacao().getTipo(), transacao.getEnumTransacao().getSinal(),transacao.getValor().toString(), transacao.getDataDaTransacao().toString());
	}
}
