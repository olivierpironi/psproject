package com.fourcamp.sbanco.domain.dto.role;

import com.fourcamp.sbanco.domain.dto.conta.ContaDTO;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class AssociaRoles {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	private ContaDTO conta;

	@ManyToOne(cascade = CascadeType.ALL)
	private Role role;

	public AssociaRoles(ContaDTO conta, Role role) {
		super();
		this.conta = conta;
		this.role = role;
	}

	
}
