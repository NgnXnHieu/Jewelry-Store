import { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import style from "./Category.module.css";
import { getProductsByCategory } from "../../../api/productApi";
import axiosInstance from "../../../api/axiosInstance";

function Category() {
    const { id } = useParams();
    const [products, setProducts] = useState([]);
    const [filteredProducts, setFilteredProducts] = useState([]);
    const [categoryName, setCategoryName] = useState("Sản phẩm");
    const [activeFilter, setActiveFilter] = useState("all");
    const [sortBy, setSortBy] = useState("default");
    const [wishlist, setWishlist] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const navigate = useNavigate();

    useEffect(() => {
        window.scrollTo(0, 0);
        const fetchCategoryName = async () => {
            try {
                const res = await axiosInstance.get(`categories/${id}`);
                setCategoryName(res.data.name);
            } catch (error) {
                console.error("Lỗi khi lấy category:", error);
            }
        };

        if (id) fetchCategoryName();
    }, [id]);

    useEffect(() => {
        if (!id) return;

        setIsLoading(true);
        getProductsByCategory(id)
            .then((data) => {
                const productsData = data.content || [];
                setProducts(productsData);
                setFilteredProducts(productsData);
                setIsLoading(false);
            })
            .catch((err) => {
                console.error("Lỗi tải sản phẩm:", err);
                setIsLoading(false);
            });
    }, [id]);

    useEffect(() => {
        let result = [...products];

        // Apply sorting
        switch (sortBy) {
            case "price-asc":
                result.sort((a, b) => a.price - b.price);
                break;
            case "price-desc":
                result.sort((a, b) => b.price - a.price);
                break;
            case "name":
                result.sort((a, b) => a.name.localeCompare(b.name));
                break;
            default:
                break;
        }

        setFilteredProducts(result);
    }, [sortBy, products]);

    const handleCardClick = (productId) => {
        navigate(`/productdetail/${productId}`);
    };

    const handleAddToCart = (product, e) => {
        e.stopPropagation();
        console.log("Thêm vào giỏ:", product);

        // Show toast notification
        const toast = document.createElement('div');
        toast.className = style.toast;
        toast.textContent = '✓ Đã thêm vào giỏ hàng';
        document.body.appendChild(toast);
        setTimeout(() => toast.remove(), 3000);
    };

    const toggleWishlist = (productId, e) => {
        e.stopPropagation();
        setWishlist(prev =>
            prev.includes(productId)
                ? prev.filter(id => id !== productId)
                : [...prev, productId]
        );
    };

    const handleQuickView = (e) => {
        e.stopPropagation();
        console.log("Quick view");
    };

    return (
        <div className={style.container}>
            {/* Breadcrumb Navigation */}
            <div className={style.breadcrumb}>
                <span className={style.breadcrumbItem} onClick={() => navigate("/")}>
                    Trang chủ
                </span>
                {/* <span className={style.breadcrumbSeparator}>/</span> */}
                {/* <span className={style.breadcrumbItem} onClick={() => navigate("/categories")}>
                    Danh mục
                </span> */}
                <span className={style.breadcrumbSeparator}>/</span>
                <span className={style.breadcrumbActive}>{categoryName}</span>
            </div>

            {/* Category Header */}
            <section className={style.categoryHeader}>
                <div className={style.categoryHeaderContent}>
                    <div className={style.categoryHeaderText}>
                        <h1 className={style.categoryTitle}>{categoryName}</h1>
                        <p className={style.categoryDescription}>
                            Khám phá bộ sưu tập {categoryName.toLowerCase()} đa dạng và tinh tế
                        </p>
                        <div className={style.categoryStats}>
                            <span className={style.statItem}>
                                ✨ {filteredProducts.length} sản phẩm
                            </span>
                            <span className={style.statItem}>
                                💎 Chất lượng cao
                            </span>
                            <span className={style.statItem}>
                                🚚 Miễn phí vận chuyển
                            </span>
                        </div>
                    </div>
                    <div className={style.categoryHeaderImage}>
                        <div className={style.decorativeCircle}></div>
                    </div>
                </div>
            </section>

            {/* Filter and Sort Bar */}
            <section className={style.controlBar}>
                <div className={style.filterSection}>
                    <button
                        className={`${style.filterButton} ${activeFilter === 'all' ? style.activeFilter : ''}`}
                        onClick={() => setActiveFilter('all')}
                    >
                        Tất cả
                    </button>
                    <button
                        className={`${style.filterButton} ${activeFilter === 'featured' ? style.activeFilter : ''}`}
                        onClick={() => setActiveFilter('featured')}
                    >
                        Nổi bật
                    </button>
                    <button
                        className={`${style.filterButton} ${activeFilter === 'sale' ? style.activeFilter : ''}`}
                        onClick={() => setActiveFilter('sale')}
                    >
                        Giảm giá
                    </button>
                </div>

                <div className={style.sortSection}>
                    <label className={style.sortLabel}>Sắp xếp:</label>
                    <select
                        className={style.sortSelect}
                        value={sortBy}
                        onChange={(e) => setSortBy(e.target.value)}
                    >
                        <option value="default">Mặc định</option>
                        <option value="price-asc">Giá: Thấp đến cao</option>
                        <option value="price-desc">Giá: Cao đến thấp</option>
                        <option value="name">Tên: A-Z</option>
                    </select>
                </div>
            </section>

            {/* Products Grid */}
            <section className={style.productsSection}>
                {isLoading ? (
                    <div className={style.loadingContainer}>
                        <div className={style.spinner}></div>
                        <p className={style.loadingText}>Đang tải sản phẩm...</p>
                    </div>
                ) : filteredProducts.length === 0 ? (
                    <div className={style.emptyState}>
                        <div className={style.emptyIcon}>🛍️</div>
                        <h3 className={style.emptyTitle}>Không có sản phẩm</h3>
                        <p className={style.emptyText}>
                            Không có sản phẩm trong danh mục này.
                        </p>
                        <button className={style.backButton} onClick={() => navigate("/")}>
                            Quay về trang chủ
                        </button>
                    </div>
                ) : (
                    <div className={style.grid}>
                        {filteredProducts.map((product) => (
                            <div
                                key={product.id}
                                className={style.card}
                                onClick={() => handleCardClick(product.id)}
                            >
                                <div className={style.cardImageWrapper}>
                                    <img
                                        src={product.image_url}
                                        alt={product.name}
                                        className={style.image}
                                    />
                                    <div className={style.cardOverlay}>
                                        <button
                                            className={style.quickViewButton}
                                            onClick={handleQuickView}
                                        >
                                            👁️ Xem nhanh
                                        </button>
                                    </div>
                                    <button
                                        className={`${style.wishlistButton} ${wishlist.includes(product.id) ? style.wishlisted : ''}`}
                                        onClick={(e) => toggleWishlist(product.id, e)}
                                    >
                                        {wishlist.includes(product.id) ? '❤️' : '🤍'}
                                    </button>
                                    {product.discount && (
                                        <span className={style.discountBadge}>-{product.discount}%</span>
                                    )}
                                </div>
                                <div className={style.cardContent}>
                                    <h3 className={style.name}>{product.name}</h3>
                                    <div className={style.rating}>
                                        ⭐⭐⭐⭐⭐ <span className={style.ratingCount}>(4.8)</span>
                                    </div>
                                    <div className={style.priceContainer}>
                                        <p className={style.price}>{product.price.toLocaleString()}₫</p>
                                        {product.originalPrice && (
                                            <p className={style.originalPrice}>
                                                {product.originalPrice.toLocaleString()}₫
                                            </p>
                                        )}
                                    </div>
                                    <button
                                        className={style.button}
                                        onClick={(e) => handleAddToCart(product, e)}
                                    >
                                        🛒 Thêm vào giỏ
                                    </button>
                                </div>
                            </div>
                        ))}
                    </div>
                )}
            </section>

            {/* Info Banner */}
            {filteredProducts.length > 0 && (
                <section className={style.infoBanner}>
                    <div className={style.infoBannerContent}>
                        <div className={style.infoItem}>
                            <div className={style.infoIcon}>✓</div>
                            <div className={style.infoText}>
                                <h4>Chính hãng 100%</h4>
                                <p>Cam kết hàng thật</p>
                            </div>
                        </div>
                        <div className={style.infoItem}>
                            <div className={style.infoIcon}>🎁</div>
                            <div className={style.infoText}>
                                <h4>Đổi trả miễn phí</h4>
                                <p>Trong vòng 30 ngày</p>
                            </div>
                        </div>
                        <div className={style.infoItem}>
                            <div className={style.infoIcon}>💳</div>
                            <div className={style.infoText}>
                                <h4>Thanh toán bảo mật</h4>
                                <p>An toàn & tiện lợi</p>
                            </div>
                        </div>
                    </div>
                </section>
            )}
        </div>
    );
}

export default Category;
