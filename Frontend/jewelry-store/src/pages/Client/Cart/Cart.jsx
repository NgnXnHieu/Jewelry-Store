import React, { useState, useEffect } from "react";
import axios from "axios";
import styles from "./Cart.module.css";
import axiosInstance from "../../../api/axiosInstance";
import Swal from "sweetalert2";
import debounce from "lodash.debounce";
import { useNavigate } from "react-router-dom";

const Cart = () => {
    const [cartItems, setCartItems] = useState([]);
    const [loading, setLoading] = useState(true);
    const navigate = useNavigate();

    // Gọi API cập nhật số lượng (debounced)
    const updateQuantity = debounce(async (id, quantity) => {
        try {
            await axiosInstance.put(`/cart_details/${id}`, { quantity });
            console.log(`✅ Đã cập nhật số lượng sản phẩm ID=${id} => ${quantity}`);
        } catch (err) {
            console.error(`❌ Lỗi khi cập nhật sản phẩm ID=${id}:`, err);
        }
    }, 1000);

    const API_URL = `http://localhost:8080/api/cart_details/cart_detailsByUserName`;

    useEffect(() => {
        window.scrollTo(0, 0);
        const fetchCart = async () => {
            try {
                const res = await axiosInstance.get("/cart_details/cart_detailsByUserName");
                console.log("Dữ liệu giỏ hàng:", res.data);

                if (res.data && res.data.content) {
                    const mappedItems = res.data.content.map((item) => ({
                        id: item.id,
                        productId: item.productId,
                        name: item.productName,
                        price: item.unitPrice,
                        quantity: item.quantity,
                        image: item.imageUrl,
                        selected: false,
                    }));
                    setCartItems(mappedItems.reverse());
                } else {
                    setCartItems([]);
                }
            } catch (error) {
                console.error("Lỗi khi tải giỏ hàng:", error);
            } finally {
                setLoading(false);
            }
        };

        fetchCart();
    }, [API_URL]);

    // Chọn/bỏ chọn từng sản phẩm
    const toggleSelect = (id) => {
        setCartItems((prev) =>
            prev.map((item) =>
                item.id === id ? { ...item, selected: !item.selected } : item
            )
        );
    };

    // Xử lý mua 1 sản phẩm
    const handleBuyOne = (item) => {
        navigate("/checkout", {
            state: {
                items: [
                    {
                        id: item.id,
                        productId: item.productId,
                        quantity: item.quantity
                    }
                ]
            }
        });
    };

    // Chọn tất cả sản phẩm
    const selectAll = (checked) => {
        setCartItems((prev) => prev.map((item) => ({ ...item, selected: checked })));
    };

    // Tăng giảm số lượng
    const increase = (id) => {
        setCartItems((prev) =>
            prev.map((item) => {
                if (item.id === id) {
                    const newQuantity = item.quantity + 1;
                    updateQuantity(id, newQuantity);
                    return { ...item, quantity: newQuantity };
                }
                return item;
            })
        );
    };

    const decrease = (id) => {
        setCartItems((prev) =>
            prev.map((item) => {
                if (item.id === id && item.quantity > 1) {
                    const newQuantity = item.quantity - 1;
                    updateQuantity(id, newQuantity);
                    return { ...item, quantity: newQuantity };
                }
                return item;
            })
        );
    };

    // Tổng tiền các sản phẩm được chọn
    const total = cartItems
        .filter((item) => item.selected)
        .reduce((sum, item) => sum + item.price * item.quantity, 0);

    // Kiểm tra nếu tất cả sản phẩm được chọn
    const allSelected = cartItems.length > 0 && cartItems.every((item) => item.selected);

    // Xử lý khi click "Mua hàng đã chọn"
    const handleBuySelected = () => {
        const selected = cartItems.filter((item) => item.selected);
        if (selected.length === 0) {
            Swal.fire({
                title: "Chưa chọn sản phẩm",
                text: "Vui lòng chọn ít nhất một sản phẩm để mua.",
                icon: "warning",
                confirmButtonColor: "#667eea"
            });
            return;
        }

        navigate("/checkout", {
            state: {
                items: selected.map((item) => ({
                    id: item.id,
                    productId: item.productId,
                    quantity: item.quantity
                }))
            }
        });
    };

    // Xử lý xóa
    const removeItem = async (id) => {
        const item = cartItems.find((i) => i.id === id);
        if (!item) return;

        Swal.fire({
            title: "Xác nhận xóa",
            text: `Bạn có chắc muốn xóa "${item.name}" khỏi giỏ hàng?`,
            icon: "warning",
            showCancelButton: true,
            confirmButtonText: "Có, xóa!",
            cancelButtonText: "Hủy",
            confirmButtonColor: "#ff4757",
            cancelButtonColor: "#667eea"
        }).then(async (result) => {
            if (result.isConfirmed) {
                try {
                    await axiosInstance.delete(`/cart_details/${id}`);
                    setCartItems((prev) => prev.filter((i) => i.id !== id));
                    Swal.fire({
                        title: "Đã xóa!",
                        text: `"${item.name}" đã bị xóa khỏi giỏ hàng.`,
                        icon: "success",
                        confirmButtonColor: "#667eea"
                    });
                } catch (error) {
                    console.error("Lỗi khi xóa sản phẩm:", error);
                    Swal.fire({
                        title: "Lỗi!",
                        text: "Không thể xóa sản phẩm. Vui lòng thử lại.",
                        icon: "error",
                        confirmButtonColor: "#667eea"
                    });
                }
            }
        });
    };

    useEffect(() => {
        const handleBeforeUnload = () => {
            updateQuantity.flush?.();
        };

        window.addEventListener("beforeunload", handleBeforeUnload);

        return () => {
            handleBeforeUnload();
            window.removeEventListener("beforeunload", handleBeforeUnload);
        };
    }, []);

    if (loading) {
        return (
            <div className={styles.container}>
                <div className={styles.loadingContainer}>
                    <div className={styles.spinner}></div>
                    <p className={styles.loadingText}>Đang tải giỏ hàng...</p>
                </div>
            </div>
        );
    }

    return (
        <div className={styles.container}>
            {/* Header */}
            <div className={styles.header}>
                <div className={styles.headerContent}>
                    <h1 className={styles.title}>🛒 Giỏ hàng của tôi</h1>
                    <p className={styles.subtitle}>
                        {cartItems.length > 0
                            ? `Bạn có ${cartItems.length} sản phẩm trong giỏ hàng`
                            : "Giỏ hàng của bạn đang trống"
                        }
                    </p>
                </div>
            </div>

            {cartItems.length === 0 ? (
                <div className={styles.emptyCart}>
                    <div className={styles.emptyIcon}>🛍️</div>
                    <h2 className={styles.emptyTitle}>Giỏ hàng trống</h2>
                    <p className={styles.emptyText}>
                        Hãy thêm sản phẩm vào giỏ hàng để tiếp tục mua sắm
                    </p>
                    <button
                        className={styles.shopNowButton}
                        onClick={() => navigate("/")}
                    >
                        Mua sắm ngay
                    </button>
                </div>
            ) : (
                <>
                    {/* Select All Bar */}
                    <div className={styles.selectAllBar}>
                        <label className={styles.selectAllLabel}>
                            <input
                                type="checkbox"
                                className={styles.checkbox}
                                checked={allSelected}
                                onChange={(e) => selectAll(e.target.checked)}
                            />
                            <span>Chọn tất cả ({cartItems.length} sản phẩm)</span>
                        </label>
                    </div>

                    {/* Cart Items */}
                    <div className={styles.cartItems}>
                        {cartItems.map((item, index) => (
                            <div
                                key={item.id}
                                className={`${styles.cartCard} ${item.selected ? styles.selected : ''}`}
                            >
                                <div className={styles.cardCheckbox}>
                                    <input
                                        type="checkbox"
                                        className={styles.checkbox}
                                        checked={item.selected}
                                        onChange={() => toggleSelect(item.id)}
                                    />
                                </div>

                                <div className={styles.cardImage}>
                                    <img src={`http://localhost:8080/images/${item.image}`} />
                                    <span className={styles.itemNumber}>{index + 1}</span>
                                </div>

                                <div className={styles.cardInfo}>
                                    <h3 className={styles.itemName}>{item.name}</h3>
                                    <p className={styles.itemPrice}>
                                        {item.price.toLocaleString()}₫
                                    </p>
                                </div>

                                <div className={styles.cardQuantity}>
                                    <label className={styles.quantityLabel}>Số lượng</label>
                                    <div className={styles.quantityControl}>
                                        <button
                                            className={styles.quantityBtn}
                                            onClick={() => decrease(item.id)}
                                            disabled={item.quantity <= 1}
                                        >
                                            −
                                        </button>
                                        <span className={styles.quantityValue}>{item.quantity}</span>
                                        <button
                                            className={styles.quantityBtn}
                                            onClick={() => increase(item.id)}
                                        >
                                            +
                                        </button>
                                    </div>
                                </div>

                                <div className={styles.cardTotal}>
                                    <label className={styles.totalLabel}>Thành tiền</label>
                                    <p className={styles.totalPrice}>
                                        {(item.price * item.quantity).toLocaleString()}₫
                                    </p>
                                </div>

                                <div className={styles.cardActions}>
                                    <button
                                        className={styles.deleteBtn}
                                        onClick={() => removeItem(item.id)}
                                        title="Xóa sản phẩm"
                                    >
                                        🗑️
                                    </button>
                                    <button
                                        className={styles.buyOneBtn}
                                        onClick={() => handleBuyOne(item)}
                                    >
                                        Mua ngay
                                    </button>
                                </div>
                            </div>
                        ))}
                    </div>

                    {/* Summary Bar */}
                    <div className={styles.summaryBar}>
                        <div className={styles.summaryContent}>
                            <div className={styles.summaryInfo}>
                                <div className={styles.summaryRow}>
                                    <span>Số lượng đã chọn:</span>
                                    <strong>{cartItems.filter(item => item.selected).length} sản phẩm</strong>
                                </div>
                                <div className={styles.summaryRow}>
                                    <span>Tổng tiền hàng:</span>
                                    <strong className={styles.totalAmount}>
                                        {total.toLocaleString()}₫
                                    </strong>
                                </div>
                            </div>
                            <button
                                className={`${styles.checkoutBtn} ${total > 0 ? styles.active : ''}`}
                                disabled={total === 0}
                                onClick={handleBuySelected}
                            >
                                {total > 0 ? '🛍️ Thanh toán' : 'Chọn sản phẩm để thanh toán'}
                            </button>
                        </div>
                    </div>
                </>
            )}
        </div>
    );
};

export default Cart;
