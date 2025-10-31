package com.example.jewelrystore.Mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.jewelrystore.DTO.AddressDTO;
import com.example.jewelrystore.Entity.Address;
import com.example.jewelrystore.Form.AddressForm.AddressCreateForm;
import com.example.jewelrystore.Form.AddressForm.AddressUpdateForm;
import com.example.jewelrystore.Repository.UserRepository;

@Mapper(componentModel = "spring")
public abstract class AddressMapper {
    @Autowired
    UserRepository userReposiroty;

    public abstract Address toEntity(AddressCreateForm addressCreateForm);

    @Mapping(source = "user.id", target = "userId")
    public abstract AddressDTO toAddressDTO(Address address);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void updateAddress(AddressUpdateForm addressUpdateForm, @MappingTarget Address address);

    @AfterMapping
    void mapRelations(AddressCreateForm addressCreateForm, @MappingTarget Address address) {
        Integer id = addressCreateForm.getUserId();
        if (id != null) {
            address.setUser(userReposiroty.findById(id)
                    .orElseThrow(() -> new RuntimeException("User not Found")));
        }
    }
}
