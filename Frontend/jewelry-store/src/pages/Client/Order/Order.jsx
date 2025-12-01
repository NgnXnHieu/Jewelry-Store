import React, { useEffect, useState, useCallback, useRef } from "react";
import axiosInstance from "../../../api/axiosInstance";
import debounce from "lodash.debounce";
import styles from "./Order.module.css";
import defaultUrl from "../../../api/defaultUrl";
export default function Order() {
    const [orders, setOrders] = useState([]);
    const [loading, setLoading] = useState(true);
    const [selectedStatus, setSelectedStatus] = useState("Tất cả");
    const [nextCursor, setNextCursor] = useState(null); // Lưu ID mốc
    const [hasMore, setHasMore] = useState(true);       // Kiểm tra còn dữ liệu không
    const [isFetchingMore, setIsFetchingMore] = useState(false); // Loading khi cuộn xuống dưới
    const observer = useRef();
    // Callback này sẽ được gắn vào phần tử đơn hàng cuối cùng
    const lastOrderRef = useCallback(node => {
        // Nếu đang load thì không làm gì cả
        if (loading || isFetchingMore) return;

        if (observer.current) observer.current.disconnect();

        observer.current = new IntersectionObserver(entries => {
            // Logic: Nếu thấy lính gác (isIntersecting) VÀ server báo còn dữ liệu (hasMore)
            if (entries[0].isIntersecting && hasMore) {
                // Gọi hàm load thêm, truyền vào cursor hiện tại
                fetchOrders(selectedStatus, nextCursor);
            }
        });

        if (node) observer.current.observe(node);
    }, [loading, isFetchingMore, hasMore, nextCursor, selectedStatus]);
    // 🔹 Hàm gọi API backend theo trạng thái
    const fetchOrders = async (statusValue, cursorId = null) => {
        // Nếu đang load dở thì chặn lại ngay để tránh gọi trùng
        if (isFetchingMore) return;

        // Xác định xem đây là load mới hay load thêm
        const isLoadMore = !!cursorId;

        if (isLoadMore) {
            setIsFetchingMore(true); // Hiện spinner nhỏ ở dưới
        } else {
            setLoading(true); // Hiện loading to toàn màn hình
        }
        try {
            setLoading(true);

            let url = "/orders/myOrdersByStatus";
            if (statusValue === "Tất cả") {
                url = "/orders/myOrders";
            }
            // Cấu hình tham số gửi lên Backend
            const params = { limit: 10 }; // Lấy 10 cái một
            if (statusValue !== "Tất cả") params.status = statusValue;

            // QUAN TRỌNG: Nếu có cursor thì gửi lên
            if (cursorId) params.cursor = cursorId;

            // Gọi API (thêm params vào axios)
            const res = await axiosInstance.get(url, { params });
            const ordersData = res.data;
            // else {
            //     url += `?status=${encodeURIComponent(statusValue)}`;
            // }

            // const res = await axiosInstance.get(url);
            // const ordersData = res.data;
            // console.log(ordersData)

            // Lấy thêm thông tin sản phẩm
            const updatedOrders = await Promise.all(
                ordersData.map(async (order) => {
                    const updatedDetails = await Promise.all(
                        order.orderDetails.map(async (detail) => {
                            try {
                                const productRes = await axiosInstance.get(
                                    `/products/${detail.productId}`
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
            // console.log(ordersData);

            // setOrders(updatedOrders.reverse());
            // --- SỬA ĐOẠN SET STATE ---
            if (updatedOrders.length > 0) {
                if (isLoadMore) {
                    // Nếu là load thêm: Giữ cái cũ, nối cái mới vào sau
                    setOrders(prev => [...prev, ...updatedOrders]);
                } else {
                    // Nếu là load lần đầu: Ghi đè mới hoàn toàn
                    setOrders(updatedOrders);
                }

                // Cập nhật cursor cho lần sau (Lấy ID của thằng cuối cùng trong đám vừa tải)
                const lastItem = updatedOrders[updatedOrders.length - 1];
                setNextCursor(lastItem.id);

                // Kiểm tra xem server đã hết hàng chưa (nếu trả về ít hơn 10 nghĩa là hết)
                setHasMore(updatedOrders.length >= 10);
            } else {
                if (!isLoadMore) setOrders([]); // Nếu trang đầu rỗng thì xóa list
                setHasMore(false);
            }
        } catch (error) {
            console.error("Lỗi khi lấy danh sách đơn hàng:", error || error.response);
        } finally {
            //Dù mất mạng hay lỗi thì finally vẫn chạy
            setLoading(false);
            setIsFetchingMore(false);
        }
    };

    // 🔹 Dùng debounce để hạn chế gọi API khi đổi trạng thái liên tục
    // const debouncedFetchOrders = useCallback(
    //     debounce((status) => {
    //         fetchOrders(status);
    //     }, 500),
    //     []
    // );

    // 🔹 Gọi API khi lần đầu vào hoặc khi đổi trạng thái
    useEffect(() => {
        window.scrollTo(0, 0);
        // Reset toàn bộ state về mặc định
        setOrders([]);
        setNextCursor(null);
        setHasMore(true);
        // Gọi hàm load lần đầu (không truyền cursor)
        fetchOrders(selectedStatus, null);
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
                    /* 👇 1. SỬA DÒNG NÀY: Thêm 'index' vào tham số và thêm dấu { */
                    orders.map((order, index) => {

                        // 👇 2. THÊM DÒNG NÀY: Tính toán xem có phải phần tử cuối không
                        const isLastElement = orders.length === index + 1;

                        return (
                            <div
                                key={order.id}
                                // 👇 3. THÊM DÒNG NÀY: Nếu là cuối thì gắn ref "lính gác"
                                ref={isLastElement ? lastOrderRef : null}
                                className={styles.orderCard}
                            >
                                {/* --- (Nội dung bên trong giữ nguyên) --- */}

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
                                    {order.orderDetails.map((detail, index) => {
                                        return (
                                            <div key={detail.id} className={styles.productCard}>
                                                <div className={styles.productImage}>
                                                    <img
                                                        src={`${defaultUrl}/images/${detail.productImage}`}
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
                                        );
                                    })}
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
                                {/* --- (Hết nội dung thẻ Card) --- */}
                            </div>
                        );
                    })
                )}

                {/* 👇 4. THÊM ĐOẠN NÀY Ở CUỐI CÙNG (Vẫn nằm trong ordersContainer) */}
                {isFetchingMore && (
                    <div className={styles.loadingContainer} style={{ padding: '20px' }}>
                        <div className={styles.spinner}></div>
                        <p className={styles.loadingText}>Đang tải thêm...</p>
                    </div>
                )}

                {!hasMore && orders.length > 0 && (
                    <p style={{ textAlign: 'center', color: '#888', padding: '10px' }}>
                        Đã hiển thị hết đơn hàng
                    </p>
                )}
            </div>
        </div >
    );
}
