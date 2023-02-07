package com.fourcamp.sbanco.domain.dto.pix;

import com.fourcamp.sbanco.domain.dto.conta.ContaDTO;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "chave_pix")
@Table(name = "chaves_pix")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ChavePixDTO {
	@OneToOne
	private ContaDTO contaAssociada;
	@Id
	private String chave;

	
}
