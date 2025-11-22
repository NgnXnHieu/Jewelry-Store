import axios from "axios";

const axiosInstance = axios.create({
    baseURL: "http://localhost:8080/api",
    withCredentials: true
});

axiosInstance.interceptors.response.use(
    (response) => response,
    async (error) => {
        const originalRequest = error.config;
        if (error.response?.status === 401 && !originalRequest._retry) {
            console.log("🔥 Phát hiện lỗi 401, đang thử Refresh...", originalRequest.url);
            originalRequest._retry = true;


            // Gọi refresh token
            try {
                await axios.post(
                    "http://localhost:8080/api/refreshToken",
                    {},
                    { withCredentials: true }
                );
                console.log("Refresh token successful");

                // retry lại request cũ
                return axiosInstance(originalRequest);
            } catch (err) {
                console.log("Refresh token failed, redirect login");
                window.location.href = "/login";
                return Promise.reject(err);
            }
        }
        return Promise.reject(error);
    }
);

export default axiosInstance;
