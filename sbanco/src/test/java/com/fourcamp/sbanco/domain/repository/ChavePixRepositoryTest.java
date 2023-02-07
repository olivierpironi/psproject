package com.fourcamp.sbanco.domain.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

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
import com.fourcamp.sbanco.domain.dto.pix.ChavePixDTO;
import com.fourcamp.sbanco.domain.enums.EnumCliente;

@DataJpaTest
@AutoConfigureTestDatabase(replace=Replace.NONE)
@DisplayName("Teste para o repositório de contas.")
class ChavePixRepositoryTest {

	@Autowired
	private ChavePixRepository pixRepository;
	@Autowired
	private  ClienteRepository clienteRepository;
	@Autowired
	private  ContaRepository contaRepository;
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
		// cenário
		ChavePixDTO chavePix = new ChavePixDTO(conta, "chave");
		
		// ação
		ChavePixDTO chaveSalva = pixRepository.save(chavePix);
		
		// verificação
		assertThat(chaveSalva).isEqualTo(chavePix);
	}
	
	@Test
	@DisplayName("Encontrar chaves pix de uma determinada conta.")
	void findByContaAssociada_AcharChavesPixAPartirDeUmaConta_ComSucesso() {
		// cenário
		ChavePixDTO chavePix = new ChavePixDTO(conta, "chave");
		pixRepository.save(chavePix);
		
		// ação
		List<ChavePixDTO> findByContaAssociada = pixRepository.findByContaAssociada(conta);
		
		// verificação
		assertThat(findByContaAssociada).hasSize(1);
		
		assertThat(findByContaAssociada).contains(chavePix);
	}

}
