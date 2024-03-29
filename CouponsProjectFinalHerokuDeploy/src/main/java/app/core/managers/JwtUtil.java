package app.core.managers;

import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import app.core.login.ClientType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class JwtUtil {

	private String signatureAlgorithm = SignatureAlgorithm.HS256.getJcaName();
	@Value("${jwt.token.util.secret: this+is+my+key+and+it+must+be+at+least+256+bits+long}")
	private String encodedSecretKey ;
	private Key decodedSecretKey;
	
	@PostConstruct
	private void init() {
		decodedSecretKey = new SecretKeySpec(Base64.getDecoder().decode(encodedSecretKey),
				this.signatureAlgorithm);
	}

	public String generateToken(UserDetails userDetails) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("userType", userDetails.clientType);
		return createToken(claims, userDetails.email);
	}

	private String createToken(Map<String, Object> claims, String subject) {
		Instant now = Instant.now();
		
		return Jwts.builder().setClaims(claims)

				.setSubject(subject)

				.setIssuedAt(Date.from(now))

				.setExpiration(Date.from(now.plus(10, ChronoUnit.HOURS)))

				.signWith(this.decodedSecretKey)

				.compact();

		
	}

	private Claims extractAllClaims(String token) throws ExpiredJwtException {
		JwtParser jwtParser = Jwts.parserBuilder().setSigningKey(this.decodedSecretKey).build();
		return jwtParser.parseClaimsJws(token).getBody();
	}

	/** returns the JWT subject - in our case the email address */
	public String extractUserEmail(String token) {
		return extractAllClaims(token).getSubject();
	}

	public ClientType extractUserType(String token) {
		return ClientType.valueOf((String) extractAllClaims(token).get("userType")) ;
	}

	public Date extractExpiration(String token) {
		return extractAllClaims(token).getExpiration();
	}
	public Date extractIssued(String token) {
		return extractAllClaims(token).getIssuedAt();
	}

	private boolean isTokenExpired(String token) {
		try {
			extractAllClaims(token);
			return false;
		} catch (ExpiredJwtException e) {
			return true;
		}
	}

	/**
	 * returns true if the user (email) in the specified token equals the one in the
	 * specified user details and the token is not expired
	 */
	public boolean validateToken(String token, String email) {
		final String username = extractUserEmail(token);
		return (username.equals(email) && !isTokenExpired(token));
	}


}
