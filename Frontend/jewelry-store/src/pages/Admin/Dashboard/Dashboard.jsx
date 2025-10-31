import { useState } from "react";
import { Bar } from "react-chartjs-2";
import {
    Chart as ChartJS,
    CategoryScale,
    LinearScale,
    BarElement,
    Title,
    Tooltip,
    Legend,
} from "chart.js";
import styles from "./Dashboard.module.css";

ChartJS.register(CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend);

function Dashboard() {
    const [stats] = useState({
        orders: 128,
        users: 456,
        revenue: 92400000,
        products: 58,
    });

    const revenueData = {
        labels: ["Tháng 1", "Tháng 2", "Tháng 3", "Tháng 4", "Tháng 5", "Tháng 6"],
        datasets: [
            {
                label: "Doanh thu (VNĐ)",
                data: [15000000, 22000000, 18000000, 27000000, 32000000, 40000000],
                backgroundColor: "rgba(75, 192, 192, 0.6)",
                borderRadius: 6,
            },
        ],
    };

    const chartOptions = {
        responsive: true,
        plugins: {
            legend: { display: true, position: "top" },
            title: { display: true, text: "Thống kê doanh thu 6 tháng gần nhất" },
        },
    };

    return (
        <div className={styles.dashboardContainer}>
            <h1 className={styles.title}>📊 Dashboard</h1>

            {/* Thẻ thống kê */}
            <div className={styles.statsGrid}>
                <div className={styles.statCard}>
                    <h3>Đơn hàng</h3>
                    <p>{stats.orders}</p>
                </div>
                <div className={styles.statCard}>
                    <h3>Người dùng</h3>
                    <p>{stats.users}</p>
                </div>
                <div className={styles.statCard}>
                    <h3>Doanh thu</h3>
                    <p>{stats.revenue.toLocaleString()}₫</p>
                </div>
                <div className={styles.statCard}>
                    <h3>Sản phẩm</h3>
                    <p>{stats.products}</p>
                </div>
            </div>

            {/* Biểu đồ doanh thu */}
            <div className={styles.chartBox}>
                <Bar data={revenueData} options={chartOptions} />
            </div>
        </div>
    );
}

export default Dashboard;
