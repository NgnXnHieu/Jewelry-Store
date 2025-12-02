package com.example.jewelrystore.CartTest;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class CartTest_TC01 {

    // CẤU HÌNH
    static final String BASE_URL = "http://localhost:5173";
    static final String PRODUCT_ID_TEST = "1"; // ID sản phẩm dùng để test (đảm bảo tồn tại)

    // TÀI KHOẢN (Theo yêu cầu)
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
            System.out.println("=== CHẠY DEMO TC_CA_01: THÊM MỚI VÀO GIỎ (DÙNG ID) ===");

            // 1. Đăng nhập
            performLogin();

            // 2. Vào trang chi tiết sản phẩm
            driver.get(BASE_URL + "/productdetail/" + PRODUCT_ID_TEST);
            System.out.println("2. Đã vào trang sản phẩm ID: " + PRODUCT_ID_TEST);

            WebElement nameEl = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("product-detail-name")));
            String productName = nameEl.getText();
            System.out.println("   -> Tên sản phẩm: " + productName);

            // 3. Nhấn nút Thêm vào giỏ
            WebElement addToCartBtn = driver.findElement(By.id("btn-add-to-cart"));
            addToCartBtn.click();
            System.out.println("3. Đã click nút 'Thêm vào giỏ' (ID: btn-add-to-cart)");

            // 4. Kiểm tra thông báo thành công
            try {
                WebElement notify = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("notification-success")));
                System.out.println("4. [PASS] Đã hiện thông báo: " + notify.getText());
            } catch (Exception e) {
                System.out.println("4. [WARN] Không bắt được thông báo (ID: notification-success không xuất hiện).");
            }

            // 5. Vào giỏ hàng kiểm tra
            driver.get(BASE_URL + "/cart");
            Thread.sleep(2000); // Đợi API load giỏ hàng

            String cartItemId = "cart-item-" + PRODUCT_ID_TEST;
            boolean exists = driver.findElements(By.id(cartItemId)).size() > 0;

            if (exists) {
                // Kiểm tra kỹ hơn: So sánh tên sản phẩm trong giỏ
                String nameInCart = driver.findElement(By.id("item-name-" + PRODUCT_ID_TEST)).getText();
                if (nameInCart.equals(productName)) {
                    System.out.println("--> KẾT QUẢ: [PASS] TC_CA_01 - Sản phẩm '" + nameInCart + "' đã nằm trong giỏ hàng.");
                } else {
                    System.out.println("--> KẾT QUẢ: [WARN] Có sản phẩm nhưng tên không khớp hoàn toàn.");
                }
            } else {
                System.out.println("--> KẾT QUẢ: [FAIL] TC_CA_01 - Không tìm thấy card sản phẩm có ID: " + cartItemId);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
//             driver.quit(); // Mở dòng này nếu muốn tự đóng trình duyệt
        }
    }

    public static void performLogin() {
        driver.get(BASE_URL + "/login");

        // Điền thông tin
        driver.findElement(By.name("username")).sendKeys(USER);
        driver.findElement(By.name("password")).sendKeys(PASS);

        // Click nút Sign in (Vẫn dùng XPath này để tránh nhầm với nút Tab)
        driver.findElement(By.xpath("//button[@type='submit' and contains(text(), 'Sign in')]")).click();

        // Xử lý Alert "Login successful"
        try {
            wait.until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().accept();
            Thread.sleep(1000);
        } catch (Exception e) {}
    }
}