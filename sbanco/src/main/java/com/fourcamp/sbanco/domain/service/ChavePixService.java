package com.fourcamp.sbanco.domain.service;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fourcamp.sbanco.domain.dto.conta.ContaDTO;
import com.fourcamp.sbanco.domain.dto.pix.ChavePixDTO;
import com.fourcamp.sbanco.domain.repository.ChavePixRepository;

@Service
public class ChavePixService {

	@Autowired
	private ChavePixRepository repository;

	public void salvar(ChavePixDTO chave) {
		repository.save(chave);
	}
	
	public void cadastrarChave(ContaDTO conta, String chave) {
		repository.save(new ChavePixDTO(conta, chave));
	}
	public ContaDTO getContaByChave(String chave) {
		ChavePixDTO chavepix = repository.findById(chave).orElseThrow(() -> new NoSuchElementException("Essa chave pix não existe."));
		return chavepix.getContaAssociada();
	}

	public void deletarById(String cpf) {
		repository.deleteById(cpf);

	}

	public List<ChavePixDTO> getChavesByNumeroDaConta(ContaDTO conta) {
		return repository.findByContaAssociada(conta);
		
	}

}
