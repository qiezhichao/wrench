package cn.zachqie.wrench.jwt;

import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections.CollectionUtils;

import java.util.*;

/**
 * @ClassName UserContext
 * @Author qiezhichao
 * @Date 2026/1/28 10:08
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserContext {
    /** 平台编码 */
    private String platformCode;
    /** 应用编码 */
    private String appCode;
    /** 用户ID */
    private String userId;
    /** 用户级别 */
    private String userLevel;
    /** 用户名称 */
    private String userName;
    /** 用户权限 */
    private Set<String> userPermissions = new HashSet<>();
    /** 其他用户信息 */
    private Map<String, Object> extInfo;


    /** 辅助字段，accessToken */
    private transient String accessToken;
    /** 辅助字段，accessToken有效时间，单位：秒 */
    private transient long accessTokenExpiresInSec;
    /** 辅助字段，最近一次登录时间 */
    private transient Date lastLoginTime;



    public void setExtInfo(String key, Object value) {
        if (Objects.isNull(this.extInfo)) {
            this.extInfo = new HashMap<>();
        }

        this.extInfo.put(key, value);
    }

    public Map<String, Object> buildJwtClaims() {
        // 不放完整用户对象，只放必要的上下文
        Map<String, Object> claims = new HashMap<>();
        claims.put("platform", this.getPlatformCode());
        claims.put("app", this.getAppCode());
        claims.put("lvl", this.getUserLevel());
        claims.put("perms", this.getUserPermissions());
        claims.put("ext", this.getExtInfo());
        return claims;
    }

    public static UserContext parseJwtClaims(Claims claims) {
        UserContext context = new UserContext();
        context.setPlatformCode(claims.get("platform", String.class));
        context.setAppCode(claims.get("app", String.class));
        context.setUserLevel(claims.get("lvl", String.class));
        context.setUserId(claims.getSubject());

        List perms = claims.get("perms", List.class);
        if (CollectionUtils.isNotEmpty(perms)) {
            context.setUserPermissions(new HashSet<>(perms));
        }

        Map ext = claims.get("ext", Map.class);
        if (ext != null && !ext.isEmpty()) {
            context.setExtInfo(ext);
        }

        return context;
    }

}
