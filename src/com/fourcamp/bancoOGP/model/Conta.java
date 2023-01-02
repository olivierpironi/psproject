package model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import exceptions.SenhaInvalidaException;
import exceptions.TransacaoInvalidaException;
import interfaces.BOGPExceptions;
import services.Formatadores;

/**
 * Essa classe abstrata é a classe mãe de qualquer conta do BANCO OGP, conta com
 * as funções saque, depósito e exibir saldo, além da possibilidade de settar o
 * titular da conta e a senha da conta.
 * 
 * Há várias formas de validar o valor que está sendo sacado e depositado, optei
 * por fazer isso utilizando um sistema de excecoes.
 * 
 * @author Olivier Gomes Pironi
 */
public abstract class Conta {
	private static long somaContas = 0L;
	private Long numeroDaConta;
	private String agencia = "0001";
	private Cliente cliente;
	protected BigDecimal saldo = new BigDecimal("0");
	private String senha;
	protected List<String> listaMovimentacoes = new ArrayList<>();

	protected Conta(Cliente cliente, String senha) {
		somaContas++;
		this.numeroDaConta = 100000000000L + Conta.somaContas;
		this.cliente = cliente;
		this.senha = senha;
	}

	public void efetuarSaque(String saque, String senha) {
		BigDecimal bdSaque = new BigDecimal(saque);
		try {
			checaSenha(senha);
			checaSaquePositivo(bdSaque);
			checaSaldoSaque(bdSaque, this.saldo);
			this.saldo = this.saldo.subtract(new BigDecimal(saque));
			this.listaMovimentacoes.add("\nSaque............................ - R$" + Formatadores.arredonda(bdSaque)
					+ "\n" + Formatadores.dataEHora);
		} catch (RuntimeException e){
			((BOGPExceptions) e).msg();
		}
	}

	public void efetuarDeposito(String deposito, String senha) {
		BigDecimal bdDeposito = new BigDecimal(deposito);
		try {
			checaSenha(senha);
			checaDepositoPositivo(bdDeposito);
			this.saldo = this.saldo.add(bdDeposito);
			this.listaMovimentacoes.add("\nDepósito........................ + R$ " + Formatadores.arredonda(bdDeposito)
					+ "\n" + Formatadores.dataEHora);
		} catch (RuntimeException e){
			((BOGPExceptions) e).msg();
		}
	}

	public void trocarSenha(String novaSenha, String senhaAntiga) {
		try {
			checaSenha(senhaAntiga);
			this.senha = novaSenha;
		} catch (RuntimeException e){
			((BOGPExceptions) e).msg();
		}
	}
  
	public void exibirExtrato(String senha) {
		try {
			checaSenha(senha);
		System.out.println("\n***************EXTRATO BANCÁRIO***************" + "\n                  BANCO OGP");
		System.out.println(this);
		listaMovimentacoes.forEach(System.out::println);
		System.out.println("\nSALDO............................ + R$" + Formatadores.arredonda(this.saldo));
		System.out.println("**********************************************");
		} catch (RuntimeException e){
			((BOGPExceptions) e).msg();
		}
	}
	

	private void checaSenha(String senha) {
		if(!senha.equals(this.senha)) {
			throw new SenhaInvalidaException ("\nSenha inválida.");
		}
	}

	private void checaDepositoPositivo(BigDecimal bdDeposito) {
		if (bdDeposito.compareTo(new BigDecimal("0")) <= 0) {
			throw new TransacaoInvalidaException("\nSó é possível depositar valores positivos.");
		}
	}

	private void checaSaquePositivo(BigDecimal bdSaque) {
		if (bdSaque.compareTo(new BigDecimal("0")) <= 0) {
			throw new TransacaoInvalidaException("\nSó é possível sacar valores positivos.");
		}
	}

	private void checaSaldoSaque(BigDecimal bdSaque, BigDecimal saldo) {
		if (saldo.compareTo(bdSaque) < 0) {
			throw new TransacaoInvalidaException("\nSó é possível sacar valores menores que seu saldo.");
		}
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

	@Override
	public String toString() {
		return "\nCONTA: " + this.numeroDaConta + "\nAGENCIA: " + this.agencia + "\nTITULAR: " + this.cliente.getNome();
	}
}
