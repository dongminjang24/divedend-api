package com.dividend.dividend.security;

import java.security.Key;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.dividend.dividend.service.MemberService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ClaimsBuilder;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TokenProvider {

	@Value("${spring.jwt.secret}")
	private String SECRET_KEY;

	private static final String KEY_ROLE = "roles";
	private static final long TOKEN_EXPIRE_TIME = 1000 * 60 * 60;
	private final MemberService memberService;

	private SecretKey getSigningKey() {
		byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
		return Keys.hmacShaKeyFor(keyBytes);
	}

	public String generateToken(String username, List<String> roles) {
		Date now = new Date();
		Date expireDate = new Date(now.getTime() + TOKEN_EXPIRE_TIME);

		return Jwts.builder()
			.subject(username)
			.claim(KEY_ROLE, roles)
			.issuedAt(now)
			.expiration(expireDate)
			.signWith(this.getSigningKey())
			.compact();
	}

	public Authentication getAuthentication(String jwt) {
		UserDetails userDetails = this.memberService.loadUserByUsername(this.getUsername(jwt));
		return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
	}

	public String getUsername(String token) {
		return parseClaims(token).getSubject();
	}

	public boolean validateToken(String token) {
		if (!StringUtils.hasText(token))
			return false;
		Claims claims = parseClaims(token);
		return !claims.getExpiration().before(new Date());
	}

	private Claims parseClaims(String token) {
		try {
			// Jwts.parser()
			// 	.setSigningKey(this.getSigningKey())
			// 	.build()
			// 	.parseClaimsJws(token)
			// 	.getBody();
			return Jwts.parser()
				.verifyWith(getSigningKey())
				.build()
				.parseSignedClaims(token)
				.getPayload();
		} catch (ExpiredJwtException e) {
			return e.getClaims();
		}

	}

}
