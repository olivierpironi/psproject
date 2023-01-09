package com.fourcamp.bancoOGP;

import com.fourcamp.bancoOGP.enums.EnumCliente;
import com.fourcamp.bancoOGP.enums.EnumSeguroCredito;
import com.fourcamp.bancoOGP.model.Cliente;
import com.fourcamp.bancoOGP.model.ContaCorrente;
import com.fourcamp.bancoOGP.model.Sistema;
import com.fourcamp.bancoOGP.services.DataBase;

public class App {

	public static void main(String[] args) {
		Sistema.temporizador();

		Cliente.criarCliente("João", "111", "20/05/2020", "endereço", EnumCliente.PREMIUM);
		Cliente.criarCliente("Olivier", "222", "20/05/2020", "endereço", EnumCliente.PREMIUM);

		ContaCorrente.criarContaCorrente(DataBase.getClienteByCpf("111"), "123");
		ContaCorrente.criarContaCorrente(DataBase.getClienteByCpf("222"), "123");

		ContaCorrente ccJoao = DataBase.getCCByCpf("111");
		ContaCorrente ccOlivier = DataBase.getCCByCpf("222");
		ccJoao.emitirCartaoDebito(456, "123");
		ccJoao.getCartaoDeDebito().setBloqueado(false, 456);
		
		ccJoao.efetuarDeposito("500", "123");
		
		ccJoao.getCartaoDeDebito().pagarDebito(ccOlivier, "100", 456);
		
		ccJoao.exibirExtrato("123");
		ccOlivier.exibirExtrato("123");

		ccJoao.emitirCartaoCredito(456, EnumSeguroCredito.CREDITO_SEGURO, "123");
		ccJoao.getCartaoDeCredito().setBloqueado(false, 456);
		ccJoao.getCartaoDeCredito().pagarComCredito(ccOlivier, "150", 456);
		ccJoao.efetuarDeposito("500", "123");
		ccJoao.getCartaoDeCredito().pagarFaturaComSaldo("150.30", 456);
		ccJoao.getCartaoDeCredito().exibirFatura(456);
		ccJoao.exibirExtrato("123");
		ccOlivier.exibirExtrato("123");
	}

}
