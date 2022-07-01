package com.kamlesh.app.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class JWT_util {

	
	private String SECRET_KEY = "kamlesh";
	
	private Claims exctractAllClaims(String token) {
		return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
	}

	public <T> T extractClaim(String token, Function<Claims,T> claimsResolver) {
		final Claims claims = exctractAllClaims(token);
		return claimsResolver.apply(claims);
	}
	
	public String extractUsername(String token){
		return extractClaim(token, Claims::getSubject);
	}
	
	public Date extractExpiration(String token){
		return extractClaim(token, Claims::getExpiration);
	}
	
	private Boolean isTokenExpired(String token){
		return extractExpiration(token).before(new Date());
	}
	
	//Below three are vimp
	
	public String generateToken(UserDetails userDetails){
		Map<String, Object> claims = new HashMap<>();// we can add claim as per requiremnet
		return createToken(claims, userDetails.getUsername());
	}
	
	private String createToken(Map<String, Object> claims, String subject) {//subject is username
		return Jwts
				.builder()
				.setClaims(claims)
				.setSubject(subject)
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
				.signWith(SignatureAlgorithm.HS256, SECRET_KEY)
				.compact();
	}
	
	public Boolean validateToken(String token, UserDetails userDetails){
		final String username = extractUsername(token);
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}
}
