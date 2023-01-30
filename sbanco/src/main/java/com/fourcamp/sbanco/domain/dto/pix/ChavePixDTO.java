package com.fourcamp.sbanco.domain.dto.pix;

import com.fourcamp.sbanco.domain.dto.conta.ContaDTO;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "chave_pix")
@Table(name = "chaves_pix")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChavePixDTO {
	@OneToOne
	private ContaDTO contaAssociada;
	@Id
	private String chave;

}
