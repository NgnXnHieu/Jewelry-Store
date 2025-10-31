package com.example.jewelrystore.Implement;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.example.jewelrystore.DTO.LoginDTO;
import com.example.jewelrystore.Form.UserForm.LoginForm;
import com.example.jewelrystore.Repository.UserRepository;
import com.example.jewelrystore.Service.AuthService;
import com.example.jewelrystore.Service.JwtService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class AuthImpl implements AuthService {
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    JwtService jwtService;
    @Autowired
    UserRepository repository;
    @Autowired
    CustomUserDetailService customUserDetailService;

    // public LoginDTO login(LoginForm request) {
    // // Xác thực tài khoản, mật khẩu
    // Authentication authentication = authenticationManager.authenticate(
    // new UsernamePasswordAuthenticationToken(request.getUsername(),
    // request.getPassword()));
    // // Lấy role của user sau khi xác thực
    // String role = authentication.getAuthorities().stream()
    // .findFirst()
    // .map(Object::toString)
    // .orElse("USER"); // nếu không có quyền thì mặc định là USER
    // // Sau khi gọi authenticationManager.authenticate() AuthenticationManager tự
    // // động gọi AuthenticationProvider --> gọi loadUserByUsername(username)
    // String jwt = jwtService.generateToken(authentication.getName(), role);
    // String refreshToken =
    // jwtService.generateRefreshToken(authentication.getName(), role);
    // return new LoginDTO(jwt, refreshToken);
    // }

    // public LoginDTO refreshToken(Map<String, String> request) {
    // String refreshToken = request.get("refreshToken");
    // if (refreshToken.isEmpty() || refreshToken == null) {
    // throw new RuntimeException("Refresh Token is empty or null");
    // }
    // String username;
    // String role;
    // try {
    // username = jwtService.extractUsername(refreshToken);
    // role = jwtService.extractRole(refreshToken); // 👈 lấy role từ refresh token
    // } catch (Exception e) {
    // throw new RuntimeException("Invalid Token");
    // }

    // String newAccessToken = jwtService.generateToken(username, role);
    // String newRefreshToken = jwtService.generateRefreshToken(username, role);
    // return new LoginDTO(newAccessToken, newRefreshToken);
    // }

    // 🔄 Refresh token bằng cookie
    @Override
    public ResponseEntity<?> refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {
        // 1️⃣ Lấy refresh token từ cookie
        String refreshToken = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }

        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing refresh token");
        }

        // 2️⃣ Kiểm tra token hợp lệ
        try {
            String username = jwtService.extractUsername(refreshToken);
            String role = jwtService.extractRole(refreshToken);

            UserDetails userDetails = customUserDetailService.loadUserByUsername(username);

            // kiểm tra hạn
            if (!jwtService.isTokenValid(refreshToken,
                    userDetails)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
            }

            // 3️⃣ Sinh token mới
            String newAccessToken = jwtService.generateToken(username, role);
            String newRefreshToken = jwtService.generateRefreshToken(username, role);

            Cookie accessCookie = new Cookie("accessToken", newAccessToken);
            accessCookie.setHttpOnly(true);
            accessCookie.setSecure(false);
            accessCookie.setPath("/");
            accessCookie.setMaxAge(60 * 15); // 15 phút

            // Ghi đè lại refresh token cũ bằng cookie mới
            Cookie refreshCookie = new Cookie("refreshToken", newRefreshToken);
            refreshCookie.setHttpOnly(true);
            refreshCookie.setSecure(false);
            refreshCookie.setPath("/");
            refreshCookie.setMaxAge(60 * 60 * 24 * 7); // 7 ngày

            response.addCookie(accessCookie);
            response.addCookie(refreshCookie);

            return ResponseEntity.ok("Access token refreshed successfully");

        } catch (

        Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token: " + e.getMessage());
        }
    }

    @Override
    public void login(LoginForm request, HttpServletResponse response) {
        // Xác thực tài khoản, mật khẩu
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        // Lấy role của user sau khi xác thực
        String role = authentication.getAuthorities().stream()
                .findFirst()
                .map(Object::toString)
                .orElse("USER"); // nếu không có quyền thì mặc định là USER
        // Sau khi gọi authenticationManager.authenticate() AuthenticationManager tự
        // động gọi AuthenticationProvider --> gọi loadUserByUsername(username)
        String accessToken = jwtService.generateToken(authentication.getName(), role);
        String refreshToken = jwtService.generateRefreshToken(authentication.getName(), role);

        // Tạo cookies
        Cookie accessCookie = new Cookie("accessToken", accessToken);
        accessCookie.setHttpOnly(true);
        accessCookie.setSecure(true); // Bật khi deploy HTTPS
        accessCookie.setPath("/");
        accessCookie.setMaxAge(60 * 15); // 15 phút

        Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(60 * 60 * 24 * 7); // 7 ngày

        accessCookie.setSecure(false);
        refreshCookie.setSecure(false);

        // 🔹 4. Gắn cookies vào response
        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);
    }

    // 🔴 Đăng xuất — xóa cookies
    @Override
    public ResponseEntity<?> logout(HttpServletResponse response) {
        Cookie accessCookie = new Cookie("accessToken", null);
        accessCookie.setHttpOnly(true);
        accessCookie.setSecure(false);
        accessCookie.setPath("/");
        accessCookie.setMaxAge(0); // Xóa cookie ngay lập tức

        Cookie refreshCookie = new Cookie("refreshToken", null);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(false);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(0); // Xóa cookie ngay lập tức

        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);

        return ResponseEntity.ok("Logged out successfully");
    }
}
