package com.linktune.api.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    // ÇOK ÖNEMLİ: Bu anahtar, token'ları imzalamak için kullanılır ve KESİNLİKLE GİZLİ TUTULMALIDIR.
    private final String SECRET_KEY_STRING = "bu_cok_guclu_ve_uzun_bir_gizli_anahtar_olmalidir_asla_paylasma_123456789";
    private final SecretKey SECRET_KEY;

    public JwtService() {
        // Normal metnin doğrudan byte dizisini alıyoruz.
        byte[] keyBytes = SECRET_KEY_STRING.getBytes(StandardCharsets.UTF_8);
        this.SECRET_KEY = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Verilen kullanıcı adı için yeni bir JWT üretir.
     */
    public String generateToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) // 24 saat geçerlilik
                .signWith(SECRET_KEY)
                .compact();
    }

    /**
     * Verilen token'ın geçerliliğini, kullanıcı bilgileriyle karşılaştırarak kontrol eder.
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    /**
     * Token'dan kullanıcı adını (subject) çıkarır.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Token'ın süresinin dolup dolmadığını kontrol eder.
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Token'dan son kullanma tarihini çıkarır.
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Token'dan belirli bir "claim"i (parçayı) çıkaran genel metot.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Token'daki tüm bilgileri (payload) çıkaran ve imzasını doğrulayan metot.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(SECRET_KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}