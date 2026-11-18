package cn.zachqie.wrench.test.jwt;

import cn.zachqie.wrench.jwt.JwtSupport;
import cn.zachqie.wrench.jwt.UserContext;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * jwt 模块测试：token 生成、解析、篡改拒绝、格式校验
 *
 * 通过反射注入密钥，避免依赖 Spring 容器与默认密钥。
 */
@Slf4j
public class JwtTest {

    private JwtSupport jwtSupport;
    private String secret = "test-secret-for-unit-test-0123456789";

    @Before
    public void setUp() throws Exception {
        jwtSupport = new JwtSupport();
        java.lang.reflect.Field secretField = JwtSupport.class.getDeclaredField("jwtSecret");
        secretField.setAccessible(true);
        secretField.set(jwtSupport, secret);
        java.lang.reflect.Field issuerField = JwtSupport.class.getDeclaredField("jwtIssuer");
        issuerField.setAccessible(true);
        issuerField.set(jwtSupport, "test-issuer");
        java.lang.reflect.Field expiresField = JwtSupport.class.getDeclaredField("accessTokenExpiresInSec");
        expiresField.setAccessible(true);
        expiresField.setLong(jwtSupport, 3600);
        jwtSupport.init();
    }

    @Test
    public void testGenerateAndParse() {
        UserContext source = new UserContext();
        source.setPlatformCode("PC");
        source.setAppCode("APP001");
        source.setUserId("user001");
        source.setUserLevel("VIP");
        source.setUserName("张三");
        Set<String> perms = new HashSet<>();
        perms.add("read");
        perms.add("write");
        source.setUserPermissions(perms);

        String token = jwtSupport.generateJwtToken(source);
        assertTrue(token.startsWith("Bearer "));

        UserContext parsed = jwtSupport.parseJwtToken(token);
        assertEquals("user001", parsed.getUserId());
        assertEquals("PC", parsed.getPlatformCode());
        assertEquals("APP001", parsed.getAppCode());
        assertEquals("VIP", parsed.getUserLevel());
        assertEquals(perms, parsed.getUserPermissions());
    }

    @Test
    public void testParseIllegalToken() {
        // 空 token
        try {
            jwtSupport.parseJwtToken(null);
            fail("should throw");
        } catch (cn.zachqie.wrench.base.InvalidRuntimeException expected) {
        }
        // 非 Bearer 前缀
        try {
            jwtSupport.parseJwtToken("Basic xxx");
            fail("should throw");
        } catch (cn.zachqie.wrench.base.InvalidRuntimeException expected) {
        }
        // 篡改过的 token 应解析失败
        try {
            String token = jwtSupport.generateJwtToken(new UserContext());
            String tampered = token.substring(0, token.length() - 3) + "aaa";
            jwtSupport.parseJwtToken(tampered);
            fail("should throw");
        } catch (Exception expected) {
            // jjwt 抛出签名校验异常
        }
    }

    @Test
    public void testTamperedSecretRejected() throws Exception {
        String token = jwtSupport.generateJwtToken(new UserContext());

        // 用不同密钥解析应失败
        SecretKey otherKey = io.jsonwebtoken.security.Keys.hmacShaKeyFor(
                "another-secret-key-0123456789abcdef".getBytes(StandardCharsets.UTF_8));
        try {
            Jwts.parser().verifyWith(otherKey).build()
                    .parseSignedClaims(token.substring(7)).getPayload();
            fail("should throw");
        } catch (Exception expected) {
        }
    }
}
