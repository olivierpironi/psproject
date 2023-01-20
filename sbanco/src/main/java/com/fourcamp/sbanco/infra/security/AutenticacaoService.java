package com.fourcamp.sbanco.infra.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.fourcamp.sbanco.repository.ContaRepository;

@Service
public class AutenticacaoService implements UserDetailsService{
	
	@Autowired
	private ContaRepository repository;

	@Override
	public UserDetails loadUserByUsername(String numeroDaContaString) throws UsernameNotFoundException {
		Long numeroDaConta = Long.parseLong(numeroDaContaString);
		return repository.findByNumeroDaConta(numeroDaConta);
	}

}
