package pcrc.gotbetter.setting.security.JWT;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtProvider {
    private final Key secretKey;
    private final Long accessExpiredTime;
    private final Long refreshExpiredTime;

    public JwtProvider(@Value("${external.jwt.secretKey}") String secretKey,
                       @Value("${external.jwt.accessTokenExpiredTime}") Long accessExpiredTime,
                       @Value("${external.jwt.refreshTokenExpiredTime}") Long refreshExpiredTime) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
        this.accessExpiredTime = accessExpiredTime;
        this.refreshExpiredTime = refreshExpiredTime;
    }

    public TokenInfo generateToken(String auth_id) {

        Map<String, Object> headers = new HashMap<>();
        headers.put("type", "token");

        Map<String, Object> payloads = new HashMap<>();
        payloads.put("id", auth_id);

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
                .setExpiration(new Date(now.getTime() + refreshExpiredTime))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();

        return TokenInfo.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
