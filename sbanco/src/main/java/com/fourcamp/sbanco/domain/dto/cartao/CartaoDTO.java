package com.fourcamp.sbanco.domain.dto.cartao;

import com.fourcamp.sbanco.domain.enums.EnumCartao;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.SequenceGenerator;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Classe mãe de qualquer tipo de cartão no Banco OGP, conta com as funções: 
 * - Bloqueio e desbloqueio de cartão; 
 * - Troca de senha; 
 * - Calcula taxa de utilização do cartão.
 * 
 * Os diversos tipos de checagens feitas antes que qualquer operação utiliza um
 * sistema de exceções, é uma regra de negócio.
 * 
 * @author Olivier Gomes Pironi
 */
@Entity(name = "cartao")
@SequenceGenerator(name = "numero_do_cartao_seq", sequenceName = "numero_do_cartao_seq", initialValue = 1000000000, allocationSize = 1)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "numero")
public abstract class CartaoDTO {
	@Enumerated(EnumType.STRING)
	protected EnumCartao tipoDeCartao;
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "numero_do_cartao_seq")
	@Id
	private Long numero;
	private Integer senha;
	protected boolean bloqueado;

	protected CartaoDTO(Integer senha) {
		this.senha = senha;
		this.bloqueado = true;
	}

}
