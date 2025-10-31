package com.example.jewelrystore.Implement;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.jewelrystore.Entity.User;
import com.example.jewelrystore.Repository.UserRepository;

@Service
public class CustomUserDetailService implements UserDetailsService {
        @Autowired
        private UserRepository userRepository;

        @Override
        public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                User user = userRepository.findByUsername(username)
                                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

                // Trả về danh sách role
                // List<GrantedAuthority> authorities = user.getRole().stream()
                // .map(role -> new SimpleGrantedAuthority(role.getName()))
                // .toList();

                // Trả về 1 role duy nhất
                List<GrantedAuthority> authorities = List.of(
                                new SimpleGrantedAuthority("ROLE_" + user.getRole()) // 1 role duy nhất
                );
                System.out.println(authorities);
                // Trả về userdetails chứa username, password, role
                return new org.springframework.security.core.userdetails.User(
                                user.getUsername(),
                                user.getPassword(),
                                authorities);
        }

}
