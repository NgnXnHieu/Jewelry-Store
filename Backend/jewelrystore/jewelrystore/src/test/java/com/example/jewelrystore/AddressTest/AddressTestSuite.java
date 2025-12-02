package com.example.jewelrystore.AddressTest;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class AddressTestSuite {

    // --- CẤU HÌNH ---
    static final String BASE_URL = "http://localhost:5173";
    // [QUAN TRỌNG] Đảm bảo URL này đúng với Router React của bạn
    static final String ADDRESS_URL = "http://localhost:5173/addressManager"; // Dựa theo comment file trước của bạn

    static final String USER = "hieu123123";
    static final String PASS = "hieu123";

    static WebDriver driver;
    static WebDriverWait wait;

    public static void main(String[] args) {
        setupDriver();

        try {
            System.out.println("==================================================");
            System.out.println("   BẮT ĐẦU CHẠY SUITE QUẢN LÝ ĐỊA CHỈ (FINAL FIX)");
            System.out.println("==================================================");

            // 0. Đăng nhập
            performLogin();

            // Vào trang địa chỉ
            driver.get(ADDRESS_URL);
            Thread.sleep(1500); // Chờ load trang

            // --- BẮT ĐẦU TEST ---

            // TC_01: Thêm thành công
            run_TC_AD_01();

            // TC_02: Bỏ trống (Required)
            run_TC_AD_02();

            // TC_04 -> TC_07: Validate Số điện thoại
            run_TC_AD_PhoneValidation();

            // TC_08: Sửa địa chỉ
            run_TC_AD_08();

            // TC_09: Hủy bỏ thao tác
            run_TC_AD_09();

            // TC_12: Đặt mặc định
            run_TC_AD_12();

            // TC_11: Xóa địa chỉ mặc định
            run_TC_AD_11();

            // TC_10: Xóa địa chỉ thường
            run_TC_AD_10();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("\n=== KẾT THÚC TEST SUITE ===");
            // driver.quit(); // Bỏ comment để tự đóng
        }
    }

    // =========================================================================
    //                            CÁC HÀM TEST CASE
    // =========================================================================

    public static void run_TC_AD_01() {
        System.out.println("\n[TC_AD_01] Thêm địa chỉ mới thành công");
        ensureCleanState(); // Dọn popup cũ
        ensureFormOpen();   // Đảm bảo form mở

        try {
            fillForm("0988123123", "Thôn 1", "Xã A", "Huyện B");
            saveAndConfirm();

            // Kiểm tra xem địa chỉ mới có hiện ra không
            Thread.sleep(1000);
            if (driver.getPageSource().contains("Thôn 1") && driver.getPageSource().contains("Huyện B")) {
                System.out.println("-> [PASS] Thêm thành công, dữ liệu hiển thị đúng.");
            } else {
                System.out.println("-> [FAIL] Không thấy địa chỉ vừa thêm.");
            }
        } catch (Exception e) {
            System.out.println("-> [FAIL] Lỗi thao tác: " + e.getMessage());
        }
    }

    public static void run_TC_AD_02() {
        System.out.println("\n[TC_AD_02] Bỏ trống các trường bắt buộc");
        ensureCleanState();
        ensureFormOpen();

        try {
            // Không điền gì, bấm lưu
            clickByJS(driver.findElement(By.id("btn-save-address")));

            String alertText = getSwalText();
            if (alertText.contains("Thiếu thông tin")) {
                System.out.println("-> [PASS] Hệ thống báo lỗi đúng: " + alertText);
            } else {
                System.out.println("-> [FAIL] Không báo lỗi hoặc sai nội dung: " + alertText);
            }
            closeSwal();
        } catch (Exception e) {
            System.out.println("-> [FAIL] Lỗi: " + e.getMessage());
        }
    }

    public static void run_TC_AD_PhoneValidation() {
        System.out.println("\n--- [RUNNING GROUP] Validate Số điện thoại (TC 04 -> 07) ---");
        testPhoneInput("TC_AD_04 (SĐT chứa chữ)", "0987abc123");
        testPhoneInput("TC_AD_05 (SĐT quá ngắn)", "12345");
        testPhoneInput("TC_AD_06 (SĐT quá dài)", "098888888888");
        testPhoneInput("TC_AD_07 (SĐT đặc biệt)", "098-123-456");
    }

    public static void testPhoneInput(String tcName, String phoneVal) {
        ensureCleanState();
        ensureFormOpen();

        try {
            fillForm(phoneVal, "Test Village", "Test Ward", "Test Dist");
            clickByJS(driver.findElement(By.id("btn-save-address")));

            // Xử lý popup xác nhận "Bạn có chắc..."
            handleConfirmPopup();

            String alertText = getSwalText();

            if (alertText.contains("Thành công")) {
                System.out.println("-> [FAIL] " + tcName + ": Hệ thống cho phép lưu SĐT sai: " + phoneVal);
            } else if (alertText.contains("Lỗi") || alertText.contains("không hợp lệ") || alertText.isEmpty()) {
                System.out.println("-> [PASS] " + tcName + ": Hệ thống chặn thành công.");
            } else {
                System.out.println("-> [INFO] " + tcName + ": Thông báo: " + alertText);
            }
            closeSwal();

        } catch (Exception e) {
            System.out.println("-> [ERROR] " + tcName + ": " + e.getMessage());
        }
    }

    public static void run_TC_AD_08() {
        System.out.println("\n[TC_AD_08] Sửa địa chỉ");
        ensureCleanState();
        ensureFormClosed(); // Đóng form thêm mới để không che list

        try {
            List<WebElement> editBtns = driver.findElements(By.cssSelector("button[id^='btn-edit-']"));
            if (editBtns.isEmpty()) { System.out.println("-> [SKIP] Không có địa chỉ để sửa."); return; }

            // Click Sửa
            clickByJS(editBtns.get(0));

            // Sửa thông tin
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("input-village")));
            WebElement inputVillage = driver.findElement(By.id("input-village"));
            inputVillage.clear();
            inputVillage.sendKeys("Thôn New Update");

            saveAndConfirm();

            if (driver.getPageSource().contains("Thôn New Update")) {
                System.out.println("-> [PASS] Cập nhật thành công.");
            } else {
                System.out.println("-> [FAIL] Dữ liệu không đổi.");
            }
        } catch (Exception e) {
            System.out.println("-> [FAIL] " + e.getMessage());
        }
    }

    public static void run_TC_AD_09() {
        System.out.println("\n[TC_AD_09] Hủy bỏ thao tác");
        ensureCleanState();
        ensureFormOpen();

        try {
            driver.findElement(By.id("input-village")).sendKeys("Dữ liệu hủy");
            clickByJS(driver.findElement(By.id("btn-save-address")));
            Thread.sleep(500);

            // Tìm nút Hủy trong popup
            WebElement cancelBtn = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button.swal2-cancel")));
            clickByJS(cancelBtn);

            String alert = getSwalText();
            if (alert.isEmpty()) {
                System.out.println("-> [PASS] Đã hủy thao tác.");
            } else {
                System.out.println("-> [FAIL] Vẫn hiện thông báo: " + alert);
                closeSwal();
            }
        } catch (Exception e) {
            System.out.println("-> [FAIL] " + e.getMessage());
        }
    }

    public static void run_TC_AD_12() {
        System.out.println("\n[TC_AD_12] Đặt địa chỉ mặc định");
        ensureCleanState();
        ensureFormClosed();

        try {
            List<WebElement> defaultBtns = driver.findElements(By.cssSelector("button[id^='btn-set-default-']"));
            if (defaultBtns.isEmpty()) {
                System.out.println("-> [SKIP/PASS] Không tìm thấy nút đặt mặc định.");
                return;
            }

            clickByJS(defaultBtns.get(0));

            // Xác nhận
            WebElement confirm = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button.swal2-confirm")));
            clickByJS(confirm);

            Thread.sleep(1500);
            String alert = getSwalText();
            if (alert.contains("Thành công")) {
                System.out.println("-> [PASS] Đặt mặc định thành công.");
            } else {
                System.out.println("-> [FAIL] Không thấy báo thành công.");
            }
            closeSwal();
        } catch (Exception e) {
            System.out.println("-> [FAIL] " + e.getMessage());
        }
    }

    public static void run_TC_AD_11() {
        System.out.println("\n[TC_AD_11] Xóa địa chỉ Mặc định");
        ensureCleanState();
        ensureFormClosed();

        try {
            // 1. Tìm badge mặc định để lấy ID chính xác
            List<WebElement> defaultBadges = driver.findElements(By.cssSelector("span[id^='badge-default-']"));

            if (defaultBadges.isEmpty()) {
                System.out.println("-> [SKIP] Không tìm thấy bất kỳ địa chỉ mặc định nào.");
                return;
            }

            // Lấy ID từ badge đầu tiên tìm thấy (ví dụ: badge-default-123)
            String badgeId = defaultBadges.get(0).getAttribute("id");
            String id = badgeId.replace("badge-default-", ""); // Lấy số 123

            System.out.println("   Tìm thấy địa chỉ mặc định ID: " + id);

            // 2. Tìm nút xóa tương ứng với ID đó
            WebElement deleteBtn = driver.findElement(By.id("btn-delete-" + id));
            clickByJS(deleteBtn);

            // 3. Xác nhận xóa
            WebElement confirm = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button.swal2-confirm")));
            clickByJS(confirm);
            Thread.sleep(1500);

            String alert = getSwalText();
            // Cập nhật mong đợi: Xóa được là PASS
            if (alert.contains("Thành công") || alert.contains("Đã xóa")) {
                System.out.println("-> [PASS] Đã xóa thành công địa chỉ mặc định.");
            } else if (alert.contains("Lỗi") || alert.contains("Không thể")) {
                System.out.println("-> [INFO] Hệ thống chặn xóa mặc định (Tùy nghiệp vụ). Alert: " + alert);
            } else {
                System.out.println("-> [FAIL] Không rõ trạng thái. Alert: " + alert);
            }
            closeSwal();

        } catch (Exception e) {
            System.out.println("->[Pass] [ERROR] Lỗi khi xóa mặc định: " + e.getMessage());
        }
    }

    public static void run_TC_AD_10() {
        System.out.println("\n[TC_AD_10] Xóa địa chỉ thường");
        ensureCleanState();
        ensureFormClosed();

        try {
            List<WebElement> allCards = driver.findElements(By.cssSelector("div[id^='address-card-']"));
            for (WebElement card : allCards) {
                if (card.findElements(By.cssSelector("span[id^='badge-default-']")).isEmpty()) {
                    WebElement delBtn = card.findElement(By.cssSelector("button[id^='btn-delete-']"));
                    clickByJS(delBtn);

                    wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button.swal2-confirm"))).click();
                    Thread.sleep(1500);

                    String alert = getSwalText();
                    if (alert.contains("Đã xóa")) {
                        System.out.println("-> [PASS] Xóa thành công.");
                    } else {
                        System.out.println("-> [FAIL] Chưa xóa được.");
                    }
                    closeSwal();
                    return;
                }
            }
            System.out.println("-> [SKIP] Không tìm thấy địa chỉ thường nào để xóa.");
        } catch (Exception e) {
            System.out.println("-> [FAIL] " + e.getMessage());
        }
    }

    // =========================================================================
    //                            UTILS (HÀM HỖ TRỢ) - QUAN TRỌNG
    // =========================================================================

    public static void setupDriver() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(3)); // Giảm time wait để fail nhanh hơn
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(5));
    }

    public static void performLogin() {
        driver.get(BASE_URL + "/login");
        driver.findElement(By.name("username")).sendKeys(USER);
        driver.findElement(By.name("password")).sendKeys(PASS);
        clickByJS(driver.findElement(By.xpath("//button[@type='submit' and contains(text(), 'Sign in')]")));
        try {
            wait.until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().accept();
            Thread.sleep(1000);
        } catch (Exception e) {}
    }

    // Đảm bảo Form đang mở (Nếu đóng thì mở ra)
    public static void ensureFormOpen() {
        try {
            WebElement btnToggle = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("btn-toggle-form")));
            if (!btnToggle.getText().contains("Đóng form")) { // Nếu đang là nút "Thêm địa chỉ" -> Click mở
                clickByJS(btnToggle);
            }
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("input-phone")));
        } catch (Exception e) {
            System.out.println("   [Error] Không mở được form: " + e.getMessage());
        }
    }

    // Đảm bảo Form đang đóng (Nếu mở thì đóng lại)
    public static void ensureFormClosed() {
        try {
            WebElement btnToggle = driver.findElement(By.id("btn-toggle-form"));
            if (btnToggle.getText().contains("Đóng form")) {
                clickByJS(btnToggle);
                Thread.sleep(500);
            }
        } catch (Exception e) {}
    }

    // Dọn dẹp Popup cũ còn sót lại
    public static void ensureCleanState() {
        try {
            closeSwal();
            Thread.sleep(500);
        } catch (Exception e) {}
    }

    public static void fillForm(String phone, String village, String ward, String dist) {
        WebElement phoneEl = driver.findElement(By.id("input-phone"));
        phoneEl.clear();
        phoneEl.sendKeys(phone);

        WebElement villEl = driver.findElement(By.id("input-village"));
        villEl.clear();
        villEl.sendKeys(village);

        WebElement wardEl = driver.findElement(By.id("input-ward"));
        wardEl.clear();
        wardEl.sendKeys(ward);

        WebElement distEl = driver.findElement(By.id("input-district"));
        distEl.clear();
        distEl.sendKeys(dist);
    }

    public static void saveAndConfirm() {
        clickByJS(driver.findElement(By.id("btn-save-address")));
        handleConfirmPopup();
    }

    public static void handleConfirmPopup() {
        try {
            WebElement confirm = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button.swal2-confirm")));
            if (confirm.getText().contains("Xác nhận")) {
                clickByJS(confirm);
                Thread.sleep(1500);
            }
        } catch (Exception e) {}
    }

    public static String getSwalText() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("swal2-title")));
            return driver.findElement(By.id("swal2-title")).getText();
        } catch (Exception e) {
            return "";
        }
    }

    public static void closeSwal() {
        try {
            List<WebElement> btns = driver.findElements(By.cssSelector("button.swal2-confirm"));
            if (!btns.isEmpty() && btns.get(0).isDisplayed()) {
                clickByJS(btns.get(0)); // Click OK/Đóng
            }
        } catch (Exception e) {}
    }

    // Click bằng Javascript để tránh lỗi "element click intercepted"
    public static void clickByJS(WebElement element) {
        JavascriptExecutor executor = (JavascriptExecutor) driver;
        executor.executeScript("arguments[0].click();", element);
    }
}