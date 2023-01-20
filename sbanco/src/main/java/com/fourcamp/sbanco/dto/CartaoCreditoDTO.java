package com.fourcamp.sbanco.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fourcamp.sbanco.enums.EnumCartao;
import com.fourcamp.sbanco.enums.EnumSeguroCredito;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Com o cartão de crédito pode-se: 
 * - Pagar com sistema de crédito; 
 * - Taxação da operação de pagamento;
 * - Emitir fatura; 
 * - Pagar fatura utilizando saldo da conta corrente.
 * 
 * Possui obrigatoriamente um contrato com uma seguradora de crédito, o valor do
 * seguro é cobrado mensalmente do usuário pelo próprio sistema de crédito.
 * 
 * @author Olivier Gomes Pironi
 *
 */
@Entity(name = "cartao_credito")
@Table(name = "cartoes_de_credito")
@Getter
@Setter
@NoArgsConstructor
public class CartaoCreditoDTO extends CartaoDTO {

	private BigDecimal limiteCredito = new BigDecimal("0");
	@OneToOne
	protected ContaCorrenteDTO contaAssociada;
	@OneToOne(mappedBy = "cartaoAssegurado", cascade = CascadeType.ALL)
	private ContratoSeguroDTO contratoSeguro;
	@OneToOne(mappedBy = "cartaoAssociado", cascade = CascadeType.ALL)
	protected FaturaDTO fatura = new FaturaDTO(this);
	protected LocalDate ultimoPagamentoSeguro = LocalDate.of(2023, 1, 1);

	public CartaoCreditoDTO(ContaCorrenteDTO contaAssociada, EnumSeguroCredito seguradora, Integer senha) {
		super(senha);
		this.contaAssociada = contaAssociada;
		this.tipoDeCartao = EnumCartao.CREDITO;
		this.contratoSeguro = new ContratoSeguroDTO(seguradora, this);
	}

	@Override
	public String toString() {
		return "\nBANCO OGP" + 
				"\nTitular: " + contaAssociada.getCliente().getNome() + 
				"\nCategoria: " + contaAssociada.getCliente().getCategoria().getDescricao() + 
				"\n" + tipoDeCartao.getDescricao() + 
				"\nNº" + getNumero();
	}
}
