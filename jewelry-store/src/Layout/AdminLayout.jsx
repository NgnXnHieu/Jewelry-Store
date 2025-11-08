import { Outlet, Link, useLocation } from "react-router-dom";
import { FiLogOut, FiMenu, FiX, FiArrowLeft } from "react-icons/fi"; // thêm icon menu & đóng
import { useState } from "react";
import styles from "./AdminLayout.module.css";

function AdminLayout() {
    const location = useLocation();
    const [isSidebarOpen, setIsSidebarOpen] = useState(true); // trạng thái sidebar

    const toggleSidebar = () => {
        setIsSidebarOpen(!isSidebarOpen);
    };

    return (
        <div className={styles.adminLayout}>
            {/* Sidebar */}
            <aside className={`${styles.sidebar} ${!isSidebarOpen ? styles.sidebarHidden : ""}`}>
                <div className={styles.adminLogo}>Admin Panel</div>

                <nav className={styles.adminNav}>
                    <ul>
                        <li className={location.pathname === "/admin" ? styles.active : ""}>
                            <Link to="/admin">Dashboard</Link>
                        </li>
                        <li className={location.pathname === "/admin/categoryManagement" ? styles.active : ""}>
                            <Link to="/admin/categoryManagement">Quản lý Danh mục</Link>
                        </li>
                        <li className={location.pathname === "/admin/productManagement" ? styles.active : ""}>
                            <Link to="/admin/productManagement">Quản lý sản phẩm</Link>
                        </li>
                        <li className={location.pathname === "/admin/users" ? styles.active : ""}>
                            <Link to="/admin/users">Quản lý người dùng</Link>
                        </li>
                        <li className={location.pathname === "/admin/orders" ? styles.active : ""}>
                            <Link to="/admin/orders">Quản lý đơn hàng</Link>
                        </li>
                        <li className={location.pathname === "/admin/humanResourcesManagement" ? styles.active : ""}>
                            <Link to="/admin/humanResourcesManagement">Quản lý nhân sự</Link>
                        </li>
                        <li className={location.pathname === "/admin/inventory" ? styles.active : ""}>
                            <Link to="/admin/inventory">Quản lý Kho</Link>
                        </li>
                    </ul>
                </nav>
            </aside>

            {/* Main Content */}
            <div className={styles.main}>
                <header className={styles.header}>
                    <button className={styles.menuBtn} onClick={toggleSidebar}>
                        {isSidebarOpen ? <FiArrowLeft size={22} /> : <FiMenu size={22} />}
                    </button>

                    <h1>Trang quản trị</h1>

                    <div className={styles.user}>
                        <span>Xin chào, Admin</span>
                        <button className={styles.logoutBtn}>
                            <FiLogOut size={22} />
                        </button>
                    </div>
                </header>

                <main className={styles.content}>
                    <Outlet />
                </main>
            </div>
        </div>
    );
}

export default AdminLayout;
