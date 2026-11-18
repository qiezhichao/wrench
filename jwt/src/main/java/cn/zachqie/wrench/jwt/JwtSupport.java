package cn.zachqie.wrench.jwt;

import cn.zachqie.wrench.base.Assert;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * @ClassName JwtSupport
 * @Author qiezhichao
 * @Date 2026/6/17 11:19
 */
@Component
@Slf4j
public class JwtSupport {

    private static final String BEARER = "Bearer ";

    // 86400秒=24小时
    @Value("${zachqie.wrench.jwt.user_context.accesstoken-expires-sec:86400}")
    private long accessTokenExpiresInSec;
    @Value("${zachqie.wrench.jwt.secret:catjdj@XUl9BRy7u&8q5jO}")
    private String jwtSecret;
    @Value("${zachqie.wrench.jwt.issuer:jwt}")
    private String jwtIssuer;

    private SecretKey jwtKey;
    @PostConstruct
    public void init() {
        this.jwtKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateJwtToken(UserContext userContext){
        log.info("generateJwtToken, userContext: {}", userContext);
        String jwtToken = Jwts.builder()
                .claims(userContext.buildJwtClaims())
                .subject(userContext.getUserId())
                .issuer(jwtIssuer)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessTokenExpiresInSec * 1000))
                .signWith(jwtKey)
                .compact();
        return BEARER + jwtToken;
    }

    public UserContext parseJwtToken(String jwtToken){
        log.info("parseJwtToken, jwtToken: {}", jwtToken);
        Assert.notBlank(jwtToken, "JWT Token不能为空");
        Assert.isTrue(jwtToken.startsWith(BEARER), "JWT Token格式错误");
        jwtToken = jwtToken.substring(7);
        Claims claims = Jwts.parser()
                .verifyWith(jwtKey)
                .build()
                .parseSignedClaims(jwtToken)
                .getPayload();
        UserContext userContext = UserContext   .parseJwtClaims(claims);
        log.info("parseJwtToken, userContext: {}", userContext);
        return userContext;
    }
}
