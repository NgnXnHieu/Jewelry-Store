import { useEffect, useState } from "react";
import style from "./CategoryManagement.module.css";
import axiosInstance from "../../../api/axiosInstance";
import Swal from "sweetalert2";

function CategoryManagement() {
    const [categories, setCategories] = useState([]);
    const [showDialog, setShowDialog] = useState(false);
    const [form, setForm] = useState({ id: "", name: "" });
    const [errors, setErrors] = useState({});
    const [currentPage, setCurrentPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const itemsPerPage = 20;

    // 🧩 Gọi API lấy danh mục
    useEffect(() => {
        fetchCategories(currentPage);
    }, [currentPage]);

    const fetchCategories = (page) => {
        axiosInstance
            .get(`/categories?page=${page}&size=${itemsPerPage}`)
            .then((res) => {
                setCategories(res.data.content);
                setTotalPages(res.data.totalPages);
            })
            .catch((err) => console.error(err));
    };

    // 🧩 Mở dialog thêm/sửa
    const handleOpenDialog = (category = null) => {
        if (category) {
            setForm({ id: category.id, name: category.name });
        } else {
            setForm({ id: "", name: "" });
        }
        setErrors({});
        setShowDialog(true);
    };

    const handleCloseDialog = () => {
        setShowDialog(false);
    };

    // 🧩 Validate form
    const validateForm = () => {
        const newErrors = {};
        if (!form.name.trim()) newErrors.name = "Tên danh mục không được để trống";
        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    // 🧩 Lưu danh mục (thêm/sửa)
    const handleSave = async () => {
        if (!validateForm()) return;
        const confirm = await Swal.fire({
            title: form.id ? "Bạn có chắc muốn cập nhật" : "Bạn có chắc muốn thêm mới",
            text: `Danh mục: ${form.name}`,
            icon: "warning",
            showCancelButton: true,
            confirmButtonText: "Xác nhận",
            cancelButtonText: "Hủy",
        });

        if (confirm.isConfirmed) {
            try {
                if (form.id) {
                    await axiosInstance.put(`/categories/${form.id}`, { name: form.name });
                } else {
                    await axiosInstance.post("/categories", { name: form.name });
                }

                Swal.fire({
                    icon: "success",
                    title: form.id ? "Cập nhật thành công" : "Thêm mới thành công",
                    timer: 1200,
                    showConfirmButton: true,
                    confirmButtonText: "OK",
                });

                setShowDialog(false);
                const pageToReload = form.id ? currentPage : 0;
                fetchCategories(pageToReload);
                setCurrentPage(pageToReload);
            } catch (err) {
                console.error(err);
            }
        }

    };

    // 🧩 Xóa danh mục
    const handleDelete = async (category) => {
        const confirm = await Swal.fire({
            title: "Bạn có chắc muốn xóa?",
            text: `Danh mục: ${category.name}`,
            icon: "warning",
            showCancelButton: true,
            confirmButtonText: "Xóa",
            cancelButtonText: "Hủy",
        });

        if (confirm.isConfirmed) {
            await axiosInstance.delete(`/categories/${category.id}`);
            fetchCategories(currentPage);
            Swal.fire({
                icon: "success",
                title: "Xóa thành công",
                timer: 1200,
                showConfirmButton: true,
                confirmButtonText: "OK",
            });
        }
    };

    return (
        <div className={style.container}>
            <div className={style.header}>
                <h2>Quản lý danh mục</h2>
                <button className={style.addBtn} onClick={() => handleOpenDialog()}>
                    + Thêm danh mục
                </button>
            </div>

            <table className={style.table}>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Tên danh mục</th>
                        <th>Hành động</th>
                    </tr>
                </thead>
                <tbody>
                    {categories.map((category) => (
                        <tr key={category.id}>
                            <td>{category.id}</td>
                            <td>{category.name}</td>
                            <td>
                                <button
                                    className={style.editBtn}
                                    onClick={() => handleOpenDialog(category)}
                                >
                                    ✏️ Sửa
                                </button>
                                <button
                                    className={style.deleteBtn}
                                    onClick={() => handleDelete(category)}
                                >
                                    🗑️ Xóa
                                </button>
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>

            {/* Dialog */}
            {showDialog && (
                <div className={style.dialogBackdrop}>
                    <div className={style.dialog}>
                        <h3>{form.id ? "Sửa danh mục" : "Thêm danh mục"}</h3>
                        <input
                            type="text"
                            placeholder="Nhập tên danh mục"
                            value={form.name}
                            onChange={(e) => setForm({ ...form, name: e.target.value })}
                        />
                        {errors.name && <p className={style.error}>{errors.name}</p>}
                        <div className={style.dialogActions}>
                            <button className={style.saveBtn} onClick={handleSave}>Lưu</button>
                            <button className={style.cancelBtn} onClick={handleCloseDialog}>Hủy</button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}

export default CategoryManagement;
