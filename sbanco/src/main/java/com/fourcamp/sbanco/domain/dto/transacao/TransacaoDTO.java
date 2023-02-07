package com.fourcamp.sbanco.domain.dto.transacao;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fourcamp.sbanco.domain.dto.conta.ContaDTO;
import com.fourcamp.sbanco.domain.enums.EnumTransacao;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Todas as movimentações financeiras do Banco OGP se dão através do objeto
 * transação, o qual conta com várias informações relevantes referentes a
 * movimentação em questão.
 * 
 * @author Olivier Gomes Pironi
 */
@Entity(name = "transacao")
@Table(name = "transacoes")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class TransacaoDTO {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@EqualsAndHashCode.Exclude
	private Long id;
	@Enumerated(EnumType.STRING)
	private EnumTransacao enumTransacao;
	@OneToOne
	private ContaDTO contaOrigem;
	@OneToOne
	private ContaDTO contaDestino;
	@EqualsAndHashCode.Exclude
	private LocalDateTime dataDaTransacao;
	private BigDecimal valor;

	public TransacaoDTO(EnumTransacao enumTransacao, ContaDTO contaOrigem, ContaDTO contaDestino, BigDecimal valor) {
		this.enumTransacao = enumTransacao;
		this.contaOrigem = contaOrigem;
		this.contaDestino = contaDestino;
		this.dataDaTransacao = LocalDateTime.now();
		this.valor = valor;
	}

	public TransacaoDTO(EnumTransacao enumTransacao, ContaDTO contaOrigem, BigDecimal valor) {
		this.enumTransacao = enumTransacao;
		this.contaOrigem = contaOrigem;
		this.contaDestino = null;
		this.dataDaTransacao = LocalDateTime.now();
		this.valor = valor;
	}

}
