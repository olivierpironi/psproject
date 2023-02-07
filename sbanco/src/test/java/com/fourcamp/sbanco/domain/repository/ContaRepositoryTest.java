package com.fourcamp.sbanco.domain.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.fourcamp.sbanco.domain.dto.cliente.ClienteDTO;
import com.fourcamp.sbanco.domain.dto.cliente.CadastroCliente;
import com.fourcamp.sbanco.domain.dto.conta.ContaDTO;
import com.fourcamp.sbanco.domain.dto.contapoupanca.ContaPoupancaDTO;
import com.fourcamp.sbanco.domain.enums.EnumCliente;


@DataJpaTest
@AutoConfigureTestDatabase(replace=Replace.NONE)
@DisplayName("Teste para o repositório de contas.")
class ContaRepositoryTest {
	
	@Autowired
	private ContaRepository contaRepository;
	@Autowired
	private  ClienteRepository clienteRepository;
	private ContaDTO conta;
	
	@BeforeEach
	void setup(){
		CadastroCliente dados = new CadastroCliente("Olivier", "123456789", "01/01/1900", "end", EnumCliente.COMUM);
		ClienteDTO cliente = new ClienteDTO(dados);
		cliente = clienteRepository.save(cliente);
		conta = contaRepository.save(new ContaPoupancaDTO(cliente, "123"));
		
	}

	@Test
	@DisplayName("Persistencia com sucesso.")
	void save_Persistencia_ComSucesso() {
		// ação
		ContaDTO contaSalvada = contaRepository.save(conta);
		
		// verificação
		assertThat(contaSalvada).isNotNull();
		
		assertThat(contaSalvada.getNumeroDaConta()).isNotNull();
		
	}
	
	@Test
	@DisplayName("Buscar por numero da conta com sucesso.")
	void findByNumeroDaConta_BuscarPorNumeroDaConta_ComSucesso() {
		// ação
		ContaDTO contaSalvada = contaRepository.findByNumeroDaConta(conta.getNumeroDaConta());
		
		// verificação
		assertThat(contaSalvada).isNotNull();
		
		assertThat(contaSalvada.getNumeroDaConta()).isEqualTo(conta.getNumeroDaConta());
		
	}

}
