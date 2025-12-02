import React, { useState, useEffect, use } from "react";
import { useLocation, useNavigate, useParams } from "react-router-dom";
// import { QRCodeSVG } from "qrcode.react";
import styles from "./QRTransfer.module.css";
import defaultUrl from "../../../api/defaultUrl";
import axiosInstance from "../../../api/axiosInstance";
import Swal from "sweetalert2";
const QRTransfer = () => {
    const { checkoutId } = useParams();
    // const location = useLocation();
    const navigate = useNavigate();
    const [qrCodeUrl, setQrCodeUrl] = useState("");
    const [orderItems, setOrderItems] = useState([]);
    const [total, setTotal] = useState(0);
    const [address, setAddress] = useState("");
    const [phone, setPhone] = useState("");
    const fetchCheckoutDetails = async () => {
        try {
            const response = await axiosInstance.get(`/checkout/${checkoutId}`)
                .then((res) => {
                    // console.log("Checkout Data:", res.data);
                    setQrCodeUrl(res.data.qr);
                    setOrderItems(res.data.checkout_Items);
                    let detailAddress = `${res.data.address.village}, ${res.data.address.ward}, ${res.data.address.district}`;
                    let calculatedTotal = res.data.checkout_Items.reduce((sum, item) => sum + item.totalPrice, 0);
                    setTotal(calculatedTotal);
                    setAddress(detailAddress);
                    setPhone(res.data.address.phone);
                    // console.log("QR Content:", qrCodeUrl);
                    // console.log("OrderItems:", orderItems);
                    // console.log("address:", address);
                    // console.log("phone:", phone);
                    // console.log("total:", total);
                });

        } catch (error) {
            console.error("Error fetching checkout details:", error);
        }
    };
    useEffect(() => {
        fetchCheckoutDetails();
    }, []);
    const [timeLeft, setTimeLeft] = useState(900); // 15 phút
    const [copied, setCopied] = useState(false);

    // Thông tin ngân hàng (có thể thay đổi theo nhu cầu)
    const bankInfo = {
        bankName: "BIDV",
        accountNumber: "21410003253607",
        accountName: "NGUYEN XUAN HIEU",
        amount: total,
        content: `JEWELRY`, // Mã đơn hàng
    };

    // // Tạo nội dung QR code theo chuẩn VietQR
    // const qrContent = `2|99|${bankInfo.accountNumber}|${bankInfo.accountName}|${bankInfo.bankName}|${bankInfo.amount}|${bankInfo.content}|0|0|${total}`;

    // Đếm ngược thời gian
    useEffect(() => {
        if (timeLeft <= 0) return;
        const timer = setInterval(() => {
            setTimeLeft((prev) => prev - 1);
        }, 1000);

        return () => clearInterval(timer);
    }, [timeLeft]);

    useEffect(() => {
        if (timeLeft <= 0) return;

        // 1. Khai báo biến timer trước (dùng let) để hàm checkStatus có thể truy cập
        let timer;

        const checkStatus = async () => {
            try {
                const res = await axiosInstance.get(`/checkout/${checkoutId}/checkStatus`);
                console.log("Status:", res.data);

                if (res.data === true) {
                    // 2. QUAN TRỌNG: Xóa timer ngay lập tức khi thành công
                    clearInterval(timer);

                    Swal.fire({
                        icon: "success",
                        title: "Thanh toán thành công!",
                        timer: 3000,
                        showConfirmButton: true,
                        confirmButtonText: "OK",
                    }).then((result) => {
                        navigate("/order", { replace: true });
                    });
                }
            } catch (error) {
                console.error(error);
            }
        };

        // Gọi lần đầu tiên ngay lập tức
        checkStatus();

        // 3. Gán ID của interval vào biến timer đã khai báo ở trên
        timer = setInterval(checkStatus, 5000);

        // Cleanup khi component unmount
        return () => clearInterval(timer);
    }, [checkoutId]); // Bạn có thể cần thêm timeLeft vào dependency nếu muốn

    // Format thời gian
    const formatTime = (seconds) => {
        const mins = Math.floor(seconds / 60);
        const secs = seconds % 60;
        return `${mins.toString().padStart(2, "0")}:${secs.toString().padStart(2, "0")}`;
    };

    // Copy số tài khoản
    const handleCopy = (text) => {
        navigator.clipboard.writeText(text);
        setCopied(true);
        setTimeout(() => setCopied(false), 2000);
    };

    // Xác nhận đã chuyển khoản
    const handleConfirmPayment = () => {
        navigate("/order", { replace: true });
    };

    if (!orderItems.length) {
        return (
            <div className={styles.emptyContainer}>
                <div className={styles.emptyIcon}>❌</div>
                <h3>Không tìm thấy thông tin đơn hàng!</h3>
                <button onClick={() => navigate("/")}>Quay về trang chủ</button>
            </div>
        );
    }

    return (
        <div className={styles.qrContainer}>
            {/* Header */}
            <div className={styles.header}>
                <div className={styles.headerContent}>
                    <div className={styles.headerIcon}>💳</div>
                    <h1 className={styles.headerTitle}>Chuyển khoản thanh toán</h1>
                    <p className={styles.headerSubtitle}>
                        Vui lòng chuyển khoản trong thời gian quy định
                    </p>
                </div>
            </div>

            {/* Countdown Timer */}
            <div className={styles.timerBox}>
                <div className={styles.timerIcon}>⏱️</div>
                <div className={styles.timerContent}>
                    <span className={styles.timerLabel}>Thời gian còn lại</span>
                    <span className={styles.timerValue}>{formatTime(timeLeft)}</span>
                </div>
            </div>

            <div className={styles.contentGrid}>
                {/* QR Code & Bank Info Section */}
                <div className={styles.leftSection}>
                    {/* QR Code */}
                    <div className={styles.qrCard}>
                        <h2 className={styles.sectionTitle}>
                            <span>📱</span> Quét mã QR để thanh toán
                        </h2>
                        <div className={styles.qrWrapper}>
                            <div className={styles.qrBox}>
                                <img src={`${qrCodeUrl}`} alt="" />
                            </div>
                            <p className={styles.qrInstruction}>
                                Mở ứng dụng ngân hàng và quét mã QR
                            </p>
                        </div>
                    </div>

                    {/* Bank Information */}
                    <div className={styles.bankCard}>
                        <h2 className={styles.sectionTitle}>
                            <span>🏦</span> Thông tin chuyển khoản
                        </h2>

                        <div className={styles.bankInfoList}>
                            <div className={styles.bankInfoItem}>
                                <span className={styles.bankLabel}>Ngân hàng</span>
                                <span className={styles.bankValue}>{bankInfo.bankName}</span>
                            </div>

                            <div className={styles.bankInfoItem}>
                                <span className={styles.bankLabel}>Số tài khoản</span>
                                <div className={styles.copyGroup}>
                                    <span className={styles.bankValue}>{bankInfo.accountNumber}</span>
                                    <button
                                        className={styles.copyButton}
                                        onClick={() => handleCopy(bankInfo.accountNumber)}
                                    >
                                        {copied ? "✓" : "📋"}
                                    </button>
                                </div>
                            </div>

                            <div className={styles.bankInfoItem}>
                                <span className={styles.bankLabel}>Chủ tài khoản</span>
                                <span className={styles.bankValue}>{bankInfo.accountName}</span>
                            </div>

                            <div className={styles.bankInfoItem}>
                                <span className={styles.bankLabel}>Số tiền</span>
                                <span className={`${styles.bankValue} ${styles.amountHighlight}`}>
                                    {total.toLocaleString()}₫
                                </span>
                            </div>

                            <div className={styles.bankInfoItem}>
                                <span className={styles.bankLabel}>Nội dung chuyển khoản</span>
                                <div className={styles.copyGroup}>
                                    <span className={styles.bankValue}>{bankInfo.content}</span>
                                    {/* <button
                                        className={styles.copyButton}
                                        onClick={() => handleCopy(bankInfo.content)}
                                    >
                                        {copied ? "✓" : "📋"}
                                    </button> */}
                                </div>
                            </div>
                        </div>

                        <div className={styles.warningBox}>
                            <div className={styles.warningIcon}>⚠️</div>
                            <div className={styles.warningText}>
                                <strong>Lưu ý:</strong> Vui lòng nhập chính xác nội dung chuyển khoản để đơn hàng được xử lý nhanh nhất
                            </div>
                        </div>
                    </div>

                    {/* Confirm Button */}
                    {/* <button className={styles.confirmButton} onClick={handleConfirmPayment}>
                        <span>✓</span> Tôi đã chuyển khoản
                    </button> */}
                </div>

                {/* Order Summary Section */}
                <div className={styles.rightSection}>
                    {/* Order Items */}
                    <div className={styles.orderCard}>
                        <h2 className={styles.sectionTitle}>
                            <span>🛒</span> Chi tiết đơn hàng
                        </h2>

                        <div className={styles.orderList}>
                            {orderItems.map((item) => (
                                <div key={item.id} className={styles.orderItem}>
                                    <div className={styles.itemImage}>
                                        {item.image_url ? (
                                            <img src={item.image_url} alt="Item" />
                                        ) : (
                                            <div className={styles.noImage}>📦</div>
                                        )}
                                        <span className={styles.itemBadge}>{item.quantity}</span>
                                    </div>
                                    <div className={styles.itemDetails}>
                                        {/* <p className={styles.itemName}>{item.name}</p> */}
                                        <p className={styles.itemPrice}>
                                            {(item.totalPrice / item.quantity).toLocaleString()}₫
                                        </p>
                                        <p className={styles.itemTotal}>
                                            {(item.totalPrice).toLocaleString()}₫
                                        </p>
                                    </div>
                                </div>
                            ))}
                        </div>

                        <div className={styles.orderSummary}>
                            <div className={styles.summaryRow}>
                                <span>Tạm tính</span>
                                <span>{total.toLocaleString()}₫</span>
                            </div>
                            <div className={styles.summaryRow}>
                                <span>Phí vận chuyển</span>
                                <span className={styles.freeShipping}>Miễn phí</span>
                            </div>
                            <div className={styles.summaryRow}>
                                <span>Giảm giá</span>
                                <span>0₫</span>
                            </div>
                        </div>

                        <div className={styles.totalBox}>
                            <span className={styles.totalLabel}>Tổng thanh toán</span>
                            <span className={styles.totalValue}>{total.toLocaleString()}₫</span>
                        </div>
                    </div>

                    {/* Delivery Address */}
                    <div className={styles.addressCard}>
                        <h2 className={styles.sectionTitle}>
                            <span>📍</span> Địa chỉ giao hàng
                        </h2>

                        <div className={styles.addressInfo}>
                            <div className={styles.addressRow}>
                                <div className={styles.addressIcon}>📞</div>
                                <div className={styles.addressDetails}>
                                    <p className={styles.addressLabel}>Số điện thoại</p>
                                    <p className={styles.addressValue}>{phone}</p>
                                </div>
                            </div>

                            <div className={styles.addressRow}>
                                <div className={styles.addressIcon}>🏠</div>
                                <div className={styles.addressDetails}>
                                    <p className={styles.addressLabel}>Địa chỉ</p>
                                    <p className={styles.addressValue}>{address}</p>
                                </div>
                            </div>
                        </div>
                    </div>

                    {/* Payment Instructions */}
                    <div className={styles.instructionCard}>
                        <h3 className={styles.instructionTitle}>📝 Hướng dẫn thanh toán</h3>
                        <ol className={styles.instructionList}>
                            <li>Mở ứng dụng ngân hàng trên điện thoại</li>
                            <li>Quét mã QR hoặc nhập thông tin chuyển khoản</li>
                            <li>Kiểm tra kỹ thông tin và số tiền</li>
                            <li>Nhập chính xác nội dung chuyển khoản</li>
                            <li>Xác nhận chuyển khoản</li>
                            <li>Nhấn "Tôi đã chuyển khoản" sau khi hoàn tất</li>
                        </ol>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default QRTransfer;
