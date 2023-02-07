package com.fourcamp.sbanco.domain.dto.conta;

import com.fourcamp.sbanco.domain.enums.EnumConta;

public record DetalhaConta(EnumConta tipoDeConta, Long numeroDaConta, String agencia, String titular) {

	public DetalhaConta(ContaDTO conta) {
		this(conta.getTipoDeConta(), conta.getNumeroDaConta(), conta.getAgencia(), conta.getCliente().getNome());
	}

}
