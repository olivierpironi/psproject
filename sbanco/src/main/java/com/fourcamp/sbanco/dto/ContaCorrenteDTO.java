package com.fourcamp.sbanco.dto;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.fourcamp.sbanco.enums.EnumConta;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Conta Corrente tem as funções:
 * - Cartão de Crédito;
 * - Cartão de Débito;
 * - Tarifa mensal de utilização da conta.
 * 
 * Um mesmo cliente só pode ter uma única conta corrente em nosso banco.
 * 
 * @author Olivier Gomes Pironi
 *
 */
@SuppressWarnings("serial")
@Entity(name = "conta_corrente")
@Table(name = "contas_corrente")
@NoArgsConstructor
@Getter
@Setter
public class ContaCorrenteDTO extends ContaDTO {
	protected LocalDate ultimaCobranca = LocalDate.of(2023, 1, 1);
	@OneToOne(mappedBy = "contaAssociada",cascade = CascadeType.MERGE)
	private CartaoCreditoDTO cartaoDeCredito;
	@OneToOne(mappedBy = "contaAssociada",cascade = CascadeType.MERGE)
	private CartaoDebitoDTO cartaoDeDebito;

	public ContaCorrenteDTO(ClienteDTO cliente, String senha) {
		super(EnumConta.CONTA_CORRENTE, cliente, senha);

	}
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() { //TODO faz sentido usar isso no meu projeto
		return List.of(new SimpleGrantedAuthority("ROLE_USER"));
	}

	@Override
	public String getPassword() {
		return getSenha();
	}

	@Override
	public String getUsername() {
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
