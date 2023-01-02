package model;

import java.math.BigDecimal;

import exceptions.DataBaseException;
import interfaces.BOGPExceptions;
import interfaces.Rentavel;
import services.DataBase;
import services.Formatadores;

/**
 * Conta poupança, tem um método especifico para válidar a criação de uma nova
 * conta poupança e implementa a interface Rentavel.
 * 
 * @author Olivier Gomes Pironi
 *
 */
public class ContaPoupanca extends Conta implements Rentavel {

	private ContaPoupanca(Cliente cliente, String senha) {
		super(cliente, senha);

	}

	public static void criarContaPoupanca(Cliente cliente, String senha) {
		try {
			checaCPByCpf(cliente);
			ContaPoupanca conta = new ContaPoupanca(cliente, senha);
			DataBase.getListaDeContas().add(conta);
		} catch (RuntimeException e){
			((BOGPExceptions) e).msg();
		}
	}

	private static void checaCPByCpf(Cliente cliente) {
		if (DataBase.getListaDeContas().contains(DataBase.getCPByCpf(cliente.getCpf()))) {
			throw new DataBaseException("\nO cliente já é cadastrado no Banco OGP");
		}
	}

	@Override
	public void executarRendimentos() {
		BigDecimal rendimentos = this.saldo.multiply(this.getCliente().getCategoria().getRendimentoPoupanca());
		this.saldo = this.saldo.add(rendimentos);
		this.listaMovimentacoes.add("\nRendimento Poupança................. + R$" + Formatadores.arredonda(rendimentos)
				+ "\n" + Formatadores.dataEHora);
	}

}
