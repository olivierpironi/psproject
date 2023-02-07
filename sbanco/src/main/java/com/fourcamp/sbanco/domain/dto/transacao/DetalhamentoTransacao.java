package com.fourcamp.sbanco.domain.dto.transacao;

import java.time.format.DateTimeFormatter;
import java.util.Objects;

public record DetalhamentoTransacao(String tipoTransacao, String sinalTransacao, String valorTransacao,
		String dataTransacao) {
	public DetalhamentoTransacao(TransacaoDTO transacao) {
		this(
				transacao.getEnumTransacao().getTipo(), 
				transacao.getEnumTransacao().getSinal(),transacao.getValor().toString(), 
				transacao.getDataDaTransacao().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
				);
	}

	@Override
	public int hashCode() {
		return Objects.hash(sinalTransacao, tipoTransacao, valorTransacao);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DetalhamentoTransacao other = (DetalhamentoTransacao) obj;
		return Objects.equals(sinalTransacao, other.sinalTransacao)
				&& Objects.equals(tipoTransacao, other.tipoTransacao)
				&& Objects.equals(valorTransacao, other.valorTransacao);
	}
}
	