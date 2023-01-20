package com.fourcamp.sbanco.dto.records;

import com.fourcamp.sbanco.dto.CartaoDebitoDTO;
import com.fourcamp.sbanco.enums.EnumCartao;

public record DetalhamentoDadosCartaoDebito(Long numeroDaConta, EnumCartao tipoDeCartao, Long numero) {

	public DetalhamentoDadosCartaoDebito(CartaoDebitoDTO cartao) {
		this(cartao.getContaAssociada().getNumeroDaConta(), cartao.getTipoDeCartao(), cartao.getNumero());
	}
}
