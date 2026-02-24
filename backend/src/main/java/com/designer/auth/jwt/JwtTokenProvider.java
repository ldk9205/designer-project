package com.designer.auth.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

// 책임:
// - Access Token(JWT) 생성(createToken)
// - JWT 유효성 검증(validateToken)
// - JWT에서 클레임 추출(getDesignerId, getEmail)

// 사용 위치:
// - AuthServiceImpl: 로그인/리프레시 시 accessToken 발급
// - JwtAuthenticationFilter: 요청마다 토큰 검증 및 사용자 식별자 추출

@Component
public class JwtTokenProvider {

    // SECRET_KEY : 현재는 hard coding. 실무/배포에서는 application.properties(yml) 또는 환경변수로 분리해야 함.
    private static final String SECRET_KEY = "designer-project-jwt-secret-key-designer-project";

    // Access Token 만료 시간 : 15분
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000L * 60 * 15;

    // HS256 서명 키 생성 : String bytes로 키 생성(길이가 충분해야 함)
    private final SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    // JWT 생성

    // payload 구성 -> sub(subject) : designerId (사용자 식별자), email : 커스텀 클레임, iat/exp : 발급시간/만료시간
    //
    public String createToken(Long designerId, String email) {

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + ACCESS_TOKEN_EXPIRE_TIME);

        return Jwts.builder()
                .setSubject(String.valueOf(designerId)) // 사용자 식별자
                .claim("email", email) // 추가 정보
                .setIssuedAt(now) // 발급 시간
                .setExpiration(expiryDate) // 만료 시간
                .signWith(key, SignatureAlgorithm.HS256) // HS256 서명
                .compact();
    }

    // 토큰 유효성 검증
    // 검증 범위 : 서명 검증(위조 여부), 만료(exp) 검증, 형식 오류 등
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token); // 여기서 검증 수행
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            // 만료/위조/잘못된 형식 포함
            return false;
        }
    }

    // 토큰에서 designerId 추출 : subject(sub)에 저장한 값을 Long으로 변환
    public Long getDesignerId(String token) {
        Claims claims = getClaims(token);
        return Long.parseLong(claims.getSubject());
    }

    // 토큰에서 email 추출 : "email" 커스텀 클레임을 String으로 조회
    public String getEmail(String token) {
        Claims claims = getClaims(token);
        return claims.get("email", String.class);
    }

    // Claims 전체 조회 (내부 공통 메서드) : parseClaimsJws에서 서명/만료 검증까지 수행됨
    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
