import { Outlet, Link, useLocation } from "react-router-dom";
import { FiLogOut } from "react-icons/fi"; // ✅ icon logout
import styles from "./AdminLayout.module.css";

function AdminLayout() {
    const location = useLocation();

    return (
        <div className={styles.adminLayout}>
            {/* Sidebar */}
            <aside className={styles.sidebar}>
                <div className={styles.adminLogo}>Admin Panel</div>

                <nav className={styles.adminNav}>
                    <ul>
                        <li className={location.pathname === "/admin" ? styles.active : ""}>
                            <Link to="/admin">Dashboard</Link>
                        </li>
                        <li className={location.pathname === "/admin/productManagement" ? styles.active : ""}>
                            <Link to="/admin/productManagement  ">Quản lý sản phẩm</Link>
                        </li>
                        <li className={location.pathname === "/admin/users" ? styles.active : ""}>
                            <Link to="/admin/users">Quản lý người dùng</Link>
                        </li>
                        <li className={location.pathname === "/admin/orders" ? styles.active : ""}>
                            <Link to="/admin/orders">Quản lý đơn hàng</Link>
                        </li>
                    </ul>
                </nav>
            </aside>

            {/* Main Content */}
            <div className={styles.main}>
                <header className={styles.header}>
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
