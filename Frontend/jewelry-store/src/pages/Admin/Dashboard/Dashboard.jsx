import { useState, useEffect } from "react";
import { Bar, Line, Doughnut } from "react-chartjs-2";
import axiosInstance from "../../../api/axiosInstance";

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
    const [selectedYear, setSelectedYear] = useState("2024");
    const [selectedMonth, setSelectedMonth] = useState("1");
    const [stats, setStats] = useState([]);
    const [detailStats, setDetailStats] = useState([]);
    useEffect(() => {
        fetchStats();
    }, [filterType, activeCategory]);
    const fetchStats = async () => {
        try {
            let responses;
            responses = await Promise.all([
                axiosInstance.get(`orders/sumByUnitTime?time=${filterType}`),
                axiosInstance.get(`orders/quantity/count/unitTime?time=${filterType}`),
                axiosInstance.get(`users/customers/count`),
                axiosInstance.get(`users/humanResources/count`),
                axiosInstance.get("products/count"),
                axiosInstance.get("categories/count")
            ]);

            // Gán từng biến từ mảng responses
            const [revenueRes, ordersRes, customersRes, staffRes, productsRes, categoriesRes] = responses;

            // Tạo formattedStats
            const formattedStats = [
                { id: "revenue", title: "Doanh thu", value: revenueRes.data.toLocaleString("vi-VN") + "₫", icon: "💰", color: "#4CAF50" },
                { id: "orders", title: "Đơn hàng", value: ordersRes.data, icon: "📦", color: "#2196F3" },
                { id: "customers", title: "Khách hàng", value: customersRes.data, icon: "👥", color: "#9C27B0" },
                { id: "staffs", title: "Nhân viên", value: staffRes.data, icon: "👨‍💼", color: "#F44336" },
                { id: "products", title: "Sản phẩm", value: productsRes.data, icon: "🛍️", color: "#FF9800" },
                { id: "categories", title: "Danh mục", value: categoriesRes.data, icon: "📂", color: "#00BCD4" },
            ];
            //Tại đây setStats chỉ mới được đánh dấu chứ chưa được set vì thread chưa rảnh
            setStats(formattedStats);

        } catch (error) {
            console.error("Lỗi khi lấy dữ liệu:", error);
        }
    };

    const fetchDetails = async () => {
        let statsArray = []
        switch (activeCategory) {
            case "revenue":
                let [revenuePerDay, maxPriceOfOdersByTimeUnit] = await Promise.all([
                    axiosInstance.get(`/orders/revenuePerDay?time=${filterType}`),
                    axiosInstance.get(`/orders/maxPriceOfOdersByTimeUnit?time=${filterType}`)
                ]);
                statsArray = [
                    { label: "Tổng doanh thu", value: stats[0]?.value ?? "..." },
                    { label: "Doanh thu trung bình/ngày", value: revenuePerDay?.data.toLocaleString("vi-VN") + "đ" ?? "..." },
                    // { label: "Tăng trưởng", value: "+12.5%" },   
                    { label: "Đơn hàng cao nhất", value: maxPriceOfOdersByTimeUnit?.data.toLocaleString("vi-VN") + "đ" ?? "..." },
                ];
                setDetailStats(statsArray);
                break;
            case "orders":
                let [ordersPerDay, unresolvedOrder, resolvedOrder] = await
                    Promise.all([
                        axiosInstance.get(`orders/perDay/count/unitTime?time=${filterType}`),
                        axiosInstance.get(`orders/resolved/count/unitTime?time=${filterType}`),
                        axiosInstance.get(`orders/unresolved/count/unitTime?time=${filterType}`)
                    ]);

                statsArray = [
                    { label: "Tổng đơn hàng", value: stats[1]?.value ?? "..." },
                    { label: "Đơn hàng/ngày", value: ordersPerDay?.data.toLocaleString("vi-VN") ?? "..." },
                    { label: "Đang xử lý", value: unresolvedOrder?.data ?? "..." },
                    { label: "Giao thành công", value: resolvedOrder?.data ?? "..." },
                ];
                setDetailStats(statsArray);
                break;
            case "customers":
                let newCustomers = await axiosInstance.get(`/users/customers/count/unitTime?time=${filterType}`)
                statsArray = [
                    { label: "Tổng người dùng", value: stats[2]?.value ?? "..." },
                    { label: `Người dùng mới/${filterType === "year" ? "năm" : (filterType === "month" ? "tháng" : "ngày")}`, value: newCustomers?.data ?? "..." },
                    // { label: "Đang hoạt động", value: "2,834" },
                    // { label: "Tỷ lệ giữ chân", value: "68.5%" },
                ];
                setDetailStats(statsArray);
                break;
            case "products":
                let [countInProducts, countOutProducts, bestSeller] = await
                    Promise.all([
                        axiosInstance.get(`products/inProducts/count`),
                        axiosInstance.get(`products/outProducts/count`),
                        axiosInstance.get(`products/oneBestSeller?time=${filterType}`)
                    ]);
                // let formattedBestSeller =bestSeller?.data ?? "..."
                // console.log(bestSeller)
                statsArray = [
                    { label: "Tổng sản phẩm", value: stats[4]?.value ?? "..." },
                    { label: "Sản phẩm còn hàng", value: countInProducts?.data ?? "..." },
                    { label: "Sản phẩm đang hết hàng", value: countOutProducts?.data ?? "..." },
                    {
                        label: `Sản phẩm được mua nhiều nhất ${filterType === "year" ? "năm" : (filterType === "month" ? "tháng" : "ngày")} `, value: (
                            <>
                                #{bestSeller?.data.producId ?? "..."}: {bestSeller?.data.productName ?? "..."}<br />
                                Lượt bán: {bestSeller?.data.sellQuantity ?? "..."}
                            </>
                        )
                    },
                ];
                setDetailStats(statsArray);
                break;

            case "categories":
                const responses = await axiosInstance.get(`products/TopAndBotSellingCategories?time=${filterType} `)
                const { minCategory, maxCategory } = (responses.data)
                // console.log(responses)
                // console.log(minCategory)
                // console.log(maxCategory)
                statsArray = [
                    { label: "Tổng danh mục", value: stats[5]?.value ?? "..." },
                    { label: `Danh mục bán nhiều nhất ${filterType === "year" ? "năm" : (filterType === "month" ? "tháng" : "ngày")} `, value: <>#{maxCategory.categoryId}: {maxCategory.categoryName}<br />Lượt bán: {maxCategory.quantity}</> },
                    { label: `Danh mục bán ít nhất ${filterType === "year" ? "năm" : (filterType === "month" ? "tháng" : "ngày")} `, value: (<>#{minCategory.categoryId}: {minCategory.categoryName}<br />Lượt bán: {minCategory.quantity}</>) },
                ];
                setDetailStats(statsArray);
                break;

            case "staffs":
                // let [countStaffs, newStaffsByUnitTime] = await
                //     Promise.all([
                //         axiosInstance.get(`users / staffs / count`),
                //         axiosInstance.get(`users / staffs / count / unitTime ? time = ${ filterType } `),
                //     ]);
                let newStaffsByUnitTime = await axiosInstance.get(`users/humanResources/count/unitTime?time=${filterType} `);
                statsArray = [
                    { label: "Tổng nhân viên", value: stats[3]?.value ?? "..." },
                    { label: `Nhân viên mới trong ${filterType === "year" ? "năm" : (filterType === "month" ? "tháng" : "ngày")} `, value: newStaffsByUnitTime?.data ?? "..." },
                ];
                setDetailStats(statsArray);
                break;

            default:
                return [];
        }
    }

    useEffect(() => {
        fetchDetails();
    }, [activeCategory, filterType, stats[0]])



    // Tạo các mốc thời gian
    const getLabels = () => {
        if (filterType === "year") {
            let d = new Date().getFullYear()
            let years = []
            for (let i = 0; i < 4; i++) {
                years[i] = d - 3 + i;
            }
            return years;
        } else if (filterType === "month") {
            let month = []
            let today = new Date();
            for (let i = 0; i < 12; i++) {
                let newDay = new Date(today);
                newDay.setMonth(newDay.getMonth() - i);
                month[11 - i] = newDay.getMonth() + 1;
            }
            return month;
        } else {
            let days = [];
            let today = new Date();
            for (let i = 0; i < 7; i++) {
                let newDay = new Date(today)
                newDay.setDate(newDay.getDate() - i);
                days[6 - i] = newDay.getDate();
            }
            return days;
        }
    };

    const [chartData, setChartData] = useState([])
    //Dữ liệu của biểu đồ cột
    useEffect(() => {
        const getData = async () => {
            switch (activeCategory) {
                case "revenue":
                    if (filterType === "year") {
                        let responses = await axiosInstance.get(`orders/sumTotalPricesByYears`)
                        setChartData(responses.data)
                    } else if (filterType === "month") {
                        let responses = await axiosInstance.get(`orders/sumTotalPricesByMonths`)
                        setChartData(responses.data)
                    } else {
                        let responses = await axiosInstance.get(`orders/sumTotalPricesByDays`)
                        setChartData(responses.data)
                    }
                    break;
                case "orders":
                    if (filterType === "year") {
                        let responses = await axiosInstance.get(`orders/countOrdersByYears`)
                        setChartData(responses.data)
                    } else if (filterType === "month") {
                        let responses = await axiosInstance.get(`orders/countOrdersByMonths`)
                        setChartData(responses.data)
                    } else {
                        let responses = await axiosInstance.get(`orders/countOrdersByDays`)
                        setChartData(responses.data)
                    }
                    break;
                case "customers":
                    if (filterType === "year") {
                        let responses = await axiosInstance.get(`users/customers/chart/years`)
                        setChartData(responses.data)
                    } else if (filterType === "month") {
                        let responses = await axiosInstance.get(`users/customers/chart/months`)
                        setChartData(responses.data)
                    } else {
                        let responses = await axiosInstance.get(`users/customers/chart/days`)
                        setChartData(responses.data)
                    }
                    break;
                case "products":

                    break;
                case "categories":
                    if (filterType === "year") {
                        return [8, 12, 15, 18, 21, 24];
                    } else if (filterType === "month") {
                        return [20, 20, 21, 21, 22, 22, 22, 23, 23, 23, 24, 24];
                    } else {
                        return [0, 0, 1, 0, 0, 0, 0];
                    }
                    break;
                case "staffs":
                    if (filterType === "year") {
                        let responses = await axiosInstance.get(`users/humanresources/chart/years`)
                        setChartData(responses.data)
                    } else if (filterType === "month") {
                        let responses = await axiosInstance.get(`users/humanresources/chart/months`)
                        setChartData(responses.data)
                    } else {
                        let responses = await axiosInstance.get(`users/humanresources/chart/days`)
                        setChartData(responses.data)
                    }
                    break;
                default:
                    return [];
            }
        };
        getData()
    }, [filterType, activeCategory])

    //Tạo biểu đồ
    const getChartData = () => {
        //Lấy ra của mục đang được chọn
        const activeColor = stats.find(s => s.id === activeCategory)?.color || "#4CAF50";
        return {
            labels: getLabels(),
            datasets: [
                {
                    label: getCategoryLabel(),
                    data: chartData,
                    backgroundColor: activeColor + "60",//Tăng độ trong suốt của cột
                    borderColor: activeColor,
                    borderWidth: 2,
                    borderRadius: 6,
                    tension: 0.4,
                },
            ],
        };
    };

    //Lấy ra lable theo từng mục
    const getCategoryLabel = () => {
        const labels = {
            revenue: "Doanh thu (VNĐ)",
            orders: "Số đơn hàng",
            customers: "Số người dùng",
            products: "Số sản phẩm",
            categories: "Số danh mục",
            staffs: "Số nhân viên",
        };
        return labels[activeCategory];
    };

    //Xử lý tiêu đề trong biểu đồ
    const chartOptions = {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
            legend: { display: true, position: "top" },
            title: {
                display: true,
                text: `Thống kê ${getCategoryLabel().toLowerCase()} - ${filterType === "year" ? "Theo năm" : filterType === "month" ? "Theo tháng" : "Theo ngày trong tuần"
                    } `,
                font: { size: 16 }
            },
        },
        scales: {
            y: {
                beginAtZero: true,
            },
        },
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
        } else if (activeCategory === "staffs") {
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
    //Xử lý tiêu đề của biểu đồ tròn
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
        return activeCategory === "categories" || activeCategory === "orders" || activeCategory === "staffs";
    };

    return (
        <div className={styles.dashboardContainer}>
            <div className={styles.header}>
                <h1 className={styles.title}>📊 Dashboard Quản Lý</h1>

                {/* Filter Bar */}
                <div className={styles.filterBar}>
                    <button
                        className={`${styles.filterBtn} ${filterType === "year" ? styles.active : ""} `}
                        onClick={() => setFilterType("year")}
                    >
                        Năm
                    </button>
                    <button
                        className={`${styles.filterBtn} ${filterType === "month" ? styles.active : ""} `}
                        onClick={() => setFilterType("month")}
                    >
                        Tháng
                    </button>
                    <button
                        className={`${styles.filterBtn} ${filterType === "day" ? styles.active : ""} `}
                        onClick={() => setFilterType("day")}
                    >
                        Ngày
                    </button>
                </div>
            </div>



            {/* Xử lý hiển thị cho từng blocks tổng quan */}
            {/* Stats Grid - Clickable Cards */}
            <div className={styles.statsGrid}>
                {stats.map((stat) => (
                    <div
                        key={stat.id}
                        className={`${styles.statCard} ${activeCategory === stat.id ? styles.activeCard : ""} `}
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

            {/* Xử lý các blocks con bên trong */}
            {/* Detail Statistics */}
            <div className={styles.detailStatsGrid}>
                {detailStats.map((detail, index) => (
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
                    {activeCategory === "revenue" || activeCategory === "customers" ? (
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


        </div>
    );
}

export default Dashboard;
