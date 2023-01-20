package com.fourcamp.sbanco.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import com.fourcamp.sbanco.dto.ContaDTO;
public interface ContaRepository extends JpaRepository<ContaDTO, Long>{
	UserDetails findByNumeroDaConta(Long login);
}
