package com.team6.apps.search.security;

import io.jsonwebtoken.ExpiredJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtRequestFilter extends OncePerRequestFilter {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());


	@Autowired
	private JwtUtil jwtUtil;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {
		try {
			logger.info("resolving token");
			String jwtToken = jwtUtil.resolveToken(request);
			logger.info("validating token");
			if (jwtUtil.validateToken(jwtToken)) {
				chain.doFilter(request, response);
			}
		} catch (IllegalArgumentException e) {
			logger.info("Unable to get JWT Token");
		} catch (ExpiredJwtException e) {
			logger.info("JWT Token has expired");
		}


	}

}
