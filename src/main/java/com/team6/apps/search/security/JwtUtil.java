package com.team6.apps.search.security;

import com.team6.apps.search.utils.Constants;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.*;
import java.util.function.Function;

@Component
public class JwtUtil {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private static final String TOKEN_HEADER = "Authorization";
	private static final String TOKEN_PREFIX = "Bearer ";

	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	public Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	public String extractRoles(String token) {
		final Claims claims = extractAllClaims(token);
		String role = (String) claims.get("roles");
		return role;
	}

	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	private Claims extractAllClaims(String token) {
		return Jwts.parser().setSigningKey(Constants.SECRET_KEY).parseClaimsJws(token).getBody();
	}

	public Boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date(System.currentTimeMillis()));
	}

	public String generateToken(String username, String role) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("roles", role);

		return createToken(claims, username);
	}

	private String createToken(Map<String, Object> claims, String subject) {

		return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + (1000 * 60 * 60)))
				.signWith(SignatureAlgorithm.HS256, Constants.SECRET_KEY).compact();
	}

	public Claims validateToken(String token) throws JwtException {
		try {
			Claims claims = extractAllClaims(token);
			logger.info("ID: " + claims.getId());
			logger.info("Subject: " + claims.getSubject());
			logger.info("Issuer: " + claims.getIssuer());
			logger.info("Expiration: " + claims.getExpiration());
			return claims;
		} catch (Exception e) {
			if (isTokenExpired(token)) {
				logger.info("token is expired");
				throw new JwtException("Token is expired");
			}
			logger.info("Error parsing token.  Token is invalid");
			throw new JwtException("Token is invalid");
		}
	}

	public String resolveToken(HttpServletRequest req) {
		String bearerToken = req.getHeader(TOKEN_HEADER);
		if (!Objects.isNull(bearerToken) && bearerToken.startsWith(TOKEN_PREFIX)) {
			return bearerToken.substring(7);
		} else {
			logger.warn("JWT Token does not begin with Bearer String");
			return null;
		}
	}
}
