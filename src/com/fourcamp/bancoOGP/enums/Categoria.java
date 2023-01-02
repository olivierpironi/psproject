package enums;

import java.math.BigDecimal;

public enum Categoria {

	COMUM("Conta Comum", new BigDecimal("0.005"), new BigDecimal("0.007"), new BigDecimal("0.0025"),
			new BigDecimal("0.0030"), new BigDecimal("1500"), new BigDecimal("1000")),
	SUPER("Conta Super", new BigDecimal("0.008"), new BigDecimal("0.005"), new BigDecimal("0.0020"),
			new BigDecimal("0.0025"), new BigDecimal("4000"), new BigDecimal("2000")),
	PREMIUM("Conta Premium", new BigDecimal("0.010"), new BigDecimal("0.003"), new BigDecimal("0.0015"),
			new BigDecimal("0.0020"), new BigDecimal("10000"), new BigDecimal("8000"));

	private String descricao;
	private BigDecimal rendimentoPoupanca;
	private BigDecimal taxaContaCorrente;
	private BigDecimal taxaUtilizacaoCartaoDeDebito;
	private BigDecimal taxaUtilizacaoCartaoDeCredito;
	private BigDecimal limiteCartaoDeCredito;
	private BigDecimal limiteDiarioTransacaoDebito;

	private Categoria(String descricao, BigDecimal rendimentoPoupanca, BigDecimal taxaContaCorrente,
			BigDecimal taxaUtilizacaoCartaoDeDebito, BigDecimal taxaUtilizacaoCartaoDeCredito,
			BigDecimal limiteCartaoDeCredito, BigDecimal limiteDiarioTransacaoDebito) {
		this.descricao = descricao;
		this.rendimentoPoupanca = rendimentoPoupanca;
		this.taxaContaCorrente = taxaContaCorrente;
		this.taxaUtilizacaoCartaoDeDebito = taxaUtilizacaoCartaoDeDebito;
		this.taxaUtilizacaoCartaoDeCredito = taxaUtilizacaoCartaoDeCredito;
		this.limiteCartaoDeCredito = limiteCartaoDeCredito;
		this.limiteDiarioTransacaoDebito = limiteDiarioTransacaoDebito;
	}

	public String getDescricao() {
		return descricao;
	}

	public BigDecimal getRendimentoPoupanca() {
		return rendimentoPoupanca;
	}

	public BigDecimal getTaxaContaCorrente() {
		return taxaContaCorrente;
	}

	public BigDecimal getTaxaUtilizacaoCartaoDeDebito() {
		return taxaUtilizacaoCartaoDeDebito;
	}

	public BigDecimal getTaxaUtilizacaoCartaoDeCredito() {
		return taxaUtilizacaoCartaoDeCredito;
	}

	public BigDecimal getLimiteCartaoDeCredito() {
		return limiteCartaoDeCredito;
	}

	public BigDecimal getLimiteDiarioTransacaoDebito() {
		return limiteDiarioTransacaoDebito;
	}

}
