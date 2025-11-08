import { useState } from "react";
import { Bar, Line, Doughnut } from "react-chartjs-2";
import {
    Chart as ChartJS,
    CategoryScale,
    LinearScale,
    BarElement,
    LineElement,
    PointElement,
    ArcElement,
    Title,
    Tooltip,
    Legend,
} from "chart.js";
import styles from "./Dashboard.module.css";

ChartJS.register(
    CategoryScale,
    LinearScale,
    BarElement,
    LineElement,
    PointElement,
    ArcElement,
    Title,
    Tooltip,
    Legend
);

function Dashboard() {
    const [activeCategory, setActiveCategory] = useState("revenue");
    const [filterType, setFilterType] = useState("month");

    // Mock data for statistics
    const stats = [
        { id: "revenue", title: "Doanh thu", value: "92,400,000₫", icon: "💰", color: "#4CAF50" },
        { id: "orders", title: "Đơn hàng", value: 1284, icon: "📦", color: "#2196F3" },
        { id: "users", title: "Người dùng", value: 4567, icon: "👥", color: "#9C27B0" },
        { id: "products", title: "Sản phẩm", value: 589, icon: "🛍️", color: "#FF9800" },
        { id: "categories", title: "Danh mục", value: 24, icon: "📂", color: "#00BCD4" },
        { id: "employees", title: "Nhân viên", value: 45, icon: "👨‍💼", color: "#F44336" },
    ];

    // Mock data based on filter type
    const getLabels = () => {
        if (filterType === "year") {
            return ["2019", "2020", "2021", "2022", "2023", "2024"];
        } else if (filterType === "month") {
            return ["Tháng 1", "Tháng 2", "Tháng 3", "Tháng 4", "Tháng 5", "Tháng 6", "Tháng 7", "Tháng 8", "Tháng 9", "Tháng 10", "Tháng 11", "Tháng 12"];
        } else {
            return ["CN", "T2", "T3", "T4", "T5", "T6", "T7"];
        }
    };

    const getData = () => {
        switch (activeCategory) {
            case "revenue":
                if (filterType === "year") {
                    return [450000000, 520000000, 680000000, 750000000, 820000000, 924000000];
                } else if (filterType === "month") {
                    return [65000000, 72000000, 68000000, 85000000, 92000000, 78000000, 88000000, 95000000, 82000000, 90000000, 98000000, 105000000];
                } else {
                    return [2800000, 3200000, 2900000, 3500000, 3800000, 4200000, 3600000];
                }
            case "orders":
                if (filterType === "year") {
                    return [5200, 6800, 8400, 9600, 11200, 12840];
                } else if (filterType === "month") {
                    return [850, 920, 880, 1050, 1120, 980, 1080, 1150, 1020, 1100, 1180, 1250];
                } else {
                    return [35, 42, 38, 45, 48, 52, 44];
                }
            case "users":
                if (filterType === "year") {
                    return [1200, 1850, 2400, 3100, 3800, 4567];
                } else if (filterType === "month") {
                    return [320, 350, 340, 380, 420, 390, 430, 460, 410, 440, 480, 520];
                } else {
                    return [15, 18, 16, 22, 25, 28, 24];
                }
            case "products":
                if (filterType === "year") {
                    return [120, 185, 265, 350, 450, 589];
                } else if (filterType === "month") {
                    return [45, 48, 46, 52, 56, 51, 58, 62, 54, 59, 64, 68];
                } else {
                    return [2, 3, 1, 4, 3, 5, 2];
                }
            case "categories":
                if (filterType === "year") {
                    return [8, 12, 15, 18, 21, 24];
                } else if (filterType === "month") {
                    return [20, 20, 21, 21, 22, 22, 22, 23, 23, 23, 24, 24];
                } else {
                    return [0, 0, 1, 0, 0, 0, 0];
                }
            case "employees":
                if (filterType === "year") {
                    return [15, 22, 28, 33, 39, 45];
                } else if (filterType === "month") {
                    return [40, 41, 41, 42, 42, 43, 43, 43, 44, 44, 45, 45];
                } else {
                    return [0, 0, 0, 1, 0, 0, 0];
                }
            default:
                return [];
        }
    };

    const getChartData = () => {
        const activeColor = stats.find(s => s.id === activeCategory)?.color || "#4CAF50";

        return {
            labels: getLabels(),
            datasets: [
                {
                    label: getCategoryLabel(),
                    data: getData(),
                    backgroundColor: activeColor + "99",
                    borderColor: activeColor,
                    borderWidth: 2,
                    borderRadius: 6,
                    tension: 0.4,
                },
            ],
        };
    };

    const getCategoryLabel = () => {
        const labels = {
            revenue: "Doanh thu (VNĐ)",
            orders: "Số đơn hàng",
            users: "Số người dùng",
            products: "Số sản phẩm",
            categories: "Số danh mục",
            employees: "Số nhân viên",
        };
        return labels[activeCategory];
    };

    const chartOptions = {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
            legend: { display: true, position: "top" },
            title: {
                display: true,
                text: `Thống kê ${getCategoryLabel().toLowerCase()} - ${filterType === "year" ? "Theo năm" : filterType === "month" ? "Theo tháng" : "Theo ngày trong tuần"
                    }`,
                font: { size: 16 }
            },
        },
        scales: {
            y: {
                beginAtZero: true,
            },
        },
    };

    // Additional detail stats based on active category
    const getDetailStats = () => {
        switch (activeCategory) {
            case "revenue":
                return [
                    { label: "Tổng doanh thu", value: "924,000,000₫" },
                    { label: "Doanh thu trung bình/ngày", value: "3,068,000₫" },
                    { label: "Tăng trưởng", value: "+12.5%" },
                    { label: "Đơn hàng cao nhất", value: "5,800,000₫" },
                ];
            case "orders":
                return [
                    { label: "Tổng đơn hàng", value: "1,284" },
                    { label: "Đơn hàng/ngày", value: "42" },
                    { label: "Đang xử lý", value: "156" },
                    { label: "Đã hoàn thành", value: "1,128" },
                ];
            case "users":
                return [
                    { label: "Tổng người dùng", value: "4,567" },
                    { label: "Người dùng mới/tháng", value: "325" },
                    { label: "Đang hoạt động", value: "2,834" },
                    { label: "Tỷ lệ giữ chân", value: "68.5%" },
                ];
            case "products":
                return [
                    { label: "Tổng sản phẩm", value: "589" },
                    { label: "Còn hàng", value: "542" },
                    { label: "Hết hàng", value: "47" },
                    { label: "Sản phẩm mới/tháng", value: "12" },
                ];
            case "categories":
                return [
                    { label: "Tổng danh mục", value: "24" },
                    { label: "Đang hoạt động", value: "22" },
                    { label: "Danh mục phổ biến nhất", value: "Điện tử" },
                    { label: "Sản phẩm/danh mục", value: "~24.5" },
                ];
            case "employees":
                return [
                    { label: "Tổng nhân viên", value: "45" },
                    { label: "Toàn thời gian", value: "38" },
                    { label: "Bán thời gian", value: "7" },
                    { label: "Nhân viên mới/tháng", value: "2" },
                ];
            default:
                return [];
        }
    };

    // Category distribution chart (for some categories)
    const getCategoryDistribution = () => {
        if (activeCategory === "categories") {
            return {
                labels: ["Điện tử", "Thời trang", "Thực phẩm", "Gia dụng", "Sách", "Khác"],
                datasets: [
                    {
                        data: [158, 142, 98, 85, 67, 39],
                        backgroundColor: [
                            "#FF6384",
                            "#36A2EB",
                            "#FFCE56",
                            "#4BC0C0",
                            "#9966FF",
                            "#FF9F40",
                        ],
                    },
                ],
            };
        } else if (activeCategory === "orders") {
            return {
                labels: ["Đã giao", "Đang giao", "Đang xử lý", "Đã hủy"],
                datasets: [
                    {
                        data: [1128, 98, 58, 42],
                        backgroundColor: ["#4CAF50", "#2196F3", "#FF9800", "#F44336"],
                    },
                ],
            };
        } else if (activeCategory === "employees") {
            return {
                labels: ["Nhân viên", "Quản lý", "Giao hàng"],
                datasets: [
                    {
                        data: [28, 9, 8],
                        backgroundColor: ["#F44336", "#FF9800", "#4CAF50"],
                    },
                ],
            };
        }
        return null;
    };

    const doughnutOptions = {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
            legend: { display: true, position: "right" },
            title: {
                display: true,
                text: activeCategory === "categories"
                    ? "Phân bố theo danh mục"
                    : activeCategory === "orders"
                        ? "Trạng thái đơn hàng"
                        : "Phân loại nhân viên",
                font: { size: 14 }
            },
        },
    };

    // Check if we should show two charts
    const hasDistributionChart = () => {
        return activeCategory === "categories" || activeCategory === "orders" || activeCategory === "employees";
    };

    return (
        <div className={styles.dashboardContainer}>
            <div className={styles.header}>
                <h1 className={styles.title}>📊 Dashboard Quản Lý</h1>

                {/* Filter Bar */}
                <div className={styles.filterBar}>
                    <button
                        className={`${styles.filterBtn} ${filterType === "year" ? styles.active : ""}`}
                        onClick={() => setFilterType("year")}
                    >
                        Năm
                    </button>
                    <button
                        className={`${styles.filterBtn} ${filterType === "month" ? styles.active : ""}`}
                        onClick={() => setFilterType("month")}
                    >
                        Tháng
                    </button>
                    <button
                        className={`${styles.filterBtn} ${filterType === "day" ? styles.active : ""}`}
                        onClick={() => setFilterType("day")}
                    >
                        Ngày
                    </button>
                </div>
            </div>

            {/* Stats Grid - Clickable Cards */}
            <div className={styles.statsGrid}>
                {stats.map((stat) => (
                    <div
                        key={stat.id}
                        className={`${styles.statCard} ${activeCategory === stat.id ? styles.activeCard : ""}`}
                        onClick={() => setActiveCategory(stat.id)}
                        style={{ borderColor: activeCategory === stat.id ? stat.color : "transparent" }}
                    >
                        <div className={styles.statIcon} style={{ backgroundColor: stat.color + "20" }}>
                            {stat.icon}
                        </div>
                        <div className={styles.statContent}>
                            <h3>{stat.title}</h3>
                            <p style={{ color: stat.color }}>{stat.value}</p>
                        </div>
                    </div>
                ))}
            </div>

            {/* Detail Statistics */}
            <div className={styles.detailStatsGrid}>
                {getDetailStats().map((detail, index) => (
                    <div key={index} className={styles.detailCard}>
                        <span className={styles.detailLabel}>{detail.label}</span>
                        <span className={styles.detailValue}>{detail.value}</span>
                    </div>
                ))}
            </div>

            {/* Charts Section */}
            <div className={hasDistributionChart() ? styles.chartsContainer : styles.chartsContainerFull}>
                {/* Main Chart */}
                <div className={styles.chartBox}>
                    {activeCategory === "revenue" || activeCategory === "users" ? (
                        <Line data={getChartData()} options={chartOptions} />
                    ) : (
                        <Bar data={getChartData()} options={chartOptions} />
                    )}
                </div>

                {/* Distribution Chart (for specific categories) */}
                {hasDistributionChart() && (
                    <div className={styles.chartBoxSmall}>
                        <Doughnut data={getCategoryDistribution()} options={doughnutOptions} />
                    </div>
                )}
            </div>

            {/* Recent Activity Table
            <div className={styles.activitySection}>
                <h2 className={styles.sectionTitle}>Hoạt động gần đây</h2>
                <div className={styles.activityTable}>
                    <table>
                        <thead>
                            <tr>
                                <th>Thời gian</th>
                                <th>Loại</th>
                                <th>Mô tả</th>
                                <th>Trạng thái</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr>
                                <td>10:30 AM</td>
                                <td>Đơn hàng</td>
                                <td>Đơn hàng #DH-2024-1284 đã được tạo</td>
                                <td><span className={styles.statusNew}>Mới</span></td>
                            </tr>
                            <tr>
                                <td>09:45 AM</td>
                                <td>Người dùng</td>
                                <td>Người dùng mới đăng ký: nguyenvana@email.com</td>
                                <td><span className={styles.statusSuccess}>Thành công</span></td>
                            </tr>
                            <tr>
                                <td>09:20 AM</td>
                                <td>Sản phẩm</td>
                                <td>Sản phẩm "iPhone 15 Pro" đã được cập nhật</td>
                                <td><span className={styles.statusSuccess}>Thành công</span></td>
                            </tr>
                            <tr>
                                <td>08:55 AM</td>
                                <td>Đơn hàng</td>
                                <td>Đơn hàng #DH-2024-1283 đã được giao</td>
                                <td><span className={styles.statusDelivered}>Đã giao</span></td>
                            </tr>
                            <tr>
                                <td>08:30 AM</td>
                                <td>Nhân viên</td>
                                <td>Nhân viên Trần Thị B đã check-in</td>
                                <td><span className={styles.statusSuccess}>Thành công</span></td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div> */}
        </div>
    );
}

export default Dashboard;
