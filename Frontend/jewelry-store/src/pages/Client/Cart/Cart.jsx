import React, { useState, useEffect, useRef, useCallback } from "react";
import axiosInstance from "../../../api/axiosInstance";
import styles from "./Cart.module.css";
import Swal from "sweetalert2";
import debounce from "lodash.debounce";
import { useNavigate } from "react-router-dom";
import defaultUrl from "../../../api/defaultUrl";
import { useBuyNow } from "../../../hook/useBuyNow";
const Cart = () => {
    // --- STATE DỮ LIỆU ---
    const [cartItems, setCartItems] = useState([]);
    const [loading, setLoading] = useState(true);
    const navigate = useNavigate();

    // --- STATE CHO INFINITE SCROLL ---
    const [nextCursor, setNextCursor] = useState(null); // ID mốc để tải tiếp
    const [hasMore, setHasMore] = useState(true);       // Còn dữ liệu không
    const [isFetchingMore, setIsFetchingMore] = useState(false); // Loading khi cuộn

    // --- 1. OBSERVER (LÍNH GÁC) ---
    const observer = useRef();
    const lastCartItemRef = useCallback(node => {
        if (loading || isFetchingMore) return;
        if (observer.current) observer.current.disconnect();

        observer.current = new IntersectionObserver(entries => {
            // Nếu thấy phần tử cuối cùng VÀ còn dữ liệu
            if (entries[0].isIntersecting && hasMore) {
                fetchCart(nextCursor); // Gọi hàm tải thêm
            }
        });

        if (node) observer.current.observe(node);
    }, [loading, isFetchingMore, hasMore, nextCursor]);

    // --- 2. HÀM GỌI API ---
    const fetchCart = async (cursorId = null) => {
        // Chặn gọi trùng
        if (cursorId && isFetchingMore) return;

        const isLoadMore = !!cursorId;

        if (isLoadMore) {
            setIsFetchingMore(true);
        } else {
            setLoading(true);
        }

        try {
            // Cấu hình tham số gửi lên Backend
            const params = { limit: 10 };
            if (cursorId) params.cursor = cursorId;

            const res = await axiosInstance.get("/cart_details/cart_detailsByUserNameV2", { params });
            console.log("🛒 Dữ liệu giỏ hàng:", res.data);

            // Lấy danh sách từ response (kiểm tra cấu trúc trả về của bạn, ở đây giả sử là .content hoặc .data)
            const dataList = res.data || [];

            if (dataList.length > 0) {
                // Map dữ liệu
                const mappedItems = dataList.map((item) => ({
                    id: item.id,
                    productId: item.productId,
                    name: item.productName,
                    price: item.unitPrice,
                    quantity: item.quantity,
                    image: item.imageUrl,
                    selected: false,
                }));

                if (isLoadMore) {
                    // Load thêm: Nối vào đuôi danh sách cũ
                    setCartItems(prev => [...prev, ...mappedItems]);
                } else {
                    // Load đầu: Ghi đè danh sách
                    setCartItems(mappedItems);
                }

                // Cập nhật Cursor (Lấy ID phần tử cuối)
                const lastItem = mappedItems[mappedItems.length - 1];
                setNextCursor(lastItem.id);

                // Nếu trả về ít hơn limit -> Hết dữ liệu
                setHasMore(mappedItems.length >= 10);
            } else {
                if (!isLoadMore) setCartItems([]);
                setHasMore(false);
            }

        } catch (error) {
            console.error("❌ Lỗi khi tải giỏ hàng:", error);
        } finally {
            setLoading(false);
            setIsFetchingMore(false);
        }
    };

    // --- 3. KHỞI TẠO ---
    useEffect(() => {
        // window.scrollTo(0, 0);
        setCartItems([]);
        setNextCursor(null);
        setHasMore(true);
        fetchCart(null);
    }, []);


    // --- CÁC HÀM LOGIC CŨ (GIỮ NGUYÊN) ---

    const updateQuantity = debounce(async (id, quantity) => {
        try {
            await axiosInstance.put(`/cart_details/${id}`, { quantity });
            console.log(`✅ Đã cập nhật ID=${id} => ${quantity}`);
        } catch (err) {
            console.error(`❌ Lỗi cập nhật ID=${id}:`, err);
        }
    }, 1000);

    const toggleSelect = (id) => {
        setCartItems((prev) =>
            prev.map((item) =>
                item.id === id ? { ...item, selected: !item.selected } : item
            )
        );
    };

    const { buyNow, isLoading } = useBuyNow();
    const handleBuyOne = (item) => {
        const convertedItem = { id: item.productId, quantity: item.quantity };
        buyNow(convertedItem);
    };

    const selectAll = (checked) => {
        setCartItems((prev) => prev.map((item) => ({ ...item, selected: checked })));
    };

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

    const total = cartItems
        .filter((item) => item.selected)
        .reduce((sum, item) => sum + item.price * item.quantity, 0);

    const allSelected = cartItems.length > 0 && cartItems.every((item) => item.selected);

    const handleBuySelected = () => {
        const selected = cartItems.filter((item) => item.selected);
        if (selected.length === 0) {
            Swal.fire({
                title: "Chưa chọn sản phẩm",
                text: "Vui lòng chọn ít nhất một sản phẩm.",
                icon: "warning",
                confirmButtonColor: "#667eea"
            });
            return;
        }
        const itemsToCheckout = selected.map((item) => ({
            id: item.productId,
            quantity: item.quantity
        }));
        buyNow(itemsToCheckout);
    };

    const removeItem = async (id) => {
        const item = cartItems.find((i) => i.id === id);
        if (!item) return;

        Swal.fire({
            title: "Xác nhận xóa",
            text: `Bạn có chắc muốn xóa "${item.name}"?`,
            icon: "warning",
            showCancelButton: true,
            confirmButtonText: "Có, xóa!",
            cancelButtonText: "Hủy",
            confirmButtonColor: "#ff4757",
        }).then(async (result) => {
            if (result.isConfirmed) {
                try {
                    await axiosInstance.delete(`/cart_details/${id}`);
                    setCartItems((prev) => prev.filter((i) => i.id !== id));
                    Swal.fire("Đã xóa!", "Sản phẩm đã bị xóa.", "success");
                } catch (error) {
                    Swal.fire("Lỗi!", "Không thể xóa sản phẩm.", "error");
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

    // --- RENDER GIAO DIỆN ---

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
            <div className={styles.header}>
                <div className={styles.headerContent}>
                    <h1 className={styles.title}>🛒 Giỏ hàng của tôi</h1>
                    <p className={styles.subtitle}>
                        {cartItems.length > 0
                            ? `Danh sách sản phẩm`
                            : "Giỏ hàng của bạn đang trống"
                        }
                    </p>
                </div>
            </div>

            {cartItems.length === 0 && !loading ? (
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
                    <div className={styles.selectAllBar}>
                        <label className={styles.selectAllLabel}>
                            <input
                                type="checkbox"
                                className={styles.checkbox}
                                checked={allSelected}
                                onChange={(e) => selectAll(e.target.checked)}
                            />
                            <span>Chọn tất cả (đã tải)</span>
                        </label>
                    </div>

                    <div className={styles.cartItems}>
                        {cartItems.map((item, index) => {
                            // 👇 Kiểm tra phần tử cuối cùng
                            const isLastElement = cartItems.length === index + 1;

                            return (
                                <div
                                    key={item.id}
                                    ref={isLastElement ? lastCartItemRef : null} // Gắn Ref lính gác
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
                                        <img src={`${defaultUrl}/images/${item.image}`} alt={item.name} />
                                        <span className={styles.itemNumber}>{index + 1}</span>
                                    </div>

                                    <div className={styles.cardInfo}>
                                        <h3 className={styles.itemName}>{item.name}</h3>
                                        <p className={styles.itemPrice}>
                                            {item.price.toLocaleString()}₫
                                        </p>
                                    </div>

                                    <div className={styles.cardQuantity}>
                                        <div className={styles.quantityControl}>
                                            <button
                                                className={styles.quantityBtn}
                                                onClick={() => decrease(item.id)}
                                                disabled={item.quantity <= 1}
                                            >−</button>
                                            <span className={styles.quantityValue}>{item.quantity}</span>
                                            <button
                                                className={styles.quantityBtn}
                                                onClick={() => increase(item.id)}
                                            >+</button>
                                        </div>
                                    </div>

                                    <div className={styles.cardTotal}>
                                        <p className={styles.totalPrice}>
                                            {(item.price * item.quantity).toLocaleString()}₫
                                        </p>
                                    </div>

                                    <div className={styles.cardActions}>
                                        <button
                                            className={styles.deleteBtn}
                                            onClick={() => removeItem(item.id)}
                                            title="Xóa sản phẩm"
                                        >🗑️</button>
                                        <button
                                            className={styles.buyOneBtn}
                                            onClick={() => handleBuyOne(item)}
                                        >Mua ngay</button>
                                    </div>
                                </div>
                            );
                        })}
                    </div>

                    {/* 👇 HIỂN THỊ LOADING KHI CUỘN */}
                    {isFetchingMore && (
                        <div className={styles.loadingContainer} style={{ padding: '20px' }}>
                            <div className={styles.spinner}></div>
                            <p className={styles.loadingText}>Đang tải thêm...</p>
                        </div>
                    )}

                    {!hasMore && cartItems.length > 0 && (
                        <p style={{ textAlign: 'center', padding: '20px', color: '#888' }}>
                            Đã hiển thị hết sản phẩm.
                        </p>
                    )}

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