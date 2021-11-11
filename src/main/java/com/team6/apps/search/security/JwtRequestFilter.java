package com.team6.apps.search.security;

import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class JwtRequestFilter extends OncePerRequestFilter {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private JwtUtil jwtUtil = null;

    public JwtRequestFilter(JwtUtil jwtUtil){
    	this.jwtUtil = jwtUtil;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {
		try {
			logger.info("resolving token");
			String jwtToken = jwtUtil.resolveToken(request);
			logger.info("validating token");
			Claims claims = jwtUtil.validateToken(jwtToken);
			this.createAuthentication(claims, jwtToken).ifPresent(authentication -> {
				SecurityContextHolder.getContext().setAuthentication(authentication);
			});
			chain.doFilter(request, response);
		} catch (IllegalArgumentException e) {
			logger.info("Unable to get JWT Token");
		} catch (JwtException e) {
			logger.info(e.getMessage());
		}
	}

	private Optional<Authentication> createAuthentication(Claims claims, String jwtToken) {
		List<GrantedAuthority> authorities = AuthorityUtils.commaSeparatedStringToAuthorityList((String) claims.get("roles"));
		User principal = new User(claims.getSubject(), "", authorities);
		return Optional.of(new UsernamePasswordAuthenticationToken(principal, jwtToken, authorities));
	}


}
