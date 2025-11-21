package com.example.jewelrystore.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.jewelrystore.Form.WebHook.SePayWebhookDTO;
import com.example.jewelrystore.Service.CheckoutService;

@RestController
@RequestMapping("/api/webhook")
public class WebhookController {

    @Autowired
    private CheckoutService checkoutService;

    // API Key lấy ở Bước 1
    private static final String MY_SEPAY_API_KEY = "spsk_test_MejgWMGWhAfhDmPLkM1CMDJKWbfD97Gq";

    @PostMapping("/sepay")
    public ResponseEntity<String> handleSePayWebhook(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody SePayWebhookDTO payload) {
        // --- DEBUG LOG (Thêm đoạn này) ---
        System.out.println(">>> NHẬN WEBHOOK SEPAY <<<");
        System.out.println("Header Auth nhận được: " + authHeader);
        System.out.println("API Key của tôi trong code: " + MY_SEPAY_API_KEY);
        // --------------------------------
        // 1. Bảo mật: Kiểm tra API Key
        // SePay gửi header dạng: "Bearer SP-KEY-12345..."
        if (authHeader == null || !authHeader.contains(MY_SEPAY_API_KEY)) {
            return ResponseEntity.status(403).body("Unauthorized"); // Chặn nếu sai key
        }

        // 2. Gọi Service xử lý
        checkoutService.processPaymentSuccess(payload);

        // 3. Trả về success
        System.out.println("Thanh toán thành công");
        return ResponseEntity.ok("{\"success\": true}");
    }
}