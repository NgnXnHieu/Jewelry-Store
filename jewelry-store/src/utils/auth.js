import { jwtDecode } from "jwt-decode";

export const getAccessToken = () => {
    const token = localStorage.getItem("token");
    console.log("🔑 [auth] Lấy access token:", token ? "✅ Có token" : "❌ Không có token");
    return token;
};

export const getRefreshToken = () => {
    const token = localStorage.getItem("refreshToken");
    console.log("🔁 [auth] Lấy refresh token:", token ? "✅ Có token" : "❌ Không có token");
    return token;
};

export const isTokenExpired = (token) => {
    try {
        const decoded = jwtDecode(token);
        const expired = decoded.exp * 1000 < Date.now();
        console.log(
            expired
                ? "⚠️ [auth] Token đã hết hạn!"
                : "✅ [auth] Token còn hạn sử dụng.",
            decoded
        );
        return expired;
    } catch (err) {
        console.error("❌ [auth] Lỗi giải mã token:", err);
        return true;
    }
};

export const refreshAccessToken = async () => {
    const refreshToken = getRefreshToken();
    if (!refreshToken) {
        console.warn("⚠️ [auth] Không có refresh token để làm mới.");
        return null;
    }

    console.log("🔄 [auth] Gửi yêu cầu refresh token...");

    try {
        const response = await fetch("http://localhost:8080/api/refresh", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ refreshToken }),
        });

        console.log("📡 [auth] Kết quả response refresh:", response.status);

        if (!response.ok) throw new Error("Refresh token failed");
        const data = await response.json();
        console.log("✅ [auth] Refresh token thành công:", data);

        localStorage.setItem("token", data.accessToken);
        return data.accessToken;
    } catch (err) {
        console.error("❌ [auth] Không thể refresh token:", err);
        localStorage.removeItem("token");
        localStorage.removeItem("refreshToken");
        return null;
    }
};
export function getUsernameFromToken() {
    const token = getAccessToken();
    if (!token) return null;

    try {
        const decoded = jwtDecode(token);
        console.log("🔍 [auth] Token payload:", decoded);
        return decoded.sub || decoded.username || null;
    } catch (err) {
        console.error("❌ [auth] Không decode được token:", err);
        return null;
    }
}