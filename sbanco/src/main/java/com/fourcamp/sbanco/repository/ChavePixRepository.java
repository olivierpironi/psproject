package com.fourcamp.sbanco.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fourcamp.sbanco.dto.ChavePixDTO;
import com.fourcamp.sbanco.dto.ContaDTO;
public interface ChavePixRepository extends JpaRepository<ChavePixDTO, String>{
	List<ChavePixDTO> findByContaAssociada(ContaDTO conta);

}
