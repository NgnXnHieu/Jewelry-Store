// import axios from "axios";

// const API_URL = "http://localhost:8080/api/products";

// export const getAllProducts = async () => {
//     const res = await axios.get(API_URL);
//     return res.data;
// };

// export const getProductById = async (id) => {
//     const res = await axios.get(`${API_URL}/${id}`);
//     return res.data;
// };

// export const createProduct = async (productData) => {
//     const res = await axios.post(API_URL, productData);
//     return res.data;
// };

// export const updateProduct = async (id, productData) => {
//     const res = await axios.put(`${API_URL}/${id}`, productData);
//     return res.data;
// };

// export const deleteProduct = async (id) => {
//     const res = await axios.delete(`${API_URL}/${id}`);
//     return res.data;
// };

// export const getRelatedProducts = async (id) => {
//     const res = await axios.get(`${API_URL}/getRelatedProducts/${id}`);
//     return res.data;
// }

// export const getProductsByCategory = async (categoryId) => {
//     const res = await axios.get(`${API_URL}/productsByCategoryId/${categoryId}`);
//     return res.data;
// };
// ⚠️ QUAN TRỌNG: Hãy trỏ đúng đường dẫn đến file axiosInstance của bạn
// Ví dụ: Nếu file này ở src/services/productService.js thì import như dưới là đúng
import axios from "../api/axiosInstance";

// Khai báo endpoint gốc (để sau này nối chuỗi cho gọn)
// Lưu ý: baseURL trong axiosInstance đã là ".../api" rồi, nên ở đây bắt đầu từ "/products"
const ENDPOINT = "/products";

export const getAllProducts = async () => {
    // Tự động hiểu là: https://...ngrok.../api/products
    const res = await axios.get(ENDPOINT);
    return res.data;
};

export const getProductById = async (id) => {
    const res = await axios.get(`${ENDPOINT}/${id}`);
    return res.data;
};

export const createProduct = async (productData) => {
    // Dùng axiosInstance sẽ tự động có Header Authorization: Bearer ...
    const res = await axios.post(ENDPOINT, productData);
    return res.data;
};

export const updateProduct = async (id, productData) => {
    const res = await axios.put(`${ENDPOINT}/${id}`, productData);
    return res.data;
};

export const deleteProduct = async (id) => {
    const res = await axios.delete(`${ENDPOINT}/${id}`);
    return res.data;
};

export const getRelatedProducts = async (id) => {
    const res = await axios.get(`${ENDPOINT}/getRelatedProducts/${id}`);
    return res.data;
};

export const getProductsByCategory = async (categoryId) => {
    const res = await axios.get(`${ENDPOINT}/productsByCategoryId/${categoryId}`);
    return res.data;
};