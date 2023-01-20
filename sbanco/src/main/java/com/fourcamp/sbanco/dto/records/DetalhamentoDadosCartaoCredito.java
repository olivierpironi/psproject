package com.fourcamp.sbanco.dto.records;

import com.fourcamp.sbanco.dto.CartaoCreditoDTO;
import com.fourcamp.sbanco.enums.EnumCartao;

public record DetalhamentoDadosCartaoCredito(Long numeroDaConta, EnumCartao tipoDeCartao, Long numero) {

	public DetalhamentoDadosCartaoCredito(CartaoCreditoDTO cartao) {
		this(cartao.getContaAssociada().getNumeroDaConta(), cartao.getTipoDeCartao(), cartao.getNumero());
	}
}
