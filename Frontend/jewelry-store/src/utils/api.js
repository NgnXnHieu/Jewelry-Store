import { getAccessToken, isTokenExpired, refreshAccessToken } from "./auth";

export const apiRequest = async (url, options = {}) => {
    console.log("🌍 [api] Chuẩn bị gọi API:", url);

    let token = getAccessToken();

    if (!token) {
        console.warn("❌ [api] Không tìm thấy access token trong localStorage!");
        throw new Error("No token found");
    }

    if (isTokenExpired(token)) {
        console.warn("⚠️ [api] Token hết hạn, đang refresh...");
        token = await refreshAccessToken();
        if (!token) {
            console.error("❌ [api] Refresh token thất bại!");
            throw new Error("Token refresh failed");
        }
    }

    console.log("🚀 [api] Gửi request với token:", token.substring(0, 20) + "...");

    const response = await fetch(url, {
        ...options,
        headers: {
            ...options.headers,
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
        },
    });

    console.log("📦 [api] Phản hồi từ server:", response.status);

    if (response.status === 401) {
        console.error("⛔ [api] 401 Unauthorized → logout người dùng.");
        localStorage.removeItem("accessToken");
        localStorage.removeItem("refreshToken");
        window.location.href = "/login";
        throw new Error("Unauthorized");
    }

    const data = await response.json();
    console.log("✅ [api] Dữ liệu trả về:", data);
    return data;
};
