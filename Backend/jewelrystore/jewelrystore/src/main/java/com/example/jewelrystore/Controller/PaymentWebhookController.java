package com.example.jewelrystore.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payment")
public class PaymentWebhookController {
    // @PostMapping("/webhook")
    // public ResponseEntity<?> handleBankWebhook(@RequestBody BankTransactionDTO
    // transaction) {
    // // 1. Lấy nội dung chuyển khoản (Ví dụ: "THANHTOAN DON 102")
    // String content = transaction.getDescription();

    // // 2. Tách chuỗi để lấy Order ID (Lấy được số 102)
    // Long orderId = extractOrderId(content);

    // // 3. Tìm đơn hàng trong DB
    // Order order = orderRepository.findById(orderId).orElse(null);

    // if (order != null && transaction.getAmount() >= order.getTotalAmount()) {
    // // 4. Check đúng số tiền -> Cập nhật thành công
    // order.setStatus("PAID"); // Đã thanh toán
    // orderRepository.save(order);
    // }

    // return ResponseEntity.ok("Received");
    // }
}