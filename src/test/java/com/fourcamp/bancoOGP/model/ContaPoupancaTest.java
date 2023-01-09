package com.fourcamp.bancoOGP.model;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.fourcamp.bancoOGP.enums.EnumCliente;
import com.fourcamp.bancoOGP.services.DataBase;

class ContaPoupancaTest {
	public static Cliente joao;
	public static Cliente maria;
	public static ContaPoupanca cpJoao;
	public static ContaPoupanca cpMaria;

	@BeforeAll
	public static void criaCP() {
		Cliente.criarCliente("Joao", "123456789", "01/01/2022", "aaa", EnumCliente.COMUM);
		Cliente.criarCliente("Maria", "789456123", "01/01/2022", "aaa", EnumCliente.COMUM);
		joao = DataBase.getClienteByCpf("123456789");
		maria = DataBase.getClienteByCpf("789456123");
		ContaPoupanca.criarContaPoupanca(joao, "123");
		ContaPoupanca.criarContaPoupanca(maria, "123");
		cpJoao = DataBase.getCPByCpf("123456789");
		cpMaria = DataBase.getCPByCpf("789456123");
	}

	@BeforeEach
	public void saldoInicial() {
		cpJoao.saldo = new BigDecimal("0");
		cpMaria.saldo = new BigDecimal("500");
	}

	@Test
	void deposito() {
		// setup
		String valorDoDeposito = "100";
		BigDecimal saldoAposDeposito = cpJoao.saldo.add(new BigDecimal(valorDoDeposito));

		// ação
		cpJoao.efetuarDeposito(valorDoDeposito, "123");

		// verificação
		assertEquals(saldoAposDeposito, cpJoao.saldo);
	}

	@Test
	void depositoSenhaIncorreta() {
		// setup
		String valorDoDeposito = "100";
		BigDecimal saldoAposDeposito = cpJoao.saldo.add(new BigDecimal(0));

		// ação
		cpJoao.efetuarDeposito(valorDoDeposito, "1253");

		// verificação
		assertEquals(saldoAposDeposito, cpJoao.saldo);
	}

	@ParameterizedTest
	@ValueSource(strings = { "-100", "0" })
	void depositoInvalido(String valor) {
		// setup
		String valorDoDeposito = valor;
		BigDecimal saldoAposDeposito = cpJoao.saldo.add(new BigDecimal(0));

		// ação
		cpJoao.efetuarDeposito(valorDoDeposito, "123");

		// verificação
		assertEquals(saldoAposDeposito, cpJoao.saldo);
	}

	@Test
	void saque() {
		// setup
		String valorASerSacado = "100";
		BigDecimal saldoAposSaque = cpMaria.saldo.subtract(new BigDecimal(valorASerSacado));

		// ação
		cpMaria.efetuarSaque(valorASerSacado, "123");

		// verificação

		assertEquals(saldoAposSaque, cpMaria.saldo);
	}

	@Test
	void saqueSenhaIncorreta() {
		// setup
		String valorASerSacado = "100";
		BigDecimal saldoAposSaque = cpMaria.saldo.subtract(new BigDecimal(0));

		// ação
		cpMaria.efetuarSaque(valorASerSacado, "1523");

		// verificação

		assertEquals(saldoAposSaque, cpMaria.saldo);
	}

	@ParameterizedTest
	@ValueSource(strings = { "-100", "0" })
	void saqueInvalido(String valor) {
		// setup
		String valorASerSacado = valor;
		BigDecimal saldoAposSaque = cpMaria.saldo.subtract(new BigDecimal(0));

		// ação
		cpMaria.efetuarSaque(valorASerSacado, "123");

		// verificação

		assertEquals(saldoAposSaque, cpMaria.saldo);
	}

	@Test
	void envioERecebimentoPix() {
		// setup
		String valorDoPix = "100";
		BigDecimal saldoDaMariaAposOPix = cpMaria.saldo.subtract(new BigDecimal(valorDoPix));
		BigDecimal saldoDoJoaoAposOPix = cpJoao.saldo.add(new BigDecimal(valorDoPix));
		cpJoao.cadastrarChavePix("chavao", "123");

		// ação
		cpMaria.enviarPix("chavao", valorDoPix, "123");

		// verificação

		assertEquals(saldoDaMariaAposOPix, cpMaria.saldo);
		assertEquals(saldoDoJoaoAposOPix, cpJoao.saldo);
	}

	@Test
	void envioERecebimentoPixSenhaIncorreta() {
		// setup
		String valorDoPix = "100";
		BigDecimal saldoDaMariaAposOPix = cpMaria.saldo.subtract(new BigDecimal(0));
		BigDecimal saldoDoJoaoAposOPix = cpJoao.saldo.add(new BigDecimal(0));

		// ação
		cpMaria.enviarPix("chave1", valorDoPix, "1235");

		// verificação

		assertEquals(saldoDaMariaAposOPix, cpMaria.saldo);
		assertEquals(saldoDoJoaoAposOPix, cpJoao.saldo);
	}

	@ParameterizedTest
	@ValueSource(strings = { "-100", "0" })
	void envioERecebimentoPixInvalido(String valor) {
		// setup
		String valorDoPix = valor;
		BigDecimal saldoDaMariaAposOPix = cpMaria.saldo.subtract(new BigDecimal(0));
		BigDecimal saldoDoJoaoAposOPix = cpJoao.saldo.add(new BigDecimal(0));

		// ação
		cpMaria.enviarPix("chave1", valorDoPix, "123");

		// verificação

		assertEquals(saldoDaMariaAposOPix, cpMaria.saldo);
		assertEquals(saldoDoJoaoAposOPix, cpJoao.saldo);
	}

	@Test
	void rendimentosDaPoupanca() {
		// setup
		cpJoao.saldo = new BigDecimal("100");
		BigDecimal Rendimentos = cpJoao.saldo.multiply(joao.getCategoria().getRendimentoPoupanca());
		BigDecimal valorAposRendimentos = cpJoao.saldo.add(Rendimentos);
		cpJoao.ultimoRendimento = LocalDate.now().minusDays(30);

		// ação
		cpJoao.execRendimentosETaxas();

		// verificação
		assertEquals(valorAposRendimentos, cpJoao.saldo);

	}

	@Test
	void criacaoCPComClienteQueJáTemConta() {
		// setup

		// ação
		ContaPoupanca.criarContaPoupanca(joao, "abc");
		ContaPoupanca.criarContaPoupanca(joao, "abc");
		long quantidadesDeContasPoupancaQueOClienteTem = (DataBase.getListaDeContas().stream()
				.filter(c -> c instanceof ContaPoupanca && c.getCliente().equals(joao))).count();

		// verificação
		assertEquals(1, quantidadesDeContasPoupancaQueOClienteTem);
	}

}
