package com.fourcamp.bancoOGP.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import com.fourcamp.bancoOGP.enums.EnumCartao;
import com.fourcamp.bancoOGP.enums.EnumTransacao;
import com.fourcamp.bancoOGP.exceptions.BOGPExceptions;
import com.fourcamp.bancoOGP.interfaces.GerenciavelPeloSistema;

/**
 * Com o cartão de débito pode-se: 
 * - Pagar utilizando o saldo da conta corrente;
 * - Taxação da operação de pagamento;
 * - Limite diário de transação.
 * 
 * @author Olivier Gomes Pironi
 *
 */
public class CartaoDebito extends Cartao implements GerenciavelPeloSistema {
	private BigDecimal limiteDiario = contaAssociada.getCliente().getCategoria().getLimiteDiarioTransacaoDebito();
	private BigDecimal limiteDisponivel = limiteDiario;
	protected LocalDate ultimaRenovacaoLimiteDisponivel = LocalDate.of(2023, 1, 1);

	protected CartaoDebito(Conta contaAssociada, Integer senha) {
		super(contaAssociada, senha);
		this.tipoDeCartao = EnumCartao.DEBITO;
	}

	public void pagarDebito(ContaCorrente recebedor, String valor, Integer senhaCartao) {
		try {
			checaSenha(senhaCartao);
			checaBloqueio();
			BigDecimal bdPagamento = new BigDecimal(valor);
			BigDecimal bdTaxa = calculaTaxaOperacao(bdPagamento);
			checaLimite(bdPagamento, limiteDisponivel);
			contaAssociada.checaSaidaSaldo(bdPagamento.add(bdTaxa));
			contaAssociada.checaSaldoDisponivel(bdPagamento.add(bdTaxa), contaAssociada.saldo);
			Transacao transacao = new Transacao(EnumTransacao.PAGAMENTO_DEBITO, contaAssociada, recebedor, bdPagamento);
			Transacao taxaOperacao = new Transacao(EnumTransacao.TAXA_CARTAO_DE_DEBITO, contaAssociada, bdTaxa);
			limiteDisponivel = limiteDisponivel.subtract(transacao.getValor().add(taxaOperacao.getValor()));
			contaAssociada.saldo = contaAssociada.saldo.subtract(transacao.getValor().add(taxaOperacao.getValor()));
			recebedor.receberTransacao(transacao);
			contaAssociada.extrato.registraTransacao(transacao, taxaOperacao);
		} catch (BOGPExceptions e) {
			e.msg();
		}
	}
	
	public void renovaLimiteDisponivel() {
		if (ultimaRenovacaoLimiteDisponivel.until(LocalDate.now(), ChronoUnit.DAYS) >= 1) {
			this.limiteDisponivel = this.limiteDiario;
			this.ultimaRenovacaoLimiteDisponivel = LocalDate.now();
		}
	}

	@Override
	public void executarDiariamente() {
		renovaLimiteDisponivel();
	}

	protected void setLimiteDiario(String novoLimite) {
		this.limiteDiario = new BigDecimal(novoLimite);
	}

	public BigDecimal getLimiteDisponivel() {
		return limiteDisponivel;
	}

	public BigDecimal getLimiteDiario() {
		return limiteDiario;
	}
}
