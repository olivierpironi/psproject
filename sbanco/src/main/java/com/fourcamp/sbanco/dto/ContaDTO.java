package com.fourcamp.sbanco.dto;

import java.math.BigDecimal;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCrypt;

import com.fourcamp.sbanco.enums.EnumConta;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Classe mãe de qualquer tipo de conta no Banco OGP, conta com as funções: -
 * Depósito; - Saque; - Pix; - Recebe pagamentos de cartões.
 * 
 * Os diversos tipos de checagens feitas antes que qualquer operação utiliza um
 * sistema de exceções, é uma regra de negócio.
 * 
 * @author Olivier Gomes Pironi
 */
@SuppressWarnings("serial")
@Entity
@SequenceGenerator(name = "numero_da_conta_seq", sequenceName = "numero_da_conta_seq", initialValue = 1000000000, allocationSize = 1)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Getter
@Setter
@NoArgsConstructor
public abstract class ContaDTO implements UserDetails{
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "numero_da_conta_seq")
	@Id
	private Long numeroDaConta;
	private String agencia = "0001";
	@Enumerated(EnumType.STRING)
	private EnumConta tipoDeConta;
	@ManyToOne
	private ClienteDTO cliente;
	protected BigDecimal saldo = new BigDecimal("0");
	private String senha;
	@OneToOne(mappedBy = "contaMae", cascade = CascadeType.ALL)
	protected ExtratoBancarioDTO extrato = new ExtratoBancarioDTO(this);

	protected ContaDTO(EnumConta tipoDeConta, ClienteDTO cliente, String senha) {
		this.tipoDeConta = tipoDeConta;
		this.cliente = cliente;
		this.senha = BCrypt.hashpw(senha, BCrypt.gensalt());
	}
	
	public void setSenha(String novaSenha) {
		this.senha = BCrypt.hashpw(novaSenha, BCrypt.gensalt());
	}

	@Override
	public String toString() {
		return "\n" + this.tipoDeConta.getDescricao() + 
				"\nNº: " + this.numeroDaConta + 
				"\nAGENCIA: " + this.agencia + 
				"\nTITULAR: " + this.cliente.getNome();
	}
	
}
