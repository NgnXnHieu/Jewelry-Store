// src/layouts/AuthLayout.jsx
import { Outlet } from "react-router-dom";

function AuthLayout() {
    return (
        <div>
            <Outlet /> {/* Hiển thị form đăng nhập hoặc đăng ký */}
        </div>
    );
}

export default AuthLayout;
