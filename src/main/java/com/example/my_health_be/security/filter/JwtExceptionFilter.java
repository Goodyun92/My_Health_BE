package com.example.my_health_be.security.filter;//package com.example.my_health_be.exception;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.http.HttpStatus;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import io.jsonwebtoken.ExpiredJwtException;
import java.io.IOException;

@Component
public class JwtExceptionFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {

            String path = request.getRequestURI();

            // 회원가입, 로그인, 토큰 갱신 요청에 대해 토큰 필터를 적용하지 않음
            if (path.startsWith("/api/users/join") || path.startsWith("/api/users/login") || path.startsWith("/api/users/refresh")) {
                filterChain.doFilter(request, response);
                return;
            }

            String jwtToken = request.getHeader("Authorization"); // 토큰 헤더 조회

            if (jwtToken == null || jwtToken.isEmpty()) {
                // JWT 토큰이 없는 경우
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.getWriter().write("JWT token is missing");
                return; // 필터 체인의 나머지 부분을 실행하지 않음
            }

            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            // JWT 만료 예외 처리
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write("Expired JWT token");
        }
        // 추가적인 JWT 관련 예외 처리가 필요한 경우 여기에 catch 블록 추가

    }
}

