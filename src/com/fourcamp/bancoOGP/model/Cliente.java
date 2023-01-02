package model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import enums.Categoria;
import exceptions.DataBaseException;
import services.DataBase;

/**
 * Classe cliente com todos os atributos "settaveis"
 * 
 * @author Olivier Gomes Pironi
 */
public class Cliente {
	private String nome;
	private String cpf;
	private LocalDate dataDeNascimento;
	private String endereco;
	private Categoria categoria;

	private Cliente(String nome, String cpf, String dataDeNascimento, String endereco, Categoria tipoDeCliente) {
		this.nome = nome;
		this.cpf = cpf;
		this.dataDeNascimento = LocalDate.parse(dataDeNascimento, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
		this.endereco = endereco;
		this.categoria = tipoDeCliente;
	}

	public static void criarCliente(String nome, String cpf, String dataDeNascimento, String endereco,
			Categoria tipoDeCliente) {
		try {
			checaClienteByCpf(cpf);
			Cliente cliente = new Cliente(nome, cpf, dataDeNascimento, endereco, tipoDeCliente);
			DataBase.getListaDeClientes().add(cliente);
		} catch (DataBaseException e) {
			e.msg();
		}
	}

	private static void checaClienteByCpf(String cpf) {
		if (DataBase.getListaDeClientes().contains(DataBase.getClienteByCpf(cpf))) {
			throw new DataBaseException("\nO cpf já é cadastrado no Banco OGP");
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

	public Categoria getCategoria() {
		return categoria;
	}

	public void setCategoria(Categoria tipoDeCliente) {
		this.categoria = tipoDeCliente;
	}

}
