package com.fourcamp.sbanco.util;

import com.fourcamp.sbanco.domain.dto.cartao.AtualizarCartao;
import com.fourcamp.sbanco.domain.dto.cartao.NumeroESenhaCartao;
import com.fourcamp.sbanco.domain.dto.cartaocredito.AtualizarCartaoCredito;
import com.fourcamp.sbanco.domain.dto.cartaocredito.CadastroCartaoCredito;
import com.fourcamp.sbanco.domain.dto.cartaocredito.CartaoCreditoDTO;
import com.fourcamp.sbanco.domain.dto.cartaocredito.DetalhaCartaoCredito;
import com.fourcamp.sbanco.domain.dto.cartaodebito.CadastroCartaoDebito;
import com.fourcamp.sbanco.domain.dto.cartaodebito.CartaoDebitoDTO;
import com.fourcamp.sbanco.domain.dto.cartaodebito.DetalhaCartaoDebito;
import com.fourcamp.sbanco.domain.dto.cliente.AtualizarCliente;
import com.fourcamp.sbanco.domain.dto.cliente.CadastroCliente;
import com.fourcamp.sbanco.domain.dto.cliente.ClienteDTO;
import com.fourcamp.sbanco.domain.dto.cliente.DetalhaCliente;
import com.fourcamp.sbanco.domain.dto.conta.AtualizarConta;
import com.fourcamp.sbanco.domain.dto.conta.CadastroConta;
import com.fourcamp.sbanco.domain.dto.conta.DetalhaConta;
import com.fourcamp.sbanco.domain.dto.conta.NumeroESenhaConta;
import com.fourcamp.sbanco.domain.dto.contacorrente.ContaCorrenteDTO;
import com.fourcamp.sbanco.domain.dto.contapoupanca.ContaPoupancaDTO;
import com.fourcamp.sbanco.domain.enums.EnumCliente;
import com.fourcamp.sbanco.domain.enums.EnumSeguroCredito;

public class ContaFactory {
	//cliente
	public CadastroCliente cadastroCliente;
	public ClienteDTO cliente;
	public DetalhaCliente detalhamentoCliente;
	public AtualizarCliente atualizarCliente;
	//conta
	public CadastroConta cadastroConta;
	public AtualizarConta dadosAtualizarConta;
	public ContaPoupancaDTO cp;
	public ContaCorrenteDTO cc;
	public DetalhaConta detalhamentoConta;
	public NumeroESenhaConta numeroESenhaConta;
	//cartao
	public CadastroCartaoDebito cadastroCartaoDebito;
	public CadastroCartaoCredito cadastroCartaoCredito;
	public CartaoDebitoDTO cartaoDebito;
	public CartaoCreditoDTO cartaoCredito;
	public AtualizarCartao atualizarCartao;
	public AtualizarCartaoCredito atualizarCartaoCredito;
	public DetalhaCartaoDebito detalhamentoCartaoDebito;
	public DetalhaCartaoCredito detalhamentoCartaoCredito;
	public NumeroESenhaCartao numeroESenhaCartaoCredito;
	
	public ContaFactory fabricarC1() {
		cadastroCliente = new CadastroCliente("Olivier", "123456789", "01/01/1900", "end", EnumCliente.COMUM);
		cliente = new ClienteDTO(cadastroCliente);
		detalhamentoCliente = new DetalhaCliente(cliente);
		atualizarCliente = new AtualizarCliente("Ana", "123", "02/02/1900", "endereco", EnumCliente.PREMIUM);
		cp = new ContaPoupancaDTO(cliente, "123");
		cp.setNumeroDaConta(0l);
		cc = new ContaCorrenteDTO(cliente, "123");
		cadastroConta = new CadastroConta("123456789", "123");
		dadosAtualizarConta = new AtualizarConta(cp.getNumeroDaConta(), "123", "456");
		detalhamentoConta = new DetalhaConta(cp);
		numeroESenhaConta = new NumeroESenhaConta(cp.getNumeroDaConta(), "123");
		cadastroCartaoDebito = new CadastroCartaoDebito(cc.getNumeroDaConta(), "123", 456);
		cartaoDebito = new CartaoDebitoDTO(cc, 456);
		detalhamentoCartaoDebito = new DetalhaCartaoDebito(cartaoDebito);
		cartaoCredito = new CartaoCreditoDTO(cc, EnumSeguroCredito.CREDITO_SEGURO ,456);
		cadastroCartaoCredito = new CadastroCartaoCredito(cc.getNumeroDaConta(), 456, EnumSeguroCredito.CREDITO_SEGURO, "123");
		detalhamentoCartaoCredito = new DetalhaCartaoCredito(cartaoCredito);
		atualizarCartao = new AtualizarCartao(cartaoDebito.getNumero(), 456, "5000");
		atualizarCartaoCredito = new AtualizarCartaoCredito(cartaoDebito.getNumero(), 456,EnumSeguroCredito.SEGURIDADE_TOTAL ,"5000");
		numeroESenhaCartaoCredito = new NumeroESenhaCartao(cartaoCredito.getNumero(), 456);
		return this;
	}
	public ContaFactory fabricarC2() {
		cadastroCliente = new CadastroCliente("Ana", "123", "02/02/1900", "endereco", EnumCliente.PREMIUM);
		cliente = new ClienteDTO(cadastroCliente);
		detalhamentoCliente = new DetalhaCliente(cliente);
		atualizarCliente = new AtualizarCliente("Olivier", "123456789", "01/01/1900", "end", EnumCliente.COMUM);
		cp = new ContaPoupancaDTO(cliente, "123");
		cp.setNumeroDaConta(1l);
		cc = new ContaCorrenteDTO(cliente, "123");
		cadastroConta = new CadastroConta("123456789", "123");
		dadosAtualizarConta = new AtualizarConta(cp.getNumeroDaConta(), "123", "456");
		detalhamentoConta = new DetalhaConta(cc);
		numeroESenhaConta = new NumeroESenhaConta(cp.getNumeroDaConta(), "123");
		cadastroCartaoDebito = new CadastroCartaoDebito(cc.getNumeroDaConta(), "123", 456);
		cartaoDebito = new CartaoDebitoDTO(cc, 456);
		detalhamentoCartaoDebito = new DetalhaCartaoDebito(cartaoDebito);
		cartaoCredito = new CartaoCreditoDTO(cc, EnumSeguroCredito.CREDITO_SEGURO ,456);
		cadastroCartaoCredito = new CadastroCartaoCredito(cc.getNumeroDaConta(), 456, EnumSeguroCredito.CREDITO_SEGURO, "123");
		detalhamentoCartaoCredito = new DetalhaCartaoCredito(cartaoCredito);
		atualizarCartao = new AtualizarCartao(cartaoDebito.getNumero(), 456, "5000");
		atualizarCartaoCredito = new AtualizarCartaoCredito(cartaoDebito.getNumero(), 456,EnumSeguroCredito.SEGURIDADE_TOTAL ,"5000");
		numeroESenhaCartaoCredito = new NumeroESenhaCartao(cartaoCredito.getNumero(), 456);
		return this;
	}

}
