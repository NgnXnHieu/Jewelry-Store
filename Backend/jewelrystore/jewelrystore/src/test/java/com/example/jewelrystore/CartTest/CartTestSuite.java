package com.example.jewelrystore.CartTest;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class CartTestSuite {

    // --- CẤU HÌNH ---
    static final String BASE_URL = "http://localhost:5173";
    static final String USER = "hieu123123";
    static final String PASS = "hieu123";

    // ID sản phẩm Test (Cần tồn tại trong DB, còn hàng)
    static final String PRODUCT_ID_TEST = "1";
    // ID sản phẩm Hết hàng (Để test TC_11)
    static final String PRODUCT_ID_OOS = "4";

    static WebDriver driver;
    static WebDriverWait wait;

    public static void main(String[] args) {
        setupDriver();

        try {
            System.out.println("==================================================");
            System.out.println("   CHẠY AUTOMATION THEO FILE WORD (11 TEST CASES)");
            System.out.println("==================================================");

            // 0. Đăng nhập hệ thống
            performLogin();

            // --- BẮT ĐẦU CHẠY CÁC TEST CASE ---

            run_TC_CA_01(); // Thêm mới
            run_TC_CA_02(); // Thêm trùng
            run_TC_CA_03(); // Tăng số lượng (+)
            run_TC_CA_04(); // Giảm số lượng (-)
            run_TC_CA_05(); // Giảm về 0 (Kiểm tra nút disable)
            run_TC_CA_06(); // Xóa sản phẩm
            run_TC_CA_07(); // Kiểm tra tổng tiền
            run_TC_CA_08(); // Link sản phẩm
            run_TC_CA_09(); // Session (Login/Logout)
            run_TC_CA_10(); // Thêm SP hết hàng

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("\n--------------------------------------------------");
            System.out.println("   KẾT THÚC TEST SUITE.");
            System.out.println("--------------------------------------------------");
            // driver.quit();
        }
    }

    // =========================================================================
    //                            CÁC HÀM TEST CASE
    // =========================================================================

    public static void run_TC_CA_01() {
        System.out.println("\n[TC_CA_01] Thêm mới sản phẩm chưa tồn tại trong giỏ");
        cleanUpCart();
        driver.get(BASE_URL + "/productdetail/" + PRODUCT_ID_TEST);

        try {
            wait.until(ExpectedConditions.elementToBeClickable(By.id("btn-add-to-cart"))).click();
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("notification-success")));

            driver.get(BASE_URL + "/cart");
            if (driver.findElements(By.id("cart-item-" + PRODUCT_ID_TEST)).size() > 0) {
                System.out.println("-> [PASS] Giỏ hàng đã thêm chính xác 1 sản phẩm.");
            } else {
                System.out.println("-> [FAIL] Không tìm thấy sản phẩm trong giỏ.");
            }
        } catch (Exception e) {
            System.out.println("-> [FAIL] Lỗi thao tác: " + e.getMessage());
        }
    }

    public static void run_TC_CA_02() throws InterruptedException {
        System.out.println("\n[TC_CA_02] Thêm SP trùng lặp");
        cleanUpCart();
        driver.get(BASE_URL + "/productdetail/" + PRODUCT_ID_TEST);

        // Thêm lần 1
        driver.findElement(By.id("btn-add-to-cart")).click();
        Thread.sleep(1500);
        // Thêm lần 2
        driver.findElement(By.id("btn-add-to-cart")).click();
        Thread.sleep(1000);

        driver.get(BASE_URL + "/cart");
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("qty-display-" + PRODUCT_ID_TEST)));
            String qty = driver.findElement(By.id("qty-display-" + PRODUCT_ID_TEST)).getText();

            if (Integer.parseInt(qty) == 2) {
                System.out.println("-> [PASS] Số lượng tăng lên 2, không thêm dòng mới.");
            } else {
                System.out.println("-> [FAIL] Số lượng hiển thị: " + qty);
            }
        } catch (Exception e) {
            System.out.println("-> [FAIL] Lỗi kiểm tra.");
        }
    }

    public static void run_TC_CA_03() {
        System.out.println("\n[TC_CA_03] Tăng số lượng (+) trong giỏ");
        prepareCartWithOneProduct(); // SL = 1

        try {
            WebElement btnInc = driver.findElement(By.id("btn-increase-" + PRODUCT_ID_TEST));
            WebElement qtyDisplay = driver.findElement(By.id("qty-display-" + PRODUCT_ID_TEST));
            WebElement totalPrice = driver.findElement(By.id("item-total-" + PRODUCT_ID_TEST));
            WebElement unitPrice = driver.findElement(By.id("item-price-" + PRODUCT_ID_TEST));

            btnInc.click();
            Thread.sleep(1500); // Chờ update

            int qty = Integer.parseInt(qtyDisplay.getText());
            long total = parsePrice(totalPrice.getText());
            long unit = parsePrice(unitPrice.getText());

            if (qty == 2 && total == unit * 2) {
                System.out.println("-> [PASS] Số lượng tăng lên 2. Tổng tiền tính đúng.");
            } else {
                System.out.println("-> [FAIL] Tính toán sai. Qty=" + qty + ", Total=" + total);
            }
        } catch (Exception e) {
            System.out.println("-> [FAIL] " + e.getMessage());
        }
    }

    public static void run_TC_CA_04() {
        System.out.println("\n[TC_CA_04] Giảm số lượng (-) khi SL > 1");
        // Cần SL = 2
        cleanUpCart();
        addProductToCart(2);
        driver.get(BASE_URL + "/cart");

        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("cart-item-" + PRODUCT_ID_TEST)));
            WebElement btnDec = driver.findElement(By.id("btn-decrease-" + PRODUCT_ID_TEST));
            WebElement qtyDisplay = driver.findElement(By.id("qty-display-" + PRODUCT_ID_TEST));

            btnDec.click();
            Thread.sleep(1500);

            int qty = Integer.parseInt(qtyDisplay.getText());
            if (qty == 1) {
                System.out.println("-> [PASS] Số lượng giảm còn 1. Tính toán đúng.");
            } else {
                System.out.println("-> [FAIL] Số lượng không giảm.");
            }
        } catch (Exception e) {
            System.out.println("-> [FAIL] " + e.getMessage());
        }
    }

    public static void run_TC_CA_05() {
        System.out.println("\n[TC_CA_05] Giảm số lượng về 0 (Mong đợi: Không trừ)");
        prepareCartWithOneProduct(); // SL = 1

        try {
            WebElement btnDec = driver.findElement(By.id("btn-decrease-" + PRODUCT_ID_TEST));

            // Check logic: Nếu button bị disable -> Pass
            if (!btnDec.isEnabled()) {
                System.out.println("-> [PASS] Nút trừ bị khóa, không thể giảm về 0.");
            } else {
                // Nếu không disable, click thử
                btnDec.click();
                Thread.sleep(1000);
                String qty = driver.findElement(By.id("qty-display-" + PRODUCT_ID_TEST)).getText();
                if (qty.equals("1")) {
                    System.out.println("-> [PASS] Click nhưng số lượng vẫn là 1.");
                } else {
                    System.out.println("-> [FAIL] Số lượng bị giảm xuống " + qty);
                }
            }
        } catch (Exception e) {
            System.out.println("-> [FAIL] " + e.getMessage());
        }
    }

    public static void run_TC_CA_06() {
        System.out.println("\n[TC_CA_06] Xóa sản phẩm");
        prepareCartWithOneProduct();

        try {
            driver.findElement(By.id("btn-delete-" + PRODUCT_ID_TEST)).click();

            // Handle SweetAlert
            WebElement confirm = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("button.swal2-confirm")));
            confirm.click();
            Thread.sleep(1500);
            try { driver.findElement(By.cssSelector("button.swal2-confirm")).click(); } catch(Exception ex){}

            if (driver.findElements(By.id("cart-item-" + PRODUCT_ID_TEST)).isEmpty()) {
                System.out.println("-> [PASS] Xóa thành công. SP biến mất.");
            } else {
                System.out.println("-> [FAIL] SP vẫn còn trong giỏ.");
            }
        } catch (Exception e) {
            System.out.println("-> [FAIL] " + e.getMessage());
        }
    }

    public static void run_TC_CA_07() {
        System.out.println("\n[TC_CA_07] Kiểm tra Tổng tiền");
        // Add 2 sản phẩm để test tính toán
        cleanUpCart();
        addProductToCart(2);
        driver.get(BASE_URL + "/cart");

        try {
            // Chọn tất cả để tính tổng
            WebElement chkAll = driver.findElement(By.id("chk-select-all"));
            if (!chkAll.isSelected()) chkAll.click();
            Thread.sleep(1000);

            long totalWeb = parsePrice(driver.findElement(By.id("cart-total-amount")).getText());
            long itemTotal = parsePrice(driver.findElement(By.id("item-total-" + PRODUCT_ID_TEST)).getText());

            if (totalWeb == itemTotal && totalWeb > 0) {
                System.out.println("-> [PASS] Hiển thị đúng: " + totalWeb + " VNĐ.");
            } else {
                System.out.println("-> [FAIL] Hiển thị sai: " + totalWeb);
            }
        } catch (Exception e) {
            System.out.println("-> [FAIL] " + e.getMessage());
        }
    }

    public static void run_TC_CA_08() {
        System.out.println("\n[TC_CA_08] Link sản phẩm (Click tên/ảnh)");
        // File word báo FAIL (Không chuyển hướng). Ta sẽ verify xem nó có Fail thật không.
        prepareCartWithOneProduct();

        try {
            String urlBefore = driver.getCurrentUrl();
            // Click tên SP
            driver.findElement(By.id("item-name-" + PRODUCT_ID_TEST)).click();
            Thread.sleep(1000);
            String urlAfter = driver.getCurrentUrl();

            if (!urlBefore.equals(urlAfter)) {
                System.out.println("-> [PASS] Đã chuyển hướng sang trang chi tiết.");
            } else {
                System.out.println("-> [FAIL] Không chuyển hướng .");
            }
        } catch (Exception e) {
            System.out.println("-> [FAIL] " + e.getMessage());
        }
    }

    public static void run_TC_CA_09() throws InterruptedException {
        System.out.println("\n[TC_CA_9] Lưu giỏ hàng (Session/Login lại)");
        prepareCartWithOneProduct();

        // Logout
        driver.get(BASE_URL + "/login"); // Hoặc nút logout nếu có
        driver.manage().deleteAllCookies();
        System.out.println("   Đã Logout...");

        // Login lại
        performLogin();
        driver.get(BASE_URL + "/cart");
        Thread.sleep(1500);

        if (!driver.findElements(By.id("cart-item-" + PRODUCT_ID_TEST)).isEmpty()) {
            System.out.println("-> [PASS] Giỏ hàng vẫn còn nguyên SP sau khi login lại.");
        } else {
            System.out.println("-> [FAIL] Giỏ hàng bị mất.");
        }
    }

    public static void run_TC_CA_10() {
        System.out.println("\n[TC_CA_10] Thêm SP hết hàng");
        // Cần ID SP hết hàng (PRODUCT_ID_OOS)
        driver.get(BASE_URL + "/productdetail/" + PRODUCT_ID_OOS);

        try {
            WebElement btnAdd = driver.findElement(By.id("btn-add-to-cart"));

            // Check xem nút có bị disable không
            if (!btnAdd.isEnabled()) {
                System.out.println("-> [PASS] Nút thêm giỏ hàng bị khóa với SP hết hàng.");
            } else {
                btnAdd.click();
                // Check xem có báo lỗi không
                try {
                    // Giả sử có alert báo lỗi
                    wait.until(ExpectedConditions.alertIsPresent());
                    System.out.println("-> [PASS] Hệ thống báo lỗi khi thêm SP hết hàng.");
                } catch (Exception e) {
                    System.out.println("-> [FAIL] Vẫn thêm được vào giỏ hàng.");
                }
            }
        } catch (Exception e) {
            System.out.println("-> [INFO] Không tìm thấy SP hết hàng ID=" + PRODUCT_ID_OOS + " để test.");
        }
    }

    // =========================================================================
    //                            HÀM HỖ TRỢ
    // =========================================================================

    public static void setupDriver() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(5));
    }

    public static void performLogin() {
        driver.get(BASE_URL + "/login");
        driver.findElement(By.name("username")).sendKeys(USER);
        driver.findElement(By.name("password")).sendKeys(PASS);
        // Login button
        driver.findElement(By.xpath("//button[@type='submit' and contains(text(), 'Sign in')]")).click();
        try {
            wait.until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().accept();
            Thread.sleep(1000);
        } catch (Exception e) {}
    }

    public static void cleanUpCart() {
        driver.get(BASE_URL + "/cart");
        try {
            Thread.sleep(1500);
            List<WebElement> deleteBtns = driver.findElements(By.cssSelector("button[id^='btn-delete-']"));
            if (!deleteBtns.isEmpty()) {
                System.out.println("   [CleanUp] Đang dọn " + deleteBtns.size() + " SP cũ...");
                for (int i=0; i<deleteBtns.size(); i++) {
                    List<WebElement> current = driver.findElements(By.cssSelector("button[id^='btn-delete-']"));
                    if(current.isEmpty()) break;
                    current.get(0).click();
                    wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("button.swal2-confirm"))).click();
                    Thread.sleep(1500);
                    try {
                        WebElement ok = driver.findElement(By.cssSelector("button.swal2-confirm"));
                        if(ok.isDisplayed()) ok.click();
                    } catch(Exception ex){}
                    Thread.sleep(500);
                }
            }
        } catch (Exception e) {}
    }

    public static void addProductToCart(int times) {
        driver.get(BASE_URL + "/productdetail/" + PRODUCT_ID_TEST);
        try {
            WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(By.id("btn-add-to-cart")));
            for(int i=0; i<times; i++) {
                btn.click();
                Thread.sleep(1000);
            }
        } catch (Exception e) {}
    }

    public static void prepareCartWithOneProduct() {
        cleanUpCart();
        addProductToCart(1);
        driver.get(BASE_URL + "/cart");
        try { Thread.sleep(1500); } catch (Exception e){}
    }

    public static long parsePrice(String price) {
        try {
            return Long.parseLong(price.replaceAll("[^0-9]", ""));
        } catch (Exception e) { return 0; }
    }
}