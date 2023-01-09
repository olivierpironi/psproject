package com.fourcamp.bancoOGP.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import com.fourcamp.bancoOGP.enums.EnumConta;
import com.fourcamp.bancoOGP.enums.EnumSeguroCredito;
import com.fourcamp.bancoOGP.enums.EnumTransacao;
import com.fourcamp.bancoOGP.exceptions.BOGPExceptions;
import com.fourcamp.bancoOGP.exceptions.DataBaseException;
import com.fourcamp.bancoOGP.interfaces.GerenciavelPeloSistema;
import com.fourcamp.bancoOGP.interfaces.RentavelOuTributavel;
import com.fourcamp.bancoOGP.services.DataBase;

/**
 * Conta Corrente tem as funções:
 * - Cartão de Crédito;
 * - Cartão de Débito;
 * - Tarifa mensal de utilização da conta.
 * 
 * Um mesmo cliente só pode ter uma única conta corrente em nosso banco.
 * 
 * @author Olivier Gomes Pironi
 *
 */
public class ContaCorrente extends Conta implements RentavelOuTributavel, GerenciavelPeloSistema {
	protected LocalDate ultimaCobranca = LocalDate.of(2023, 1, 1);
	private CartaoCredito cartaoDeCredito;
	private CartaoDebito cartaoDeDebito;

	private ContaCorrente(EnumConta tipoDeConta, Cliente cliente, String senha) {
		super(tipoDeConta, cliente, senha);

	}

	public static void criarContaCorrente(Cliente cliente, String senha) {
		try {
			checaCCByCpf(cliente);
			ContaCorrente conta = new ContaCorrente(EnumConta.CONTA_CORRENTE, cliente, senha);
			DataBase.getListaDeContas().add(conta);
		} catch (BOGPExceptions e) {
			e.msg();
		}
	}
	public void emitirCartaoCredito(Integer senhaDoCartao, EnumSeguroCredito seguradora, String senhaDaConta) {
		try {
		checaSenha(senhaDaConta);
		CartaoCredito cartao = new CartaoCredito(this, senhaDoCartao, seguradora);
		DataBase.getListaDeCartoes().add(cartao);
		this.cartaoDeCredito = cartao;
		} catch(BOGPExceptions e) {
			e.msg();
		}
	}

	public void emitirCartaoDebito(Integer senhaDoCartao, String senhaDaConta) {
		try {
		checaSenha(senhaDaConta);
		CartaoDebito cartao = new CartaoDebito(this, senhaDoCartao);
		DataBase.getListaDeCartoes().add(cartao);
		this.cartaoDeDebito = cartao;
		} catch(BOGPExceptions e) {
			e.msg();
		}
	}

	@Override
	public void execRendimentosETaxas() {
		if (ultimaCobranca.until(LocalDate.now(), ChronoUnit.DAYS) >= 30) {

			BigDecimal taxa = this.saldo.multiply(this.getCliente().getCategoria().getTaxaContaCorrente());
			Transacao transacao = new Transacao(EnumTransacao.TARIFA_CONTACORRENTE, this, taxa);
			this.saldo = this.saldo.subtract(transacao.getValor());
			extrato.registraTransacao(transacao);
			ultimaCobranca = LocalDate.now();
		}
	}

	@Override
	public void executarDiariamente() {
		execRendimentosETaxas();
	
	}

	private static void checaCCByCpf(Cliente cliente) {
		if (DataBase.getCCByCpf(cliente.getCpf()) != null) {
			throw new DataBaseException("\nO cliente já tem uma Conta Corrente no Banco OGP");
		}
	}

	public CartaoCredito getCartaoDeCredito() {
		return cartaoDeCredito;
	}

	public CartaoDebito getCartaoDeDebito() {
		return cartaoDeDebito;
	}

}
