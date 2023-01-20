package com.fourcamp.sbanco.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fourcamp.sbanco.dto.TransacaoDTO;

public interface TransacaoRepository extends JpaRepository<TransacaoDTO, Long> {
}
