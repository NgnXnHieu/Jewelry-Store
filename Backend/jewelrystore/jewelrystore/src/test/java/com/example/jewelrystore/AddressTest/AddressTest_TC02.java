package com.example.jewelrystore.AddressTest;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class AddressTest_TC02 {

    // --- CẤU HÌNH ---
    static final String BASE_URL = "http://localhost:5173";
    static final String ADDRESS_URL = "http://localhost:5173/addressManager"; // Sửa lại nếu URL khác

    static final String USER = "hieu123123";
    static final String PASS = "hieu123";

    static WebDriver driver;
    static WebDriverWait wait;

    public static void main(String[] args) {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            System.out.println("=== CHẠY DEMO TC_AD_02: KIỂM TRA BỎ TRỐNG TRƯỜNG BẮT BUỘC ===");

            // 1. Đăng nhập
            performLogin();

            // 2. Vào trang Quản lý địa chỉ
            driver.get(ADDRESS_URL);
            System.out.println("2. Đã vào trang địa chỉ.");

            // 3. Nhấn nút "Thêm địa chỉ mới" (Dùng ID)
            WebElement btnAdd = wait.until(ExpectedConditions.elementToBeClickable(By.id("btn-toggle-form")));
            btnAdd.click();
            System.out.println("3. Đã mở form thêm mới.");

            // 4. Không nhập gì cả, nhấn nút "Thêm địa chỉ" (Lưu) luôn
            WebElement btnSave = driver.findElement(By.id("btn-save-address"));
            btnSave.click();
            System.out.println("4. Đã nhấn nút Lưu (khi để trống).");

            // 5. Kiểm tra Popup báo lỗi (SweetAlert)
            try {
                // SweetAlert thường có title ID là 'swal2-title' hoặc class 'swal2-title'
                WebElement swalTitle = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("swal2-title")));
                String titleText = swalTitle.getText();

                // Kiểm tra nội dung text alert trong file AddressManager.jsx: "⚠️ Thiếu thông tin"
                if (titleText.contains("Thiếu thông tin")) {
                    System.out.println("--> KẾT QUẢ: [PASS] Hệ thống đã báo lỗi: " + titleText);
                } else {
                    System.out.println("--> KẾT QUẢ: [FAIL] Hiện popup nhưng sai nội dung: " + titleText);
                }

                // Đóng popup
                driver.findElement(By.cssSelector("button.swal2-confirm")).click();

            } catch (Exception e) {
                System.out.println("--> KẾT QUẢ: [FAIL] Không thấy popup báo lỗi xuất hiện.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // driver.quit();
        }
    }

    public static void performLogin() {
        driver.get(BASE_URL + "/login");
        driver.findElement(By.name("username")).sendKeys(USER);
        driver.findElement(By.name("password")).sendKeys(PASS);
        driver.findElement(By.xpath("//button[@type='submit' and contains(text(), 'Sign in')]")).click();
        try {
            wait.until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().accept();
            Thread.sleep(1000);
        } catch (Exception e) {}
    }
}