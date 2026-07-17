package org.example.CampusMart.common.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class UserContextInterceptor implements HandlerInterceptor {

    public static final String USER_ID_HEADER = "X-User-Id";
    public static final String USER_ID_ATTR = "userId";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String userIdHeader = request.getHeader(USER_ID_HEADER);
        if (StringUtils.hasText(userIdHeader)) {
            try {
                Long userId = Long.valueOf(userIdHeader);
                request.setAttribute(USER_ID_ATTR, userId);
            } catch (NumberFormatException ignored) {
            }
        }
        return true;
    }
}
