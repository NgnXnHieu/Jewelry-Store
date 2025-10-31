package com.example.jewelrystore.Implement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.jewelrystore.DTO.UserDTO;
import com.example.jewelrystore.Entity.User;
import com.example.jewelrystore.Form.UserForm.RegisterForm;
import com.example.jewelrystore.Form.UserForm.UserUpdateForm;
import com.example.jewelrystore.Mapper.UserMapper;
import com.example.jewelrystore.Repository.UserRepository;
import com.example.jewelrystore.Service.UserService;

@Service

public class UserImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public UserDTO create(RegisterForm form) {
        User created = userRepository.findByUsername(form.getUsername()).orElse(null);
        if (created == null) {
            created = userMapper.toEntity(form);
            created.setRole("USER");
            created.setPassword(passwordEncoder.encode(form.getPassword()));
            userRepository.save(created);
            return userMapper.toUserDTO(created);
        }
        return null;
    }

    @Override
    public UserDTO updateUser(Integer id, UserUpdateForm userUpdateForm) {
        User existing = userRepository.findById(id).orElse(null);
        if (existing != null) {
            userMapper.updateUser(userUpdateForm, existing);
            userRepository.save(existing);
            return userMapper.toUserDTO(existing);
        }
        return null;
    }

    @Override
    public Page<UserDTO> getAllUser(Pageable pageable) {
        return userRepository.findAll(pageable).map(userMapper::toUserDTO);
    }

    @Override
    public UserDTO getUserById(Integer id) {
        return userRepository.findById(id).map(userMapper::toUserDTO).orElse(null);
    }

    @Override
    public void deleteUser(Integer id) {
        userRepository.deleteById(id);
    }

    @Override
    public UserDTO getInfor(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return userMapper.toUserDTO(user);
    }
}
