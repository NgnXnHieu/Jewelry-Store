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


    // const userId = 21; // tạm thời gắn cứng, có thể lấy từ context hoặc token sau này
    const API_URL = `http://localhost:8080/api/cart_details/cart_detailsByUserName`;

    useEffect(() => {
        const fetchCart = async () => {
            try {
                const res = await axiosInstance.get("/cart_details/cart_detailsByUserName");
                // const res = await axios.get(API_URL);
                console.log("Dữ liệu giỏ hàng:", res.data);

                // Nếu backend trả về kiểu Page (có field content)
                if (res.data && res.data.content) {
                    // Chuyển đổi để phù hợp với cấu trúc đang dùng
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

    //Xử lý mua 1 sản phẩm
    const handleBuyOne = (item) => {
        // Chuyển hướng sang trang checkout, kèm dữ liệu sản phẩm
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


    //Tăng giảm số lượng
    const increase = (id) => {
        setCartItems((prev) =>
            prev.map((item) => {
                if (item.id === id) {
                    const newQuantity = item.quantity + 1;
                    updateQuantity(id, newQuantity); // ✅ gọi hàm debounce
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
                    updateQuantity(id, newQuantity); // ✅ gọi hàm debounce
                    return { ...item, quantity: newQuantity };
                }
                return item;
            })
        );
    };


    // const removeItem = (id) => {
    //     setCartItems((prev) => prev.filter((item) => item.id !== id));
    // };

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
            Swal.fire("Chưa chọn sản phẩm", "Vui lòng chọn ít nhất một sản phẩm để mua.", "warning");
            return;
        }

        // Điều hướng sang trang checkout, truyền danh sách sản phẩm đã chọn
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

    //Xử lý xóa
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
        }).then(async (result) => {
            if (result.isConfirmed) {
                try {
                    await axiosInstance.delete(`/cart_details/${id}`);
                    setCartItems((prev) => prev.filter((i) => i.id !== id));
                    // Swal.fire("Đã xóa!", "Sản phẩm đã bị xóa khỏi giỏ hàng.", "success");

                    // ✅ Cập nhật giao diện
                    setCartItems((prev) => prev.filter((i) => i.id !== id));

                    Swal.fire("Đã xóa!", `"${item.name}" đã bị xóa khỏi giỏ hàng.`, "success");
                } catch (error) {
                    console.error("Lỗi khi xóa sản phẩm:", error);
                    Swal.fire("Lỗi!", "Không thể xóa sản phẩm. Vui lòng thử lại.", "error");
                }
            }
        });
    };




    useEffect(() => {
        const handleBeforeUnload = () => {
            // Chờ tất cả các debounce còn lại chạy xong
            updateQuantity.flush?.(); // flush để chạy ngay các debounce còn pending
        };

        window.addEventListener("beforeunload", handleBeforeUnload);

        return () => {
            handleBeforeUnload();
            window.removeEventListener("beforeunload", handleBeforeUnload);
        };
    }, []);

    if (loading) return <p>Đang tải giỏ hàng...</p>;

    return (
        <div className={styles.container}>
            <h1>Giỏ hàng của tôi</h1>
            {cartItems.length === 0 ? (
                <p>Giỏ hàng trống.</p>
            ) : (
                <>
                    <table className={styles.table}>
                        <thead>
                            <tr>
                                <th>
                                    <input
                                        type="checkbox"
                                        checked={allSelected}
                                        onChange={(e) => selectAll(e.target.checked)}
                                    />
                                </th>
                                <th>STT</th>
                                <th>Ảnh</th>
                                <th>Tên sản phẩm</th>
                                <th>SL</th>
                                <th>Đơn giá</th>
                                <th>Thành tiền</th>
                                <th colSpan="2">Thao tác</th>
                            </tr>
                        </thead>
                        <tbody>
                            {cartItems.map((item, index) => (
                                <tr key={item.id}>
                                    <td>
                                        <input
                                            type="checkbox"
                                            checked={item.selected}
                                            onChange={() => toggleSelect(item.id)}
                                        />
                                    </td>
                                    <td>{index + 1}</td>
                                    <td>
                                        <img
                                            src={item.image}
                                            alt={item.name}
                                            className={styles.image}
                                        />
                                    </td>
                                    <td>{item.name}</td>
                                    <td>
                                        <div className={styles.quantityBox}>
                                            <button onClick={() => decrease(item.id)}>-</button>
                                            <span>{item.quantity}</span>
                                            <button onClick={() => increase(item.id)}>+</button>
                                        </div>
                                    </td>
                                    <td>{item.price.toLocaleString()}đ</td>
                                    <td>{(item.price * item.quantity).toLocaleString()}đ</td>
                                    <td>
                                        <button
                                            className={styles.deleteButton}
                                            onClick={() => removeItem(item.id)}
                                        >
                                            Xóa
                                        </button>
                                    </td>
                                    <td>
                                        <button className={styles.buyButton} onClick={() => handleBuyOne(item)}>Mua</button>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>

                    <div className={styles.bottomBar}>
                        <div className={styles.total}>
                            <strong>Tổng cộng: {total.toLocaleString()}đ</strong>
                        </div>
                        <button
                            className={`${styles.buySelectedButton} ${total > 0 ? styles.active : ""
                                }`}
                            disabled={total === 0}
                            onClick={handleBuySelected}
                        >
                            Mua hàng
                        </button>
                    </div>
                </>
            )}
        </div>
    );
};

export default Cart;
