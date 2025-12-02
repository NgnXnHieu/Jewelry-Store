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

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleLogin = async (e) => {
        e.preventDefault();
        try {
            const res = await axiosInstance.post("/login", {
                username: formData.username,
                password: formData.password
            }, { withCredentials: true });

            alert("Login successful!");
            if (res.data.role === "ROLE_ADMIN") {
                navigate("/admin");
            } else {
                navigate("/");
            }
        } catch (err) {
            console.error(err);
            alert("Login failed. Please check your credentials.");
        }
    };

    const handleRegister = async (e) => {
        e.preventDefault();
        if (formData.password !== formData.confirmPassword) {
            alert("Passwords do not match!");
            return;
        }
        try {
            await axiosInstance.post("/register", {
                username: formData.username,
                fullname: formData.fullname,
                email: formData.email,
                password: formData.password,
                phone: formData.phone,
            }, { withCredentials: true });

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
                            id="tab-signin" // ✅ ID Tab Đăng nhập
                            className={`${styles.tabBtn} ${tab === "signin" ? styles.active : ""}`}
                            onClick={() => setTab("signin")}
                        >
                            Sign in
                        </button>
                        <button
                            id="tab-signup" // ✅ ID Tab Đăng ký
                            className={`${styles.tabBtn} ${tab === "signup" ? styles.active : ""}`}
                            onClick={() => setTab("signup")}
                        >
                            Create an account
                        </button>
                    </div>

                    {/* Form Login */}
                    {tab === "signin" ? (
                        <form id="form-login" className={styles.form} onSubmit={handleLogin}>
                            <label>Username</label>
                            <input
                                id="login-username" // ✅ ID Input User
                                type="text"
                                name="username"
                                placeholder="Enter your username"
                                onChange={handleChange}
                                required
                            />

                            <label>Password</label>
                            <input
                                id="login-password" // ✅ ID Input Pass
                                type="password"
                                name="password"
                                placeholder="Enter your password"
                                onChange={handleChange}
                                required
                            />

                            <button id="btn-login" type="submit" className={styles.btnSignIn}>
                                Sign in
                            </button>

                            <p className={styles.forgot}>
                                <a href="#">Forgot password?</a>
                            </p>
                        </form>
                    ) : (
                        // Form Register
                        <form id="form-register" className={styles.form} onSubmit={handleRegister}>
                            <label>Full Name</label>
                            <input
                                id="reg-fullname"
                                type="text"
                                name="fullname"
                                placeholder="Full Name"
                                onChange={handleChange}
                                required
                            />
                            <label>Phone Number</label>
                            <input
                                id="reg-phone"
                                type="text"
                                name="phone"
                                placeholder="Phone Number"
                                onChange={handleChange}
                                required
                            />
                            <label>User Name</label>
                            <input
                                id="reg-username"
                                type="text"
                                name="username"
                                placeholder="User Name"
                                onChange={handleChange}
                                required
                            />
                            <label>Password</label>
                            <input
                                id="reg-password"
                                type="password"
                                name="password"
                                placeholder="Password"
                                onChange={handleChange}
                                required
                            />
                            <label>Confirm Password</label>
                            <input
                                id="reg-confirm-password"
                                type="password"
                                name="confirmPassword"
                                placeholder="Confirm Password"
                                onChange={handleChange}
                                required
                            />
                            <label>Email</label>
                            <input
                                id="reg-email"
                                type="text"
                                name="email"
                                placeholder="Email"
                                onChange={handleChange}
                                required
                            />

                            <button id="btn-register" type="submit" className={styles.btnSignIn}>
                                Create
                            </button>
                        </form>
                    )}
                </div>
            </div>
        </div>
    );
}