package com.example.jewelrystore.Controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.jewelrystore.DTO.LoginDTO;
import com.example.jewelrystore.Form.UserForm.LoginForm;
import com.example.jewelrystore.Form.UserForm.RegisterForm;
import com.example.jewelrystore.Service.AuthService;
import com.example.jewelrystore.Service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
public class AuthController {
    @Autowired
    private UserService userService;
    @Autowired
    private AuthService authService;

    @GetMapping("api/forEverybody")
    public String getMethod() {
        return "This is API for EveryOne without Permission";
    }

    @GetMapping("/api/checkProtocol")
    public String checkProtocol(HttpServletRequest request) {
        return "Scheme: " + request.getScheme(); // sẽ trả về "http" hoặc "https"
    }

    @GetMapping("api/forAdmin")
    public String getMethodName() {
        return "This is API for ADMIN";
    }

    @GetMapping("api/forUser")
    public String getMethodName2() {
        return "This is API for USER";
    }

    @PostMapping("api/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterForm form) {
        userService.create(form);
        return ResponseEntity.ok("Register Successfull");
    }

    // @PostMapping("api/login")
    // public ResponseEntity<LoginDTO> login(@RequestBody LoginForm request) {
    // return ResponseEntity.ok(authService.login(request));
    // }

    @PostMapping("api/login")
    public String login(@RequestBody LoginForm form, HttpServletResponse response) {
        authService.login(form, response);
        return "Login succesfull";
    }

    // @PostMapping("api/refreshToken")
    // public ResponseEntity<LoginDTO> refreshToken(@RequestBody Map<String, String>
    // request) {
    // return ResponseEntity.ok(authService.refreshToken(request));
    // }

    // 🔄 Làm mới access token bằng refresh token
    @PostMapping("/api/refreshToken")
    public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        return authService.refreshAccessToken(request, response);
    }

    // 🔴 Đăng xuất
    @PostMapping("/api/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        return authService.logout(response);
    }

}
