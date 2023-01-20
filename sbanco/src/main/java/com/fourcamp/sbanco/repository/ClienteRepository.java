package com.fourcamp.sbanco.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fourcamp.sbanco.dto.ClienteDTO;
public interface ClienteRepository extends JpaRepository<ClienteDTO, String>{

}
