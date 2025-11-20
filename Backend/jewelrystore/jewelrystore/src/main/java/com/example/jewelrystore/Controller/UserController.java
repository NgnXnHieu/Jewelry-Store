package com.example.jewelrystore.Controller;

import java.util.List;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.jewelrystore.DTO.LoginDTO;
import com.example.jewelrystore.DTO.UserDTO;
import com.example.jewelrystore.Form.UserForm.LoginForm;
import com.example.jewelrystore.Form.UserForm.RegisterForm;
import com.example.jewelrystore.Form.UserForm.UserCreateForm;
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

    @GetMapping("/humanResources")
    public Page<UserDTO> getAllHumanResources(Pageable pageable) {
        return userService.getAllHumanresources(pageable);
    }

    // Admin tạo tài khoản cho nhân viên
    @PostMapping
    public ResponseEntity create(@RequestParam("username") String username,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("role") String role,
            @RequestParam("full_name") String full_name,
            @RequestParam("phone") String phone,
            @RequestParam(value = "image_url", required = false) String imageUrl,
            @RequestParam("is_active") Boolean is_active,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        UserCreateForm userCreateForm = new UserCreateForm(username, email, password, full_name, phone, role, is_active,
                imageUrl);
        UserDTO created = userService.create(userCreateForm, image);
        return ResponseEntity.status(HttpStatus.CREATED).body(created != null ? created : "Tài khoản đã tồn tại");
    }

    @GetMapping("/{id}")
    public UserDTO getUserById(@PathVariable Integer id) {
        return userService.getUserById(id);
    }

    // Admin Update thông tin nhân viên
    @PutMapping("/{id}")
    public ResponseEntity update(
            @PathVariable @Valid Integer id,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "role", required = false) String role,
            @RequestParam(value = "full_name", required = false) String full_name,
            @RequestParam(value = "phone", required = false) String phone,
            @RequestParam(value = "image_url", required = false) String imageUrl,
            @RequestParam(value = "is_active", required = false) Boolean is_active,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        UserUpdateForm userUpdateForm = new UserUpdateForm(email, full_name, phone, role, is_active, imageUrl);
        UserDTO updated = userService.updateUser(id, userUpdateForm, image);
        return (updated != null) ? ResponseEntity.ok(updated != null ? updated : "Tài khoản không tồn tại")
                : ResponseEntity.notFound().build();
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

    // Tổng số khách hàng
    @GetMapping("/customers/count")
    public ResponseEntity<Long> getTotalCustomers() {
        Long total = userService.getCountByRole("CUSTOMER");
        return ResponseEntity.ok(total);
    }

    // Tổng số khách hàng theo đơn vị thời gian
    @GetMapping("/customers/count/unitTime")
    public ResponseEntity<Long> getTotalCustomers(@RequestParam String time) {
        Long total = userService.getCountUsersByRoleBetweenDates(time, "CUSTOMER");
        return ResponseEntity.ok(total);
    }

    // Tổng số nhân viên (admin, staff, shipper) theo thời gian
    @GetMapping("/humanResources/count/unitTime")
    public ResponseEntity<Long> getTotalHumanResources(@RequestParam String time) {
        Long total = userService.getCountUserNotRoleBetweenDates(time, "CUSTOMER");
        return ResponseEntity.ok(total);
    }

    // Đếm tổng số nhân viên hiện tại
    @GetMapping("/humanResources/count")
    public Long getCountByNotRole() {
        return userService.getCountByRoleNot("CUSTOMER");
    }

    // Danh sách tổng số nhân viên theo 4 năm mới nhất
    @GetMapping("customers/chart/years")
    public List<Long> getCountCustomersByYears() {
        return userService.getCountCustomersByYears();
    }

    // Danh sách tổng số nhân viên theo 4 năm mới nhất
    @GetMapping("customers/chart/months")
    public List<Long> getCountCustomersByMonths() {
        return userService.getCountCustomersByMonths();
    }// Danh sách tổng số nhân viên theo 4 năm mới nhất

    @GetMapping("customers/chart/days")
    public List<Long> getCountCustomersByDays() {
        return userService.getCountCustomersByDays();
    }

    @GetMapping("humanresources/chart/years")
    public List<Long> getCountHumanresourcesByYears() {
        return userService.getCountHumanResoucesByYears();
    }

    @GetMapping("humanresources/chart/months")
    public List<Long> getCountHumanresourcesByMonths() {
        return userService.getCountHumanResoucesByMonths();
    }

    @GetMapping("humanresources/chart/days")
    public List<Long> getCountHumanresourcesByDays() {
        return userService.getCountHumanResoucesByDays();
    }

}
