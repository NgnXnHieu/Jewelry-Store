package com.example.jewelrystore.Implement;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.example.jewelrystore.Service.JwtService;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtImpl implements JwtService {

    private static final String SECRET_KEY = "12345678901234567890123456789012"; // đủ 32 ký tự
    private static final long ACCESS_TOKEN_EXPIRATION = 1000 * 60 * 1; // 15 phút
    private static final long REFRESH_TOKEN_EXPIRATION = 1000L * 60 * 60 * 24 * 7; // 7 ngày

    private Key getSignInKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    @Override
    public String generateToken(String username, String role) {
        return buildToken(username, role, ACCESS_TOKEN_EXPIRATION);
    }

    @Override
    public String generateRefreshToken(String username, String role) {
        return buildToken(username, role, REFRESH_TOKEN_EXPIRATION);
    }

    private String buildToken(String username, String role, long expirationMs) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Truyền token và function lấy subject
    @Override
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Nhận token và dùng extractAllClaims để giải token sau đó dùng function được
    // truyền vào để lấy thông tin
    @Override
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Giải mã token và lấy phần body chứa subject, expirydate,...
    // Có thể ném ra exception nếu token có chữ kí không hợp lệ, token hết hạn,
    // null, sai định dạng, mã hóa khác loại
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
}
