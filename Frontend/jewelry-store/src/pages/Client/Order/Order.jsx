import React, { useEffect, useState, useCallback } from "react";
import axiosInstance from "../../../api/axiosInstance";
import debounce from "lodash.debounce";
import styles from "./Order.module.css";

export default function Order() {
    const [orders, setOrders] = useState([]);
    const [loading, setLoading] = useState(true);
    const [selectedStatus, setSelectedStatus] = useState("Tất cả");

    // 🔹 Hàm gọi API backend theo trạng thái
    const fetchOrders = async (statusValue) => {
        try {
            setLoading(true);

            let url = "http://localhost:8080/api/orders/myOrdersByStatus";
            if (statusValue === "Tất cả") {
                url = "http://localhost:8080/api/orders/myOrders";
            } else {
                // ví dụ: /api/orders/myOrders?status=Đang giao hàng
                url += `?status=${encodeURIComponent(statusValue)}`;
            }

            const res = await axiosInstance.get(url);
            const ordersData = res.data;

            // Lấy thêm thông tin sản phẩm
            const updatedOrders = await Promise.all(
                ordersData.map(async (order) => {
                    const updatedDetails = await Promise.all(
                        order.orderDetails.map(async (detail) => {
                            try {
                                const productRes = await axiosInstance.get(
                                    `http://localhost:8080/api/products/${detail.productId}`
                                );
                                const product = productRes.data;
                                return {
                                    ...detail,
                                    productName: product.name,
                                    productImage: product.image_url,
                                };
                            } catch {
                                return detail;
                            }
                        })
                    );
                    return { ...order, orderDetails: updatedDetails };
                })
            );

            setOrders(updatedOrders.reverse());
        } catch (error) {
            console.error("Lỗi khi lấy danh sách đơn hàng:", error);
        } finally {
            setLoading(false);
        }
    };

    // 🔹 Dùng debounce để hạn chế gọi API khi đổi trạng thái liên tục
    const debouncedFetchOrders = useCallback(
        debounce((status) => {
            fetchOrders(status);
        }, 500),
        [] // chỉ khởi tạo 1 lần
    );

    // 🔹 Gọi API khi lần đầu vào hoặc khi đổi trạng thái
    useEffect(() => {
        debouncedFetchOrders(selectedStatus);
    }, [selectedStatus]);

    const getStatusClass = (status) => {
        switch (status?.toLowerCase()) {
            case "chờ xác nhận": return styles.pending;
            case "đã xác nhận": return styles.confirmed;
            case "đang giao hàng": return styles.shipping;
            case "đã nhận hàng": return styles.delivered;
            default: return styles.defaultStatus;
        }
    };

    if (loading) return <p className={styles.loading}>Đang tải dữ liệu...</p>;

    return (
        <div className={styles.container}>
            <h1 id={styles.title}>Lịch sử đơn hàng</h1>

            {/* 🔹 Thanh chọn trạng thái */}
            <div className={styles.filterBar}>
                {["Tất cả", "Chờ xác nhận", "Đã xác nhận", "Đang giao hàng", "Đã nhận hàng"].map((status) => (
                    <button
                        key={status}
                        className={`${styles.filterButton} ${selectedStatus === status ? styles.activeFilter : ""}`}
                        onClick={() => setSelectedStatus(status)}
                    >
                        {status}
                    </button>
                ))}
            </div>

            {orders.length === 0 ? (
                <p className={styles.empty}>Không có đơn hàng nào với trạng thái này.</p>
            ) : (
                orders.map((order) => (
                    <div key={order.id} className={styles.orderCard}>
                        <div className={styles.orderHeader}>
                            <div>
                                <h2 className={styles.orderCode}>Mã đơn #{order.id}</h2>
                                <p className={styles.orderDate}>
                                    Ngày đặt: {new Date(order.orderDate).toLocaleString("vi-VN")}
                                </p>
                            </div>
                            <span className={`${styles.status} ${getStatusClass(order.status)}`}>
                                {order.status}
                            </span>
                        </div>

                        <table className={styles.productTable}>
                            <thead>
                                <tr>
                                    <th>#</th>
                                    <th>Ảnh</th>
                                    <th>Tên sản phẩm</th>
                                    <th>Số lượng</th>
                                    <th>Đơn giá</th>
                                    <th>Thành tiền</th>
                                </tr>
                            </thead>
                            <tbody>
                                {order.orderDetails.map((detail, index) => (
                                    <tr key={detail.id}>
                                        <td>{index + 1}</td>
                                        <td>
                                            <img
                                                src={detail.productImage || "https://via.placeholder.com/80"}
                                                alt={detail.productName || "Sản phẩm"}
                                                className={styles.image}
                                            />
                                        </td>
                                        <td className={styles.nameCell}>
                                            {detail.productName || `SP #${detail.productId}`}
                                        </td>
                                        <td>{detail.quantity}</td>
                                        <td>{detail.price?.toLocaleString()}₫</td>
                                        <td>{detail.totalPrice?.toLocaleString()}₫</td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>

                        <div className={styles.footer}>
                            <div className={styles.addressBox}>
                                <p><strong>Địa chỉ giao hàng:</strong> {order.address}</p>
                                <p><strong>SĐT:</strong> {order.phone}</p>
                            </div>
                            <div className={styles.totalBox}>
                                <p className={styles.totalQuantity}>
                                    <strong>Tổng số lượng: {order.quantity}</strong>
                                </p>
                                <p className={styles.totalAmount}>
                                    <strong>Tổng tiền:</strong> {order.totalAmount?.toLocaleString()}₫
                                </p>
                            </div>
                        </div>
                    </div>
                ))
            )}
        </div>
    );
}
