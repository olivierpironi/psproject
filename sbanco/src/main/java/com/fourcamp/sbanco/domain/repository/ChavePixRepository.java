package com.fourcamp.sbanco.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fourcamp.sbanco.domain.dto.conta.ContaDTO;
import com.fourcamp.sbanco.domain.dto.pix.ChavePixDTO;
public interface ChavePixRepository extends JpaRepository<ChavePixDTO, String>{
	List<ChavePixDTO> findByContaAssociada(ContaDTO conta);

}
