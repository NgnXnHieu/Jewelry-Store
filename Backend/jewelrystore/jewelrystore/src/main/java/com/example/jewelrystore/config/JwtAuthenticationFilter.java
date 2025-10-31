package com.example.jewelrystore.config;

import java.io.IOException;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.jewelrystore.Service.JwtService;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
// extend OncePerRequestFilter để Override doFilterInternal
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsService userDetailsService;

    // Hàm này sẽ được Spring nhận ra
    // @Override
    // protected void doFilterInternal(HttpServletRequest request,
    // HttpServletResponse response,
    // FilterChain filterChain)
    // throws ServletException, IOException {

    // String path = request.getServletPath();

    // // ✅ Bỏ qua filter cho các URL public (login, register, refresh token)
    // if (path.equals("/api/login") || path.equals("/api/register") ||
    // path.equals("/api/refreshToken")) {
    // filterChain.doFilter(request, response);
    // return; // Dừng lại, không kiểm tra JWT nữa
    // }

    // String authHeader = request.getHeader("Authorization");
    // String token = null;
    // String username = null;

    // if (authHeader != null && authHeader.startsWith("Bearer ")) {
    // token = authHeader.substring(7);
    // username = jwtService.extractUsername(token); // decode username từ token
    // }

    // if (username != null &&
    // SecurityContextHolder.getContext().getAuthentication() == null) {
    // UserDetails userDetails = userDetailsService.loadUserByUsername(username);

    // if (jwtService.isTokenValid(token, userDetails)) {
    // UsernamePasswordAuthenticationToken authToken = new
    // UsernamePasswordAuthenticationToken(userDetails,
    // null, userDetails.getAuthorities());
    // authToken.setDetails(new
    // WebAuthenticationDetailsSource().buildDetails(request));
    // SecurityContextHolder.getContext().setAuthentication(authToken); // 🔥 Quan
    // trọng
    // }
    // }
    // try {
    // filterChain.doFilter(request, response);
    // } catch (java.io.IOException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // } catch (ServletException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }
    // }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getServletPath();

        // ✅ Bỏ qua filter cho các URL public (login, register, refreshToken)
        if (path.equals("/api/login") || path.equals("/api/register") || path.equals("/api/refreshToken")) {
            filterChain.doFilter(request, response);
            return;
        }

        // ✅ Lấy token từ cookie (thay vì Authorization header)
        String token = extractTokenFromCookies(request);
        String username = null;

        if (token != null) {
            try {
                username = jwtService.extractUsername(token); // giải mã lấy username
            } catch (ExpiredJwtException e) {
                // ✅ Token hết hạn → trả về 401
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Access token expired");
                return;
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid token");
                return;
            }
        }

        // ✅ Kiểm tra hợp lệ và chưa có authentication trong context
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (jwtService.isTokenValid(token, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,
                        null, userDetails.getAuthorities());

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken); // ✅ set user vào context
            } else {
                // Token hết hạn → trả 401
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Token expired");
                return;
            }
        }
        // System.out.println("Cookies: " + Arrays.toString(request.getCookies()));
        // System.out.println("Token from cookie: " + token);
        // System.out.println("Username: " + username);

        filterChain.doFilter(request, response);
    }

    // ✅ Hàm lấy token từ cookie
    private String extractTokenFromCookies(HttpServletRequest request) {
        if (request.getCookies() == null)
            return null;

        return Arrays.stream(request.getCookies())
                .filter(cookie -> "accessToken".equals(cookie.getName())) // tên cookie backend đã set
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }
}
