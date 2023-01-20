package com.fourcamp.sbanco.dto;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.fourcamp.sbanco.dto.records.DadosCadastroCliente;
import com.fourcamp.sbanco.enums.EnumCliente;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * O cadastro de cliente se dá através de suas informações básicas e da escolha
 * do tipo de cliente: - Comum; - Super; - Premium.
 * 
 * A escolha do tipo de cliente influencia nas taxas de rendimento, taxas de
 * tarifa e disponibilização de crédito para os clientes. Um mesmo cpf só pode
 * ter um cadastro em nosso banco.
 * 
 * @author Olivier Gomes Pironi
 *
 */
@Entity(name = "Cliente")
@Table(name = "clientes")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "cpf")
public class ClienteDTO {
	@NotBlank
	private String nome;
	@NotBlank
	@Id
	private String cpf;
	@NotNull
	private LocalDate dataDeNascimento;
	private String endereco;
	@NotNull
	@Enumerated(EnumType.STRING)
	private EnumCliente categoria;

	public ClienteDTO(DadosCadastroCliente dados) {
		this.nome = dados.nome();
		this.cpf = dados.cpf();
		this.dataDeNascimento = LocalDate.parse(dados.dataDeNascimento(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
		this.endereco = dados.endereco();
		this.categoria = dados.tipoDeCliente();
	}

}
