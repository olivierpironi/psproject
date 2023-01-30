package com.fourcamp.sbanco.domain.dto.cartaocredito;

import com.fourcamp.sbanco.domain.enums.EnumCartao;

public record DetalhamentoDadosCartaoCredito(Long numeroDaConta, EnumCartao tipoDeCartao, Long numero, boolean bloqueado) {

	public DetalhamentoDadosCartaoCredito(CartaoCreditoDTO cartao) {
		this(cartao.getContaAssociada().getNumeroDaConta(), cartao.getTipoDeCartao(), cartao.getNumero(), cartao.isBloqueado());
	}
}
