package com.fourcamp.sbanco.dto.records;

import jakarta.validation.constraints.NotNull;

public record NumeroDaConta(
		
		@NotNull
		Long numeroDaConta) {

}
