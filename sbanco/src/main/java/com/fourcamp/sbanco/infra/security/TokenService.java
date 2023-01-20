package com.fourcamp.sbanco.infra.security;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.fourcamp.sbanco.dto.ContaDTO;
import com.fourcamp.sbanco.infra.exceptions.TokenInvalidoOuNuloException;

@Service
public class TokenService {
	
	@Value("api.security.token.segredo")
	private String segredo;

	public String gerarToken(ContaDTO conta) {
		LocalDateTime agora= LocalDateTime.now();
		try {
		    Algorithm algoritmo = Algorithm.HMAC256(segredo);
		    String token = JWT.create()
		        .withIssuer("biblioteca auth0, API BANCO OGP")
		        .withSubject(conta.getNumeroDaConta().toString())
		        .withExpiresAt(agora.plusHours(2).toInstant(ZoneOffset.of("-03:00")))
		        .sign(algoritmo);
		    return token;
		} catch (JWTCreationException exception){
		    throw new RuntimeException("Erro ao gerar token de segurança");
		}
	}
	
	public String getSubject(String tokenJWT) {
		try {
		    Algorithm algoritimo = Algorithm.HMAC256(segredo);
		    return JWT.require(algoritimo)
		        .withIssuer("biblioteca auth0, API BANCO OGP")
		        .build()
		        .verify(tokenJWT)
		        .getSubject();
		} catch (JWTVerificationException exception){
		    throw new TokenInvalidoOuNuloException("Token Inválido.");
		} 
	}
}

