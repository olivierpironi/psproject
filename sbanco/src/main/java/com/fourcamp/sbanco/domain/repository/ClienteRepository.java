package com.fourcamp.sbanco.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fourcamp.sbanco.domain.dto.cliente.ClienteDTO;
public interface ClienteRepository extends JpaRepository<ClienteDTO, String>{

}
