package com.fourcamp.sbanco.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fourcamp.sbanco.dto.CartaoDebitoDTO;
public interface CartaoDebitoRepository extends JpaRepository<CartaoDebitoDTO, Long>{
}
