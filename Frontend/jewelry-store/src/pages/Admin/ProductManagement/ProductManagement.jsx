import { useEffect, useState } from "react";
import style from "./ProductManagement.module.css";
import axiosInstance from "../../../api/axiosInstance";

function ProductManagement() {
    const [products, setProducts] = useState([]);
    const [showDialog, setShowDialog] = useState(false);
    const [currentPage, setCurrentPage] = useState(0); // backend 0-index
    const [totalPages, setTotalPages] = useState(0);
    const itemsPerPage = 20;
    const [form, setForm] = useState({
        id: "",
        name: "",
        description: "",
        price: "",
        quantity: "",
        image_url: "",
        categoryId: ""
    });


    const fetchProducts = (page) => {
        axiosInstance.get(`/products?page=${page}&size=20`)
            .then(res => {
                setProducts(res.data.content);
                setTotalPages(res.data.totalPages);
            })
            .catch(err => console.error(err));
    };

    useEffect(() => {
        fetchProducts(currentPage); // gọi backend mỗi khi currentPage thay đổi
    }, [currentPage]);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setForm({ ...form, [name]: value });
    };

    const handleAddProduct = () => setShowDialog(true);
    const handleCloseDialog = () => {
        setShowDialog(false);
        setForm({
            name: "",
            description: "",
            price: "",
            quantity: "",
            image_url: "",
            categoryId: "",
        });
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        const newProduct = {
            id: products.length + 1,
            name: form.name,
            price: form.price + "đ",
            quantity: form.quantity,
            description: form.description,
            image_url: form.image_url,
            categoryId: form.categoryId
        };
        setProducts([...products, newProduct]);
        handleCloseDialog();
    };

    // Calculate displayed products for current page
    // const startIndex = (currentPage - 1) * itemsPerPage;
    // const endIndex = startIndex + itemsPerPage;
    // const displayedProducts = products.slice(startIndex, endIndex);
    // const totalPages = Math.ceil(products.length / itemsPerPage);
    const handlePrev = () => setCurrentPage(prev => Math.max(prev - 1, 0));
    const handleNext = () => setCurrentPage(prev => Math.min(prev + 1, totalPages - 1));


    return (
        <div className={style.productContainer}>
            <h2 className={style.title}>Quản lý sản phẩm</h2>

            <div className={style.actions}>
                <button className={style.addBtn} onClick={handleAddProduct}>
                    + Thêm sản phẩm
                </button>
                <input
                    type="text"
                    placeholder="Tìm kiếm sản phẩm..."
                    className={style.searchBox}
                />
            </div>

            <div className={style.tableWrapper}>
                <table className={style.table}>
                    <thead>
                        <tr>
                            <th>Mã SP</th>
                            <th>Ảnh</th>
                            <th>Tên</th>
                            <th>Loại hàng</th>
                            <th>Giá</th>
                            <th>Số lượng</th>
                            <th>Mô tả</th>
                            <th>Hành động</th>
                        </tr>
                    </thead>
                    <tbody>
                        {products.map((p) => (
                            <tr key={p.id}>
                                <td>{p.id}</td>
                                <td>
                                    {p.image_url ? (
                                        <img src={p.image_url} alt={p.name} className={style.productImage} />
                                    ) : (
                                        "Chưa có ảnh"
                                    )}
                                </td>
                                <td>{p.name}</td>
                                <td>{p.categoryName || p.categoryId}</td>
                                <td>{p.price}</td>
                                <td>{p.quantity}</td>
                                <td>{p.description}</td>
                                <td>
                                    <div className={style.actionButtons}>
                                        <button className={style.editBtn}>Sửa</button>
                                        <button className={style.deleteBtn}>Xóa</button>
                                    </div>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>

            {/* Pagination */}
            <div className={style.pagination}>
                <button
                    onClick={handlePrev}
                    disabled={currentPage === 0}
                >
                    Prev
                </button>
                <span>{currentPage + 1} / {totalPages}</span>
                <button
                    onClick={handleNext}
                    disabled={currentPage === totalPages - 1}
                >
                    Next
                </button>
            </div>

            {/* Dialog thêm sản phẩm */}
            {showDialog && (
                <div className={style.dialogOverlay}>
                    <div className={style.dialog}>
                        <h3>Thêm sản phẩm</h3>
                        <div className={style.formContainer}>
                            <div className={style.imageBox}>
                                {form.image_url ? (
                                    <img src={form.image_url} alt="Preview" />
                                ) : (
                                    <label htmlFor="imageInput">
                                        <svg
                                            xmlns="http://www.w3.org/2000/svg"
                                            width="64"
                                            height="64"
                                            fill="none"
                                            stroke="black"
                                            strokeWidth="2"
                                            viewBox="0 0 24 24"
                                        >
                                            <rect x="3" y="3" width="18" height="18" rx="2" ry="2"></rect>
                                            <circle cx="8.5" cy="8.5" r="1.5"></circle>
                                            <path d="M21 15l-5-5L5 21"></path>
                                        </svg>
                                    </label>
                                )}
                                <input
                                    id="imageInput"
                                    type="text"
                                    name="image_url"
                                    value={form.image_url}
                                    onChange={handleChange}
                                    placeholder="Dán link ảnh..."
                                />
                            </div>

                            <form onSubmit={handleSubmit} className={style.formFields}>
                                <label>
                                    Tên sản phẩm *
                                    <input type="text" name="name" value={form.name} onChange={handleChange} required />
                                </label>
                                <label>
                                    Mã danh mục *
                                    <input type="number" name="categoryId" value={form.categoryId} onChange={handleChange} required />
                                </label>
                                <label>
                                    Giá *
                                    <input type="number" name="price" value={form.price} onChange={handleChange} required min="1" />
                                </label>
                                <label>
                                    Số lượng *
                                    <input type="number" name="quantity" value={form.quantity} onChange={handleChange} required min="1" />
                                </label>
                                <label className={style.fullWidth}>
                                    Mô tả *
                                    <textarea name="description" value={form.description} onChange={handleChange} required></textarea>
                                </label>

                                <div className={style.dialogActions}>
                                    <button type="button" className={style.cancelBtn} onClick={handleCloseDialog}>
                                        Hủy
                                    </button>
                                    <button type="submit" className={style.saveBtn}>
                                        Lưu
                                    </button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}

export default ProductManagement;
