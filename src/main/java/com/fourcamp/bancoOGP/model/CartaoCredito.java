package com.fourcamp.bancoOGP.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import com.fourcamp.bancoOGP.enums.EnumCartao;
import com.fourcamp.bancoOGP.enums.EnumSeguroCredito;
import com.fourcamp.bancoOGP.enums.EnumTransacao;
import com.fourcamp.bancoOGP.exceptions.BOGPExceptions;
import com.fourcamp.bancoOGP.interfaces.GerenciavelPeloSistema;

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
public class CartaoCredito extends Cartao implements GerenciavelPeloSistema {

	private BigDecimal limiteCredito = contaAssociada.getCliente().getCategoria().getLimiteCartaoDeCredito();
	private ContratoSeguro contratoSeguro;
	protected Fatura fatura = new Fatura(this);
	protected LocalDate ultimoPagamentoSeguro = LocalDate.of(2023, 1, 1);

	protected CartaoCredito(Conta contaAssociada, Integer senha, EnumSeguroCredito seguradora) {
		super(contaAssociada, senha);
		this.tipoDeCartao = EnumCartao.CREDITO;
		this.contratoSeguro = new ContratoSeguro(seguradora, this);
	}

	public void pagarComCredito(Conta recebedor, String valor, Integer senhaCartao) {
		try {
			checaSenha(senhaCartao);
			checaBloqueio();
			BigDecimal bdPagamento = new BigDecimal(valor);
			BigDecimal bdTaxa = calculaTaxaOperacao(bdPagamento);
			contaAssociada.checaSaidaSaldo(bdPagamento.add(bdTaxa));
			checaLimite(bdPagamento.add(bdTaxa), fatura.getLimiteDisponivel());
			Transacao transacao = new Transacao(EnumTransacao.PAGAMENTO_CREDITO, contaAssociada, recebedor, bdPagamento);
			Transacao taxaOperacao = new Transacao(EnumTransacao.TAXA_CARTAO_DE_CREDITO, contaAssociada, bdTaxa);
			fatura.registraTransacao(transacao, taxaOperacao);
			recebedor.receberTransacao(transacao);
			
		} catch (BOGPExceptions e) {
			e.msg();
		}
	}

	public void pagarFaturaComSaldo(String pagamento, Integer senha) {
		try {
			checaSenha(senha);
			BigDecimal bdPagamento = new BigDecimal(pagamento);
			contaAssociada.checaSaidaSaldo(bdPagamento);
			contaAssociada.checaSaldoDisponivel(bdPagamento, contaAssociada.saldo);
			Transacao transacao = new Transacao(EnumTransacao.PAGAMENTO_FATURA, contaAssociada, bdPagamento);
			contaAssociada.saldo = contaAssociada.saldo.subtract(transacao.getValor());
			contaAssociada.extrato.registraTransacao(transacao);
			fatura.registraTransacao(transacao);
		} catch (BOGPExceptions e) {
			e.msg();
		}
	}

	public void execMensalidadeSeguro() {
		if (ultimoPagamentoSeguro.until(LocalDate.now(), ChronoUnit.DAYS) >= 30) {

			try {
				BigDecimal mensalidade = contratoSeguro.getSeguradora().getValor();
				checaLimite(mensalidade, fatura.getLimiteDisponivel());
				Transacao transacao = new Transacao(EnumTransacao.MENSALIDADE_SEGURO, contaAssociada, mensalidade);
				fatura.registraTransacao(transacao);
				ultimoPagamentoSeguro = LocalDate.now();
			} catch (BOGPExceptions e) {
				e.msg();
			}
		}
	}

	public void exibirFatura(Integer senha) {
		try {
			checaSenha(senha);
			fatura.exibe();
		} catch (BOGPExceptions e) {
			e.msg();
		}
	}

	@Override
	public void executarDiariamente() {
		execMensalidadeSeguro();

	}

	protected BigDecimal getLimiteCredito() {
		return limiteCredito;
	}

	public ContratoSeguro getContratoSeguro() {
		return contratoSeguro;
	}

}
