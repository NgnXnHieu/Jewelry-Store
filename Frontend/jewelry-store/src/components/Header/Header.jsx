import React, { useState, useEffect } from "react";
import { Link, useNavigate } from "react-router-dom";
import { FaUserCircle, FaShoppingCart, FaHistory, FaSearch, FaBars, FaTimes } from "react-icons/fa";
import style from "./Header.module.css";
import axiosInstance from "../../api/axiosInstance";

function Header() {
    const navigate = useNavigate();
    const [username, setUsername] = useState(null);
    const [showMenu, setShowMenu] = useState(false);
    const [categories, setCategories] = useState([]);
    const [searchQuery, setSearchQuery] = useState("");
    const [isScrolled, setIsScrolled] = useState(false);
    const [showMobileMenu, setShowMobileMenu] = useState(false);
    const [cartCount, setCartCount] = useState(0);

    // Detect scroll for header shadow effect
    useEffect(() => {
        const handleScroll = () => {
            setIsScrolled(window.scrollY > 20);
        };
        window.addEventListener("scroll", handleScroll);
        return () => window.removeEventListener("scroll", handleScroll);
    }, []);

    useEffect(() => {
        // Fetch user info from cookie
        const fetchUser = async () => {
            try {
                const res = await axiosInstance.get("/users/infor");
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
            await axiosInstance.post("/logout");
            setUsername(null);
            navigate("/login");
        } catch (err) {
            console.log(err);
        }
    };

    // Fetch categories
    useEffect(() => {
        axiosInstance
            .get("/categories")
            .then((res) => {
                setCategories(res.data.content || res.data);
            })
            .catch((err) => {
                console.error("Lỗi khi lấy danh mục:", err);
            });
    }, []);

    const handleCategoryClick = (categoryId) => {
        navigate(`/category/${categoryId}`);
        setShowMobileMenu(false);
    };

    const goToHome = () => {
        navigate(`/home`)
        window.scroll(0, 0)
    }

    const handleSearch = (e) => {
        e.preventDefault();
        if (searchQuery.trim()) {
            navigate(`/search?q=${searchQuery}`);
            setSearchQuery("");
        }
    };

    return (
        <div className={`${style.allHeader} ${isScrolled ? style.scrolled : ""}`}>
            {/* Top Header */}
            <div className={style.header1}>
                <Link to="/" className={style.logoLink}>
                    <div className={style.logo}>
                        <span className={style.logoIcon}>💎</span>
                        <h1 className={style.logoText} onClick={goToHome}>Jewelry Store</h1>
                    </div>
                </Link>

                {/* Search Bar */}
                <form onSubmit={handleSearch} className={style.searchContainer}>
                    <FaSearch className={style.searchIcon} />
                    <input
                        type="text"
                        placeholder="Tìm kiếm sản phẩm, danh mục..."
                        className={style.searchBar}
                        value={searchQuery}
                        onChange={(e) => setSearchQuery(e.target.value)}
                    />
                    <button type="submit" className={style.searchButton}>
                        Tìm kiếm
                    </button>
                </form>

                {/* Desktop Icons */}
                <div className={style.iconGroup}>
                    <Link to="/cart" className={style.iconButton}>
                        <div className={style.iconWrapper}>
                            <FaShoppingCart />
                            {cartCount > 0 && <span className={style.badge}>{cartCount}</span>}
                        </div>
                        <span className={style.iconLabel}>Giỏ hàng</span>
                    </Link>

                    <Link to="/order" className={style.iconButton}>
                        <div className={style.iconWrapper}>
                            <FaHistory />
                        </div>
                        <span className={style.iconLabel}>Đơn hàng</span>
                    </Link>

                    <div className={style.userMenuWrapper} onClick={() => setShowMenu(!showMenu)}>
                        <div className={style.iconButton}>
                            <div className={style.iconWrapper}>
                                <FaUserCircle className={style.userIcon} />
                            </div>
                            <span className={style.iconLabel}>
                                {username ? username : "Tài khoản"}
                            </span>
                        </div>
                        {showMenu && (
                            <div className={style.dropdownMenu}>
                                {username ? (
                                    <>
                                        <div className={style.menuHeader}>
                                            <FaUserCircle className={style.menuHeaderIcon} />
                                            <div>
                                                <p className={style.menuHeaderName}>Xin chào!</p>
                                                <p className={style.menuHeaderUsername}>{username}</p>
                                            </div>
                                        </div>
                                        <div className={style.menuDivider}></div>
                                        <Link to="/profile" className={style.menuItem}>
                                            <span className={style.menuIcon}>👤</span>
                                            Thông tin cá nhân
                                        </Link>
                                        <Link to="/addressManager" className={style.menuItem}>
                                            <span className={style.menuIcon}>📍</span>
                                            Địa chỉ
                                        </Link>
                                        <Link to="/change-password" className={style.menuItem}>
                                            <span className={style.menuIcon}>🔒</span>
                                            Đổi mật khẩu
                                        </Link>
                                        <div className={style.menuDivider}></div>
                                        <button onClick={handleLogout} className={`${style.menuItem} ${style.logoutBtn}`}>
                                            <span className={style.menuIcon}>🚪</span>
                                            Đăng xuất
                                        </button>
                                    </>
                                ) : (
                                    <Link to="/login" className={style.menuItem}>
                                        <span className={style.menuIcon}>🔑</span>
                                        Đăng nhập / Đăng ký
                                    </Link>
                                )}
                            </div>
                        )}
                    </div>
                </div>

                {/* Mobile Menu Toggle */}
                <button
                    className={style.mobileMenuToggle}
                    onClick={() => setShowMobileMenu(!showMobileMenu)}
                >
                    {showMobileMenu ? <FaTimes /> : <FaBars />}
                </button>
            </div>

            {/* Categories Navigation */}
            <div className={`${style.header2} ${showMobileMenu ? style.mobileMenuOpen : ""}`}>
                <div className={style.categoriesWrapper}>
                    {categories.map((item) => (
                        <Link
                            key={item.id}
                            to={`/category/${item.id}`}
                            className={style.navLink}
                            onClick={() => setShowMobileMenu(false)}
                        >
                            {item.name}
                        </Link>
                    ))}
                </div>
            </div>

            {/* Mobile Menu Overlay */}
            {showMobileMenu && (
                <div className={style.mobileOverlay} onClick={() => setShowMobileMenu(false)} />
            )}
        </div>
    );
}

export default Header;
