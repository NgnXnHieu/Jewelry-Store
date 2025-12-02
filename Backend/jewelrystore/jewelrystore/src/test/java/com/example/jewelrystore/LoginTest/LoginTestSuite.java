package com.example.jewelrystore.LoginTest;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class LoginTestSuite {

    // --- CẤU HÌNH ---
    static final String BASE_URL = "http://localhost:5173";
    static final String LOGIN_URL = BASE_URL + "/login";
    static final String ADMIN_URL = BASE_URL + "/admin";
    static final String HOME_URL = BASE_URL + "/";

    // --- TÀI KHOẢN TEST ---
    static final String USER_VALID = "hieu123123";
    static final String PASS_VALID = "hieu123";
    static final String ADMIN_VALID = "hieu123456"; // Cần quyền ADMIN
    static final String PASS_ADMIN = "hieu123456";

    static WebDriver driver;
    static WebDriverWait wait;

    public static void main(String[] args) {
        setupDriver();

        try {
            System.out.println("==================================================");
            System.out.println("   BẮT ĐẦU CHẠY SUITE ĐĂNG NHẬP (11 TEST CASES)");
            System.out.println("==================================================");

            // TC_01: Rỗng cả hai
            run_TC_01();

            // TC_02: Đăng nhập đúng (User)
            run_TC_02();

            // TC_03: Tài khoản rỗng
            run_TC_03();

            // TC_04: Mật khẩu rỗng
            run_TC_04();

            // TC_05: Sai mật khẩu
            run_TC_05();

            // TC_06: Tài khoản không tồn tại
            run_TC_06();

            // TC_07: Brute-force (Nhập sai 5 lần)
            run_TC_07();

            // TC_08: Show/Hide mật khẩu
            run_TC_08();

            // TC_09: Admin đăng nhập
            run_TC_09();

            // TC_10: Customer truy cập Admin
            run_TC_10();

            // TC_11: Admin mua hàng
            run_TC_11();

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

    public static void run_TC_01() {
        System.out.println("\n[TC_01] Tài khoản, mật khẩu rỗng");
        refreshPage();

        // Không nhập gì, nhấn Login
        clickLogin();

        // Kiểm tra validation HTML5
        WebElement userField = driver.findElement(By.id("login-username"));
        if (!checkHtml5Validation(userField)) {
            System.out.println("-> [FAIL] Không có cảnh báo nhập thiếu thông tin.");
        } else {
            System.out.println("-> [PASS] Hệ thống cảnh báo nhập thiếu thông tin.");
        }
    }

    public static void run_TC_02() {
        System.out.println("\n[TC_02] Kiểm tra tài khoản, mật khẩu đúng");
        refreshPage();

        fillLogin(USER_VALID, PASS_VALID);
        clickLogin();

        String alert = getAlertTextAndAccept();
        if (alert.contains("successful")) {
            System.out.println("-> [PASS] Đăng nhập thành công.");
        } else {
            System.out.println("-> [FAIL] Không báo thành công. Alert: " + alert);
        }
    }

    public static void run_TC_03() {
        System.out.println("\n[TC_03] Kiểm tra tài khoản rỗng");
        refreshPage();

        driver.findElement(By.id("login-password")).sendKeys(PASS_VALID);
        clickLogin();

        WebElement userField = driver.findElement(By.id("login-username"));
        if (checkHtml5Validation(userField)) {
            System.out.println("-> [PASS] Hệ thống cảnh báo không bỏ trống tài khoản.");
        } else {
            System.out.println("-> [FAIL] Không có cảnh báo.");
        }
    }

    public static void run_TC_04() {
        System.out.println("\n[TC_04] Kiểm tra mật khẩu rỗng");
        refreshPage();

        driver.findElement(By.id("login-username")).sendKeys(USER_VALID);
        clickLogin();

        WebElement passField = driver.findElement(By.id("login-password"));
        if (checkHtml5Validation(passField)) {
            System.out.println("-> [PASS] Hệ thống yêu cầu nhập mật khẩu.");
        } else {
            System.out.println("-> [FAIL] Không có cảnh báo.");
        }
    }

    public static void run_TC_05() {
        System.out.println("\n[TC_05] Đăng nhập sai mật khẩu");
        refreshPage();

        fillLogin(USER_VALID, "SaiMatKhau");
        clickLogin();

        String alert = getAlertTextAndAccept();
        if (alert.contains("failed") || alert.contains("không chính xác")) {
            System.out.println("-> [PASS] Báo lỗi đúng: " + alert);
        } else {
            System.out.println("-> [FAIL] Không báo lỗi sai mật khẩu. Alert: " + alert);
        }
    }

    public static void run_TC_06() {
        System.out.println("\n[TC_06] Đăng nhập tài khoản không tồn tại");
        refreshPage();

        fillLogin("khongco@gmail.com", "123456");
        clickLogin();

        String alert = getAlertTextAndAccept();
        if (alert.contains("failed") || alert.contains("không tồn tại")) {
            System.out.println("-> [PASS] Báo lỗi đúng: " + alert);
        } else {
            System.out.println("-> [FAIL] Phản hồi sai. Alert: " + alert);
        }
    }

    public static void run_TC_07() {
        System.out.println("\n[TC_07] Kiểm tra khóa tài khoản (Brute-force)");
        refreshPage();

        // Thử nhập sai 5 lần
        for (int i = 1; i <= 5; i++) {
            fillLogin(USER_VALID, "PassSai" + i);
            clickLogin();
            getAlertTextAndAccept();
        }

        // Lần 6: Nhập đúng
        System.out.println("   Lần 6: Nhập đúng pass...");
        fillLogin(USER_VALID, PASS_VALID);
        clickLogin();

        String alert = getAlertTextAndAccept();
        if (alert.contains("successful")) {
            System.out.println("-> [FAIL] Tài khoản KHÔNG bị khóa (Cho phép nhập sai vô hạn).");
        } else if (alert.contains("lock") || alert.contains("khóa")) {
            System.out.println("-> [PASS] Tài khoản đã bị khóa.");
        } else {
            System.out.println("-> [INFO] Alert khác: " + alert);
        }
    }

    public static void run_TC_08() {
        System.out.println("\n[TC_08] Kiểm tra hiển thị mật khẩu (Show/Hide)");
        refreshPage();

        // Tìm icon con mắt (Giả sử ID là toggle-password)
        try {
            driver.findElement(By.id("login-password")).sendKeys("Test1234");
            WebElement toggleBtn = driver.findElement(By.className("toggle-password")); // Cần class này trong React
            toggleBtn.click();

            String type = driver.findElement(By.id("login-password")).getAttribute("type");
            if (type.equals("text")) {
                System.out.println("-> [PASS] Mật khẩu đã hiện rõ.");
            } else {
                System.out.println("-> [FAIL] Mật khẩu vẫn ẩn.");
            }
        } catch (Exception e) {
            System.out.println("-> [FAIL] Chức năng này chưa có trên giao diện (Code React thiếu icon).");
        }
    }

    public static void run_TC_09() {
        System.out.println("\n[TC_09] Admin đăng nhập thành công");
        refreshPage();

        fillLogin(ADMIN_VALID, PASS_ADMIN);
        clickLogin();

        String alert = getAlertTextAndAccept();
        if (alert.contains("successful")) {
            try { Thread.sleep(2000); } catch(Exception e){}
            if (driver.getCurrentUrl().contains("admin")) {
                System.out.println("-> [PASS] Chuyển hướng vào Admin Dashboard thành công.");
            } else {
                System.out.println("-> [FAIL] Không vào được trang Admin. URL: " + driver.getCurrentUrl());
            }
        } else {
            System.out.println("-> [FAIL] Đăng nhập Admin thất bại.");
        }
    }

    public static void run_TC_10() {
        System.out.println("\n[TC_10] Customer cố tình truy cập trang Admin");
        // 1. Login Customer
        refreshPage();
        fillLogin(USER_VALID, PASS_VALID);
        clickLogin();
        getAlertTextAndAccept();
        try { Thread.sleep(1000); } catch(Exception e){}

        // 2. Gõ URL Admin
        System.out.println("   Đang truy cập: " + ADMIN_URL);
        driver.get(ADMIN_URL);
        try { Thread.sleep(2000); } catch(Exception e){}

        // 3. Check xem có bị đá ra không
        String currentUrl = driver.getCurrentUrl();
        String pageSource = driver.getPageSource();

        if (currentUrl.contains("/admin") && !pageSource.contains("403") && !pageSource.contains("Access Denied")) {
            System.out.println("-> [FAIL] Nguy hiểm! Customer vẫn vào được trang Admin.");
        } else {
            System.out.println("-> [PASS] Hệ thống đã chặn truy cập.");
        }
    }

    public static void run_TC_11() {
        System.out.println("\n[TC_11] Admin truy cập trang giao diện khách hàng");
        // 1. Login Admin
        refreshPage();
        fillLogin(ADMIN_VALID, PASS_ADMIN);
        clickLogin();
        getAlertTextAndAccept();
        try { Thread.sleep(1000); } catch(Exception e){}

        // 2. Vào trang chủ mua hàng
        driver.get(HOME_URL);
        try { Thread.sleep(1000); } catch(Exception e){}

        if (driver.getCurrentUrl().equals(HOME_URL)) {
            System.out.println("-> [FAIL] Admin vẫn truy cập trang mua hàng bình thường (Theo yêu cầu là phải chặn).");
        } else {
            System.out.println("-> [PASS] Admin bị chặn mua hàng.");
        }
    }

    // =========================================================================
    //                            UTILS (HÀM HỖ TRỢ)
    // =========================================================================

    public static void setupDriver() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(3));
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(5));
    }

    public static void refreshPage() {
        driver.get(LOGIN_URL);
        try { Thread.sleep(500); } catch (Exception e){}
    }

    public static void fillLogin(String u, String p) {
        driver.findElement(By.id("login-username")).clear();
        driver.findElement(By.id("login-username")).sendKeys(u);
        driver.findElement(By.id("login-password")).clear();
        driver.findElement(By.id("login-password")).sendKeys(p);
    }

    public static void clickLogin() {
        driver.findElement(By.id("btn-login")).click();
    }

    public static String getAlertTextAndAccept() {
        try {
            wait.until(ExpectedConditions.alertIsPresent());
            org.openqa.selenium.Alert alert = driver.switchTo().alert();
            String text = alert.getText();
            alert.accept();
            return text;
        } catch (Exception e) {
            return "";
        }
    }

    public static boolean checkHtml5Validation(WebElement element) {
        try {
            // Check thuộc tính "required" và validationMessage của trình duyệt
            JavascriptExecutor js = (JavascriptExecutor) driver;
            boolean isInvalid = (Boolean) js.executeScript("return arguments[0].validity.valueMissing;", element);
            return isInvalid;
        } catch (Exception e) {
            return false;
        }
    }
}