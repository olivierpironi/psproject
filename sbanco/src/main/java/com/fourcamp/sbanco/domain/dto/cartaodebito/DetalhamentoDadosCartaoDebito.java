package com.fourcamp.sbanco.domain.dto.cartaodebito;

import com.fourcamp.sbanco.domain.enums.EnumCartao;

public record DetalhamentoDadosCartaoDebito(Long numeroDaConta, EnumCartao tipoDeCartao, Long numeroDoCartao, boolean bloqueado) {

	public DetalhamentoDadosCartaoDebito(CartaoDebitoDTO cartao) {
		this(cartao.getContaAssociada().getNumeroDaConta(), cartao.getTipoDeCartao(), cartao.getNumero(), cartao.isBloqueado());
	}
}
