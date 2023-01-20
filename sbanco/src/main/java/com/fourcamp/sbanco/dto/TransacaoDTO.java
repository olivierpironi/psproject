package com.fourcamp.sbanco.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fourcamp.sbanco.enums.EnumTransacao;
import com.fourcamp.sbanco.infra.Formatadores;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
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
public class TransacaoDTO {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Enumerated(EnumType.STRING)
	private EnumTransacao enumTransacao;
	@OneToOne
	private ContaDTO contaOrigem;
	@OneToOne
	private ContaDTO contaDestino;
	private LocalDateTime dataDaTransacao;
	private BigDecimal valor;
	

	public TransacaoDTO(EnumTransacao enumTransacao, ContaDTO contaOrigem, ContaDTO contaDestino, LocalDateTime dataDaTransacao,
			BigDecimal valor){
		this.enumTransacao = enumTransacao;
		this.contaOrigem = contaOrigem;
		this.contaDestino = contaDestino;
		this.dataDaTransacao = dataDaTransacao;
		this.valor = valor;
	}

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
	
		
	private String info() {
	
		return enumTransacao.equals(EnumTransacao.PIX_RECEBIDO) ? "\nDe: " + this.contaOrigem.getCliente().getNome()
				: enumTransacao.equals(EnumTransacao.PIX_ENVIADO) ? "\nPara: " + this.contaDestino.getCliente().getNome()
				: enumTransacao.equals(EnumTransacao.PAGAMENTO_RECEBIDO) ? "\nDe: " + this.contaOrigem.getCliente().getNome()
				: enumTransacao.equals(EnumTransacao.PAGAMENTO_CREDITO) ? "\nPara: " + this.contaDestino.getCliente().getNome()
				: enumTransacao.equals(EnumTransacao.PAGAMENTO_DEBITO) ? "\nPara: " + this.contaDestino.getCliente().getNome()
				: " ";
	
	} 

	@Override
	public String toString() {
	
		return "\n" + enumTransacao.getTipo() + info() + "............................ " + enumTransacao.getSinal()
				+ " R$" + Formatadores.arredonda(this.valor) + "\n" + this.dataDaTransacao.format(Formatadores.formato);
	}

}
