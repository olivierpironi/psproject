package com.fourcamp.sbanco.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fourcamp.sbanco.domain.dto.conta.ContaDTO;
public interface ContaRepository extends JpaRepository<ContaDTO, Long>{
	ContaDTO findByNumeroDaConta(Long login);
}
