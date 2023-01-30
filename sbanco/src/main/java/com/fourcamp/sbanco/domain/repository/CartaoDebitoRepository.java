package com.fourcamp.sbanco.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fourcamp.sbanco.domain.dto.cartaodebito.CartaoDebitoDTO;
public interface CartaoDebitoRepository extends JpaRepository<CartaoDebitoDTO, Long>{
}
