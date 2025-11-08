package com.example.jewelrystore.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.example.jewelrystore.DTO.UserDTO;
import com.example.jewelrystore.Form.UserForm.RegisterForm;
import com.example.jewelrystore.Form.UserForm.UserCreateForm;
import com.example.jewelrystore.Form.UserForm.UserUpdateForm;

public interface UserService {
    UserDTO register(RegisterForm userCreateForm);

    UserDTO create(UserCreateForm createForm, MultipartFile image);

    UserDTO updateUser(Integer id, UserUpdateForm userUpdateForm, MultipartFile image);

    Page<UserDTO> getAllUser(Pageable pageable);

    Page<UserDTO> getAllHumanresources(Pageable pageable);

    UserDTO getUserById(Integer id);

    void deleteUser(Integer id);

    UserDTO getInfor(String username);
}
