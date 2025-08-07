package com.postion.airlineorderbackend.service.impl;

import com.postion.airlineorderbackend.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


@Service
public class JwtServiceImpl implements JwtService {

    @Value("${jwt.secret}")
    private String secretKeyString;

    @Value("${jwt.expiration.ms}")
    private Long jwtExpiration;

    // 生成JWT,仅包含用户信息（用户名）
    @Override
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    // 生成JWT核心方法，可以包含额外的claims
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {

        return Jwts
                // 构建一个jwt
                .builder()

                //设置需要自定义的数据（如用户角色，权限等）
                .setClaims(extraClaims)

                // 设置jwt主题，存放用户名，这是用户的唯一标识 ***很重要**
                .setSubject(userDetails.getUsername())

                // 令牌的生命周期
                .setIssuedAt(new Date(System.currentTimeMillis()))

                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))

                // 签名核心 生成第三部分的signature，确保令牌完整性
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)

                // 将三部分组合成最终的String格式
                .compact();
    }

    // 从Jwt中提取用户名
	// extractClaim的具体应用
    @Override
    public String extractUsername(String token) {

        // getSubject是Claims接口的一个方法，专门用于获取subject claim，精确获取用户名
        return extractClaim(token, Claims::getSubject);
    }

    // 通用方法，用于从Jwt中提取任何类型的claim
    @Override
	// Function<Claims, T> java8的函数式接口→给我一个Claims对象，我返回一个T类型的结果
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {

        final Claims claims = extractAllClaims(token);

        return claimsResolver.apply(claims);
    }

    // 解析整个JWT，返回包含所有claims的Claims对象，这是所有claim提取操作的基础
    private Claims extractAllClaims(String token) {
          return Jwts.parser()

                  // 验证密钥
                  .verifyWith(getSignInKey())

                  // 构建解析器
                  .build()

                  // 解析token
                  .parseSignedClaims(token)

                  // 获取解析结果
                  .getPayload();
    }

    // 验证JWT是否有效
    // 身份验证/时效性验证
    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {

        final String username = extractUsername(token);

        // 检查从token中提取的用户名是否与UserDetails中用户名匹配，并且token没有过期
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    // 检查JWT是否过期
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // 从JWT中提取过期时间
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // 获取用于签名的密钥对象（key）
    // 将配置文件中的Base64字符串转换成'java.security.Key'对象
    private SecretKey getSignInKey() {

        // 将Base64编码的密钥字符串解码为字节数组
        byte[] keyBytes = Decoders.BASE64.decode(secretKeyString);

        // 使用HMAC-SHA算法生成安全密钥对象
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
