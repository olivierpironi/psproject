package com.fourcamp.bancoOGP.model;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fourcamp.bancoOGP.enums.EnumSeguroCredito;
import com.fourcamp.bancoOGP.services.Formatadores;

public class ContratoSeguro {
	private EnumSeguroCredito seguradora;
	private String apoliceEletronica;
	private LocalDateTime dataContratacao;
	private CartaoCredito cartaoAssegurado;

	/**
	 * Os contratos de seguro são obrigatórios para a emissão de um cartão de
	 * crédito, contam com: 
	 * - Apólice eletrônica única para cada contrato; 
	 * - Informações relevantes sobre o cartão assegurado;
	 * 
	 * @author Olivier Gomes Pironi
	 */
	protected ContratoSeguro(EnumSeguroCredito seguradora, CartaoCredito cartaoAssegurado) {
		super();
		this.seguradora = seguradora;
		this.apoliceEletronica = UUID.randomUUID().toString();
		this.dataContratacao = LocalDateTime.now();
		this.cartaoAssegurado = cartaoAssegurado;
	}

	public EnumSeguroCredito getSeguradora() {
		return seguradora;
	}

	@Override
	public String toString() {
		return "\nSeguradora: " + seguradora.getNomeSeguradora() + 
				"\nApólice Eletrônica: " + apoliceEletronica + 
				"\nValor: R$" + Formatadores.arredonda(seguradora.getValor()) + " mensais." + 
				"\nData de contratação: " + dataContratacao.format(Formatadores.formato) + 
				"\nCondições de Acionamento: " + seguradora.getCondicoesAssionamento() + 
				"\nInformações do Cartão Assegurado:" + cartaoAssegurado;
	}

}
