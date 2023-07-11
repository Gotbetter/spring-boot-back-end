package pcrc.gotbetter.user.login_method.jwt.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import pcrc.gotbetter.user.login_method.jwt.service.CustomUserDetailService;

import java.security.Key;
import java.util.*;

@Component
public class JwtProvider {
    private final Key secretKey;
    private final Long accessExpiredTime;
    private final Long refreshExpiredTime;
    private static final String AUTHORITIES_KEY = "role";
    private final CustomUserDetailService customUserDetailService;

    public JwtProvider(@Value("${external.jwt.secretKey}") String secretKey,
                       @Value("${external.jwt.accessTokenExpiredTime}") Long accessExpiredTime,
                       @Value("${external.jwt.refreshTokenExpiredTime}") Long refreshExpiredTime,
                       CustomUserDetailService customUserDetailService) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
        this.accessExpiredTime = accessExpiredTime;
        this.refreshExpiredTime = refreshExpiredTime;
        this.customUserDetailService = customUserDetailService;
    }

    public TokenInfo generateToken(String userId) {

        Map<String, Object> headers = new HashMap<>();
        headers.put("type", "token");

        Map<String, Object> payloads = new HashMap<>();
        payloads.put("id", userId);
        payloads.put(AUTHORITIES_KEY, "USER");

        Date now = new Date();

        //Access Token 생성
        String accessToken = Jwts.builder()
                .setHeader(headers)
                .setClaims(payloads)
                .setSubject("got-better")
                .setExpiration(new Date(now.getTime() + accessExpiredTime))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();

        //Refresh Token 생성
        String refreshToken = Jwts.builder()
                .setClaims(payloads)
                .setExpiration(new Date(now.getTime() + refreshExpiredTime))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();

        return TokenInfo.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public Boolean validateJwtToken(ServletRequest request, String token) {

        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException exception) {
            request.setAttribute("exception", "UnauthorizedException"); // Invalid JWT signature
        } catch (MalformedJwtException exception) {
            request.setAttribute("exception", "MalformedJwtException"); // JWT가 올바르게 구성되지 않았을 때 Invalid JWT token
        } catch (ExpiredJwtException exception) {
            request.setAttribute("exception", "ExpiredJwtException"); // 토큰 만료 Expired Jwt token
        } catch (UnsupportedJwtException exception) {
            request.setAttribute("exception", "UnsupportedJwtException"); // 예상하는 형식과 일치하지 않는 특정 형식이나 구성의 JWT일 경우 Unsupported Jwt token
        } catch (JwtException | IllegalArgumentException exception) {
            request.setAttribute("exception", "IllegalArgumentException"); // Jwt token compact of handler are invalid
        }
        return false;
    }

    public Authentication getAuthentication(ServletRequest request, String accessToken) {

        Claims claims = parseClaims(accessToken);
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(new String[]{claims.get(AUTHORITIES_KEY).toString()})
                        .map(SimpleGrantedAuthority::new).toList();
        UserDetails userDetails;

        try {
            userDetails = customUserDetailService.loadUserByUsername((String) parseClaims(accessToken).get("id"));
        } catch (UsernameNotFoundException ex) {
            request.setAttribute("exception", "UsernameOrPasswordNotFound");
            return null;
        }
        return new UsernamePasswordAuthenticationToken(userDetails, "", authorities);
    }

    public Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    public String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
