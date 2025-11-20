package com.example.jewelrystore.Form.WebHook;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SePayWebhookDTO {
    private Long id; // ID giao dịch SePay
    private String accountNumber; // Số TK người nhận
    private String content; // Nội dung: "JEWELRY 105"
    private Double transferAmount; // Số tiền thực nhận
    private String transactionDate; // Thời gian
    // ... các trường khác không quan trọng lắm
}