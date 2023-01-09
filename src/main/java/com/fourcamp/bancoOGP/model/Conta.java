package com.fourcamp.bancoOGP.model;

import java.math.BigDecimal;
import java.util.stream.Collectors;

import com.fourcamp.bancoOGP.enums.EnumConta;
import com.fourcamp.bancoOGP.enums.EnumTransacao;
import com.fourcamp.bancoOGP.exceptions.BOGPExceptions;
import com.fourcamp.bancoOGP.exceptions.DataBaseException;
import com.fourcamp.bancoOGP.exceptions.SenhaInvalidaException;
import com.fourcamp.bancoOGP.exceptions.TransacaoInvalidaException;
import com.fourcamp.bancoOGP.services.DataBase;

/**
 * Classe mãe de qualquer tipo de conta no Banco OGP, conta com as funções:
 * - Depósito;
 * - Saque;
 * - Pix;
 * - Recebe pagamentos de cartões.
 * 
 * Os diversos tipos de checagens feitas antes que qualquer operação utiliza um sistema de exceções, 
 * é uma regra de negócio.
 * 
 * @author Olivier Gomes Pironi
 */
public abstract class Conta {
	private static long somaContas = 0L;
	private Long numeroDaConta;
	private String agencia = "0001";
	private EnumConta tipoDeConta;
	private Cliente cliente;
	protected BigDecimal saldo = new BigDecimal("0");
	private String senha;
	protected ExtratoBancario extrato = new ExtratoBancario(this);

	protected Conta(EnumConta tipoDeConta, Cliente cliente, String senha) {
		somaContas++;
		this.numeroDaConta = 100000000000L + Conta.somaContas;
		this.tipoDeConta = tipoDeConta;
		this.cliente = cliente;
		this.senha = senha;
	}

	public void efetuarDeposito(String deposito, String senha) {
		try {
			checaSenha(senha);
			BigDecimal bdDeposito = new BigDecimal(deposito);
			checaEntradaSaldo(bdDeposito);
			Transacao transacao = new Transacao(EnumTransacao.DEPOSITO, this, bdDeposito);
			this.saldo = this.saldo.add(transacao.getValor());
			extrato.registraTransacao(transacao);
		} catch (BOGPExceptions e) {
			e.msg();
		}
	}

	public void efetuarSaque(String saque, String senha) {
		try {
			checaSenha(senha);
			BigDecimal bdSaque = new BigDecimal(saque);
			checaSaidaSaldo(bdSaque);
			checaSaldoDisponivel(bdSaque, this.saldo);
			Transacao transacao = new Transacao(EnumTransacao.SAQUE, this, bdSaque);
			this.saldo = this.saldo.subtract(transacao.getValor());
			extrato.registraTransacao(transacao);
		} catch (BOGPExceptions e) {
			e.msg();
		}
	}

	public void cadastrarChavePix(String novaChave, String senha) {
		try {
			checaSenha(senha);
			checaCadastroPix(novaChave);
			DataBase.cadastrarChavePix(novaChave, this);
		} catch (BOGPExceptions e) {
			e.msg();
		}
	}

	public void exibirChavesPix(String senha) {
		try {
			checaSenha(senha);
			DataBase.getMapaContasPix().entrySet().stream()
					.filter(e -> e.getValue().getCliente().equals(this.getCliente())).collect(Collectors.toList())
					.forEach(c -> System.out.println(c.getKey()));
		} catch (BOGPExceptions e) {
			e.msg();
		}

	}

	public void enviarPix(String chavePixDestino, String valorPix, String senha) {
		try {
			checaChavePix(chavePixDestino);
			checaSenha(senha);
			BigDecimal bdPix = new BigDecimal(valorPix);
			checaSaidaSaldo(bdPix);
			checaSaldoDisponivel(bdPix, this.saldo);
			Conta contaDestino = DataBase.getContaByChavePix(chavePixDestino);
			Transacao transacao = new Transacao(EnumTransacao.PIX_ENVIADO, this, contaDestino, bdPix);
			this.saldo = this.saldo.subtract(transacao.getValor());
			this.extrato.registraTransacao(transacao);
			contaDestino.receberPix(transacao);
		} catch (BOGPExceptions e) {
			e.msg();
		}

	}

	public void exibirExtrato(String senha) {
		try {
			checaSenha(senha);
			extrato.exibe();
		} catch (BOGPExceptions e) {
			e.msg();
		}
	}

	public void trocarSenha(String novaSenha, String senhaAntiga) {
		try {
			checaSenha(senhaAntiga);
			this.senha = novaSenha;
		} catch (BOGPExceptions e) {
			e.msg();
		}
	}

	public void receberTransacao(Transacao transacao) {
		if (transacao.getEnumTransacao().equals(EnumTransacao.PIX_ENVIADO))
			receberPix(transacao);
		if (transacao.getEnumTransacao().equals(EnumTransacao.PAGAMENTO_CREDITO)
				|| transacao.getEnumTransacao().equals(EnumTransacao.PAGAMENTO_DEBITO))
			receberPagamento(transacao);

	}

	protected void receberPagamento(Transacao pagamento) {
		try {
			Transacao transacao = new Transacao(EnumTransacao.PAGAMENTO_RECEBIDO, pagamento.getContaOrigem(), this,
					pagamento.getValor());
			this.saldo = this.saldo.add(transacao.getValor());
			this.extrato.registraTransacao(transacao);
		} catch (BOGPExceptions e) {
			e.msg();
		}
	}

	protected void receberPix(Transacao pix) {
		try {
			Transacao transacao = new Transacao(EnumTransacao.PIX_RECEBIDO, pix.getContaOrigem(), this, pix.getValor());
			this.saldo = this.saldo.add(transacao.getValor());
			this.extrato.registraTransacao(transacao);
		} catch (BOGPExceptions e) {
			e.msg();
		}

	}

	protected void checaSenha(String senha) {
		if (!senha.equals(this.senha)) {
			throw new SenhaInvalidaException("\nSenha inválida.");
		}
	}

	protected void checaEntradaSaldo(BigDecimal bdDeposito) {
		if (bdDeposito.compareTo(new BigDecimal("0")) <= 0) {
			throw new TransacaoInvalidaException("\nSó é possível adicionar ao saldo valores positivos.");
		}
	}

	protected void checaSaidaSaldo(BigDecimal bdSaque) {
		if (bdSaque.compareTo(new BigDecimal("0")) <= 0) {
			throw new TransacaoInvalidaException("\nSó é possível subtrair do saldo valores positivos.");
		}
	}

	protected void checaSaldoDisponivel(BigDecimal bdSaque, BigDecimal saldo) {
		if (saldo.compareTo(bdSaque) < 0) {
			throw new TransacaoInvalidaException("\nO saldo atual não é suficiente para efetuar essa transação.");
		}
	}

	private void checaCadastroPix(String chavePix) {
		if (DataBase.getMapaContasPix().containsKey(chavePix))
			throw new DataBaseException("Essa chave pix já está em uso");
	}

	private void checaChavePix(String chavePix) {
		if (DataBase.getContaByChavePix(chavePix) == null)
			throw new DataBaseException("Não há conta associada a essa chave pix.");

	}

	public Cliente getCliente() {
		return cliente;
	}

	public Long getNumeroDaConta() {
		return numeroDaConta;
	}

	public String getAgencia() {
		return agencia;
	}

	public EnumConta getTipoDeConta() {
		return tipoDeConta;
	}

	@Override
	public String toString() {
		return "\n" + this.tipoDeConta.getDescricao() + 
				"\nNº: " + this.numeroDaConta + 
				"\nAGENCIA: " + this.agencia + 
				"\nTITULAR: " + this.cliente.getNome();
	}
}
