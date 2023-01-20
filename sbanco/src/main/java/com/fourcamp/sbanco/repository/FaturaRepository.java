package com.fourcamp.sbanco.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fourcamp.sbanco.dto.FaturaDTO;
public interface FaturaRepository extends JpaRepository<FaturaDTO, Long>{
}
