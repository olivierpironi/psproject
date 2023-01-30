package com.fourcamp.sbanco.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fourcamp.sbanco.domain.dto.fatura.FaturaDTO;
public interface FaturaRepository extends JpaRepository<FaturaDTO, Long>{
}
