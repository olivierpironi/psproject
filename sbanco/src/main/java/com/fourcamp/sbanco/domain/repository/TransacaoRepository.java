package com.fourcamp.sbanco.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fourcamp.sbanco.domain.dto.transacao.TransacaoDTO;

public interface TransacaoRepository extends JpaRepository<TransacaoDTO, Long> {
}
