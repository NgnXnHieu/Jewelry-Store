import axios from "axios";

const API_URL = "http://localhost:8080/api/products";

export const getAllProducts = async () => {
    const res = await axios.get(API_URL);
    return res.data;
};

export const getProductById = async (id) => {
    const res = await axios.get(`${API_URL}/${id}`);
    return res.data;
};

export const createProduct = async (productData) => {
    const res = await axios.post(API_URL, productData);
    return res.data;
};

export const updateProduct = async (id, productData) => {
    const res = await axios.put(`${API_URL}/${id}`, productData);
    return res.data;
};

export const deleteProduct = async (id) => {
    const res = await axios.delete(`${API_URL}/${id}`);
    return res.data;
};

export const getRelatedProducts = async (id) => {
    const res = await axios.get(`${API_URL}/getRelatedProducts/${id}`);
    return res.data;
}

export const getProductsByCategory = async (categoryId) => {
    const res = await axios.get(`${API_URL}/productsByCategoryId/${categoryId}`);
    return res.data;
};
