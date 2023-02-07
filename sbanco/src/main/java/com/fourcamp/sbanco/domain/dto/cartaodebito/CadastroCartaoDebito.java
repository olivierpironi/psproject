package com.fourcamp.sbanco.domain.dto.cartaodebito;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CadastroCartaoDebito(
		
		@NotNull
		Long numeroDaConta, 
		
		@NotBlank
		String senhaDaConta, 

		@NotNull
		Integer senhaCartao) {

}
