package com.fourcamp.sbanco.domain.dto.cartaodebito;

import java.util.Objects;

import com.fourcamp.sbanco.domain.enums.EnumCartao;

public record DetalhaCartaoDebito(Long numeroDaConta, EnumCartao tipoDeCartao, Long numeroDoCartao, boolean bloqueado) {

	public DetalhaCartaoDebito(CartaoDebitoDTO cartao) {
		this(cartao.getContaAssociada().getNumeroDaConta(), cartao.getTipoDeCartao(), cartao.getNumero(), cartao.isBloqueado());
	}

	@Override
	public int hashCode() {
		return Objects.hash(numeroDoCartao);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DetalhaCartaoDebito other = (DetalhaCartaoDebito) obj;
		return Objects.equals(numeroDoCartao, other.numeroDoCartao);
	}
}
