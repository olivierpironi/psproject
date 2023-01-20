package com.fourcamp.sbanco.dto;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Toda conta possui um extrato bancário responsável por registrar e emitir todo
 * histórico de transações e saldo de uma conta.
 * 
 * @author Olivier Gomes Pironi
 */
@Entity(name = "extrato_bancario")
@Table(name = "extratos_bancarios")
@Getter
@Setter
@NoArgsConstructor
public class ExtratoBancarioDTO {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@OneToOne
	private ContaDTO contaMae;
	@OneToMany(fetch = FetchType.EAGER,cascade = CascadeType.MERGE)
    @ElementCollection(targetClass=TransacaoDTO.class)
	private List<TransacaoDTO> historicoTransacoes = new ArrayList<>();

	public ExtratoBancarioDTO(ContaDTO conta) {
		this.contaMae=conta;
	}

}
