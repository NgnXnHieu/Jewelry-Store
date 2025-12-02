package com.example.jewelrystore.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
public class SecurityConfig {
    @Autowired
    JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // .csrf(AbstractHttpConfigurer::disable)
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // ✅ bật CORS
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/api/refreshToken", "/api/register", "/api/login", "/api/forEverybody",
                                "/api/webhook/sepay")
                        .permitAll() // không cần xác thực
                        .requestMatchers("/api/logout")
                        .hasAnyRole("USER", "STAFF", "MANAGER", "SHIPPER", "ADMIN")
                        .requestMatchers("/api/forAdmin","/api/admin/**")
                        .hasAnyRole("ADMIN","MANAGER","STAFF")
                        .requestMatchers("/api/forUser")
                        .hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/api/users")
                        // .hasRole("ADMIN")
                        .permitAll()
                        // .requestMatchers("/api/products", "/api/products/{id}",
                        // "/api/products/getRelatedProducts/{id}")
                        // .permitAll()
                        .requestMatchers("/api/users/infor", "/api/cart_details/cart_detailsByUserName",
                                "/api/orders/**")
                        .authenticated()
                        // .hasAnyRole("USER", "ADMIN")
                        // .anyRequest().authenticated() // các request khác cần xác thực
                        .anyRequest().permitAll())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 1. SỬA LỖI 403: Thêm khối này
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            // Khi người dùng chưa xác thực (anonymous) truy cập tài nguyên cần bảo vệ
                            // Trả về lỗi 401 thay vì 403
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.getWriter().write("Unauthorized: " + authException.getMessage());
                        }))
                // add 1 vài filer sẽ chạy trước
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        ;

        return http.build();
    }

    // tạo bean authenticationManager để xử lý xác thực
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // ✅ Cấu hình CORS cho phép React truy cập
    // @Bean
    // public CorsConfigurationSource corsConfigurationSource() {
    // CorsConfiguration config = new CorsConfiguration();
    // config.setAllowedOrigins(List.of("http://localhost:5173")); // domain React
    // config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    // config.setAllowedHeaders(List.of("*"));
    // config.setAllowCredentials(true);

    // UrlBasedCorsConfigurationSource source = new
    // UrlBasedCorsConfigurationSource();
    // source.registerCorsConfiguration("/**", config);
    // return source;
    // }

    // ✅ Chỉ giữ lại 1 bản corsConfigurationSource hoàn chỉnh
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(List.of("http://localhost:5173")); // React app
        config.setAllowedHeaders(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.addExposedHeader("Set-Cookie"); // để frontend nhận cookies

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
