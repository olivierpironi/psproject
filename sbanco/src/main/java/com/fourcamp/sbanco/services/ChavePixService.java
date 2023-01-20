package com.fourcamp.sbanco.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fourcamp.sbanco.dto.ChavePixDTO;
import com.fourcamp.sbanco.dto.ContaDTO;
import com.fourcamp.sbanco.repository.ChavePixRepository;

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
		return repository.findById(chave).get().getContaAssociada();
	}

	public void deletarById(String cpf) {
		repository.deleteById(cpf);

	}

	public List<ChavePixDTO> getChavesByNumeroDaConta(ContaDTO conta) {
		return repository.findByContaAssociada(conta);
		
	}

}
