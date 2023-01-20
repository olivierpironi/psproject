package com.fourcamp.sbanco.dto;

import java.math.BigDecimal;
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
 * Todo cartão de crédito possui uma fatura responsável por registrar e emitir
 * todo histórico de pagamentos e limite disponível de um cartão de crédito.
 * 
 * @author Olivier Gomes Pironi
 */
@Entity(name = "fatura")
@Table(name = "faturas")
@Getter
@Setter
@NoArgsConstructor
public class FaturaDTO {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@OneToOne
	private CartaoDTO cartaoAssociado;
	private BigDecimal limiteDisponivel;
	private BigDecimal valorFatura;
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
	@ElementCollection(targetClass = TransacaoDTO.class)
	private List<TransacaoDTO> historicoTransacoes = new ArrayList<>();

	protected FaturaDTO(CartaoCreditoDTO cartaoAssociado) {
		this.cartaoAssociado = cartaoAssociado;
		this.limiteDisponivel = cartaoAssociado.getLimiteCredito();
		this.valorFatura = new BigDecimal("0");
	}

}
