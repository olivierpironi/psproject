package com.fourcamp.sbanco.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fourcamp.sbanco.domain.dto.cartaocredito.CartaoCreditoDTO;
public interface CartaoCreditoRepository extends JpaRepository<CartaoCreditoDTO, Long>{
}
