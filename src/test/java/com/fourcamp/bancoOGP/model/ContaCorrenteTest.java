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
import com.fourcamp.bancoOGP.enums.EnumSeguroCredito;
import com.fourcamp.bancoOGP.services.DataBase;

class ContaCorrenteTest {

	public static Cliente joao;
	public static Cliente maria;
	public static ContaCorrente ccJoao;
	public static ContaCorrente ccMaria;

	@BeforeAll
	public static void criaCC() {
		Cliente.criarCliente("Joao", "123456789", "01/01/2022", "aaa", EnumCliente.COMUM);
		Cliente.criarCliente("Maria", "789456123", "01/01/2022", "aaa", EnumCliente.COMUM);
		joao = DataBase.getClienteByCpf("123456789");
		maria = DataBase.getClienteByCpf("789456123");
		ContaCorrente.criarContaCorrente(joao, "123");
		ContaCorrente.criarContaCorrente(maria, "123");
		ccJoao = DataBase.getCCByCpf("123456789");
		ccMaria = DataBase.getCCByCpf("789456123");
		ccJoao.cadastrarChavePix("chave1", "123");
		ccMaria.cadastrarChavePix("chave2", "123");
	}

	@BeforeEach
	public void saldoInicial() {
		ccJoao.saldo = new BigDecimal("0");
		ccMaria.saldo = new BigDecimal("500");
	}

	@Test
	void envioERecebimentoPix() {
		// setup
		String valorDoPix = "100";
		BigDecimal saldoDaMariaAposOPix = ccMaria.saldo.subtract(new BigDecimal(valorDoPix));
		BigDecimal saldoDoJoaoAposOPix = ccJoao.saldo.add(new BigDecimal(valorDoPix));

		// ação
		ccMaria.enviarPix("chave1", valorDoPix, "123");

		// verificação

		assertEquals(saldoDaMariaAposOPix, ccMaria.saldo);
		assertEquals(saldoDoJoaoAposOPix, ccJoao.saldo);
	}

	@Test
	void envioERecebimentoPixSenhaIncorreta() {
		// setup
		String valorDoPix = "100";
		BigDecimal saldoDaMariaAposOPix = ccMaria.saldo.subtract(new BigDecimal(0));
		BigDecimal saldoDoJoaoAposOPix = ccJoao.saldo.add(new BigDecimal(0));

		// ação
		ccMaria.enviarPix("chave1", valorDoPix, "1235");

		// verificação

		assertEquals(saldoDaMariaAposOPix, ccMaria.saldo);
		assertEquals(saldoDoJoaoAposOPix, ccJoao.saldo);
	}

	@ParameterizedTest
	@ValueSource(strings = { "-100", "0" })
	void envioERecebimentoPixInvalido(String valor) {
		// setup
		String valorDoPix = valor;
		BigDecimal saldoDaMariaAposOPix = ccMaria.saldo.subtract(new BigDecimal(0));
		BigDecimal saldoDoJoaoAposOPix = ccJoao.saldo.add(new BigDecimal(0));

		// ação
		ccMaria.enviarPix("chave1", valorDoPix, "123");

		// verificação

		assertEquals(saldoDaMariaAposOPix, ccMaria.saldo);
		assertEquals(saldoDoJoaoAposOPix, ccJoao.saldo);
	}

	@Test
	void tarifaContaCorrente() {
		// setup
		BigDecimal Tarifa = ccMaria.saldo.multiply(maria.getCategoria().getTaxaContaCorrente());
		BigDecimal valorAposTarifa = ccMaria.saldo.subtract(Tarifa);
		ccMaria.ultimaCobranca = LocalDate.now().minusDays(30);

		// ação
		ccMaria.execRendimentosETaxas();

		// verificação
		assertEquals(valorAposTarifa, ccMaria.saldo);
	}

	@Test
	void criacaoCCComClienteQueJáTemConta() {
		// setup

		// ação
		ContaCorrente.criarContaCorrente(joao, "abc");
		ContaCorrente.criarContaCorrente(joao, "abc");
		long quantidadesDeContasCorrenteQueOClienteTem = (DataBase.getListaDeContas().stream()
				.filter(c -> c instanceof ContaCorrente && c.getCliente().equals(joao))).count();

		// verificação
		assertEquals(1, quantidadesDeContasCorrenteQueOClienteTem);
	}

	@Test
	void emitirCartaoCreditoSenhaErrada() {
		// setup

		// ação
		ccJoao.emitirCartaoCredito(456, EnumSeguroCredito.CREDITO_SEGURO, "1234");

		// verificação
		assertEquals(null, ccJoao.getCartaoDeCredito());
	}

	@Test
	void emitirCartaoDebitoSenhaErrada() {
		// setup

		// ação
		ccJoao.emitirCartaoDebito(456, "1234");

		// verificação
		assertEquals(null, ccJoao.getCartaoDeDebito());
	}

}
