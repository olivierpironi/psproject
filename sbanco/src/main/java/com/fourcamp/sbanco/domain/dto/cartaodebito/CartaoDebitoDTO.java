package com.fourcamp.sbanco.domain.dto.cartaodebito;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fourcamp.sbanco.domain.dto.cartao.CartaoDTO;
import com.fourcamp.sbanco.domain.dto.contacorrente.ContaCorrenteDTO;
import com.fourcamp.sbanco.domain.enums.EnumCartao;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Com o cartão de débito pode-se: 
 * - Pagar utilizando o saldo da conta corrente;
 * - Taxação da operação de pagamento;
 * - Limite diário de transação.
 * 
 * @author Olivier Gomes Pironi
 *
 */
@Entity(name = "cartao_debito")
@Table(name = "cartoes_de_debito")
@Getter
@Setter
@NoArgsConstructor
public class CartaoDebitoDTO extends CartaoDTO {
	@OneToOne
	protected ContaCorrenteDTO contaAssociada;
	private BigDecimal limiteDiario = new BigDecimal("0");
	private BigDecimal limiteDisponivel = limiteDiario;
	protected LocalDate ultimaRenovacaoLimiteDisponivel = LocalDate.of(2023, 1, 1);

	public CartaoDebitoDTO(ContaCorrenteDTO contaAssociada, Integer senha) {
		super(senha);
		this.contaAssociada = contaAssociada;
		this.tipoDeCartao = EnumCartao.DEBITO;
	}
}