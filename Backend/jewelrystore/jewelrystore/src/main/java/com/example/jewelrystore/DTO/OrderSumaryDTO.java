package com.example.jewelrystore.DTO;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
@NoArgsConstructor
public class OrderSumaryDTO {
    private Integer id;
    private LocalDateTime orderDate;
    private Double totalAmount;
    private String status;
    private Integer userId; // chỉ lấy id của user
    private String address;
    private String phone;
    private Long quantity;
}
