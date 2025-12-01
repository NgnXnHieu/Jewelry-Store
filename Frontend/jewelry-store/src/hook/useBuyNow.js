import { useState } from "react";
import { useNavigate } from "react-router-dom";
import axiosInstance from "../api/axiosInstance"; // Điều chỉnh đường dẫn import tùy cấu trúc folder của bạn

export const useBuyNow = () => {
    const navigate = useNavigate();
    const [isLoading, setIsLoading] = useState(false);

    // Hàm xử lý mua hàng
    const buyNow = async (items) => {
        // items có thể là:
        // 1. Một object đơn: { id: 1, quantity: 2 }
        // 2. Một mảng: [{ id: 1, quantity: 2 }, { id: 5, quantity: 1 }]

        if (!items) return;

        setIsLoading(true);
        try {
            let itemMap = {};

            // BƯỚC 1: Xử lý dữ liệu đầu vào thành Map
            if (Array.isArray(items)) {
                // Trường hợp là mảng (nhiều item)
                // Dùng reduce để chuyển Array -> Object { id: quantity, id: quantity }
                itemMap = items.reduce((acc, item) => {
                    acc[item.id] = item.quantity;
                    return acc;
                }, {});
            } else {
                // Trường hợp là object đơn (1 item)
                itemMap = {
                    [items.id]: items.quantity
                };
            }

            // BƯỚC 2: Tạo payload
            const payload = {
                itemList: itemMap
            };

            // Log ra xem thử cấu trúc đúng chưa
            console.log("Payload gửi đi:", payload);

            // BƯỚC 3: Gọi API
            const response = await axiosInstance.post('/checkout', payload);

            // const { checkoutId } = response.data;
            console.log("Checkout ID nhận được:", response.data);
            navigate(`/checkout/${response.data}`);
        } catch (error) {
            console.error("Lỗi khi tạo checkout:", error);
            // Bạn có thể dùng thư viện toast để thông báo đẹp hơn alert
            alert("Không thể tạo đơn hàng, vui lòng thử lại!");
        } finally {
            setIsLoading(false);
        }
    };

    // Trả về hàm thực thi và trạng thái loading
    return { buyNow, isLoading };
};