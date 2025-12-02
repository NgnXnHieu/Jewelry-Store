import React, { useState, useEffect } from "react";
import axiosInstance from "../../../api/axiosInstance";
import Swal from "sweetalert2";
import { useLocation, useParams } from "react-router-dom";
import styles from "./Checkout.module.css";
import { useNavigate } from "react-router-dom";

const Checkout = () => {
    // const location = useLocation();
    // const { items } = location.state || { items: [] };

    // console.log("Danh sách sản phẩm mua:", items);
    const [addresses, setAddresses] = useState([]);
    const [selectedAddress, setSelectedAddress] = useState(null);
    const [orderItems, setOrderItems] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const navigate = useNavigate()
    const { checkoutId } = useParams();

    // ✅ Lấy thông tin địa chỉ
    useEffect(() => {
        const fetchAddresses = async () => {
            try {
                const allRes = await axiosInstance.get("/addresses/myAddress");
                setAddresses(allRes.data);
                console.log("Địa chỉ nhận hàng:", allRes.data);
            } catch (err) {
                console.error("Lỗi khi lấy địa chỉ:", err);
                setError(err);
            }
        };
        fetchAddresses();
    }, []);

    // ✅ Lấy chi tiết sản phẩm theo id được truyền sang
    useEffect(() => {
        const fetchProducts = async () => {
            // if (!items.length) return;

            try {
                axiosInstance.get(`/checkout/${checkoutId}`)
                    .then((res) => {
                        setOrderItems(res.data.checkout_Items);
                        console.log(res.data);
                        if (res.data.address !== null) {
                            setSelectedAddress(res.data.address);
                        } else {
                            axiosInstance.get("/addresses/defaultAddress")
                                .then((res) => {
                                    setSelectedAddress(res.data);
                                    console.log("Địa chỉ mặc định:", selectedAddress);
                                });

                        }
                    })
                // setOrderItems(detailedItems);
            } catch (err) {
                console.error("Lỗi khi tải sản phẩm:", err);
                setError(err);
            } finally {
                setLoading(false);
            }
        };

        fetchProducts();
    }, []);

    // ✅ Tính tổng tiền
    const total = orderItems.reduce(
        (sum, item) => sum + item.totalPrice,
        0
    );

    const totalQuantity = orderItems.reduce(
        (sum, item) => sum + item.quantity,
        0
    );

    // ✅ Chọn địa chỉ giao hàng
    const handleChangeAddress = async () => {
        if (!addresses.length) {
            Swal.fire("Chưa có địa chỉ nào!", "", "info");
            return;
        }

        let selectedId = selectedAddress?.id || null;

        const addressHtml = addresses
            .map(
                (addr) => `
            <div class="address-option" 
                 data-id="${addr.id}"
                 style="
                    text-align:left; 
                    padding:12px 14px; 
                    border:1.5px solid #ddd; 
                    border-radius:10px; 
                    margin-bottom:10px; 
                    cursor:pointer; 
                    transition:all 0.2s;">
                <strong>${addr.village}, ${addr.ward}, ${addr.district}</strong><br/>
                <span style="color:#555;">SĐT: ${addr.phone}</span>
            </div>
        `
            )
            .join("");

        const style = document.createElement("style");
        style.innerHTML = `
            .swal-address-popup .address-option.active {
                border-color: #1677ff !important;
                background-color: #f0f7ff !important;
                box-shadow: 0 0 6px rgba(22, 119, 255, 0.3) !important;
            }
            .swal-address-popup .address-option:hover {
                background-color: #f5faff !important;
            }
        `;
        document.head.appendChild(style);

        const swal = Swal.fire({
            title: "Chọn địa chỉ giao hàng",
            html: `<div id="address-container" style="max-height:300px; overflow-y:auto;">${addressHtml}</div>`,
            showCancelButton: true,
            confirmButtonText: "Xác nhận",
            cancelButtonText: "Hủy",
            didOpen: () => {
                const container = Swal.getPopup().querySelector("#address-container");
                const options = container.querySelectorAll(".address-option");
                options.forEach((opt) => {
                    if (parseInt(opt.dataset.id) === selectedId) {
                        opt.classList.add("active");
                    }
                    opt.addEventListener("click", () => {
                        options.forEach((o) => o.classList.remove("active"));
                        opt.classList.add("active");
                        selectedId = parseInt(opt.dataset.id);
                    });
                });
            },
            preConfirm: () => selectedId,
            width: "600px",
            customClass: { popup: "swal-address-popup" },
        });

        const result = await swal;

        if (result.isConfirmed && selectedId) {
            const chosen = addresses.find((a) => a.id === selectedId);
            setSelectedAddress(chosen);
            Swal.fire(
                "Đã thay đổi!",
                `Địa chỉ mới: ${chosen.village}, ${chosen.ward}, ${chosen.district}`,
                "success"
            );
        }
    };

    // ✅ Xác nhận đặt hàng
    const handleConfirm = async (e) => {
        e.preventDefault();

        if (!selectedAddress) {
            Swal.fire("Chưa có địa chỉ mặc định!", "Vui lòng thêm địa chỉ trước.", "warning");
            return;
        }

        const paymentMethod = document.querySelector('input[name="paymentMethod"]:checked').value;
        const orderData = {
            // address: `${selectedAddress.village}, ${selectedAddress.ward}, ${selectedAddress.district}`,
            // phone: selectedAddress.phone,
            // idAndQuantityList: orderItems.map((item) => ({
            //     productId: item.id,
            //     quantity: item.quantity,
            // })),
            addressId: selectedAddress.id,
            checkoutId: checkoutId,
        };
        if (paymentMethod === "cod") {
            try {
                orderData["payment_method"] = "COD";
                const result = await Swal.fire({
                    title: "Xác nhận đặt hàng?",
                    text: `Giao tới ${selectedAddress.village}, ${selectedAddress.ward}, ${selectedAddress.district}`,
                    icon: "question",
                    showCancelButton: true,
                    confirmButtonText: "Đặt hàng",
                    cancelButtonText: "Hủy",
                });

                if (result.isConfirmed) {
                    const res = await axiosInstance.post("/checkout/placeOrder", orderData);

                    Swal.fire(
                        "Thành công!",
                        `Đơn hàng đã được tạo thành công!`,
                        "success"
                    );
                    navigate('/order', { replace: true });
                }
            } catch (error) {
                console.error("Lỗi khi tạo đơn hàng:", error);
                Swal.fire("Thất bại!", "Không thể tạo đơn hàng. Vui lòng thử lại!", "error");
            }

            return;
        } else if (paymentMethod === "bank") {
            orderData["payment_method"] = "BANK";
            const res = await axiosInstance.post("/checkout/placeOrder", orderData);
            console.log(res.data);
            // navigate('/qrTransfer', {
            //     state: {
            //         qr: res.data,
            //         orderItems: orderItems,
            //         total: total,
            //         address: `${selectedAddress.village}, ${selectedAddress.ward}, ${selectedAddress.district}`,
            //         phone: selectedAddress.phone
            //     }
            // });
            navigate(`/qrTransfer/${checkoutId}`);
            return;
        }

        Swal.fire("Chưa hỗ trợ!", "Phương thức thanh toán này đang được phát triển.", "info");
    };

    if (loading) {
        return (
            <div className={styles.loadingContainer}>
                <div className={styles.loadingSpinner}></div>
                <p className={styles.loadingText}>Đang tải thông tin đơn hàng...</p>
            </div>
        );
    }

    if (error) {
        return (
            <div className={styles.errorContainer}>
                <div className={styles.errorIcon}>❌</div>
                <h3>Lỗi khi tải dữ liệu!</h3>
                <p>Vui lòng thử lại sau</p>
            </div>
        );
    }

    if (!orderItems.length) {
        return (
            <div className={styles.emptyContainer}>
                <div className={styles.emptyIcon}>🛒</div>
                <h3>Không có sản phẩm để thanh toán!</h3>
                <p>Vui lòng thêm sản phẩm vào giỏ hàng</p>
            </div>
        );
    }

    return (
        <div className={styles.checkoutContainer}>
            {/* Hero Header */}
            <div className={styles.heroHeader}>
                <div className={styles.heroContent}>
                    <div className={styles.heroIcon}>🛍️</div>
                    <h1 className={styles.heroTitle}>Thanh toán đơn hàng</h1>
                    <p className={styles.heroSubtitle}>
                        Hoàn tất đơn hàng của bạn với {totalQuantity} sản phẩm
                    </p>
                </div>
            </div>

            {/* Checkout Steps */}
            <div className={styles.stepsContainer}>
                <div className={`${styles.step} ${styles.stepActive}`}>
                    <div className={styles.stepNumber}>1</div>
                    <span>Thông tin giao hàng</span>
                </div>
                <div className={styles.stepLine}></div>
                <div className={`${styles.step} ${styles.stepActive}`}>
                    <div className={styles.stepNumber}>2</div>
                    <span>Thanh toán</span>
                </div>
                <div className={styles.stepLine}></div>
                <div className={styles.step}>
                    <div className={styles.stepNumber}>3</div>
                    <span>Hoàn thành</span>
                </div>
            </div>

            <div className={styles.checkoutGrid}>
                {/* Thông tin người nhận */}
                <form className={styles.infoSection} onSubmit={handleConfirm}>
                    <div className={styles.sectionCard}>
                        <div className={styles.sectionHeader}>
                            <h2>📍 Thông tin giao hàng</h2>
                        </div>

                        <div className={styles.addressBox}>
                            <div className={styles.addressHeader}>
                                <div className={styles.addressIcon}>📦</div>
                                <div className={styles.addressDetails}>
                                    <p className={styles.addressLabel}>Số điện thoại</p>
                                    <p className={styles.addressValue}>{selectedAddress?.phone}</p>
                                </div>
                            </div>
                            <div className={styles.addressHeader}>
                                <div className={styles.addressIcon}>🏠</div>
                                <div className={styles.addressDetails}>
                                    <p className={styles.addressLabel}>Địa chỉ giao hàng</p>
                                    <p className={styles.addressValue}>
                                        {`${selectedAddress?.village}, ${selectedAddress?.ward}, ${selectedAddress?.district}`}
                                    </p>
                                </div>
                            </div>
                            <button
                                type="button"
                                onClick={handleChangeAddress}
                                className={styles.changeButton}
                            >
                                🔄 Thay đổi địa chỉ
                            </button>
                        </div>

                        <div className={styles.formGroup}>
                            <label className={styles.formLabel}>
                                💬 Ghi chú (nếu có):
                            </label>
                            <textarea
                                name="note"
                                className={styles.formTextarea}
                                placeholder="Nhập ghi chú cho đơn hàng..."
                            ></textarea>
                        </div>
                    </div>

                    <div className={styles.sectionCard}>
                        <div className={styles.sectionHeader}>
                            <h2>💳 Phương thức thanh toán</h2>
                        </div>

                        <div className={styles.paymentMethods}>
                            <label className={styles.paymentOption}>
                                <input
                                    type="radio"
                                    name="paymentMethod"
                                    value="cod"
                                    defaultChecked
                                />
                                <div className={styles.paymentContent}>
                                    <div className={styles.paymentIcon}>💵</div>
                                    <div className={styles.paymentInfo}>
                                        <span className={styles.paymentTitle}>Thanh toán khi nhận hàng (COD)</span>
                                        <span className={styles.paymentDesc}>Thanh toán bằng tiền mặt khi nhận hàng</span>
                                    </div>
                                </div>
                            </label>

                            <label className={styles.paymentOption}>
                                <input
                                    type="radio"
                                    name="paymentMethod"
                                    value="bank"
                                />
                                <div className={styles.paymentContent}>
                                    <div className={styles.paymentIcon}>🏦</div>
                                    <div className={styles.paymentInfo}>
                                        <span className={styles.paymentTitle}>Chuyển khoản ngân hàng</span>
                                        <span className={styles.paymentDesc}>Chuyển khoản qua tài khoản ngân hàng</span>
                                    </div>
                                </div>
                            </label>

                            <label className={styles.paymentOption}>
                                <input
                                    type="radio"
                                    name="paymentMethod"
                                    value="vnpay"
                                />
                                <div className={styles.paymentContent}>
                                    <div className={styles.paymentIcon}>💳</div>
                                    <div className={styles.paymentInfo}>
                                        <span className={styles.paymentTitle}>Thanh toán qua VNPAY</span>
                                        <span className={styles.paymentDesc}>Thanh toán qua cổng VNPAY</span>
                                    </div>
                                </div>
                            </label>
                        </div>
                    </div>

                    <button type="submit" className={styles.confirmButton}>
                        <span>✓</span> Xác nhận đặt hàng
                    </button>
                </form>

                {/* Tóm tắt đơn hàng */}
                <div className={styles.summarySection}>
                    <div className={styles.sectionCard}>
                        <div className={styles.sectionHeader}>
                            <h2>🛒 Đơn hàng của bạn</h2>
                            <span className={styles.itemCount}>{totalQuantity} sản phẩm</span>
                        </div>

                        <div className={styles.orderList}>
                            {orderItems.map((item) => (
                                <div key={item.id} className={styles.orderItem}>
                                    <div className={styles.itemImage}>
                                        {item.image_url ? (
                                            <img src={item.image_url} alt={item.name} />
                                        ) : (
                                            <div className={styles.noImage}>📦</div>
                                        )}
                                        <span className={styles.itemBadge}>{item.quantity}</span>
                                    </div>
                                    <div className={styles.itemDetails}>
                                        <p className={styles.itemName}>{item.name}</p>
                                        <p className={styles.itemPrice}>
                                            x{item.quantity}
                                        </p>
                                        <p className={styles.itemPrice}>
                                            Đơn giá: {(item.totalPrice / item.quantity).toLocaleString()}₫
                                        </p>
                                        <p className={styles.itemPrice}>
                                            Tổng: {item.totalPrice.toLocaleString()}₫
                                        </p>
                                    </div>
                                </div>
                            ))}
                        </div>

                        <div className={styles.summaryDetails}>
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
                            <div className={styles.totalLabel}>Tổng cộng</div>
                            <div className={styles.totalValue}>{total.toLocaleString()}₫</div>
                        </div>

                        <div className={styles.guaranteeBox}>
                            <div className={styles.guaranteeItem}>
                                <span>✓</span> Đảm bảo hoàn tiền
                            </div>
                            <div className={styles.guaranteeItem}>
                                <span>✓</span> Giao hàng nhanh chóng
                            </div>
                            <div className={styles.guaranteeItem}>
                                <span>✓</span> Hỗ trợ 24/7
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default Checkout;
