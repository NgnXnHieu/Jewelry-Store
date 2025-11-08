package com.example.jewelrystore.Service;

import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.example.jewelrystore.DTO.LoginDTO;
import com.example.jewelrystore.Form.UserForm.LoginForm;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {
    // LoginDTO login(LoginForm request);

    ResponseEntity<?> login(LoginForm request, HttpServletResponse response);

    // LoginDTO refreshToken(Map<String, String> request);

    ResponseEntity<?> refreshAccessToken(HttpServletRequest request, HttpServletResponse response);

    ResponseEntity<?> logout(HttpServletResponse response);
}
