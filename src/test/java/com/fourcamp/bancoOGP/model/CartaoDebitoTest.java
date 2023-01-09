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

class CartaoDebitoTest {
	public static Cliente joao;
	public static Cliente maria;
	public static ContaCorrente ccJoao;
	public static ContaCorrente ccMaria;
	public static CartaoDebito cartaoMaria;

	@BeforeAll
	public static void criaCartaoDebito() {
		Cliente.criarCliente("Joao", "123456789", "01/01/2022", "aaa", EnumCliente.COMUM);
		Cliente.criarCliente("Maria", "789456123", "01/01/2022", "aaa", EnumCliente.COMUM);
		joao = DataBase.getClienteByCpf("123456789");
		maria = DataBase.getClienteByCpf("789456123");
		ContaCorrente.criarContaCorrente(joao, "123");
		ContaCorrente.criarContaCorrente(maria, "123");
		ccJoao = DataBase.getCCByCpf("123456789");
		ccMaria = DataBase.getCCByCpf("789456123");
		ccMaria.emitirCartaoDebito(456, "123");
		cartaoMaria = DataBase.getCartaoDebitoByCC(ccMaria);
		cartaoMaria.setBloqueado(false, 456);

	}

	@BeforeEach
	public void saldoInicial() {
		ccMaria.saldo = new BigDecimal("2000");
		ccJoao.saldo = new BigDecimal("0");
		cartaoMaria.setLimiteDiario("1000");
	}

	@Test
	void renovaLimiteDisponivel() {
		// setup
		cartaoMaria.ultimaRenovacaoLimiteDisponivel = LocalDate.now().minusDays(1);

		// ação
		cartaoMaria.renovaLimiteDisponivel();

		// verificação
		assertEquals(cartaoMaria.getLimiteDiario(), cartaoMaria.getLimiteDisponivel());
	}

	@Test
	void pagarComDebito() {
		// setup
		String pagamento = "100";
		BigDecimal bdPagamento = new BigDecimal(pagamento);
		BigDecimal bdTaxa = cartaoMaria.calculaTaxaOperacao(bdPagamento);
		BigDecimal saldoMariaAposPagamento = ccMaria.saldo.subtract(bdTaxa.add(bdPagamento));

		// ação
		cartaoMaria.pagarDebito(ccJoao, pagamento, 456);

		// verificação
		assertEquals(bdPagamento, ccJoao.saldo);
		assertEquals(saldoMariaAposPagamento, ccMaria.saldo);
	}

	@ParameterizedTest
	@ValueSource(strings = { "-100", "0" })
	void pagarComDebitoInvalido(String valor) {
		// setup
		String pagamento = valor;
		BigDecimal saldoJoaoAposPagamento = new BigDecimal(0);
		BigDecimal saldoMariaAposPagamento = ccMaria.saldo.subtract(new BigDecimal("0"));

		// ação
		cartaoMaria.pagarDebito(ccJoao, pagamento, 456);

		// verificação
		assertEquals(saldoJoaoAposPagamento, ccJoao.saldo);
		assertEquals(saldoMariaAposPagamento, ccMaria.saldo);
	}

	@Test
	void pagarComDebitoSemSaldo() {
		// setup
		ccMaria.saldo = new BigDecimal("0");
		BigDecimal saldoJoaoAposPagamento = new BigDecimal("0");

		// ação
		cartaoMaria.pagarDebito(ccJoao, "100", 456);

		// verificação

		assertEquals(saldoJoaoAposPagamento, ccJoao.saldo);
	}

	@Test
	void pagarComDebitoSemLimite() {
		// setup
		BigDecimal saldoMariaAposPagamento = ccMaria.saldo;
		BigDecimal saldoJoaoAposPagamento = ccJoao.saldo;

		// ação
		cartaoMaria.pagarDebito(ccJoao, "999999999", 456);

		assertEquals(saldoMariaAposPagamento, ccMaria.saldo);
		assertEquals(saldoJoaoAposPagamento, ccJoao.saldo);
	}

	@Test
	void pagarComDebitoSenhaIncorreta() {
		// setup
		String pagamento = "100";
		BigDecimal saldoMariaAposPagamento = ccMaria.saldo;
		BigDecimal saldoJoaoAposPagamento = ccJoao.saldo;

		// ação
		cartaoMaria.pagarDebito(ccJoao, pagamento, 48956);

		// verificação
		assertEquals(saldoMariaAposPagamento, ccMaria.saldo);
		assertEquals(saldoJoaoAposPagamento, ccJoao.saldo);
	}

}
