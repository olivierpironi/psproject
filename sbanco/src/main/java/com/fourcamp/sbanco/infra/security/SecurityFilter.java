package com.fourcamp.sbanco.infra.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fourcamp.sbanco.domain.repository.ContaRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class SecurityFilter extends OncePerRequestFilter {

	@Autowired
	private TokenService tokenService;
	@Autowired
	ContaRepository repository;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		var tokenJWT = recuperarToken(request);
		if (tokenJWT != null) {
			String subject = tokenService.getSubject(tokenJWT);
			var conta = repository.findByNumeroDaConta(Long.parseLong(subject));
			var authentication = new UsernamePasswordAuthenticationToken(conta, null, conta.getAuthorities());
			SecurityContextHolder.getContext().setAuthentication(authentication);
		}
		filterChain.doFilter(request, response);
	}

	private String recuperarToken(HttpServletRequest request) {
		var aut = request.getHeader("Authorization");
		if (aut != null) {
			return aut.replace("Bearer ", "");
		}
		return null;
	}

}
