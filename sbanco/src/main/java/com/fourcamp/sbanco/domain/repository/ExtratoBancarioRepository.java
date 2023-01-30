package com.fourcamp.sbanco.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fourcamp.sbanco.domain.dto.extratobancario.ExtratoBancarioDTO;
public interface ExtratoBancarioRepository extends JpaRepository<ExtratoBancarioDTO, Long>{
}
