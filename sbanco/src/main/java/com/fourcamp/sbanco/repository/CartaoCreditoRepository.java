package com.fourcamp.sbanco.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fourcamp.sbanco.dto.CartaoCreditoDTO;
public interface CartaoCreditoRepository extends JpaRepository<CartaoCreditoDTO, Long>{
}
