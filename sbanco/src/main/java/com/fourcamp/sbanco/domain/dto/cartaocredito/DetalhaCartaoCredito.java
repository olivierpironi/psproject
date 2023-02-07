package com.fourcamp.sbanco.domain.dto.cartaocredito;

import java.util.Objects;

import com.fourcamp.sbanco.domain.enums.EnumCartao;

public record DetalhaCartaoCredito(Long numeroDaConta, EnumCartao tipoDeCartao, Long numero, boolean bloqueado) {

	public DetalhaCartaoCredito(CartaoCreditoDTO cartao) {
		this(cartao.getContaAssociada().getNumeroDaConta(), cartao.getTipoDeCartao(), cartao.getNumero(), cartao.isBloqueado());
	}

	@Override
	public int hashCode() {
		return Objects.hash(numero, numeroDaConta, tipoDeCartao);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DetalhaCartaoCredito other = (DetalhaCartaoCredito) obj;
		return Objects.equals(numero, other.numero) && Objects.equals(numeroDaConta, other.numeroDaConta)
				&& tipoDeCartao == other.tipoDeCartao;
	}
}
