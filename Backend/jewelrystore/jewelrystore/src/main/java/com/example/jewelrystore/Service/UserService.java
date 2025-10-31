package com.example.jewelrystore.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.jewelrystore.DTO.UserDTO;
import com.example.jewelrystore.Form.UserForm.RegisterForm;
import com.example.jewelrystore.Form.UserForm.UserUpdateForm;

public interface UserService {
    UserDTO create(RegisterForm userCreateForm);

    UserDTO updateUser(Integer id, UserUpdateForm userUpdateForm);

    Page<UserDTO> getAllUser(Pageable pageable);

    UserDTO getUserById(Integer id);

    void deleteUser(Integer id);

    UserDTO getInfor(String username);
}
