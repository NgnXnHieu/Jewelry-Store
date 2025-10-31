import React, { useState, useEffect } from "react";
import axiosInstance from "../../../api/axiosInstance";
import styles from "./AddressManager.module.css";
import { FaHome, FaTrash, FaEdit, FaPlus, FaCheckCircle } from "react-icons/fa";
import Swal from "sweetalert2"; // ✅ import thư viện SweetAlert2

const AddressManager = () => {
    const [addresses, setAddresses] = useState([]);
    const [showForm, setShowForm] = useState(false);
    const [formData, setFormData] = useState({
        id: null,
        // name: "",
        phone: "",
        district: "",
        village: "",
        ward: "",
        isDefault: false
    });
    const [editing, setEditing] = useState(false);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchAddresses = async () => {
            try {
                const response = await axiosInstance.get("/addresses/myAddress");
                const data = response.data.map(addr => ({
                    id: addr.id,
                    // name: addr.userFullName || "Người dùng",
                    phone: addr.phone,
                    address: `${addr.village}, ${addr.ward}, ${addr.district}`,
                    isDefault: addr.is_defaut
                }));
                setAddresses(data);
            } catch (err) {
                console.error("Lỗi khi lấy địa chỉ:", err);
                setError(err);
            } finally {
                setLoading(false);
            }
        };
        fetchAddresses();
    }, []);

    const handleInputChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    // 🟢 Toggle form & reset
    const toggleForm = () => {
        setShowForm(!showForm);
        if (!showForm) {
            setEditing(false);
            setFormData({
                id: null,
                // name: "",
                phone: "",
                district: "",
                village: "",
                ward: "",
                isDefault: false
            });
        }
    };

    // 🟡 Thêm hoặc cập nhật
    const handleAddAddress = async () => {
        if (!formData.phone || !formData.village || !formData.ward || !formData.district) {
            return Swal.fire("⚠️ Thiếu thông tin", "Vui lòng điền đầy đủ các trường!", "warning");
        }

        const confirmResult = await Swal.fire({
            title: editing ? "Xác nhận chỉnh sửa" : "Xác nhận thêm mới",
            text: editing
                ? "Bạn có chắc muốn lưu thay đổi địa chỉ này?"
                : "Bạn có chắc muốn thêm địa chỉ mới?",
            icon: "question",
            showCancelButton: true,
            confirmButtonText: "Xác nhận",
            cancelButtonText: "Hủy",
            confirmButtonColor: "#3085d6",
            cancelButtonColor: "#d33"
        });

        if (!confirmResult.isConfirmed) return;

        try {
            if (editing) {
                const response = await axiosInstance.put(`/addresses/myAddress/${formData.id}`, {
                    phone: formData.phone,
                    village: formData.village,
                    ward: formData.ward,
                    district: formData.district,
                    is_defaut: formData.isDefault
                });

                setAddresses(addresses.map(a =>
                    a.id === formData.id
                        ? {
                            ...a,
                            phone: response.data.phone,
                            address: `${response.data.village}, ${response.data.ward}, ${response.data.district}`,
                            isDefault: response.data.is_defaut
                        }
                        : a
                ));
                Swal.fire("✅ Thành công", "Cập nhật địa chỉ thành công!", "success");
            } else {
                const response = await axiosInstance.post("/addresses/myAddress", {
                    phone: formData.phone,
                    village: formData.village,
                    ward: formData.ward,
                    district: formData.district,
                    is_defaut: formData.isDefault
                });

                const newAddr = response.data;
                setAddresses([
                    ...addresses,
                    {
                        id: newAddr.id,
                        // name: newAddr.userFullName || "Người dùng",
                        phone: newAddr.phone,
                        address: `${newAddr.village}, ${newAddr.ward}, ${newAddr.district}`,
                        isDefault: newAddr.is_defaut
                    }
                ]);
                Swal.fire("✅ Thành công", "Đã thêm địa chỉ mới!", "success");
            }
        } catch (err) {
            console.error("Lỗi khi lưu địa chỉ:", err);
            Swal.fire("❌ Lỗi", "Không thể lưu địa chỉ, vui lòng thử lại!", "error");
        }

        setFormData({
            id: null,
            // name: "",
            phone: "",
            district: "",
            village: "",
            ward: "",
            isDefault: false
        });
        setShowForm(false);
        setEditing(false);
    };

    // 🟠 Sửa địa chỉ
    const handleEdit = (addr) => {
        const parts = addr.address.split(",").map(p => p.trim());
        setFormData({
            id: addr.id,
            // name: addr.name,
            phone: addr.phone,
            village: parts[0] || "",
            ward: parts[1] || "",
            district: parts[2] || "",
            isDefault: addr.isDefault
        });
        setEditing(true);
        setShowForm(true);
    };

    // 🔴 Xóa địa chỉ
    const handleDelete = async (id) => {
        const confirmResult = await Swal.fire({
            title: "Xác nhận xóa",
            text: "Bạn có chắc muốn xóa địa chỉ này không?",
            icon: "warning",
            showCancelButton: true,
            confirmButtonText: "Xóa",
            cancelButtonText: "Hủy",
            confirmButtonColor: "#d33",
            cancelButtonColor: "#3085d6"
        });

        if (!confirmResult.isConfirmed) return;

        try {
            await axiosInstance.delete(`/addresses/${id}`);
            setAddresses(addresses.filter(a => a.id !== id));
            Swal.fire("🗑️ Đã xóa", "Địa chỉ đã được xóa thành công!", "success");
        } catch (err) {
            console.error("Lỗi khi xóa địa chỉ:", err);
            Swal.fire("❌ Lỗi", "Không thể xóa địa chỉ!", "error");
        }
    };

    // 🟣 Đặt mặc định
    const handleSetDefault = async (id) => {
        const confirmResult = await Swal.fire({
            title: "Đặt làm mặc định?",
            text: "Bạn có chắc muốn đặt địa chỉ này làm mặc định?",
            icon: "question",
            showCancelButton: true,
            confirmButtonText: "Xác nhận",
            cancelButtonText: "Hủy",
            confirmButtonColor: "#3085d6",
            cancelButtonColor: "#d33"
        });

        if (!confirmResult.isConfirmed) return;

        try {
            await axiosInstance.put(`/addresses/myAddress/${id}`, { is_defaut: true });
            setAddresses(addresses.map(a => ({ ...a, isDefault: a.id === id })));
            Swal.fire("✅ Thành công", "Đã đặt địa chỉ làm mặc định!", "success");
        } catch (err) {
            console.error("Lỗi khi đặt mặc định:", err);
            Swal.fire("❌ Lỗi", "Không thể đặt mặc định!", "error");
        }
    };

    if (loading) return <p>Loading addresses...</p>;
    if (error) return <p>Error loading addresses</p>;

    return (
        <div className={styles.container}>
            <h2 className={styles.title}>📍 Quản lý địa chỉ của bạn</h2>

            <button className={styles.addButton} onClick={toggleForm}>
                <FaPlus /> {showForm ? "Đóng form" : "Thêm địa chỉ mới"}
            </button>

            {showForm && (
                <div className={styles.formContainer}>
                    {/* <input
                        type="text"
                        name="name"
                        placeholder="Họ và tên"
                        value={formData.name}
                        onChange={handleInputChange}
                        className={styles.input}
                    /> */}
                    <input
                        type="text"
                        name="phone"
                        placeholder="Số điện thoại"
                        value={formData.phone}
                        onChange={handleInputChange}
                        className={styles.input}
                    />
                    <input
                        type="text"
                        name="village"
                        placeholder="Village"
                        value={formData.village}
                        onChange={handleInputChange}
                        className={styles.input}
                    />
                    <input
                        type="text"
                        name="ward"
                        placeholder="Ward"
                        value={formData.ward}
                        onChange={handleInputChange}
                        className={styles.input}
                    />
                    <input
                        type="text"
                        name="district"
                        placeholder="District"
                        value={formData.district}
                        onChange={handleInputChange}
                        className={styles.input}
                    />
                    <button className={styles.saveButton} onClick={handleAddAddress}>
                        {editing ? "Lưu chỉnh sửa" : "Thêm địa chỉ"}
                    </button>
                </div>
            )}

            <div className={styles.addressList}>
                {addresses.map((addr) => (
                    <div
                        key={addr.id}
                        className={`${styles.addressCard} ${addr.isDefault ? styles.default : ""}`}
                    >
                        <div className={styles.cardHeader}>
                            <FaHome className={styles.icon} />
                            {/* <h3>{addr.name}</h3> */}
                            {addr.isDefault && (
                                <span className={styles.defaultBadge}>
                                    <FaCheckCircle /> Mặc định
                                </span>
                            )}
                        </div>
                        <p><strong>Điện thoại:</strong> {addr.phone}</p>
                        <p><strong>Địa chỉ:</strong> {addr.address}</p>

                        <div className={styles.actions}>
                            {!addr.isDefault && (
                                <button
                                    className={styles.defaultButton}
                                    onClick={() => handleSetDefault(addr.id)}
                                >
                                    Đặt làm mặc định
                                </button>
                            )}
                            <button
                                className={styles.editButton}
                                onClick={() => handleEdit(addr)}
                            >
                                <FaEdit /> Sửa
                            </button>
                            <button
                                className={styles.deleteButton}
                                onClick={() => handleDelete(addr.id)}
                            >
                                <FaTrash /> Xóa
                            </button>
                        </div>
                    </div>
                ))}
            </div>
        </div>
    );
};

export default AddressManager;
