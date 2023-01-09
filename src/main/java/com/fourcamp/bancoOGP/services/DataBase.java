package com.fourcamp.bancoOGP.services;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.fourcamp.bancoOGP.model.Cartao;
import com.fourcamp.bancoOGP.model.CartaoCredito;
import com.fourcamp.bancoOGP.model.CartaoDebito;
import com.fourcamp.bancoOGP.model.Cliente;
import com.fourcamp.bancoOGP.model.Conta;
import com.fourcamp.bancoOGP.model.ContaCorrente;
import com.fourcamp.bancoOGP.model.ContaPoupanca;

public class DataBase {
	private static Collection<Conta> listaDeContas = new HashSet<>();
	private static Collection<Cliente> listaDeClientes = new HashSet<>();
	private static Collection<Cartao> listaDeCartoes = new HashSet<>();
	private static Map<String, Conta> mapaContasPix = new HashMap<>();

	private DataBase() {
	}

	
	public static Collection<Cartao> getListaDeCartoes() {
		return listaDeCartoes;
	}
	
	public static CartaoDebito getCartaoDebitoByCC(ContaCorrente conta) {

		return (CartaoDebito) listaDeCartoes.stream()
				.filter(c -> c instanceof CartaoDebito && c.getContaAssociada().getCliente().getCpf().equals(conta.getCliente().getCpf())).findFirst()
				.orElse(null);

	}
	
	public static CartaoCredito getCartaoCreditoByCC(ContaCorrente conta) {

		return (CartaoCredito) listaDeCartoes.stream()
				.filter(c -> c instanceof CartaoCredito && c.getContaAssociada().getCliente().getCpf().equals(conta.getCliente().getCpf())).findFirst()
				.orElse(null);

	}


	public static ContaPoupanca getCPByCpf(String clienteCPF) {

		return (ContaPoupanca) listaDeContas.stream()
				.filter(c -> c instanceof ContaPoupanca && c.getCliente().getCpf().equals(clienteCPF)).findFirst()
				.orElse(null);

	}

	public static ContaCorrente getCCByCpf(String clienteCPF) {

		return (ContaCorrente) listaDeContas.stream()
				.filter(c -> c instanceof ContaCorrente && c.getCliente().getCpf().equals(clienteCPF)).findFirst()
				.orElse(null);

	}

	public static Cliente getClienteByCpf(String clienteCPF) {
		return listaDeClientes.stream().filter(c -> c.getCpf().equals(clienteCPF)).findFirst().orElse(null);
	}

	public static Collection<Conta> getListaDeContas() {
		return listaDeContas;
	}

	public static Collection<Cliente> getListaDeClientes() {
		return listaDeClientes;
	}

	public static void cadastrarChavePix(String chavePix, Conta c) {
		mapaContasPix.put(chavePix, c);
	}

	public static Conta getContaByChavePix(String chavePix) {
		return mapaContasPix.get(chavePix);

	}

	public static Map<String, Conta> getMapaContasPix() {
		return mapaContasPix;
	}

}
