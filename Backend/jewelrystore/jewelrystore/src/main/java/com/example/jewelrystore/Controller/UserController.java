package com.example.jewelrystore.Controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.jewelrystore.DTO.LoginDTO;
import com.example.jewelrystore.DTO.UserDTO;
import com.example.jewelrystore.Form.UserForm.LoginForm;
import com.example.jewelrystore.Form.UserForm.RegisterForm;
import com.example.jewelrystore.Form.UserForm.UserUpdateForm;
import com.example.jewelrystore.Service.AuthService;
import com.example.jewelrystore.Service.UserService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/users")
@Validated
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping
    public Page<UserDTO> getAll(Pageable pageable) {
        return userService.getAllUser(pageable);
    }

    @PostMapping
    public ResponseEntity create(@RequestBody @Valid RegisterForm userCreateForm) {
        UserDTO created = userService.create(userCreateForm);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public UserDTO getUserById(@PathVariable Integer id) {
        return userService.getUserById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity update(@PathVariable Integer id, @RequestBody UserUpdateForm userUpdateForm) {
        UserDTO updated = userService.updateUser(id, userUpdateForm);
        return (updated != null) ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Integer id) {
        userService.deleteUser(id);
        return "Xóa thành công";
    }

    @GetMapping("/infor")
    public UserDTO getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return null; // hoặc ném exception 401
        }
        return userService.getInfor(userDetails.getUsername());

    }

    // @GetMapping("/api/infor")
    // public String getCurrentUser() {
    // return "haha";

    // }

    // @GetMapping("/api/infor")
    // public UserDTO getCurrentUser(Authentication authentication) {
    // System.out.println(authentication);
    // if (authentication == null)
    // return null;
    // UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    // return userService.getInfor(userDetails.getUsername());
    // }

}
