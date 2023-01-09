package com.fourcamp.bancoOGP.enums;

public enum EnumTransacao {
	SAQUE("SAQUE","-" ),
	DEPOSITO("DEPOSITO","+" ),
	PIX_RECEBIDO("PIX RECEBIDO","+"),
	PIX_ENVIADO("PIX ENVIADO","-"),
	TARIFA_CONTACORRENTE("TARIFA MENSAL \nCONTA CORRENTE","-"),
	RENDIMENTO_POUPANCA("RENDIMENTO \nPOUPANCA","+"),
	MENSALIDADE_SEGURO("MENSALIDADE \nSEGURO","+"),
	PAGAMENTO_FATURA("FATURA PAGA","-"),
	PAGAMENTO_CREDITO("PAGAMENTO CRÉDITO","+"),
	PAGAMENTO_DEBITO("PAGAMENTO DÉBITO","-"),
	PAGAMENTO_RECEBIDO("PAGAMENTO RECEBIDO","+"),
	TAXA_CARTAO_DE_CREDITO("TAXA DE UTILIZAÇÃO DO \nCARTÃO DE CRÉDITO","+"),
	TAXA_CARTAO_DE_DEBITO("TAXA DE UTILIZAÇÃO DO \nCARTÃO DE DÉBITO","-");
	private String tipo;
	private String sinal;


	private EnumTransacao(String tipo, String sinal) {
		this.tipo = tipo;
		this.sinal = sinal;
	}


	public String getTipo() {
		return tipo;
	}


	public String getSinal() {
		return sinal;
	}

}
