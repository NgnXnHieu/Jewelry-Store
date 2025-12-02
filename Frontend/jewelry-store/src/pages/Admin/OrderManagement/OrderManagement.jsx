import { useState, useEffect } from "react";
import styles from "./OrderManagement.module.css";
import axiosInstance from "../../../api/axiosInstance";
import Swal from "sweetalert2";
import defaultUrl from "../../../api/defaultUrl";

const STATUS_LEVELS = {
    "Hủy đơn hàng": -1,
    "Chờ xác nhận": 0,
    "Đã xác nhận": 1,
    "Đang giao hàng": 2,
    "Đã nhận hàng": 3,
};

const ALL_STATUSES = ["Chờ xác nhận", "Đã xác nhận", "Đang giao hàng", "Đã nhận hàng", "Hủy đơn hàng"];

// ==================== COMPONENTS ====================

// Update Field Dialog Component
function UpdateFieldDialog({ open, onOpenChange, field, currentValue, orderId, onUpdate }) {
    const [value, setValue] = useState(currentValue);

    useEffect(() => {
        setValue(currentValue);
    }, [currentValue]);

    useEffect(() => {
        const handleEscape = (e) => {
            if (e.key === 'Escape' && open) {
                onOpenChange(false);
            }
        };

        if (open) {
            document.addEventListener('keydown', handleEscape);
        }

        return () => {
            document.removeEventListener('keydown', handleEscape);
        };
    }, [open, onOpenChange]);

    const handleUpdate = () => {
        onUpdate(orderId, field, value);
        onOpenChange(false);
    };

    if (!open) return null;

    const label = field === "phone" ? "Số điện thoại" : "Địa chỉ";

    return (
        <div className={styles.dialogOverlay} onClick={() => onOpenChange(false)}>
            <div className={styles.updateDialog} onClick={(e) => e.stopPropagation()}>
                <div className={styles.updateDialogHeader}>
                    <h2 className={styles.updateDialogTitle}>Cập nhật {label}</h2>
                    <button className={styles.closeButton} onClick={() => onOpenChange(false)}>
                        <svg width="15" height="15" viewBox="0 0 15 15" fill="none">
                            <path d="M11.7816 4.03157C12.0062 3.80702 12.0062 3.44295 11.7816 3.2184C11.5571 2.99385 11.193 2.99385 10.9685 3.2184L7.50005 6.68682L4.03164 3.2184C3.80708 2.99385 3.44301 2.99385 3.21846 3.2184C2.99391 3.44295 2.99391 3.80702 3.21846 4.03157L6.68688 7.49999L3.21846 10.9684C2.99391 11.193 2.99391 11.557 3.21846 11.7816C3.44301 12.0061 3.80708 12.0061 4.03164 11.7816L7.50005 8.31316L10.9685 11.7816C11.193 12.0061 11.5571 12.0061 11.7816 11.7816C12.0062 11.557 12.0062 11.193 11.7816 10.9684L8.31322 7.49999L11.7816 4.03157Z" fill="currentColor" fillRule="evenodd" clipRule="evenodd" />
                        </svg>
                    </button>
                </div>
                <div className={styles.updateDialogContent}>
                    <label className={styles.label}>{label}</label>
                    {field === "phone" ? (
                        <input
                            type="text"
                            value={value}
                            onChange={(e) => setValue(e.target.value)}
                            placeholder={`Nhập ${label.toLowerCase()}`}
                            className={styles.input}
                        />
                    ) : (
                        <textarea
                            value={value}
                            onChange={(e) => setValue(e.target.value)}
                            placeholder={`Nhập ${label.toLowerCase()}`}
                            rows={3}
                            className={styles.textarea}
                        />
                    )}
                </div>
                <div className={styles.updateDialogFooter}>
                    <button className={styles.btnCancel} onClick={() => onOpenChange(false)}>
                        Hủy
                    </button>
                    <button className={styles.btnPrimary} onClick={handleUpdate}>
                        Cập nhật
                    </button>
                </div>
            </div>
        </div>
    );
}
function ProductInfo({ productId }) {
    const [product, setProduct] = useState(null);

    useEffect(() => {
        axiosInstance.get(`/products/${productId}`)
            .then(res => setProduct(res.data))
            .catch(err => console.error(err));
    }, [productId]);

    if (!product) return <span>Đang tải...</span>;

    return (
        <div className={styles.imagAndName}>
            <img src={`${defaultUrl}/images/${product.image_url}`} alt={product.image_url} className={styles.productImage} />
            {/* <div>{product.name}</div> */}
            <div className={styles.productName}>{product.name}</div>
        </div>
    );
}

// Order Details Dialog Component
function OrderDetailsDialog({ orderId, open, onOpenChange }) {
    const [items, setItems] = useState([]);
    const [product, setProduct] = useState(null);
    useEffect(() => {
        axiosInstance.get(`/order_details/${orderId}/orderDetalils`)
            .then(res => {
                setItems(res.data);
                console.log("Orders:", res.data);
            })
            .catch(err => {
                console.error("Lỗi khi gọi API:", err);
            });
    }, [orderId]);


    const formatCurrency = (amount) => {
        return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(amount);
    };

    // const getProduct = (productId) => {
    //     axiosInstance.get(`/products/${productId}`)
    //         .then(res => {
    //             console.log("Product:", res.data);
    //             return res.data;
    //         })
    // };

    const totalAmount = items.reduce((sum, item) => sum + item.totalPrice, 0);

    useEffect(() => {
        const handleEscape = (e) => {
            if (e.key === 'Escape' && open) {
                onOpenChange(false);
            }
        };

        if (open) {
            document.addEventListener('keydown', handleEscape);
        }

        return () => {
            document.removeEventListener('keydown', handleEscape);
        };
    }, [open, onOpenChange]);

    if (!open) return null;

    return (
        <div className={styles.dialogOverlay} onClick={() => onOpenChange(false)}>
            <div className={styles.detailsDialog} onClick={(e) => e.stopPropagation()}>
                <div className={styles.detailsDialogHeader}>
                    <div>
                        <h2 className={styles.detailsDialogTitle}>Chi tiết đơn hàng #{orderId}</h2>
                        <p className={styles.detailsDialogDescription}>
                            Danh sách sản phẩm trong đơn hàng ({items.length} sản phẩm)
                        </p>
                    </div>
                    <button className={styles.closeButton} onClick={() => onOpenChange(false)}>
                        <svg width="15" height="15" viewBox="0 0 15 15" fill="none">
                            <path d="M11.7816 4.03157C12.0062 3.80702 12.0062 3.44295 11.7816 3.2184C11.5571 2.99385 11.193 2.99385 10.9685 3.2184L7.50005 6.68682L4.03164 3.2184C3.80708 2.99385 3.44301 2.99385 3.21846 3.2184C2.99391 3.44295 2.99391 3.80702 3.21846 4.03157L6.68688 7.49999L3.21846 10.9684C2.99391 11.193 2.99391 11.557 3.21846 11.7816C3.44301 12.0061 3.80708 12.0061 4.03164 11.7816L7.50005 8.31316L10.9685 11.7816C11.193 12.0061 11.5571 12.0061 11.7816 11.7816C12.0062 11.557 12.0062 11.193 11.7816 10.9684L8.31322 7.49999L11.7816 4.03157Z" fill="currentColor" fillRule="evenodd" clipRule="evenodd" />
                        </svg>
                    </button>
                </div>

                <div className={styles.detailsDialogContent}>
                    <div className={styles.scrollArea}>
                        {/* Desktop View */}
                        <div className={styles.desktopView}>
                            <div className={styles.tableWrapper}>
                                <div className={styles.tableHeader}>
                                    <div className={styles.headerRow}>
                                        <div className={styles.stickyIdHeader}>ID</div>
                                        <div className={styles.headerContent}>
                                            {/* <div className={styles.orderIdHeader}>Order ID</div> */}
                                            <div className={styles.productHeader}>Sản phẩm</div>
                                            <div className={styles.productIdHeader}>Product ID</div>
                                            <div className={styles.priceHeader}>Đơn giá</div>
                                            <div className={styles.quantityHeader}>Số lượng</div>
                                            <div className={styles.totalHeader}>Tổng giá</div>
                                        </div>
                                    </div>
                                </div>

                                {items.length > 0 ? (
                                    items.map((item) => {
                                        // const product = getProduct(item.productId);
                                        return (
                                            <div key={item.id} className={styles.tableRow}>
                                                <div className={styles.stickyIdCell}>{item.id}</div>
                                                <div className={styles.rowContent}>
                                                    {/* <div className={styles.orderIdCell}>{item.orderId}</div> */}
                                                    <div className={styles.productCell}>
                                                        <ProductInfo productId={item.productId} />
                                                    </div>
                                                    <div className={styles.productIdCell}>{item.id}</div>
                                                    <div className={styles.priceCell}>{formatCurrency(item.price)}</div>
                                                    <div className={styles.quantityCell}>{item.quantity}</div>
                                                    <div className={styles.totalCell}>{formatCurrency(item.totalPrice)}</div>
                                                </div>
                                            </div>
                                        );
                                    })
                                ) : (
                                    <div className={styles.emptyState}>Không có sản phẩm nào</div>
                                )}
                            </div>
                        </div>

                        {/* Mobile View */}
                        {/* <div className={styles.mobileView}>
                            {items.length > 0 ? (
                                items.map((item) => {
                                    const product = getProduct(item.productId);
                                    return (
                                        <div key={item.id} className={styles.mobileCard}>
                                            <div className={styles.mobileCardHeader}>
                                                {product && <img src={`http://localhost:8080/images/${product.image_url}`} alt={product.image_url} className={styles.mobileProductImage} />}
                                                <div className={styles.mobileProductInfo}>
                                                    <p className={styles.mobileProductName}>{product?.name || "Không tìm thấy"}</p>
                                                    <p className={styles.mutedText}>ID: {item.id}</p>
                                                </div>
                                            </div>
                                            <div className={styles.mobileGrid}>
                                                <div><span className={styles.mutedText}>Order ID:</span><p>{item.order_id}</p></div>
                                                <div><span className={styles.mutedText}>Product ID:</span><p>{item.product_id}</p></div>
                                                <div><span className={styles.mutedText}>Đơn giá:</span><p>{formatCurrency(item.price)}</p></div>
                                                <div><span className={styles.mutedText}>Số lượng:</span><p>{item.quantity}</p></div>
                                                <div className={styles.mobileGridFull}><span className={styles.mutedText}>Tổng giá:</span><p>{formatCurrency(item.totalPrice)}</p></div>
                                            </div>
                                        </div>
                                    );
                                })
                            ) : (
                                <div className={styles.emptyState}>Không có sản phẩm nào</div>
                            )}
                        </div> */}
                    </div>
                </div>

                {items.length > 0 && (
                    <div className={styles.detailsDialogFooter}>
                        <div className={styles.footerContent}>
                            <p className={styles.mutedText}>Tổng số lượng: {items.reduce((sum, item) => sum + item.quantity, 0)}</p>
                            <div className={styles.footerTotal}>
                                <p className={styles.mutedText}>Tổng cộng</p>
                                <p className={styles.totalAmount}>{formatCurrency(totalAmount)}</p>
                            </div>
                        </div>
                    </div>
                )}
            </div>
        </div>
    );
}

// ==================== MAIN COMPONENT ====================

export default function OrderManagement() {
    const [orders, setOrders] = useState([]);
    const [selectedOrderId, setSelectedOrderId] = useState(null);
    const [detailsDialogOpen, setDetailsDialogOpen] = useState(false);
    const [updateDialogOpen, setUpdateDialogOpen] = useState(false);
    const [updateField, setUpdateField] = useState("phone");
    const [updateOrderId, setUpdateOrderId] = useState(0);
    const [updateCurrentValue, setUpdateCurrentValue] = useState("");
    const [highlightedRowId, setHighlightedRowId] = useState(null);
    const [currentPage, setCurrentPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);

    const handlePrev = () => setCurrentPage(prev => Math.max(prev - 1, 0));
    const handleNext = () => setCurrentPage(prev => Math.min(prev + 1, totalPages - 1));


    const fetchOrders = (page) => {
        axiosInstance.get(`/orders/orderSumaries?page=${page}&size=20`)
            .then(res => {
                setOrders(res.data.content);
                setTotalPages(res.data.totalPages);
                console.log(res.data);
            })
            .catch(err => console.error(err));
    };

    useEffect(() => {
        fetchOrders(currentPage);
    }, [currentPage]);


    const formatCurrency = (amount) => {
        return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(amount);
    };


    const getStatusBadgeClass = (status) => {
        switch (status) {
            case "Hủy đơn hàng": return styles.badgeDestructive;
            case "Chờ xác nhận": return styles.badgeSecondary;
            case "Đã xác nhận": return styles.badgeDefault;
            case "Đang giao hàng": return styles.badgeDefault;
            case "Đã nhận hàng": return styles.badgeSuccess;
            default: return styles.badgeDefault;
        }
    };

    const getQuantityStyle = (quantity) => {
        if (quantity > 20) {
            return { color: 'orange' };

        }
        else if (quantity > 10) {
            return { color: 'blue' };
        } else if (quantity > 5) {
            return { color: 'green' };
        } else {
            return { color: 'black' };
        }
    };


    const canChangeToStatus = (currentStatus, newStatus) => {
        const currentLevel = STATUS_LEVELS[currentStatus];
        const newLevel = STATUS_LEVELS[newStatus];

        if (newStatus === "Hủy đơn hàng") {
            return currentStatus === "Chờ xác nhận" || currentStatus === "Đã xác nhận" || currentStatus === "Đang giao hàng";
        }

        if (currentStatus === "Hủy đơn hàng") {
            return false;
        }

        return newLevel > currentLevel;
    };

    const getAvailableStatuses = (currentStatus) => {
        return ALL_STATUSES.filter(status => status === currentStatus || canChangeToStatus(currentStatus, status));
    };

    const handleStatusChange = async (orderId, newStatus) => {
        const result = await Swal.fire({
            title: `Bạn có chắc muốn cập nhật trạng thái của đơn hàng này sang "${newStatus}" ?`,
            icon: 'question',
            showCancelButton: true,
            confirmButtonText: 'Chắc chắn',
            cancelButtonText: 'Hủy'
        });
        if (!result.isConfirmed) {
            return;
        }

        try {
            // Gửi yêu cầu cập nhật đến backend
            const res = await axiosInstance.put(`/orders/${orderId}`, {
                ["status"]: newStatus, // chỉ gửi field cần sửa
            });

            // Nếu API trả về thành công
            if (res.status === 200) {
                // Cập nhật lại danh sách order trên UI
                setOrders(prevOrders =>
                    prevOrders.map(order =>
                        order.id === orderId ? { ...order, ["status"]: newStatus } : order
                    )
                );

                Swal.fire({
                    icon: "success",
                    title: "Thành công!",
                    text: `Trạng thái đơn hàng đã được cập nhật.`
                });
            }
        } catch (error) {
            console.error("❌ Lỗi khi cập nhật trạng thái đơn hàng:", error);
            Swal.fire({
                icon: "error",
                title: "Lỗi!",
                text: "Không thể cập nhật trạng thái đơn hàng. Vui lòng thử lại!"
            });
        }
    };

    const handleOpenDetails = (orderId) => {
        setSelectedOrderId(orderId);
        setDetailsDialogOpen(true);
        setHighlightedRowId(orderId);
    };

    const handleOpenUpdateDialog = (orderId, field, currentValue) => {
        setUpdateOrderId(orderId);
        setUpdateField(field);
        setUpdateCurrentValue(currentValue);
        setUpdateDialogOpen(true);
        setHighlightedRowId(orderId);
    };

    const handleUpdate = async (orderId, field, value) => {
        const result = await Swal.fire({
            title: `Bạn có chắc muốn cập nhật ${field === "phone" ? "số điện thoại" : "địa chỉ"} sản phẩm này?`,
            icon: 'question',
            showCancelButton: true,
            confirmButtonText: 'Có',
            cancelButtonText: 'Hủy'
        });
        if (!result.isConfirmed) {
            return;
        }

        try {
            // Gửi yêu cầu cập nhật đến backend
            const res = await axiosInstance.put(`/orders/${orderId}`, {
                [field]: value, // chỉ gửi field cần sửa
            });

            // Nếu API trả về thành công
            if (res.status === 200) {
                // Cập nhật lại danh sách order trên UI
                setOrders(prevOrders =>
                    prevOrders.map(order =>
                        order.id === orderId ? { ...order, [field]: value } : order
                    )
                );

                Swal.fire({
                    icon: "success",
                    title: "Thành công!",
                    text: `${field === "phone" ? "Số điện thoại" : "Địa chỉ"} đã được cập nhật.`
                });
            }
        } catch (error) {
            console.error("❌ Lỗi khi cập nhật đơn hàng:", error);
            Swal.fire({
                icon: "error",
                title: "Lỗi!",
                text: "Không thể cập nhật đơn hàng. Vui lòng thử lại!"
            });
        }
        // showToast(`${field === "phone" ? "Số điện thoại" : "Địa chỉ"} đã được cập nhật`);
    };

    const handleRowClick = (orderId) => {
        setHighlightedRowId(orderId);
    };

    const showToast = (message) => {
        // Simple toast notification
        const toast = document.createElement('div');
        toast.className = styles.toast;
        toast.textContent = message;
        document.body.appendChild(toast);
        setTimeout(() => toast.classList.add(styles.toastShow), 100);
        setTimeout(() => {
            toast.classList.remove(styles.toastShow);
            setTimeout(() => document.body.removeChild(toast), 300);
        }, 3000);
    };

    const sortedOrders = [...orders].sort((a, b) =>
        new Date(b.order_date).getTime() - new Date(a.order_date).getTime()
    );

    return (
        <div className={styles.container}>
            <div className={styles.mainContent}>
                <div className={styles.header}>
                    <div className={styles.headerContent}>
                        <svg className={styles.packageIcon} width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                            <path d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z" />
                            <polyline points="3.27 6.96 12 12.01 20.73 6.96" />
                            <line x1="12" y1="22.08" x2="12" y2="12" />
                        </svg>
                        <div>
                            <h1 className={styles.title}>Quản lý đơn hàng</h1>
                            <p className={styles.subtitle}>Quản lý và theo dõi tất cả đơn hàng từ khách hàng</p>
                        </div>
                    </div>
                </div>

                <div className={styles.tableContainer}>
                    <div className={styles.tableResponsive}>
                        <table className={styles.table}>
                            <thead>
                                <tr>
                                    <th>Mã đơn</th>
                                    <th>Ngày đặt</th>
                                    <th>Khách hàng</th>
                                    <th>Số điện thoại</th>
                                    <th>Địa chỉ</th>
                                    <th className={styles.textRight}>Số lượng</th>
                                    <th className={styles.textRight}>Tổng tiền</th>
                                    <th>Trạng thái</th>
                                </tr>
                            </thead>
                            <tbody>
                                {sortedOrders.map((order) => (
                                    <tr
                                        key={order.id}
                                        className={`${styles.tableRowHover} ${highlightedRowId === order.id ? styles.highlighted : ''}`}
                                        onClick={() => handleRowClick(order.id)}
                                    >
                                        <td
                                            className={styles.idStyle}
                                            onClick={(e) => { e.stopPropagation(); handleOpenDetails(order.id); }}
                                        >
                                            #{order.id}
                                        </td>
                                        <td>{new Date(order.orderDate).toLocaleString("vi-VN")}</td>
                                        <td>User #{order.userId}</td>
                                        <td
                                            className={styles.linkText}
                                            onClick={(e) => { e.stopPropagation(); handleOpenUpdateDialog(order.id, "phone", order.phone); }}
                                        >
                                            {order.phone}
                                        </td>
                                        <td
                                            className={`${styles.linkText} ${styles.truncate}`}
                                            onClick={(e) => { e.stopPropagation(); handleOpenUpdateDialog(order.id, "address", order.address); }}
                                            title={order.address}
                                        >
                                            {order.address}
                                        </td>
                                        <td className={styles.textRight1}>{order.quantity}</td>
                                        <td className={styles.textRight2}>{formatCurrency(order.totalAmount)}</td>
                                        <td onClick={(e) => e.stopPropagation()}>
                                            <select
                                                value={order.status}
                                                onChange={(e) => handleStatusChange(order.id, e.target.value)}
                                                className={`${styles.statusSelect} ${getStatusBadgeClass(order.status)}`}
                                            >
                                                {getAvailableStatuses(order.status).map((status) => (
                                                    <option key={status} value={status}>{status}</option>
                                                ))}
                                            </select>
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                        {/* Pagination */}
                        <div className={styles.pagination}>
                            <button
                                onClick={handlePrev}
                                disabled={currentPage === 0}
                                className={styles.paginationBtn}
                            >
                                <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
                                </svg>
                                Trang trước
                            </button>
                            <span className={styles.pageInfo}>
                                Trang {currentPage + 1} / {totalPages || 1}
                            </span>
                            <button
                                onClick={handleNext}
                                disabled={currentPage === totalPages - 1 || totalPages === 0}
                                className={styles.paginationBtn}
                            >
                                Trang sau
                                <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5l7 7-7 7" />
                                </svg>
                            </button>
                        </div>
                    </div>
                </div>
            </div>

            {
                selectedOrderId &&
                <OrderDetailsDialog
                    orderId={selectedOrderId}
                    open={detailsDialogOpen}
                    onOpenChange={setDetailsDialogOpen}
                />
            }

            <UpdateFieldDialog
                open={updateDialogOpen}
                onOpenChange={setUpdateDialogOpen}
                field={updateField}
                currentValue={updateCurrentValue}
                orderId={updateOrderId}
                onUpdate={handleUpdate}
            />
        </div >
    );
}
