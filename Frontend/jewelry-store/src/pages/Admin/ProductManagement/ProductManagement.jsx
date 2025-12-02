import { useEffect, useState } from "react";
import style from "./ProductManagement.module.css";
import axiosInstance from "../../../api/axiosInstance";
import Swal from "sweetalert2";
import defaultUrl from "../../../api/defaultUrl";

function ProductManagement() {
    const [products, setProducts] = useState([]);
    const [showDialog, setShowDialog] = useState(false);
    const [currentPage, setCurrentPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const itemsPerPage = 20;
    const [categories, setCategories] = useState([]);
    const [imagePreview, setImagePreview] = useState("");
    const [errors, setErrors] = useState({});
    const [fullSizeImage, setFullSizeImage] = useState(null); // For image zoom



    const validateForm = () => {
        const newErrors = {};

        if (!form.name.trim()) {
            newErrors.name = "Tên sản phẩm không được để trống";
        }

        if (!form.categoryId) {
            newErrors.categoryId = "Bạn phải chọn danh mục";
        }

        if (!form.price || form.price <= 0) {
            newErrors.price = "Giá phải lớn hơn 0";
        }

        if (!form.quantity || form.quantity <= 0) {
            newErrors.quantity = "Số lượng phải lớn hơn 0";
        }

        if (!form.description.trim()) {
            newErrors.description = "Mô tả không được để trống";
        }

        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const [form, setForm] = useState({
        id: "",
        name: "",
        description: "",
        price: 0,
        quantity: 0,
        image_url: "",
        categoryId: "",
        image: null
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
        fetchProducts(currentPage);
    }, [currentPage]);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setForm({ ...form, [name]: value });
        // Clear error when user types
        if (errors[name]) {
            setErrors({ ...errors, [name]: "" });
        }
    };

    const handleAddProduct = () => {
        setShowDialog(true);
        axiosInstance.get('/categories/all')
            .then(res => {
                setCategories(res.data);
            })
            .catch(err => console.error(err));
    };

    const handleCloseDialog = () => {
        if (imagePreview) {
            URL.revokeObjectURL(imagePreview);
        }
        setImagePreview("");
        setShowDialog(false);
        setErrors({});
        setForm({
            name: "",
            description: "",
            price: "",
            quantity: "",
            image_url: "",
            categoryId: "",
            image: null
        });
    };

    const handleSave = async (e) => {
        e.preventDefault();
        if (!validateForm()) {
            console.log("❌ Form chưa hợp lệ");
            return;
        }

        const actionText = form.id ? "cập nhật" : "thêm"; // sửa hoặc thêm

        const result = await Swal.fire({
            title: `Bạn có chắc muốn ${actionText} sản phẩm này?`,
            icon: 'question',
            showCancelButton: true,
            confirmButtonText: 'Có',
            cancelButtonText: 'Hủy'
        });

        if (!result.isConfirmed) return; // nếu người dùng hủy, thoát

        try {
            const formData = new FormData();
            formData.append("name", form.name);
            formData.append("price", parseFloat(form.price));
            formData.append("quantity", parseInt(form.quantity));
            formData.append("description", form.description);
            formData.append("categoryId", form.categoryId);

            if (form.image) {
                formData.append("image", form.image);
            } else if (form.image_url) {
                formData.append("image_url", form.image_url);
            }

            const isUpdate = !!form.id;
            const res = isUpdate
                ? await axiosInstance.put(`/products/${form.id}`, formData)
                : await axiosInstance.post("/products", formData);

            Swal.fire({
                icon: 'success',
                title: 'Thành công!',
                text: `Sản phẩm đã được ${actionText} thành công.`
            });

            handleCloseDialog();

            const pageToFetch = isUpdate ? currentPage : 0;

            axiosInstance.get(`/products?page=${pageToFetch}&size=20`)
                .then(res => {
                    setProducts(res.data.content);
                    setTotalPages(res.data.totalPages);
                    setCurrentPage(pageToFetch);
                })
                .catch(err => console.error(err));

        } catch (error) {
            console.error("❌ Lỗi khi lưu sản phẩm:", error);
            Swal.fire({
                icon: 'error',
                title: 'Lỗi!',
                text: 'Đã xảy ra lỗi khi lưu sản phẩm. Vui lòng thử lại!'
            });
        }
    };


    const handleEditProduct = (product) => {
        setForm({
            id: product.id,
            name: product.name,
            description: product.description,
            price: product.price,
            quantity: product.quantity,
            categoryId: product.categoryId,
            image_url: product.image_url || "",
            image: null
        });
        setImagePreview(product.image_url ? `${defaultUrl}/images/${product.image_url}` : "");

        // axiosInstance.get('/categories/all')
        //     .then(res => setCategories(res.data))
        //     .catch(err => console.error(err));

        setShowDialog(true);
    };

    const handleDeleteProduct = async (product) => {
        const result = await Swal.fire({
            title: `Bạn có chắc muốn xóa sản phẩm "${product.name}"?`,
            icon: 'warning',
            showCancelButton: true,
            confirmButtonText: 'Xóa',
            cancelButtonText: 'Hủy'
        });

        if (!result.isConfirmed) return;

        try {
            await axiosInstance.delete(`/products/${product.id}`);
            setProducts(products.filter(p => p.id !== product.id));

            Swal.fire({
                icon: 'success',
                title: 'Đã xóa!',
                text: `Sản phẩm "${product.name}" đã được xóa.`
            });
        } catch (error) {
            console.error(error);
            Swal.fire({
                icon: 'error',
                title: 'Lỗi!',
                text: 'Xóa sản phẩm thất bại!'
            });
        }
    };



    const handlePrev = () => setCurrentPage(prev => Math.max(prev - 1, 0));
    const handleNext = () => setCurrentPage(prev => Math.min(prev + 1, totalPages - 1));

    const formatPrice = (price) => {
        return new Intl.NumberFormat('vi-VN', {
            style: 'currency',
            currency: 'VND'
        }).format(price);
    };

    return (
        <div className={style.productContainer}>
            <div className={style.header}>
                <h2 className={style.title}>Quản lý sản phẩm</h2>
                <p className={style.subtitle}>Quản lý danh sách sản phẩm, thêm mới, chỉnh sửa và xóa sản phẩm</p>
            </div>

            <div className={style.actions}>
                <button className={style.addBtn} onClick={handleAddProduct}>
                    <svg className={style.icon} fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v16m8-8H4" />
                    </svg>
                    Thêm sản phẩm
                </button>
                <div className={style.searchWrapper}>
                    <svg className={style.searchIcon} fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
                    </svg>
                    <input
                        type="text"
                        placeholder="Tìm kiếm sản phẩm..."
                        className={style.searchBox}
                    />
                </div>
            </div>

            <div className={style.tableWrapper}>
                <table className={style.table}>
                    <thead>
                        <tr>
                            <th>Mã SP</th>
                            <th>Ảnh</th>
                            <th>Tên sản phẩm</th>
                            <th>Loại hàng</th>
                            <th>Giá</th>
                            <th>Số lượng</th>
                            <th>Mô tả</th>
                            <th>Hành động</th>
                        </tr>
                    </thead>
                    <tbody>
                        {products.length === 0 ? (
                            <tr>
                                <td colSpan="8" className={style.emptyState}>
                                    <svg className={style.emptyIcon} fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M20 13V6a2 2 0 00-2-2H6a2 2 0 00-2 2v7m16 0v5a2 2 0 01-2 2H6a2 2 0 01-2-2v-5m16 0h-2.586a1 1 0 00-.707.293l-2.414 2.414a1 1 0 01-.707.293h-3.172a1 1 0 01-.707-.293l-2.414-2.414A1 1 0 006.586 13H4" />
                                    </svg>
                                    <p>Chưa có sản phẩm nào</p>
                                </td>
                            </tr>
                        ) : (
                            products.map((p) => (
                                <tr key={p.id}>
                                    <td>#{p.id}</td>
                                    <td>
                                        <div className={style.imageCell}>
                                            {p.image_url ? (
                                                <img
                                                    src={`${defaultUrl}/images/${p.image_url}`}
                                                    alt={p.name}
                                                    className={style.productImage}
                                                    onClick={() => setFullSizeImage({ url: `${defaultUrl}/images/${p.image_url}`, name: p.name })}
                                                    style={{ cursor: 'pointer' }}
                                                />
                                            ) : (
                                                <div className={style.noImage}>
                                                    <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
                                                    </svg>
                                                </div>
                                            )}
                                        </div>
                                    </td>
                                    <td className={style.productName}>{p.name}</td>
                                    <td>
                                        <span className={style.categoryBadge}>
                                            {p.categoryName || `ID: ${p.categoryId}`}
                                        </span>
                                    </td>
                                    <td className={style.price}>{formatPrice(p.price)}</td>
                                    <td>
                                        <span className={`${style.quantityBadge} ${p.quantity <= 10 ? style.lowStock : ''}`}>
                                            {p.quantity}
                                        </span>
                                    </td>
                                    <td className={style.description}>{p.description}</td>
                                    <td>
                                        <div className={style.actionButtons}>
                                            <button className={style.editBtn} title="Sửa" onClick={() => handleEditProduct(p)}>
                                                <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                                                </svg>
                                            </button>
                                            <button className={style.deleteBtn} title="Xóa" onClick={() => handleDeleteProduct(p)}>
                                                <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                                                </svg>
                                            </button>
                                        </div>
                                    </td>
                                </tr>
                            ))
                        )}
                    </tbody>
                </table>
            </div>

            {/* Pagination */}
            <div className={style.pagination}>
                <button
                    onClick={handlePrev}
                    disabled={currentPage === 0}
                    className={style.paginationBtn}
                >
                    <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
                    </svg>
                    Trang trước
                </button>
                <span className={style.pageInfo}>
                    Trang {currentPage + 1} / {totalPages || 1}
                </span>
                <button
                    onClick={handleNext}
                    disabled={currentPage === totalPages - 1 || totalPages === 0}
                    className={style.paginationBtn}
                >
                    Trang sau
                    <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5l7 7-7 7" />
                    </svg>
                </button>
            </div>

            {/* Dialog thêm sản phẩm */}
            {showDialog && (
                <div className={style.dialogOverlay} onClick={(e) => {
                    if (e.target === e.currentTarget) handleCloseDialog();
                }}>
                    <div className={style.dialog}>
                        <div className={style.dialogHeader}>
                            <h3>Thêm sản phẩm mới</h3>
                            <button className={style.closeBtn} onClick={handleCloseDialog}>
                                <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                                </svg>
                            </button>
                        </div>

                        <form onSubmit={handleSave}>
                            <div className={style.formContainer}>
                                <div className={style.imageSection}>
                                    <label className={style.label}>Ảnh sản phẩm</label>
                                    <div className={style.imageBox}>
                                        {form.image || form.image_url ? (
                                            <div className={style.imagePreviewWrapper}>
                                                {/* Hiển thị ảnh nếu tồn tại */}
                                                <img
                                                    src={imagePreview || form.image_url}
                                                    alt="Preview"
                                                    className={style.imagePreview}
                                                />
                                                {/* Nút gỡ ảnh */}
                                                <button
                                                    type="button"
                                                    className={style.removeImageBtn}
                                                    onClick={() => {
                                                        if (imagePreview) {
                                                            URL.revokeObjectURL(imagePreview);
                                                        }
                                                        setImagePreview("");
                                                        setForm({ ...form, image: null, image_url: "" });
                                                    }}
                                                >
                                                    <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                                                    </svg>
                                                </button>
                                            </div>
                                        ) : (
                                            // Hiển thị khi chưa có ảnh
                                            <label htmlFor="fileInput" className={style.uploadLabel}>
                                                <svg className={style.uploadIcon} fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M7 16a4 4 0 01-.88-7.903A5 5 0 1115.9 6L16 6a5 5 0 011 9.9M15 13l-3-3m0 0l-3 3m3-3v12" />
                                                </svg>
                                                <span>Tải ảnh lên</span>
                                                <span className={style.uploadHint}>hoặc kéo thả ảnh vào đây</span>
                                            </label>
                                        )}
                                    </div>
                                    {/* Input của ảnh */}
                                    <input
                                        id="fileInput"
                                        type="file"
                                        accept="image/*"
                                        className={style.fileInput}
                                        onChange={(e) => {
                                            //Lấy ra file đầu tiên
                                            const file = e.target.files[0];
                                            //Nếu đã có file trước đó được chọn thì xóa đi rồi set file mới
                                            if (file) {
                                                if (imagePreview) {
                                                    URL.revokeObjectURL(imagePreview);
                                                }
                                                setImagePreview(URL.createObjectURL(file));
                                                setForm({
                                                    ...form,
                                                    image: file,
                                                    image_url: "",
                                                });
                                            }
                                        }}
                                    />
                                    <input
                                        type="text"
                                        placeholder="Hoặc dán link ảnh..."
                                        value={form.image_url}
                                        className={style.urlInput}
                                        onChange={(e) => {
                                            //Nếu chưa có file ảnh được up thì mới được dán link
                                            if (form.image == null) {
                                                setForm({
                                                    ...form,
                                                    image_url: e.target.value,
                                                });
                                            }
                                        }}
                                    />
                                </div>

                                <div className={style.formFields}>
                                    <div className={style.formGroup}>
                                        <label className={style.label}>
                                            Tên sản phẩm <span className={style.required}>*</span>
                                        </label>
                                        <input
                                            type="text"
                                            name="name"
                                            value={form.name}
                                            onChange={handleChange}
                                            className={`${style.input} ${errors.name ? style.inputError : ''}`}
                                            placeholder="Nhập tên sản phẩm"
                                        />
                                        {errors.name && <span className={style.error}>{errors.name}</span>}
                                    </div>

                                    <div className={style.formGroup}>
                                        <label className={style.label}>
                                            Loại sản phẩm <span className={style.required}>*</span>
                                        </label>
                                        <select
                                            name="categoryId"
                                            value={form.categoryId}
                                            onChange={handleChange}
                                            className={`${style.select} ${errors.categoryId ? style.inputError : ''}`}
                                        >
                                            <option value="">Chọn danh mục</option>
                                            {categories.map((cat) => (
                                                <option key={cat.id} value={cat.id}>{cat.name}</option>
                                            ))}
                                        </select>
                                        {errors.categoryId && <span className={style.error}>{errors.categoryId}</span>}
                                    </div>

                                    <div className={style.formRow}>
                                        <div className={style.formGroup}>
                                            <label className={style.label}>
                                                Giá <span className={style.required}>*</span>
                                            </label>
                                            <input
                                                type="number"
                                                name="price"
                                                value={form.price}
                                                onChange={handleChange}
                                                min="1"
                                                className={`${style.input} ${errors.price ? style.inputError : ''}`}
                                                placeholder="0"
                                            />
                                            {errors.price && <span className={style.error}>{errors.price}</span>}
                                        </div>


                                        {!form.id && (
                                            <div className={style.formGroup}>
                                                <label className={style.label}>
                                                    Số lượng <span className={style.required}>*</span>
                                                </label>
                                                <input
                                                    type="number"
                                                    name="quantity"
                                                    value={form.quantity}
                                                    onChange={handleChange}
                                                    min="1"
                                                    className={`${style.input} ${errors.quantity ? style.inputError : ''}`}
                                                    placeholder="0"
                                                />
                                                {errors.quantity && <span className={style.error}>{errors.quantity}</span>}
                                            </div>
                                        )}

                                    </div>

                                    <div className={style.formGroup}>
                                        <label className={style.label}>
                                            Mô tả <span className={style.required}>*</span>
                                        </label>
                                        <textarea
                                            name="description"
                                            value={form.description}
                                            onChange={handleChange}
                                            className={`${style.textarea} ${errors.description ? style.inputError : ''}`}
                                            placeholder="Nhập mô tả sản phẩm"
                                            rows="4"
                                        ></textarea>
                                        {errors.description && <span className={style.error}>{errors.description}</span>}
                                    </div>
                                </div>
                            </div>

                            <div className={style.dialogActions}>
                                <button type="button" className={style.cancelBtn} onClick={handleCloseDialog}>
                                    Hủy
                                </button>
                                <button type="submit" className={style.saveBtn}>
                                    Lưu sản phẩm
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            )}

            {/* Full Size Image Viewer */}
            {fullSizeImage && (
                <div
                    className={style.imageViewerOverlay}
                    onClick={() => setFullSizeImage(null)}
                >
                    <div className={style.imageViewerContainer}>
                        <button
                            className={style.imageViewerClose}
                            onClick={() => setFullSizeImage(null)}
                        >
                            <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                            </svg>
                        </button>
                        <img
                            src={fullSizeImage.url}
                            alt={fullSizeImage.name}
                            className={style.imageViewerImage}
                            onClick={(e) => e.stopPropagation()}
                        />
                        <div className={style.imageViewerLabel}>{fullSizeImage.name}</div>
                    </div>
                </div>
            )}
        </div>
    );
}

export default ProductManagement;
