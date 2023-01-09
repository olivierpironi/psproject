package com.fourcamp.bancoOGP.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;

import com.fourcamp.bancoOGP.enums.EnumTransacao;
import com.fourcamp.bancoOGP.services.Formatadores;

/**
 * Todo cartão de crédito possui uma fatura responsável por registrar e emitir
 * todo histórico de pagamentos e limite disponível de um cartão de crédito.
 * 
 * @author Olivier Gomes Pironi
 */
public class Fatura {
	private CartaoCredito cartaoAssociado;
	private BigDecimal limiteDisponivel;
	private BigDecimal valorFatura;
	private Collection<Transacao> historicoTransacoes = new ArrayList<>();

	public Fatura(CartaoCredito cartaoAssociado) {
		this.cartaoAssociado = cartaoAssociado;
		this.limiteDisponivel = cartaoAssociado.getLimiteCredito();
		this.valorFatura = new BigDecimal("0");
	}

	protected void registraTransacao(Transacao transacao) {
		if (transacao.getEnumTransacao().equals(EnumTransacao.PAGAMENTO_CREDITO)
				|| transacao.getEnumTransacao().equals(EnumTransacao.MENSALIDADE_SEGURO)) {
			limiteDisponivel = limiteDisponivel.subtract(transacao.getValor());
			valorFatura = valorFatura.add(transacao.getValor());
			
		} else if (transacao.getEnumTransacao().equals(EnumTransacao.PAGAMENTO_FATURA)) {
			limiteDisponivel = limiteDisponivel.add(transacao.getValor());
			valorFatura = valorFatura.subtract(transacao.getValor());
		}
		historicoTransacoes.add(transacao);
	}
	
	protected void registraTransacao(Transacao transacao, Transacao taxaOperacao) {
		if (transacao.getEnumTransacao().equals(EnumTransacao.PAGAMENTO_CREDITO)
				|| transacao.getEnumTransacao().equals(EnumTransacao.MENSALIDADE_SEGURO)) {
			limiteDisponivel = limiteDisponivel.subtract(transacao.getValor().add(taxaOperacao.getValor()));
			valorFatura = valorFatura.add(transacao.getValor().add(taxaOperacao.getValor()));
			historicoTransacoes.add(taxaOperacao);
			
		} else if (transacao.getEnumTransacao().equals(EnumTransacao.PAGAMENTO_FATURA)) {
			limiteDisponivel = limiteDisponivel.add(transacao.getValor());
			valorFatura = valorFatura.subtract(transacao.getValor());
		}
		historicoTransacoes.add(transacao);
	}

	protected BigDecimal getLimiteDisponivel() {
		return limiteDisponivel;
	}

	protected void setLimiteDisponivel(BigDecimal valorDisponivel) {
		this.limiteDisponivel = valorDisponivel;
	}

	protected BigDecimal getValorFatura() {
		return valorFatura;
	}

	protected void setValorFatura(BigDecimal valorFatura) {
		this.valorFatura = valorFatura;
	}

	protected void exibe() {
		System.out.println("\n**********FATURA CARTÃO DE CRÉDITO**********" + "\n                  BANCO OGP");
		System.out.println(this.cartaoAssociado);
		historicoTransacoes.stream().forEach(System.out::println);
		System.out.println("\nFATURA ATUAL........................ + R$" + Formatadores.arredonda(valorFatura));
		System.out.println("CRÉDITO DISPONÍVEL.................. + R$" + Formatadores.arredonda(limiteDisponivel));
		System.out.println("**********************************************");

	}
}
