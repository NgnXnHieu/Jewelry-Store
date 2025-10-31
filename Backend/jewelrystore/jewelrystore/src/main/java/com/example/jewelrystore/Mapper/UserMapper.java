package com.example.jewelrystore.Mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.example.jewelrystore.DTO.UserDTO;
import com.example.jewelrystore.Entity.User;
import com.example.jewelrystore.Form.UserForm.RegisterForm;
import com.example.jewelrystore.Form.UserForm.UserUpdateForm;

@Mapper(componentModel = "spring")

public abstract class UserMapper {
    public abstract User toEntity(RegisterForm userCreateForm);

    public abstract UserDTO toUserDTO(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void updateUser(UserUpdateForm form,
            @MappingTarget User user);

}
