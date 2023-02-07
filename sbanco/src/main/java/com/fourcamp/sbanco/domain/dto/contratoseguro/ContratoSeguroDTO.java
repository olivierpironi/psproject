package com.fourcamp.sbanco.domain.dto.contratoseguro;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fourcamp.sbanco.domain.dto.cartaocredito.CartaoCreditoDTO;
import com.fourcamp.sbanco.domain.enums.EnumSeguroCredito;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Entity(name = "contrato_seguro")
@Table(name = "contratos_seguro")
@Getter
@Setter
@NoArgsConstructor
public class ContratoSeguroDTO {
	@Enumerated(EnumType.STRING)
	private EnumSeguroCredito seguradora;
	@Id
	private String apoliceEletronica;
	private LocalDateTime dataContratacao;
	@OneToOne
	private CartaoCreditoDTO cartaoAssegurado;

	/**
	 * Os contratos de seguro são obrigatórios para a emissão de um cartão de
	 * crédito, contam com: 
	 * - Apólice eletrônica única para cada contrato; 
	 * - Informações relevantes sobre o cartão assegurado;
	 * 
	 * @author Olivier Gomes Pironi
	 */
	
	public ContratoSeguroDTO(EnumSeguroCredito seguradora, CartaoCreditoDTO cartaoAssegurado) {
		super();
		this.seguradora = seguradora;
		this.apoliceEletronica = UUID.randomUUID().toString();
		this.dataContratacao = LocalDateTime.now();
		this.cartaoAssegurado = cartaoAssegurado;
	}

}
