package com.inrsystem.interceptor;

import com.inrsystem.annotation.Authorized;
import com.inrsystem.enums.ErrorEnum;
import com.inrsystem.exception.LocalRunTimeException;
import com.inrsystem.mapper.TeamMembersMapper;
import com.inrsystem.util.JwtUtil;
import io.micrometer.common.util.StringUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.HashMap;
import java.util.Map;

@Component
public class TokenInterceptor implements HandlerInterceptor {
    @Resource
    private JwtUtil jwtUtil;

    @Resource
    private TeamMembersMapper teamMembersMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String headToken = request.getHeader("token"); // 获取头中token
        if (!StringUtils.isEmpty(headToken)) {// 验证是否为空
            jwtUtil.verifyToken(headToken);
            Map<String, Object> info = jwtUtil.getInfoFromToken(headToken); // 调用jwt解密方法解密

            request.setAttribute("info", info); // 把jwt解密后的信息放入request域中

            // 解析JWT token并获取角色信息
            Integer role = Integer.parseInt(info.get("role").toString());
            // 获取当前请求对应的Controller类上的Authorized注解
            Class<?> controllerClass = ((HandlerMethod) handler).getBeanType();
            Authorized authorizedAnnotation = controllerClass.getAnnotation(Authorized.class);

            if (authorizedAnnotation == null) {
                // 没有Authorized注解，无需权限验证，直接放行
                return true;
            }
            int[] teamRoles = authorizedAnnotation.TeamRole();
            // 验证角色是否满足权限要求
            int[] allowedRoles = authorizedAnnotation.roles();
            if(role==2){
                for (Integer allowedRole : allowedRoles) {
                    for (Integer allowedTeamRole:teamRoles) {
                       String account = info.get("account").toString();
                       Map<String,Object> map =new HashMap<>();
                       map.put("account",account);
                       Integer teamRole = teamMembersMapper.selectByMap(map).get(0).getTeamRole();
                       if (allowedRole.equals(role)&&allowedTeamRole.equals(teamRole)) {
                        // 角色匹配，允许访问
                        return true;
                    }
                }
                }}
            else {
                for (Integer allowedRole : allowedRoles) {
                    if (allowedRole.equals(role)) {
                        // 角色匹配，允许访问
                        return true;
                    }
                }
            }
            throw new LocalRunTimeException(ErrorEnum.AUTHORITY_ERROR);
        }
        throw new LocalRunTimeException(ErrorEnum.EMPTY_TOKEN);
    }
}
