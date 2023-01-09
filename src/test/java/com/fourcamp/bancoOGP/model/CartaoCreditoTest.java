package com.fourcamp.bancoOGP.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

public class CartaoCreditoTest {

	public static Cliente joao;
	public static Cliente maria;
	public static ContaCorrente ccJoao;
	public static ContaCorrente ccMaria;
	public static CartaoCredito cartaoMaria;

	@BeforeAll
	public static void criaCartaoCredito() {
		Cliente.criarCliente("Joao", "123456789", "01/01/2022", "aaa", EnumCliente.COMUM);
		Cliente.criarCliente("Maria", "789456123", "01/01/2022", "aaa", EnumCliente.COMUM);
		joao = DataBase.getClienteByCpf("123456789");
		maria = DataBase.getClienteByCpf("789456123");
		ContaCorrente.criarContaCorrente(joao, "123");
		ContaCorrente.criarContaCorrente(maria, "123");
		ccJoao = DataBase.getCCByCpf("123456789");
		ccMaria = DataBase.getCCByCpf("789456123");
		ccMaria.emitirCartaoCredito(456, EnumSeguroCredito.CREDITO_SEGURO, "123");
		cartaoMaria = DataBase.getCartaoCreditoByCC(ccMaria);
		cartaoMaria.setBloqueado(false, 456);

	}

	@BeforeEach
	public void saldoInicial() {
		ccJoao.saldo = new BigDecimal("0");
		cartaoMaria.fatura.setValorFatura(new BigDecimal("0"));
	}

	@Test
	void pagamentoCredito() {
		// setup
		String pagamento = "150";
		BigDecimal bdPagamento = new BigDecimal(pagamento);
		BigDecimal taxaDeOperacao = cartaoMaria.calculaTaxaOperacao(bdPagamento);

		// ação
		cartaoMaria.pagarComCredito(ccJoao, pagamento, 456);

		// verificação
		assertEquals(bdPagamento.add(taxaDeOperacao), cartaoMaria.fatura.getValorFatura());
		assertEquals(bdPagamento, ccJoao.saldo);
	}

	@ParameterizedTest
	@ValueSource(strings = { "-100", "0" })
	void pagamentoCreditoInvalido(String valor) {
		// setup
		String pagamento = valor;
		BigDecimal saldoJoaoApósPagamento = new BigDecimal(0);
		BigDecimal faturaCartaoMariaAposPagamento = new BigDecimal(0);

		// ação
		cartaoMaria.pagarComCredito(ccJoao, pagamento, 456);

		// verificação
		assertEquals(faturaCartaoMariaAposPagamento, cartaoMaria.fatura.getValorFatura());
		assertEquals(saldoJoaoApósPagamento, ccJoao.saldo);
	}

	@Test
	void pagamentoCreditoSenhaIncorreta() {
		// setup
		String pagamento = "150";
		BigDecimal saldoJoaoApósPagamento = new BigDecimal(0);
		BigDecimal faturaCartaoMariaAposPagamento = new BigDecimal(0);

		// ação
		cartaoMaria.pagarComCredito(ccJoao, pagamento, 48956);

		// verificação
		assertEquals(faturaCartaoMariaAposPagamento, cartaoMaria.fatura.getValorFatura());
		assertEquals(saldoJoaoApósPagamento, ccJoao.saldo);
	}

	@Test
	void pagamentoCreditoCartaoBloqueado() {
		// setup
		String pagamento = "150";
		cartaoMaria.setBloqueado(true, 456);
		BigDecimal saldoJoaoApósPagamento = new BigDecimal(0);
		BigDecimal faturaCartaoMariaAposPagamento = new BigDecimal(0);

		// ação
		cartaoMaria.pagarComCredito(ccJoao, pagamento, 48956);

		// verificação
		assertEquals(faturaCartaoMariaAposPagamento, cartaoMaria.fatura.getValorFatura());
		assertEquals(saldoJoaoApósPagamento, ccJoao.saldo);
	}

	@Test
	void pagamentoCreditoSemLimite() {
		// setup
		String pagamento = "999999999999";
		BigDecimal saldoJoaoApósPagamento = new BigDecimal(0);
		BigDecimal faturaCartaoMariaAposPagamento = new BigDecimal(0);

		// ação
		cartaoMaria.pagarComCredito(ccJoao, pagamento, 48956);

		// verificação
		assertEquals(faturaCartaoMariaAposPagamento, cartaoMaria.fatura.getValorFatura());
		assertEquals(saldoJoaoApósPagamento, ccJoao.saldo);
	}

	@Test
	void pagarFatura() {
		// setup
		String pagamento = "100";
		BigDecimal bdPagamento = new BigDecimal(pagamento);
		BigDecimal taxaOperacao = cartaoMaria.calculaTaxaOperacao(bdPagamento);
		BigDecimal valorASerPagoNaFatura = bdPagamento.add(taxaOperacao);
		BigDecimal valorEsperadoNaFatura = new BigDecimal("0.0000");
		ccMaria.saldo = new BigDecimal("200");

		// ação
		cartaoMaria.pagarComCredito(ccJoao, pagamento, 456);
		cartaoMaria.pagarFaturaComSaldo(valorASerPagoNaFatura.toString(), 456);

		// verificação
		assertEquals(valorEsperadoNaFatura, cartaoMaria.fatura.getValorFatura());
		assertEquals(bdPagamento, ccJoao.saldo);
	}

	@Test
	void mensalidadeSeguro() {
		// setup
		cartaoMaria.ultimoPagamentoSeguro = LocalDate.now().minusDays(30);
		BigDecimal valorDoSeguro = cartaoMaria.getContratoSeguro().getSeguradora().getValor();

		// ações
		cartaoMaria.execMensalidadeSeguro();

		// verificação
		assertEquals(valorDoSeguro, cartaoMaria.fatura.getValorFatura());

	}

}
