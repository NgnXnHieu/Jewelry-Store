import React, { useState, useEffect } from "react";
import axiosInstance from "../../../api/axiosInstance";
import Swal from "sweetalert2";
import { useLocation } from "react-router-dom";
import styles from "./Checkout.module.css";

const Checkout = () => {
    const location = useLocation();
    const { items } = location.state || { items: [] };

    console.log("Danh sách sản phẩm mua:", items);
    // const { items = [] } = location.state || {}; // ✅ Nhận danh sách {id, quantity}
    const [addresses, setAddresses] = useState([]);
    const [selectedAddress, setSelectedAddress] = useState(null);
    const [orderItems, setOrderItems] = useState([]); // ✅ sản phẩm thực tế
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    // ✅ Lấy thông tin địa chỉ
    useEffect(() => {
        const fetchAddresses = async () => {
            try {
                const [defaultRes, allRes] = await Promise.all([
                    axiosInstance.get("/addresses/defaultAddress"),
                    axiosInstance.get("/addresses/myAddress"),
                ]);

                setSelectedAddress(defaultRes.data);
                setAddresses(allRes.data);
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
            if (!items.length) return;

            try {
                // Gọi API song song theo danh sách id
                const responses = await Promise.all(
                    items.map((it) => {
                        const productId = it.productId || it.id; // ✅ nếu có productId thì dùng, không thì dùng id
                        return axiosInstance.get(`/products/${productId}`);
                    })
                    // items.map((it) => axiosInstance.get(`/products/${it.productId}`))
                );

                // Gộp dữ liệu chi tiết + số lượng
                const detailedItems = responses.map((res, idx) => ({
                    ...res.data,
                    quantity: items[idx].quantity,
                }));

                setOrderItems(detailedItems);
            } catch (err) {
                console.error("Lỗi khi tải sản phẩm:", err);
                setError(err);
            } finally {
                setLoading(false);
            }
        };

        fetchProducts();
    }, [items]);

    // ✅ Tính tổng tiền
    const total = orderItems.reduce(
        (sum, item) => sum + item.price * item.quantity,
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

        // ✅ Lấy phương thức thanh toán từ radio button
        const paymentMethod = document.querySelector('input[name="paymentMethod"]:checked').value;

        // ✅ Nếu là COD thì xử lý luôn
        if (paymentMethod === "cod") {
            const orderData = {
                address: `${selectedAddress.village}, ${selectedAddress.ward}, ${selectedAddress.district}`,
                phone: selectedAddress.phone,
                idAndQuantityList: orderItems.map((item) => ({
                    productId: item.id,
                    quantity: item.quantity,
                })),
            };

            try {
                const result = await Swal.fire({
                    title: "Xác nhận đặt hàng?",
                    text: `Giao tới ${selectedAddress.village}, ${selectedAddress.ward}, ${selectedAddress.district}`,
                    icon: "question",
                    showCancelButton: true,
                    confirmButtonText: "Đặt hàng",
                    cancelButtonText: "Hủy",
                });

                if (result.isConfirmed) {
                    // ✅ Gửi API tạo đơn hàng
                    const res = await axiosInstance.post("/orders/myOrder", orderData);

                    Swal.fire(
                        "Thành công!",
                        `Đơn hàng đã được tạo thành công!`,
                        "success"
                    );

                    console.log("Đơn hàng mới:", res.data);

                    // ✅ Xóa giỏ hàng tạm nếu có hoặc chuyển hướng
                    // localStorage.removeItem("cart");
                    // navigate("/orders");
                }
            } catch (error) {
                console.error("Lỗi khi tạo đơn hàng:", error);
                Swal.fire("Thất bại!", "Không thể tạo đơn hàng. Vui lòng thử lại!", "error");
            }

            return; // ✅ Kết thúc luôn, chưa làm các phương thức khác
        }

        // ✅ Các phương thức khác (chưa làm)
        Swal.fire("Chưa hỗ trợ!", "Phương thức thanh toán này đang được phát triển.", "info");
    };


    if (loading) return <p>Đang tải dữ liệu...</p>;
    if (error) return <p>Lỗi khi tải dữ liệu!</p>;
    if (!orderItems.length) return <p>Không có sản phẩm để thanh toán!</p>;

    return (
        <div className={styles.checkoutContainer}>
            <h1>Thanh toán</h1>
            <div className={styles.checkoutGrid}>
                {/* Thông tin người nhận */}
                <form className={styles.infoSection} onSubmit={handleConfirm}>
                    <h2>Thông tin giao hàng</h2>

                    <div className={styles.addressBox}>
                        <p><strong>SĐT:</strong> {selectedAddress?.phone}</p>
                        <p><strong>Địa chỉ:</strong> {`${selectedAddress?.village}, ${selectedAddress?.ward}, ${selectedAddress?.district}`}</p>
                        <button type="button" onClick={handleChangeAddress} className={styles.changeButton}>
                            Thay đổi địa chỉ
                        </button>
                    </div>

                    <label>
                        Ghi chú (nếu có):
                        <textarea name="note"></textarea>
                    </label>

                    <h3>Phương thức thanh toán</h3>
                    <div className={styles.paymentMethods}>
                        <label><input type="radio" name="paymentMethod" value="cod" defaultChecked />Thanh toán khi nhận hàng (COD)</label>
                        <label><input type="radio" name="paymentMethod" value="bank" />Chuyển khoản ngân hàng</label>
                        <label><input type="radio" name="paymentMethod" value="vnpay" />Thanh toán qua VNPAY</label>
                    </div>

                    <button type="submit" className={styles.confirmButton}>
                        Xác nhận đặt hàng
                    </button>
                </form>

                {/* Tóm tắt đơn hàng */}
                <div className={styles.summarySection}>
                    <h2>Đơn hàng của bạn</h2>
                    <div className={styles.orderList}>
                        {orderItems.map((item) => (
                            <div key={item.id} className={styles.orderItem}>
                                <img src={item.image_url} alt={item.name} />
                                <div>
                                    <p className={styles.name}>{item.name}</p>
                                    <p>SL: {item.quantity} x {item.price.toLocaleString()}đ</p>
                                    <p className={styles.subtotal}>{(item.price * item.quantity).toLocaleString()}đ</p>
                                </div>
                            </div>
                        ))}
                    </div>

                    <div className={styles.totalBox}>
                        <strong>Tổng cộng:</strong>
                        <span>{total.toLocaleString()}đ</span>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default Checkout;
