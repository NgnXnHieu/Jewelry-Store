import { useState, useEffect } from 'react';
import styles from './Inventory.module.css';
import axiosInstance from '../../../api/axiosInstance';
import { Users, Plus, Search, Edit2, Trash2, X, Upload, ChevronLeft, ChevronRight } from 'lucide-react';
import Swal from "sweetalert2";
import defaultUrl from '../../../api/defaultUrl';

export default function Inventory() {
    const [products, setProducts] = useState([]);
    const [stockHistory, setStockHistory] = useState([]);
    const [productFilter, setProductFilter] = useState('all');
    const [historyProductFilter, setHistoryProductFilter] = useState('all');
    const [showAddStockModal, setShowAddStockModal] = useState(false);
    const [selectedProduct, setSelectedProduct] = useState(null);
    const [stockToAdd, setStockToAdd] = useState('');
    const [activeTab, setActiveTab] = useState('products');
    const [currentPage, setCurrentPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0)
    const [fullSizeImage, setFullSizeImage] = useState(null); // For image zoom

    // Generate page numbers for pagination
    const getPageNumbers = () => {
        const pages = [];
        const maxVisiblePages = 5;

        if (totalPages <= maxVisiblePages) {
            for (let i = 1; i <= totalPages; i++) {
                pages.push(i);
            }
        } else {
            if (currentPage <= 3) {
                for (let i = 1; i <= 4; i++) {
                    pages.push(i);
                }
                pages.push('...');
                pages.push(totalPages);
            } else if (currentPage >= totalPages - 2) {
                pages.push(1);
                pages.push('...');
                for (let i = totalPages - 3; i <= totalPages; i++) {
                    pages.push(i);
                }
            } else {
                pages.push(1);
                pages.push('...');
                pages.push(currentPage - 1);
                pages.push(currentPage);
                pages.push(currentPage + 1);
                pages.push('...');
                pages.push(totalPages);
            }
        }

        return pages;
    };

    const handlePageChange = (pageNumber) => {
        setCurrentPage(pageNumber - 1);
    };

    //Chỉ chạy 1 lần
    //Lấy ra danh sách sản phẩm
    const fetchProducts = (page) => {
        if (productFilter === "all") {
            axiosInstance.get(`/products?page=${page}&size=10`)
                .then(res => {
                    setProducts(res.data.content);
                    setTotalPages(res.data.totalPages);
                    console.log(res.data);
                    console.log("API response data:", res.data.content);
                    // console.log("Fetched users:", users);
                })
                .catch(err => console.error(err));
        } else {
            axiosInstance.get(`/products/productsByStatus?page=${page}&size=10&`, {
                params: {
                    page: page,
                    size: 10,
                    status: productFilter
                }
            })
                .then(res => {
                    setProducts(res.data.content);
                    setTotalPages(res.data.totalPages);
                    console.log(res.data);
                    console.log("API response data:", res.data.content);
                    // console.log("Fetched users:", users);
                })
                .catch(err => console.error(err));
        }
    };

    const fetchHistoryStock = (page) => {
        axiosInstance.get(`/inventory_histories?page=${page}&size=10`)
            .then(res => {
                setStockHistory(res.data.content);
                setTotalPages(res.data.totalPages);
                console.log(res.data);
                console.log("API response data:", res.data.content);
            })
            .catch(err => console.error(err));
    }

    useEffect(() => {
        setCurrentPage(0)
    }, [activeTab])

    //Chạy lại hàm code bên trong useEffect khi trạng thái thay đổi
    useEffect(() => {
        if (activeTab === "products") {
            fetchProducts(currentPage);
        } else if (activeTab === "history") {
            fetchHistoryStock(currentPage)
        }
    }, [currentPage, productFilter, activeTab]);

    // Get stock status | Trả về trạng thái số lượng sản phẩm hiển thị trong bảng
    const getStockStatus = (quantity) => {
        if (quantity === 0) return 'out';
        if (quantity <= 20) return 'low';
        return 'in';
    };

    // Filter products | Lọc sản phẩm
    const filteredProducts = products.filter((product) => {
        if (productFilter === 'all') return true;
        return getStockStatus(product.quantity) === productFilter;
    });

    // Filter stock history | lọc lịch sử nhập kho
    const filteredStockHistory = stockHistory.filter((entry) => {
        if (historyProductFilter === 'all') return true;
        return entry.productId === historyProductFilter;
    });

    // Open add stock modal | Xử lý nút mở form nhập kho
    const handleAddStock = (product) => {
        setSelectedProduct(product);
        setStockToAdd('');
        setShowAddStockModal(true);
    };
    // useEffect(() => {
    //     if (selectedProduct) {
    //         setStockToAdd('');
    //         setShowAddStockModal(true);
    //     }
    // }, [selectedProduct]); // Chạy khi selectedProduct thay đổi

    // const handleAddStock = (product) => {
    //     setSelectedProduct(product); // Khi set xong, useEffect sẽ tự kích hoạt
    // };
    // Submit add stock | Xử lý nút Nhập kho
    const handleSubmitAddStock = async () => {
        if (!stockToAdd || parseInt(stockToAdd) <= 0) {
            alert('Please enter a valid quantity');
            return;
        }
        const result = await Swal.fire({
            title: `Bạn có chắc muốn nhập thêm ${stockToAdd} món hàng cho "${selectedProduct.name}" ?`,
            icon: 'question',
            timer: 5000,
            timerProgressBar: true,
            showCancelButton: true,
            confirmButtonText: 'Chắc chắn',
            cancelButtonText: 'Hủy'
        });
        if (!result.isConfirmed) {
            return;
        }

        const quantity = parseInt(stockToAdd);
        console.log(selectedProduct.id)

        axiosInstance.post(`/inventory_histories`, {
            "productId": selectedProduct.id,
            "importQuantity": quantity
        })
            .then((res) => {
                // ✅ Sau khi nhập kho thành công, load lại danh sách tại trang hiện tại
                fetchProducts(currentPage);

                // ✅ Thông báo cho người dùng (tùy chọn)
                alert('Nhập kho thành công!');
            })
            .catch((err) => {
                console.error(err);
                alert('Đã xảy ra lỗi khi nhập kho');
            })
            .finally(() => {
                // ✅ Đóng modal và reset các trường
                setShowAddStockModal(false);
                setSelectedProduct(null);
                setStockToAdd('');
            });
    };

    // Format date | Formmat lại date
    const formatDate = (dateString) => {
        const date = new Date(dateString);
        return date.toLocaleDateString('en-US', {
            year: 'numeric',
            month: 'short',
            day: 'numeric',
            hour: '2-digit',
            minute: '2-digit',
        });
    };

    // Calculate statistics |  thống kê
    // const totalProducts = products.length;
    // const inStockCount = products.filter(p => getStockStatus(p.quantity) === 'in').length;
    // const lowStockCount = products.filter(p => getStockStatus(p.quantity) === 'low').length;
    // const outOfStockCount = products.filter(p => getStockStatus(p.quantity) === 'out').length;
    // const totalUnits = products.reduce((sum, p) => sum + p.quantity, 0);
    const [stats, setStats] = useState({
        // Các phần tử trong Map phải trùng tên với biến bên frontend nhận
        totalProducts: 0,
        totalUnits: 0,
        inStockCount: 0,
        lowStockCount: 0,
        outOfStockCount: 0
    });

    useEffect(() => {
        // Dữ liệu gửi lên API (nếu API cần @RequestBody)
        const stockRanges = {
            minOfLow: 0,
            maxOfLow: 21,
            in: 0,
            out: 0
        };

        axiosInstance
            .post("/products/StockStats", stockRanges) // dùng POST nếu có body
            .then((res) => {
                setStats(res.data);
                console.log(res.data);
            })
            .catch((err) => console.error(err));
    }, [products]);
    //Xử lý chuyển trang
    const handlePrev = () => setCurrentPage(prev => Math.max(prev - 1, 0));
    const handleNext = () => setCurrentPage(prev => Math.min(prev + 1, totalPages - 1));

    return (
        <div className={styles.container}>
            {/* Header */}
            <div className={styles.header}>
                <div>
                    <h1>Inventory Management System</h1>
                    <p>Manage product stock levels and track inventory movements</p>
                </div>
            </div>

            {/* Statistics Cards */}
            <div className={styles.statsGrid}>
                <div className={styles.statCard}>
                    <div className={styles.statLabel}>Total Products</div>
                    <div className={styles.statValue}>{stats.totalProducts}</div>
                </div>
                <div className={styles.statCard}>
                    <div className={styles.statLabel}>Total Units</div>
                    <div className={styles.statValue}>{stats.totalUnits}</div>
                </div>
                <div className={`${styles.statCard} ${styles.statSuccess}`}>
                    <div className={styles.statLabel}>In Stock</div>
                    <div className={styles.statValue}>{stats.inStockCount}</div>
                </div>
                <div className={`${styles.statCard} ${styles.statWarning}`}>
                    <div className={styles.statLabel}>Low Stock</div>
                    <div className={styles.statValue}>{stats.lowStockCount}</div>
                </div>
                <div className={`${styles.statCard} ${styles.statDanger}`}>
                    <div className={styles.statLabel}>Out of Stock</div>
                    <div className={styles.statValue}>{stats.outOfStockCount}</div>
                </div>
            </div>

            {/* Tabs */}
            <div className={styles.tabs}>
                <button
                    className={`${styles.tab} ${activeTab === 'products' ? styles.tabActive : ''} `}
                    onClick={() => setActiveTab('products')}
                >
                    Product Inventory
                </button>
                <button
                    className={`${styles.tab} ${activeTab === 'history' ? styles.tabActive : ''} `}
                    onClick={() => setActiveTab('history')}
                >
                    Stock-In History
                </button>
            </div>

            {/* Products Tab */}
            {activeTab === 'products' && (
                <div className={styles.section}>
                    <div className={styles.sectionHeader}>
                        <h2>Product List</h2>
                        <div className={styles.filters}>
                            {/* Lọc danh sách theo trạng thái tồn kho */}
                            <button
                                className={`${styles.filterButton} ${productFilter === 'all' ? styles.filterActive : ''} `}
                                onClick={() => {
                                    setProductFilter('all');
                                    setCurrentPage(0);
                                }
                                }
                            >
                                All ({stats.totalUnits})
                            </button>
                            <button
                                className={`${styles.filterButton} ${productFilter === 'in' ? styles.filterActive : ''} `}
                                onClick={() => {
                                    setProductFilter('in');
                                    setCurrentPage(0)
                                }}
                            >
                                In Stock ({stats.inStockCount})
                            </button>
                            <button
                                className={`${styles.filterButton} ${productFilter === 'low' ? styles.filterActive : ''} `}
                                onClick={() => { setProductFilter('low'); setCurrentPage(0) }}
                            >
                                Low Stock ({stats.lowStockCount})
                            </button>
                            <button
                                className={`${styles.filterButton} ${productFilter === 'out' ? styles.filterActive : ''} `}
                                onClick={() => { setProductFilter('out'); setCurrentPage(0) }}
                            >
                                Out of Stock ({stats.outOfStockCount})
                            </button>
                        </div>
                    </div>

                    <div className={styles.tableWrapper}>
                        <table className={styles.table}>
                            <thead>
                                <tr>
                                    <th>Mã sản phẩm</th>
                                    <th>Tên sản phẩm</th>
                                    <th>Hình ảnh</th>
                                    <th>Số lượng</th>
                                    <th>Trạng thái</th>
                                    <th>Hành động</th>
                                </tr>
                            </thead>
                            <tbody>
                                {products.map((product) => {
                                    const status = getStockStatus(product.quantity);
                                    return (
                                        <tr key={product.id}>
                                            <td>
                                                <span className={styles.productId}>{product.id}</span>
                                            </td>
                                            <td>
                                                <span className={styles.productName}>{product.name}</span>
                                            </td>
                                            <td>
                                                <img
                                                    src={`${defaultUrl}/images/${product.image_url}`}
                                                    alt={product.name}
                                                    className={styles.productImage}
                                                    onClick={() => setFullSizeImage({ url: `${defaultUrl}/images/${product.image_url}`, name: product.name })}
                                                    style={{ cursor: 'pointer' }}

                                                />
                                            </td >
                                            <td>
                                                <span className={styles.quantity}>{product.quantity} units</span>
                                            </td>
                                            <td>
                                                <span className={`${styles.statusBadge} ${styles[`status-${status}`]}`}>
                                                    {status === 'out' && 'Out of Stock'}
                                                    {status === 'low' && 'Low Stock'}
                                                    {status === 'in' && 'In Stock'}
                                                </span>
                                            </td>
                                            <td>
                                                <button
                                                    className={styles.actionButton}
                                                    onClick={() => handleAddStock(product)}
                                                >
                                                    Add Stock
                                                </button>
                                            </td>
                                        </tr >
                                    );
                                })}
                            </tbody >
                        </table >
                    </div >
                </div >
            )}

            {/* Stock-In History Tab */}
            {
                activeTab === 'history' && (
                    <div className={styles.section}>
                        <div className={styles.sectionHeader}>
                            <h2>Stock-In History</h2>
                            <div className={styles.historyFilters}>
                                <label htmlFor="productSelect">Filter by Product:</label>
                                <select
                                    id="productSelect"
                                    className={styles.select}
                                    value={historyProductFilter}
                                    onChange={(e) => setHistoryProductFilter(e.target.value)}
                                >
                                    <option value="all">All Products</option>
                                    {products.map((product) => (
                                        <option key={product.id} value={product.id}>
                                            {product.id} - {product.name}
                                        </option>
                                    ))}
                                </select>
                            </div>
                        </div>

                        <div className={styles.tableWrapper}>
                            <table className={styles.table}>
                                <thead>
                                    <tr>
                                        <th>ID</th>
                                        <th>Tên sản phẩm</th>
                                        <th>Ảnh</th>
                                        <th>Số lượng thêm</th>
                                        <th>Thời gian</th>
                                        <th>Tổng số lượng</th>
                                        <th>Người nhập kho</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {stockHistory.map((entry) => (
                                        <tr key={entry.id}>
                                            <td>
                                                <span className={styles.productId}>{entry.productId}</span>
                                            </td>
                                            <td>
                                                <span className={styles.productName}>{entry.productName}</span>
                                            </td>
                                            <td>
                                                <img
                                                    src={`${defaultUrl}/images/${entry.image_url}`}
                                                    alt={entry.productName}
                                                    className={styles.productImage}
                                                    onClick={() => setFullSizeImage({ url: `${defaultUrl}/images/${entry.image_url}`, name: entry.productName })}
                                                    style={{ cursor: 'pointer' }}
                                                />
                                            </td>
                                            <td>
                                                <span className={styles.quantityAdded}>+{entry.importQuantity}</span>
                                            </td>
                                            <td>{formatDate(entry.date)}</td>
                                            <td>
                                                <span className={styles.quantity}>{entry.currentQuantity} units</span>
                                            </td>
                                            <td>
                                                <span className={styles.addedBy}>#{entry.userId}: {entry.userFullName}</span>
                                            </td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        </div>
                    </div>
                )
            }

            {/* Pagination */}
            <div className={styles.paginationContainer}>
                <div className={styles.paginationInfo}>
                    The Page {currentPage + 1} of {totalPages}
                    {/* Showing {indexOfFirstUser + 1} to {Math.min(indexOfLastUser, filteredUsers.length)} of {filteredUsers.length} users */}
                </div>
                <div className={styles.pagination}>
                    {/* Nút quay lại trang trước */}
                    <button
                        className={`${styles.paginationButton} ${currentPage === 0 ? styles.disabled : ''}`}
                        // onClick={handlePreviousPage}
                        onClick={handlePrev}
                        disabled={currentPage + 1 === 0}

                    >
                        <ChevronLeft size={18} />
                        <span className={styles.paginationButtonText}>Previous</span>
                    </button>

                    <div className={styles.paginationNumbers}>
                        {getPageNumbers().map((page, index) => (
                            page === '...' ? (
                                <span key={`ellipsis-${index}`} className={styles.paginationEllipsis}>
                                    ...
                                </span>
                            ) : (
                                <button
                                    key={page}
                                    className={`${styles.paginationNumber} ${currentPage + 1 === page ? styles.activePage : ''}`}
                                    // onClick={() => handlePageChange(page)}
                                    onClick={() => handlePageChange(page)}
                                >
                                    {page}
                                </button>
                            )
                        ))}
                    </div>
                    {/* Nút chuyển trang kế tiếp */}
                    <button
                        className={`${styles.paginationButton} ${currentPage + 1 === totalPages ? styles.disabled : ''}`}
                        onClick={handleNext}
                        // onClick={handleNextPage}
                        disabled={currentPage === totalPages - 1}
                    >
                        <span className={styles.paginationButtonText}>Next</span>
                        <ChevronRight size={18} />
                    </button>
                </div>
            </div>

            {/* Full Size Image Viewer */}
            {fullSizeImage && (
                <div
                    className={styles.imageViewerOverlay}
                    onClick={() => setFullSizeImage(null)}
                >
                    <div className={styles.imageViewerContainer}>
                        <button
                            className={styles.imageViewerClose}
                            onClick={() => setFullSizeImage(null)}
                        >
                            <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                            </svg>
                        </button>
                        <img
                            src={fullSizeImage.url}
                            alt={fullSizeImage.name}
                            className={styles.imageViewerImage}
                            onClick={(e) => e.stopPropagation()}
                        />
                        <div className={styles.imageViewerLabel}>{fullSizeImage.name}</div>
                    </div>
                </div>
            )}

            {/* Add Stock Modal */}
            {
                showAddStockModal && (
                    <div className={styles.modalOverlay} onClick={() => setShowAddStockModal(false)}>
                        <div className={styles.modal} onClick={(e) => e.stopPropagation()}>
                            <div className={styles.modalHeader}>
                                <h2>Add Stock</h2>
                                <button
                                    className={styles.closeButton}
                                    onClick={() => setShowAddStockModal(false)}
                                >
                                    ×
                                </button>
                            </div>
                            <div className={styles.modalBody}>
                                <div className={styles.modalProductInfo}>
                                    <img src={selectedProduct?.image} alt={selectedProduct?.name} />
                                    <div>
                                        <div className={styles.modalLabel}>Product ID</div>
                                        <div className={styles.modalProductId}>{selectedProduct?.id}</div>
                                        <div className={styles.modalLabel}>Product Name</div>
                                        <div className={styles.modalProductName}>{selectedProduct?.name}</div>
                                        <div className={styles.modalLabel}>Current Stock</div>
                                        <div className={styles.modalCurrentStock}>{selectedProduct?.quantity} units</div>
                                    </div>
                                </div>

                                <div className={styles.divider}></div>

                                <div className={styles.formGroup}>
                                    <label htmlFor="quantityInput">Quantity to Add *</label>
                                    <input
                                        id="quantityInput"
                                        type="number"
                                        min="1"
                                        className={styles.input}
                                        value={stockToAdd}
                                        onChange={(e) => setStockToAdd(e.target.value)}
                                        placeholder="Enter quantity to add"
                                        autoFocus
                                    />
                                </div>

                                {stockToAdd && parseInt(stockToAdd) > 0 && (
                                    <div className={styles.calculation}>
                                        <div className={styles.calculationRow}>
                                            <span>Current Stock:</span>
                                            <span>{selectedProduct?.quantity} units</span>
                                        </div>
                                        <div className={styles.calculationRow}>
                                            <span>Adding:</span>
                                            <span>+{stockToAdd} units</span>
                                        </div>
                                        <div className={`${styles.calculationRow} ${styles.calculationTotal}`}>
                                            <span>New Stock Total:</span>
                                            <span>{selectedProduct?.quantity + parseInt(stockToAdd)} units</span>
                                        </div>
                                    </div>
                                )}
                            </div>
                            <div className={styles.modalFooter}>
                                <button
                                    className={styles.cancelButton}
                                    onClick={() => setShowAddStockModal(false)}
                                >
                                    Cancel
                                </button>
                                <button className={styles.submitButton} onClick={handleSubmitAddStock}>
                                    Confirm & Add Stock
                                </button>
                            </div>
                        </div>
                    </div>
                )
            }


        </div >
    );
}
