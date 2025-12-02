package com.example.jewelrystore.LoginTest;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class LoginTest_TC02 {

    // --- CẤU HÌNH ---
    static final String BASE_URL = "http://localhost:5173";
    static final String LOGIN_URL = BASE_URL + "/login";

    // Tài khoản đúng (Theo dữ liệu bạn cung cấp)
    static final String USER_VALID = "hieu123123";
    static final String PASS_VALID = "hieu123";

    static WebDriver driver;
    static WebDriverWait wait;

    public static void main(String[] args) {
        // 1. Khởi tạo Driver
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            System.out.println("=== CHẠY RIÊNG TC_02: ĐĂNG NHẬP THÀNH CÔNG ===");

            // 2. Truy cập trang đăng nhập
            driver.get(LOGIN_URL);
            System.out.println("2. Đã mở trang Login.");

            // 3. Nhập Username (Dùng ID chuẩn)
            WebElement userField = driver.findElement(By.id("login-username"));
            userField.clear();
            userField.sendKeys(USER_VALID);
            System.out.println("3. Đã nhập Username: " + USER_VALID);

            // 4. Nhập Password (Dùng ID chuẩn)
            WebElement passField = driver.findElement(By.id("login-password"));
            passField.clear();
            passField.sendKeys(PASS_VALID);
            System.out.println("4. Đã nhập Password.");

            // 5. Nhấn nút Đăng nhập (Dùng ID chuẩn)
            WebElement btnLogin = driver.findElement(By.id("btn-login"));
            btnLogin.click();
            System.out.println("5. Đã nhấn nút Đăng nhập.");

            // 6. Kiểm tra kết quả (Xử lý Alert & Chuyển hướng)
            try {
                // Chờ Alert xuất hiện (Tối đa 5s)
                wait.until(ExpectedConditions.alertIsPresent());

                org.openqa.selenium.Alert alert = driver.switchTo().alert();
                String alertText = alert.getText();
                System.out.println("!!! THÔNG BÁO TỪ WEB: [" + alertText + "]");

                // Đóng Alert
                alert.accept();

                // Kiểm tra nội dung Alert
                if (alertText.contains("successful") || alertText.contains("thành công")) {
                    System.out.println("-> [CHECK 1 - PASS] Thông báo đúng mong đợi.");
                } else {
                    System.out.println("-> [CHECK 1 - FAIL] Thông báo sai nội dung.");
                }

                // Chờ chuyển hướng trang (Sau khi tắt alert)
                Thread.sleep(2000);
                String currentUrl = driver.getCurrentUrl();
                System.out.println("-> URL hiện tại: " + currentUrl);

                if (!currentUrl.contains("login")) {
                    System.out.println("--> KẾT QUẢ CUỐI CÙNG: [PASS] TC_02 - Đăng nhập và chuyển trang thành công.");
                } else {
                    System.out.println("--> KẾT QUẢ CUỐI CÙNG: [FAIL] TC_02 - Vẫn kẹt ở trang Login.");
                }

            } catch (Exception e) {
                System.out.println("--> [FAIL] Không thấy thông báo nào xuất hiện (Lỗi logic hoặc mạng).");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("=== KẾT THÚC TEST CASE ===");
            // driver.quit(); // Bỏ comment nếu muốn tự động đóng trình duyệt
        }
    }
}