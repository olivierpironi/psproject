package com.fourcamp.sbanco.domain.dto.contapoupanca;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;

import com.fourcamp.sbanco.domain.dto.cliente.ClienteDTO;
import com.fourcamp.sbanco.domain.dto.conta.ContaDTO;
import com.fourcamp.sbanco.domain.dto.role.Role;
import com.fourcamp.sbanco.domain.enums.EnumConta;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Conta Poupança tem as funções: - Rendimento mensal através da poupança.
 * 
 * @author Olivier Gomes Pironi
 *
 */
@SuppressWarnings("serial")
@Entity(name = "conta_poupanca")
@Table(name = "contas_poupanca")
@NoArgsConstructor
@Getter
@Setter
public class ContaPoupancaDTO extends ContaDTO {

	private LocalDate ultimoRendimento = LocalDate.of(2023, 1, 1);
	@ManyToMany(fetch = FetchType.EAGER)
	private List<Role> roles = new ArrayList<>();

	public ContaPoupancaDTO(ClienteDTO cliente, String senha) {
		super(EnumConta.CONTA_POUPANCA, cliente, senha);

	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() { //TODO faz sentido usar isso no meu projeto
		return roles;
	}

	@Override
	public String getPassword() {
		// TODO Auto-generated method stub
		return getSenha();
	}

	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return getNumeroDaConta().toString();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}
