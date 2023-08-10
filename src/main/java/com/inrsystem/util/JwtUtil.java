package com.inrsystem.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.inrsystem.enums.ErrorEnum;
import com.inrsystem.exception.LocalRunTimeException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {
    private final RedisUtil redisUtil;
    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expiration}")
    private int expiration;

    public JwtUtil(RedisUtil redisUtil) {
        this.redisUtil = redisUtil;
    }

    public String createToken(Map<String, Object> user) {

        //设置算法以及签名
        Algorithm algorithmHS = Algorithm.HMAC256(secret);
        //设置头部
        Map<String, Object> map = new HashMap<>();
        map.put("typ", "JWT");
        map.put("alg", "HS256");
        // 添加当前时间戳到自定义载荷
        long currentTimestamp = System.currentTimeMillis(); // 获取当前时间戳（毫秒）
        String token = JWT.create().withHeader(map)
                //签发人
                .withIssuer("INR")
                //主题
                .withSubject("login")
                //受众
                .withAudience("users")
                //自定义载荷
                .withClaim("account", user.get("account").toString())
                .withClaim("name", user.get("name").toString())
                .withClaim("role", user.get("role").toString())
                .withClaim("timestamp", currentTimestamp)
                .sign(algorithmHS);
        try {
            redisUtil.set("TOKEN:"+user.get("account").toString(), token, expiration);
        } catch (Exception e) {
            throw new LocalRunTimeException(ErrorEnum.REDIS_ERROR);
        }
        return token;
    }

    public Map<String, Claim> verifyToken(String token) {
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret)).build();
            DecodedJWT decodedJWT = verifier.verify(token);
            Map<String, Claim> claims = decodedJWT.getClaims();
            if (isExpired("TOKEN:"+claims.get("account").asString())) {
                redisUtil.set("TOKEN:"+claims.get("account").asString(), token, expiration);
                return decodedJWT.getClaims();
            }
            throw new LocalRunTimeException(ErrorEnum.REDIS_NOT_EXIST);
        } catch (JWTVerificationException e) {
            e.printStackTrace();
            throw new LocalRunTimeException(ErrorEnum.TOKEN_ERROR);
        }
    }

    /**
     * 获取id（学号或者工号）和角色 从token中
     */
    public Map<String, Object> getInfoFromToken(String token) {
        Map<String, Claim> jwt = verifyToken(token);
        Map<String, Object> info = new HashMap<>();
        info.put("account", jwt.get("account").asString());
        info.put("role", jwt.get("role").asString());
        return info;
    }


    /**
     * 判断一个account在redis中的token是否过期
     */
    public Boolean isExpired(String id) {
        return redisUtil.hasKey(id);
    }
}