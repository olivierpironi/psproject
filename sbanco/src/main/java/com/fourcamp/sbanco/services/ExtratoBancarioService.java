package com.fourcamp.sbanco.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fourcamp.sbanco.dto.ExtratoBancarioDTO;
import com.fourcamp.sbanco.dto.TransacaoDTO;
import com.fourcamp.sbanco.infra.Formatadores;
import com.fourcamp.sbanco.repository.ExtratoBancarioRepository;

@Service
public class ExtratoBancarioService {
	
	@Autowired
	private ExtratoBancarioRepository repository;
	
	public void salvar(ExtratoBancarioDTO extrato) {
		repository.save(extrato);
	}
	
	public void registraTransacao(ExtratoBancarioDTO extrato, TransacaoDTO transacao) {
		extrato.getHistoricoTransacoes().add(transacao);
	}

	public void registraTransacao(ExtratoBancarioDTO extrato, TransacaoDTO transacao, TransacaoDTO taxaOperacao) {
		extrato.getHistoricoTransacoes().add(transacao);
		extrato.getHistoricoTransacoes().add(taxaOperacao);
	}

	public void exibe(ExtratoBancarioDTO extrato) {
		System.out.println("\n***************EXTRATO BANCÁRIO***************" + "\n                  BANCO OGP");
		System.out.println(extrato.getContaMae());
		extrato.getHistoricoTransacoes().stream().forEach(System.out::println);
		System.out.println("\nSALDO............................ + R$" + Formatadores.arredonda(extrato.getContaMae().getSaldo()));
		System.out.println("**********************************************");

	}
}
