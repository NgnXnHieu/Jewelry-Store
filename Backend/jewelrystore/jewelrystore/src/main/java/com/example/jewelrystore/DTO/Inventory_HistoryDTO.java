package com.example.jewelrystore.DTO;

import java.time.LocalDateTime;

import com.example.jewelrystore.Entity.Product;
import com.example.jewelrystore.Entity.User;

import io.micrometer.common.lang.NonNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Inventory_HistoryDTO {
    private Integer id;
    private LocalDateTime date;
    // private Integer productId; // lấy từ Product
    // private Integer userId; // lấy từ User
    private Long importQuantity;
    private Long currentQuantity;
    private Integer userId;
    private String userFullName;
    private Integer productId;
    private String image_url;
    private String productName;
}
