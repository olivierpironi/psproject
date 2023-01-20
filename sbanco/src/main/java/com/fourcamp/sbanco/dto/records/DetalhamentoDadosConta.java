package com.fourcamp.sbanco.dto.records;

import com.fourcamp.sbanco.dto.ContaDTO;
import com.fourcamp.sbanco.enums.EnumConta;

public record DetalhamentoDadosConta(EnumConta tipoDeConta, Long numeroDaConta, String agencia, String titular) {

	public DetalhamentoDadosConta(ContaDTO conta) {
		this(conta.getTipoDeConta(), conta.getNumeroDaConta(), conta.getAgencia(), conta.getCliente().getNome());
	}

}
