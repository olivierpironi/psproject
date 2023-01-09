package com.fourcamp.bancoOGP.model;

import java.util.ArrayList;
import java.util.Collection;

import com.fourcamp.bancoOGP.services.Formatadores;

/**
 * Toda conta possui um extrato bancário responsável por registrar e emitir todo
 * histórico de transações e saldo de uma conta.
 * 
 * @author Olivier Gomes Pironi
 */
public class ExtratoBancario {
	private Conta contaMae;
	private Collection<Transacao> historicoTransacoes = new ArrayList<>();

	protected ExtratoBancario(Conta contaMae) {
		this.contaMae = contaMae;
	}

	protected void registraTransacao(Transacao transacao) {
		historicoTransacoes.add(transacao);
	}
	
	protected void registraTransacao(Transacao transacao, Transacao taxaOperacao) {
		historicoTransacoes.add(transacao);
		historicoTransacoes.add(taxaOperacao);
	}

	protected void exibe() {
		System.out.println("\n***************EXTRATO BANCÁRIO***************" + "\n                  BANCO OGP");
		System.out.println(this.contaMae);
		historicoTransacoes.stream().forEach(System.out::println);
		System.out.println("\nSALDO............................ + R$" + Formatadores.arredonda(contaMae.saldo));
		System.out.println("**********************************************");

	}

}
