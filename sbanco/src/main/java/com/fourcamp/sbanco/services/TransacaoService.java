package com.fourcamp.sbanco.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fourcamp.sbanco.dto.TransacaoDTO;
import com.fourcamp.sbanco.repository.TransacaoRepository;

@Service
public class TransacaoService {
	
	@Autowired
	private TransacaoRepository repository;
	
	public void salvar(TransacaoDTO transacao) {
		repository.save(transacao);
	}

}
