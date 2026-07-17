package org.example.CampusMart.web.custom.interceptor;


import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.CampusMart.common.login.LoginUser;
import org.example.CampusMart.common.login.LoginUserHolder;
import org.example.CampusMart.common.utils.JwtUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthenticationInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String userIdHeader = request.getHeader("X-User-Id");
        if (org.springframework.util.StringUtils.hasText(userIdHeader)) {
            Long id = Long.valueOf(userIdHeader);
            request.setAttribute("userId", id);
            LoginUserHolder.setLoginUser(new LoginUser(id, "gateway"));
            return true;
        }

        String token = request.getHeader("access-token");
        Claims claims = JwtUtil.parseToken(token);

        Long id = claims.get("userId", Long.class);
        String username = claims.get("username", String.class);
        request.setAttribute("userId", id);
        LoginUserHolder.setLoginUser(new LoginUser(id, username));

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        LoginUserHolder.clear();
    }
}
