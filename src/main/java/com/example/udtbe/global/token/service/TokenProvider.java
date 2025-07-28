package com.example.udtbe.global.token.service;

import static com.example.udtbe.global.token.exception.TokenErrorCode.INVALID_TOKEN;
import static com.example.udtbe.global.token.exception.TokenErrorCode.MALFORMED_TOKEN;

import com.example.udtbe.domain.auth.service.AuthQuery;
import com.example.udtbe.domain.member.entity.Member;
import com.example.udtbe.global.exception.RestApiException;
import com.example.udtbe.global.security.dto.CustomOauth2User;
import com.example.udtbe.global.token.exception.TokenErrorCode;
import com.example.udtbe.global.util.RedisUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class TokenProvider {

    private static final String ROLE_KEY = "ROLE";
    private static final String[] BLACKLIST = new String[]{"black_list_token"};
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 90L;
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24L;

    private final AuthQuery authQuery;
    private final RedisUtil redisUtil;

    @Value("${spring.jwt.key}")
    private String key;
    private SecretKey secretKey;

    @PostConstruct
    private void initSecretKey() {
        this.secretKey = Keys.hmacShaKeyFor(key.getBytes());
    }

    public String generateAccessToken(Member findMember, CustomOauth2User authentication,
            Date now) {
        return generateToken(findMember, authentication, ACCESS_TOKEN_EXPIRE_TIME, now);
    }

    public void generateRefreshToken(Member findMember, CustomOauth2User authentication, Date now) {
        String refreshToken = generateToken(findMember, authentication, REFRESH_TOKEN_EXPIRE_TIME,
                now);

        // redis Refresh 저장
        redisUtil.setValues("RT:" + authentication.getEmail(), refreshToken,
                Duration.ofMillis(REFRESH_TOKEN_EXPIRE_TIME));
    }

    private String generateToken(Member findMember, CustomOauth2User authentication,
            long tokenExpireTime, Date now) {
        Date expiredTime = createExpiredDateWithTokenType(now, tokenExpireTime);
        String authorities = getAuthorities(authentication);

        return Jwts.builder()
                .subject(String.valueOf(findMember.getId()))
                .claim(ROLE_KEY, authorities)
                .issuedAt(now)
                .expiration(expiredTime)
                .signWith(secretKey, Jwts.SIG.HS512)
                .compact();
    }

    private String getAuthorities(CustomOauth2User authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining());
    }

    private Date createExpiredDateWithTokenType(Date date, long tokenExpireTime) {
        return new Date(date.getTime() + tokenExpireTime);
    }

    public Authentication getAuthentication(String token) {
        Claims claims = parseToken(token);
        List<SimpleGrantedAuthority> authorities = getAuthorities(claims);

        String subject = claims.getSubject();
        Member principal = authQuery.getMemberById(Long.valueOf(subject));

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    public boolean validateToken(String token, Date date) {
        if (!StringUtils.hasText(token)) {
            return false;
        }

        Claims claims = parseToken(token);
        if (!claims.getExpiration().after(date)) {
            throw new RestApiException(TokenErrorCode.EXPIRED_TOKEN);
        }

        return true;
    }

    private Claims parseToken(String token) {
        try {
            return Jwts.parser().verifyWith(secretKey).build()
                    .parseSignedClaims(token).getPayload();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        } catch (MalformedJwtException e) {
            throw new RestApiException(MALFORMED_TOKEN);
        } catch (Exception e) {
            throw new RestApiException(INVALID_TOKEN);
        }
    }

    private List<SimpleGrantedAuthority> getAuthorities(Claims claims) {
        return Collections.singletonList(new SimpleGrantedAuthority(
                claims.get(ROLE_KEY).toString()
        ));
    }

    public Long getExpiration(String token, Date date) {
        Claims claims = parseToken(token);
        Date expiration = claims.getExpiration();
        return (expiration.getTime() - date.getTime());
    }

    public boolean verifyBlackList(String accessToken) {
        String value = redisUtil.getValues(accessToken);
        return Arrays.asList(BLACKLIST).contains(value);
    }

    public Member getMemberAllowExpired(String token) {
        Claims claims = parseToken(token);

        String subject = claims.getSubject();
        return authQuery.getMemberById(Long.valueOf(subject));
    }

    public String getMemberId(String token) {
        try {
            Claims claims = parseToken(token);
            return claims.getSubject();
        } catch (Exception e) {
            return null;
        }
    }

}
