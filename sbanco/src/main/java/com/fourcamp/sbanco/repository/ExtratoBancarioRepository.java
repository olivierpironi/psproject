package com.fourcamp.sbanco.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fourcamp.sbanco.dto.ExtratoBancarioDTO;
public interface ExtratoBancarioRepository extends JpaRepository<ExtratoBancarioDTO, Long>{
}
