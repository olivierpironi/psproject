package services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import enums.Categoria;
import interfaces.Rentavel;
import model.Cliente;
import model.Conta;
import model.ContaPoupanca;

public class DataBase {
	private static Collection<Conta> listaDeContas = new ArrayList<>();
	private static Collection<Cliente> listaDeClientes = new ArrayList<>();

	private DataBase() {
	}

	public static ContaPoupanca getCPByCpf(String clienteCPF) {
		for (Conta conta : getListaDeContas()) {

			if (conta instanceof ContaPoupanca && conta.getCliente().getCpf().equals(clienteCPF)) {
				return (ContaPoupanca) conta;
			}
		}
		return null;
	}

	public static Cliente getClienteByCpf(String clienteCPF) {
		for (Cliente c : listaDeClientes) {

			if (c.getCpf().equals(clienteCPF)) {
				return c;
			}
		}
		return null;
	}

	public static Collection<Conta> getListaDeContas() {
		return listaDeContas;
	}

	public static void setListaDeContas(List<Conta> listaDeContas) {
		DataBase.listaDeContas = listaDeContas;
	}

	public static Collection<Cliente> getListaDeClientes() {
		return listaDeClientes;
	}

	public static void setListaDeClientes(List<Cliente> listaDeClientes) {
		DataBase.listaDeClientes = listaDeClientes;
	}

	public static void starter() {
		Cliente.criarCliente("Olivier", "11203406673", "01/03/1999", "Algum lugar", Categoria.PREMIUM);
		Cliente.criarCliente("Olivier", "11203406673", "01/03/1999", "Algum lugar", Categoria.PREMIUM);

		ContaPoupanca.criarContaPoupanca(DataBase.getClienteByCpf("11203406673"), "123");
		ContaPoupanca.criarContaPoupanca(DataBase.getClienteByCpf("11203406673"), "123");
		getCPByCpf("11203406673");
		temporizador();

	}

	private static void temporizador() {
		int intervalo = 1000 * 10; // intervalo de 10 seg a cada execução.
		int delay = 1000 * 20; // delay inicial de 20 seg.
		Timer timer = new Timer();

		timer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				for (Conta conta : getListaDeContas()) {
					if (conta instanceof Rentavel) {

						((Rentavel) conta).executarRendimentos();
						conta.exibirExtrato("123");
					}
				}
			}
		}, delay, intervalo);

	}
}
