package com.designer.auth.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenProvider {

    /**
     * ⚠️ 실무에서는 application.yml / 환경변수로 분리
     * HS256은 최소 256bit(32byte) 이상 필요
     */
    private static final String SECRET_KEY =
            "designer-project-jwt-secret-key-designer-project";

    // Access Token 유효기간 (1시간)
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000L * 60 * 60;

    private final SecretKey key =
            Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    /**
     * ✅ JWT 생성
     */
    public String createToken(Long designerId, String email) {

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + ACCESS_TOKEN_EXPIRE_TIME);

        return Jwts.builder()
                .setSubject(String.valueOf(designerId)) // 사용자 식별자
                .claim("email", email)                  // 추가 정보
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * ✅ 토큰 유효성 검증
     * - 만료
     * - 위조
     * - 형식 오류
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * ✅ 토큰에서 designerId 추출
     */
    public Long getDesignerId(String token) {
        Claims claims = getClaims(token);
        return Long.parseLong(claims.getSubject());
    }

    /**
     * ✅ 토큰에서 email 추출
     */
    public String getEmail(String token) {
        Claims claims = getClaims(token);
        return claims.get("email", String.class);
    }

    /**
     * ✅ Claims 전체 조회 (내부 공통 메서드)
     */
    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
