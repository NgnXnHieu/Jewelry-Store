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
        []
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

    const getStatusIcon = (status) => {
        switch (status?.toLowerCase()) {
            case "chờ xác nhận": return "⏳";
            case "đã xác nhận": return "✓";
            case "đang giao hàng": return "🚚";
            case "đã nhận hàng": return "✨";
            default: return "📦";
        }
    };

    if (loading) {
        return (
            <div className={styles.container}>
                <div className={styles.loadingContainer}>
                    <div className={styles.spinner}></div>
                    <p className={styles.loadingText}>Đang tải dữ liệu...</p>
                </div>
            </div>
        );
    }

    return (
        <div className={styles.container}>
            {/* Header */}
            <div className={styles.header}>
                <div className={styles.headerContent}>
                    <h1 className={styles.title}>📋 Lịch sử đơn hàng</h1>
                    <p className={styles.subtitle}>
                        Quản lý và theo dõi tất cả đơn hàng của bạn
                    </p>
                </div>
            </div>

            {/* Filter Bar */}
            <div className={styles.filterBar}>
                {["Tất cả", "Chờ xác nhận", "Đã xác nhận", "Đang giao hàng", "Đã nhận hàng"].map((status) => (
                    <button
                        key={status}
                        className={`${styles.filterButton} ${selectedStatus === status ? styles.activeFilter : ""}`}
                        onClick={() => setSelectedStatus(status)}
                    >
                        {getStatusIcon(status)} {status}
                    </button>
                ))}
            </div>

            {/* Orders List */}
            <div className={styles.ordersContainer}>
                {orders.length === 0 ? (
                    <div className={styles.emptyState}>
                        <div className={styles.emptyIcon}>📦</div>
                        <h3 className={styles.emptyTitle}>Không có đơn hàng</h3>
                        <p className={styles.emptyText}>
                            Không có đơn hàng nào với trạng thái này.
                        </p>
                    </div>
                ) : (
                    orders.map((order) => (
                        <div key={order.id} className={styles.orderCard}>
                            {/* Order Header */}
                            <div className={styles.orderHeader}>
                                <div className={styles.orderHeaderLeft}>
                                    <h2 className={styles.orderCode}>Đơn hàng #{order.id}</h2>
                                    <p className={styles.orderDate}>
                                        📅 {new Date(order.orderDate).toLocaleString("vi-VN")}
                                    </p>
                                </div>
                                <span className={`${styles.status} ${getStatusClass(order.status)}`}>
                                    {getStatusIcon(order.status)} {order.status}
                                </span>
                            </div>

                            {/* Products Grid */}
                            <div className={styles.productsGrid}>
                                {order.orderDetails.map((detail, index) => (
                                    <div key={detail.id} className={styles.productCard}>
                                        <div className={styles.productImage}>
                                            <img
                                                src={detail.productImage || "https://via.placeholder.com/120"}
                                                alt={detail.productName || "Sản phẩm"}
                                            />
                                            <span className={styles.productIndex}>{index + 1}</span>
                                        </div>
                                        <div className={styles.productInfo}>
                                            <h4 className={styles.productName}>
                                                {detail.productName || `Sản phẩm #${detail.productId}`}
                                            </h4>
                                            <div className={styles.productDetails}>
                                                <span className={styles.productQuantity}>
                                                    SL: {detail.quantity}
                                                </span>
                                                <span className={styles.productPrice}>
                                                    {detail.price?.toLocaleString()}₫
                                                </span>
                                            </div>
                                            <p className={styles.productTotal}>
                                                Tổng: <strong>{detail.totalPrice?.toLocaleString()}₫</strong>
                                            </p>
                                        </div>
                                    </div>
                                ))}
                            </div>

                            {/* Order Footer */}
                            <div className={styles.orderFooter}>
                                <div className={styles.deliveryInfo}>
                                    <div className={styles.infoItem}>
                                        <span className={styles.infoIcon}>📍</span>
                                        <div>
                                            <p className={styles.infoLabel}>Địa chỉ giao hàng</p>
                                            <p className={styles.infoValue}>{order.address}</p>
                                        </div>
                                    </div>
                                    <div className={styles.infoItem}>
                                        <span className={styles.infoIcon}>📞</span>
                                        <div>
                                            <p className={styles.infoLabel}>Số điện thoại</p>
                                            <p className={styles.infoValue}>{order.phone}</p>
                                        </div>
                                    </div>
                                </div>
                                <div className={styles.orderSummary}>
                                    <div className={styles.summaryRow}>
                                        <span>Tổng số lượng:</span>
                                        <strong>{order.quantity} sản phẩm</strong>
                                    </div>
                                    <div className={styles.summaryRow}>
                                        <span>Tổng thanh toán:</span>
                                        <strong className={styles.totalAmount}>
                                            {order.totalAmount?.toLocaleString()}₫
                                        </strong>
                                    </div>
                                </div>
                            </div>
                        </div>
                    ))
                )}
            </div>
        </div>
    );
}
