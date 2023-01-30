package com.fourcamp.sbanco.domain.dto.conta;

import jakarta.validation.constraints.NotNull;

public record NumeroDaConta(
		
		@NotNull
		Long numeroDaConta) {

}
