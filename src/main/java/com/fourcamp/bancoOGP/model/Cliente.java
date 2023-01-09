package com.fourcamp.bancoOGP.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.fourcamp.bancoOGP.enums.EnumCliente;
import com.fourcamp.bancoOGP.exceptions.BOGPExceptions;
import com.fourcamp.bancoOGP.exceptions.DataBaseException;
import com.fourcamp.bancoOGP.services.DataBase;

/**
 * O cadastro de cliente se dá através de suas informações básicas e da escolha
 * do tipo de cliente: 
 * - Comum; 
 * - Super; 
 * - Premium.
 * 
 * A escolha do tipo de cliente influencia nas taxas de rendimento, taxas de
 * tarifa e disponibilização de crédito para os clientes. Um mesmo cpf só pode
 * ter um cadastro em nosso banco.
 * 
 * @author Olivier Gomes Pironi
 *
 */
public class Cliente {
	private String nome;
	private String cpf;
	private LocalDate dataDeNascimento;
	private String endereco;
	private EnumCliente enumCliente;

	private Cliente(String nome, String cpf, String dataDeNascimento, String endereco, EnumCliente tipoDeCliente) {
		this.nome = nome;
		this.cpf = cpf;
		this.dataDeNascimento = LocalDate.parse(dataDeNascimento, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
		this.endereco = endereco;
		this.enumCliente = tipoDeCliente;
	}

	public static void criarCliente(String nome, String cpf, String dataDeNascimento, String endereco,
			EnumCliente tipoDeCliente) {
		try {
			checaClienteByCpf(cpf);
			Cliente cliente = new Cliente(nome, cpf, dataDeNascimento, endereco, tipoDeCliente);
			DataBase.getListaDeClientes().add(cliente);
		} catch (BOGPExceptions e) {
			e.msg();
		}
	}

	@Override
	public boolean equals(Object o) {
		if ((o instanceof Cliente)) {
			Cliente c = (Cliente) o;
			return this.cpf.equals(c.getCpf());
		}
		return false;
	}

	private static void checaClienteByCpf(String cpf) {
		if (DataBase.getClienteByCpf(cpf) != null) {
			throw new DataBaseException("\nCPF já cadastrado no Banco OGP");
		}

	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public LocalDate getDataDeNascimento() {

		return dataDeNascimento;
	}

	public void setDataDeNascimento(LocalDate dataDeNascimento) {
		this.dataDeNascimento = dataDeNascimento;
	}

	public String getEndereco() {
		return endereco;
	}

	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}

	public EnumCliente getCategoria() {
		return enumCliente;
	}

	public void setCategoria(EnumCliente tipoDeCliente) {
		this.enumCliente = tipoDeCliente;
	}
}
