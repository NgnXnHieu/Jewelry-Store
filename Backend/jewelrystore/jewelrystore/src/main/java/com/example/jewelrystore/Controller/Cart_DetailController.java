package com.example.jewelrystore.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.jewelrystore.DTO.Cart_DetailDTO;
import com.example.jewelrystore.Entity.User;
import com.example.jewelrystore.Form.Cart_DetailForm.Cart_DetailCreateForm;
import com.example.jewelrystore.Form.Cart_DetailForm.Cart_DetailUpdateForm;
import com.example.jewelrystore.Repository.UserRepository;
import com.example.jewelrystore.Service.Cart_DetailService;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/cart_details")
@Validated
public class Cart_DetailController {
    @Autowired
    private Cart_DetailService cart_DetailService;
    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public Page<Cart_DetailDTO> getAll(Pageable pageable) {
        return cart_DetailService.getAll(pageable);
    }

    @PostMapping
    public ResponseEntity create(@AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid Cart_DetailCreateForm productCreateForm) {
        if (userDetails == null) {
            return null; // hoặc ném exception 401
        }
        Cart_DetailDTO created = cart_DetailService.createCart_Detail(userDetails.getUsername(), productCreateForm);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public Cart_DetailDTO getProductById(@PathVariable Integer id) {
        return cart_DetailService.getCart_DetailById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity update(@PathVariable Integer id, @RequestBody Cart_DetailUpdateForm userUpdateForm,
            @AuthenticationPrincipal UserDetails details) {
        if (details == null) {
            // return null; // hoặc ném exception 401
            throw new AccessDeniedException("You don't have permission to change quantity this item");

        }
        Cart_DetailDTO updated = cart_DetailService.updateCart_Detail(id, userUpdateForm, details.getUsername());
        return (updated != null) ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Integer id, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return null; // hoặc ném exception 401
        }
        cart_DetailService.deleteCart_Detail(id, userDetails.getUsername());
        return "Xóa thành công";
    }

    @GetMapping("cart_detailsByUserName")
    public Page<Cart_DetailDTO> getCart_DetailsByUserId(@AuthenticationPrincipal UserDetails userDetails,
            Pageable pageable) {
        // 🔹 Cách 2: in ra authentication thực tế khi controller được gọi
        var auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Authentication at controller = " + auth);
        if (userDetails == null) {
            // System.out.println("Hello");
            return null; // hoặc ném exception 401
        }
        return cart_DetailService.getCart_DetailsByUserName(userDetails.getUsername(), pageable);
    }

}
