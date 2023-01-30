package com.fourcamp.sbanco.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fourcamp.sbanco.domain.dto.extratobancario.ExtratoBancarioDTO;
import com.fourcamp.sbanco.domain.dto.transacao.TransacaoDTO;
import com.fourcamp.sbanco.domain.repository.ExtratoBancarioRepository;
import com.fourcamp.sbanco.infra.util.Formatadores;

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
