import React, { useState, useEffect } from "react";
import { Link, useNavigate } from "react-router-dom";
import { FaUserCircle, FaShoppingCart, FaHistory } from "react-icons/fa";
import style from "./Header.module.css";
import axiosInstance from "../../api/axiosInstance";

function Header() {
    const navigate = useNavigate();
    const [username, setUsername] = useState(null);
    const [showMenu, setShowMenu] = useState(false);
    const [categories, setCategories] = useState([]);


    useEffect(() => {
        // Gọi API để lấy thông tin user từ cookie
        const fetchUser = async () => {
            try {
                const res = await axiosInstance.get("/users/infor"); // backend trả { username: "...", role: "..." }
                setUsername(res.data.username);
            } catch (err) {
                setUsername(null);
            }
        };
        fetchUser();
    }, []);

    useEffect(() => {
        const handleClickOutside = (e) => {
            if (!e.target.closest(`.${style.userMenuWrapper}`)) {
                setShowMenu(false);
            }
        };
        document.addEventListener("click", handleClickOutside);
        return () => document.removeEventListener("click", handleClickOutside);
    }, []);

    const handleLogout = async () => {
        try {
            await axiosInstance.post("/logout"); // logout backend, xóa cookies
            setUsername(null);
            navigate("/login");
        } catch (err) {
            console.log(err);
        }
    };

    // ✅ Lấy danh sách category từ backend
    useEffect(() => {
        axiosInstance
            .get("http://localhost:8080/api/categories")
            .then((res) => {
                setCategories(res.data.content || res.data); // nếu API trả trong `content`
            })
            .catch((err) => {
                console.error("Lỗi khi lấy danh mục:", err);
            });
    }, []);

    // ✅ Khi click vào category
    const handleCategoryClick = (categoryId) => {
        // Cách 1: Điều hướng sang trang khác (ví dụ /category/:id)
        navigate(`/category/${categoryId}`);

        // Cách 2: Hoặc nếu bạn chỉ muốn gọi API trong component này
        // axios.get(`http://localhost:8080/api/categories/productsByCategoryId/${categoryId}`)
        //     .then(res => console.log(res.data))
        //     .catch(err => console.error(err));
    };


    return (
        <div className={style.allHeader}>
            <div className={style.header1}>
                <Link to="/"><h1>Jewelry Store</h1></Link>

                <input
                    type="text"
                    placeholder="Tìm kiếm sản phẩm..."
                    className={style.search_bar}
                />

                <div className={style.iconGroup}>
                    <Link to="/cart" className={style.iconButton}><FaShoppingCart /></Link>
                    <Link to="/order" className={style.iconButton}><FaHistory /></Link>

                    <div className={style.userMenuWrapper} onClick={() => setShowMenu(true)}>
                        <FaUserCircle className={style.userIcon} />
                        {showMenu && (
                            <div className={style.dropdownMenu}>
                                {username ? (
                                    <>
                                        <p className={style.menuHeader}>👋 Xin chào, {username}</p>
                                        <Link to="/profile" className={style.menuItem}>Thông tin cá nhân</Link>
                                        <Link to="/addressManager" className={style.menuItem}>Địa chỉ</Link>
                                        <Link to="/change-password" className={style.menuItem}>Đổi mật khẩu</Link>
                                        <button onClick={handleLogout} className={style.menuItem}>Đăng xuất</button>
                                    </>
                                ) : (
                                    <Link to="/login" className={style.menuItem}>Đăng nhập / Đăng ký</Link>
                                )}
                            </div>
                        )}
                    </div>
                </div>
            </div>

            <div className={style.header2}>
                {categories.map((item) => (
                    <Link
                        key={item.id}
                        to={`/category/${item.id}`}
                        className={style.navLink}
                    // onClick={() => handleCategoryClick(item.id)}
                    >
                        {item.name}
                    </Link>
                ))}
            </div>
        </div>
    );
}

export default Header;
