// src/api/cartApi.js
import axios from "axios";

const BASE_URL = "http://localhost:8080/api/cart_details";

export const addToCart = async (productId, quantity = 1) => {
    try {
        const response = await axios.post(
            BASE_URL,
            { productId, quantity },
            {
                headers: {
                    "Content-Type": "application/json",
                },
                withCredentials: true, // ⚠️ gửi cookie JWT sang backend
            }
        );
        return response.data;
    } catch (error) {
        console.error("Lỗi khi thêm giỏ hàng:", error);
        throw error;
    }
};
