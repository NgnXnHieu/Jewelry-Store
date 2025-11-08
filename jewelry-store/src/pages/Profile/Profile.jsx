import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import axiosInstance from "../../api/axiosInstance";
import style from "./Profile.module.css";
import { FaCamera, FaEnvelope, FaPhone, FaLock, FaUserEdit } from "react-icons/fa";

const Profile = () => {
    const navigate = useNavigate();
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);
    const [avatarPreview, setAvatarPreview] = useState(null);

    useEffect(() => {
        const fetchUser = async () => {
            try {
                const res = await axiosInstance.get("/users/infor"); // backend trả JSON user
                setUser({
                    fullName: res.data.full_name,
                    email: res.data.email,
                    phone: res.data.phone,
                    avatar: res.data.avatar, // nếu backend có
                    address: res.data.address, // nếu backend có
                    role: res.data.role,
                });
            } catch (err) {
                console.error("❌ [Profile] Lỗi API:", err);
                navigate("/login"); // nếu chưa login hoặc 401
            } finally {
                setLoading(false);
            }
        };

        fetchUser();
    }, [navigate]);

    const handleImageChange = (e) => {
        const file = e.target.files[0];
        if (file) {
            setAvatarPreview(URL.createObjectURL(file));
        }
    };

    const handleLogout = async () => {
        try {
            await axiosInstance.post("/logout"); // backend xóa cookie
            navigate("/login");
        } catch (err) {
            console.error(err);
        }
    };

    if (loading) return <div className="text-center mt-10">Đang tải thông tin...</div>;
    if (!user) return null;

    return (
        <div className={style.container}>
            <div className={style.profileCard}>
                <div className={style.avatarSection}>
                    <img
                        src={avatarPreview || user.avatar || "https://cdn-icons-png.flaticon.com/512/149/149071.png"}
                        alt="Avatar"
                        className={style.avatar}
                    />
                    <label htmlFor="avatarInput" className={style.cameraButton}>
                        <FaCamera />
                    </label>
                    <input
                        id="avatarInput"
                        type="file"
                        accept="image/*"
                        onChange={handleImageChange}
                        className={style.hiddenInput}
                    />
                </div>

                <h2 className={style.userName}>{user.fullName}</h2>

                <div className={style.infoSection}>
                    <p><FaEnvelope /> {user.email}</p>
                    <p><FaPhone /> {user.phone || "Chưa cập nhật"}</p>
                    <p><FaUserEdit /> {user.address || "Chưa cập nhật địa chỉ"}</p>
                </div>

                <div className={style.actionButtons}>
                    <button className={style.editButton}><FaUserEdit /> Đổi tên</button>
                    <button className={style.editButton}><FaEnvelope /> Đổi email</button>
                    <button className={style.editButton}><FaPhone /> Đổi số điện thoại</button>
                    <button className={style.editButton}><FaLock /> Đổi mật khẩu</button>
                </div>

                <button onClick={handleLogout} className={style.logoutButton}>Đăng xuất</button>
            </div>
        </div>
    );
};

export default Profile;
