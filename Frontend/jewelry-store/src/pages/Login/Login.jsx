import { useState } from "react";
import styles from "./Login.module.css";
import axiosInstance from "../../api/axiosInstance";
import { useNavigate } from "react-router-dom";

export default function Login() {
    const navigate = useNavigate();
    const [tab, setTab] = useState("signin");
    const [formData, setFormData] = useState({
        email: "",
        password: "",
        role: "USER",
        phone: "",
        fullname: "",
        username: "",
        confirmPassword: ""
    });

    // Khi người dùng nhập dữ liệu
    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    // 🔸 Xử lý đăng nhập
    const handleLogin = async (e) => {
        e.preventDefault();

        try {
            // Gửi login request với axios, cookies sẽ tự set bởi backend
            const res = await axiosInstance.post(
                "/login",
                {
                    username: formData.username,
                    password: formData.password
                },
                {
                    withCredentials: true // 🔑 bắt buộc nếu backend set HttpOnly cookies
                }
            );

            // Không cần lưu token ở frontend nữa
            alert("Login successful!");

            // Nếu backend trả role trong response, dùng để chuyển hướng
            if (res.data.role === "ADMIN") {
                navigate("/productdetail");
            } else {
                navigate("/");
            }

        } catch (err) {
            console.error(err);
            alert("Login failed. Please check your credentials.");
        }
    };

    // 🔸 Xử lý đăng ký
    const handleRegister = async (e) => {
        e.preventDefault();
        if (formData.password !== formData.confirmPassword) {
            alert("Passwords do not match!");
            return;
        }

        try {
            await axiosInstance.post(
                "/register",
                {
                    username: formData.username,
                    fullname: formData.fullname,
                    email: formData.email,
                    password: formData.password,
                    phone: formData.phone,
                },
                { withCredentials: true } // nếu backend trả cookie ngay sau register
            );

            alert("Registration successful! Please sign in.");
            setTab("signin");
        } catch (err) {
            console.error(err);
            alert("Registration failed. Please try again.");
        }
    };

    return (
        <div className={styles.pageWrapper}>
            <div className={styles.loginContainer}>
                <div className={styles.loginRight}>
                    <div className={styles.closeBtn}>×</div>

                    {/* Tabs */}
                    <div className={styles.tab}>
                        <button
                            className={`${styles.tabBtn} ${tab === "signin" ? styles.active : ""}`}
                            onClick={() => setTab("signin")}
                        >
                            Sign in
                        </button>
                        <button
                            className={`${styles.tabBtn} ${tab === "signup" ? styles.active : ""}`}
                            onClick={() => setTab("signup")}
                        >
                            Create an account
                        </button>
                    </div>

                    {/* Form */}
                    {tab === "signin" ? (
                        <form className={styles.form} onSubmit={handleLogin}>
                            <label>Username</label>
                            <input
                                type="text"
                                name="username"
                                placeholder="Enter your username"
                                onChange={handleChange}
                                required
                            />

                            <label>Password</label>
                            <input
                                type="password"
                                name="password"
                                placeholder="Enter your password"
                                onChange={handleChange}
                                required
                            />

                            <button type="submit" className={styles.btnSignIn}>
                                Sign in
                            </button>

                            <p className={styles.forgot}>
                                <a href="#">Forgot password?</a>
                            </p>
                        </form>
                    ) : (
                        <form className={styles.form} onSubmit={handleRegister}>
                            <label>Full Name</label>
                            <input
                                type="text"
                                name="fullname"
                                placeholder="Full Name"
                                onChange={handleChange}
                                required
                            />
                            <label>Phone Number</label>
                            <input
                                type="text"
                                name="phone"
                                placeholder="Phone Number"
                                onChange={handleChange}
                                required
                            />
                            <label>User Name</label>
                            <input
                                type="text"
                                name="username"
                                placeholder="User Name"
                                onChange={handleChange}
                                required
                            />
                            <label>Password</label>
                            <input
                                type="password"
                                name="password"
                                placeholder="Password"
                                onChange={handleChange}
                                required
                            />
                            <label>Confirm Password</label>
                            <input
                                type="password"
                                name="confirmPassword"
                                placeholder="Confirm Password"
                                onChange={handleChange}
                                required
                            />
                            <label>Email</label>
                            <input
                                type="text"
                                name="email"
                                placeholder="Email"
                                onChange={handleChange}
                                required
                            />

                            <button type="submit" className={styles.btnSignIn}>
                                Create
                            </button>
                        </form>
                    )}
                </div>
            </div>
        </div>
    );
}
