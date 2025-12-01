import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import style from "./BestSeller.module.css";
import axios from "axios";
import { FaCheckCircle } from "react-icons/fa";
import PageNumber from "../../../components/Header/PageNumber/PageNumber";
import defaultUrl from "../../../api/defaultUrl";
import axiosInstance from "../../../api/axiosInstance";
import { useBuyNow } from "../../../hook/useBuyNow";
function BestSeller() {
    const [bestSellers, setBestSellers] = useState([]);
    const [wishlist, setWishlist] = useState([]);
    const [showNotification, setShowNotification] = useState(false);
    const [currentPage, setCurrentPage] = useState(0);
    const [totalPages, setTotalPages] = useState(1);
    const navigate = useNavigate();

    useEffect(() => {
        window.scrollTo(0, 0);
        axios.get(`products/bestSeller?page=${currentPage}&size=20`)
            .then(res => {
                setBestSellers(res.data.content || res.data);
                console.log(res.data);
                setCurrentPage(res.data.number);
                setTotalPages(res.data.totalPages);
            })
            .catch(err => console.error("Lỗi khi tải dữ liệu:", err));
    }, [currentPage]);

    const handleCardClick = (id) => {
        navigate(`/productdetail/${id}`);
    };

    const handleAddToCart = (product, e) => {
        e.stopPropagation();
        console.log("Thêm vào giỏ:", product);
        // Gọi redux/context để thêm sản phẩm vào giỏ
        setShowNotification(true);
        setTimeout(() => setShowNotification(false), 3000);
    };

    const toggleWishlist = (productId, e) => {
        e.stopPropagation();
        setWishlist(prev =>
            prev.includes(productId)
                ? prev.filter(id => id !== productId)
                : [...prev, productId]
        );
    };

    return (
        <div className={style.container}>
            {/* Hero Header */}
            <section className={style.heroHeader}>
                <div className={style.heroContent}>
                    <div className={style.heroText}>
                        <span className={style.heroSubtitle}>🏆 Top Sản Phẩm</span>
                        <h1 className={style.heroTitle}>Best Seller</h1>
                        <p className={style.heroDescription}>
                            Khám phá những sản phẩm bán chạy nhất được yêu thích bởi hàng ngàn khách hàng
                        </p>
                    </div>
                    <div className={style.heroStats}>
                        <div className={style.statCard}>
                            <div className={style.statNumber}>{bestSellers.length}+</div>
                            <div className={style.statLabel}>Sản phẩm</div>
                        </div>
                        <div className={style.statCard}>
                            <div className={style.statNumber}>10K+</div>
                            <div className={style.statLabel}>Đã bán</div>
                        </div>
                        <div className={style.statCard}>
                            <div className={style.statNumber}>4.8⭐</div>
                            <div className={style.statLabel}>Đánh giá</div>
                        </div>
                    </div>
                </div>
            </section>

            {/* Products Section */}
            <section className={style.productsSection}>
                <div className={style.sectionHeader}>
                    <div>
                        <h2 className={style.sectionTitle}>✨ Sản phẩm bán chạy nhất</h2>
                        <p className={style.sectionSubtitle}>
                            {bestSellers.length} sản phẩm được khách hàng tin tưởng và lựa chọn
                        </p>
                    </div>
                </div>

                <div className={style.grid}>
                    {bestSellers.map(product => (
                        <div
                            key={product.id}
                            className={style.card}
                            onClick={() => handleCardClick(product.id)}
                        >
                            <div className={style.cardImageWrapper}>
                                {product.imageUrl ? (
                                    <img
                                        src={`${defaultUrl}/images/${product.imageUrl}`}
                                        alt={product.name}
                                        className={style.image}
                                    />
                                ) : (
                                    <div className={style.noImage}>
                                        <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
                                        </svg>
                                    </div>
                                )}

                                <div className={style.cardOverlay}>
                                    <button
                                        className={style.quickViewButton}
                                        onClick={(e) => {
                                            e.stopPropagation();
                                            handleCardClick(product.id);
                                        }}
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

                                <span className={style.bestSellerBadge}>🏆 Best Seller</span>
                            </div>

                            <div className={style.cardContent}>
                                <h3 className={style.name}>{product.name}</h3>
                                <div className={style.rating}>
                                    ⭐⭐⭐⭐⭐ <span className={style.ratingCount}>(4.8)</span>
                                </div>
                                <p className={style.price}>
                                    {product.price.toLocaleString()}₫
                                </p>
                                <p className={style.sold}>
                                    🔥 Đã bán: {product.totalQuantity || 0} sản phẩm
                                </p>
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

                {bestSellers.length === 0 && (
                    <div className={style.emptyState}>
                        <div className={style.emptyIcon}>📦</div>
                        <h3>Chưa có sản phẩm best seller</h3>
                        <p>Hãy quay lại sau để khám phá những sản phẩm bán chạy nhất!</p>
                    </div>
                )}
            </section>
            <PageNumber
                currentPage={currentPage}
                totalPages={totalPages}
                setCurrentPage={setCurrentPage} // Truyền hàm set state xuống
            />
            {/* Features Section */}
            <section className={style.featuresSection}>
                <div className={style.featuresGrid}>
                    <div className={style.featureCard}>
                        <div className={style.featureIcon}>🏆</div>
                        <h3 className={style.featureTitle}>Chất lượng hàng đầu</h3>
                        <p className={style.featureDesc}>Top sản phẩm được lựa chọn nhiều nhất</p>
                    </div>
                    <div className={style.featureCard}>
                        <div className={style.featureIcon}>⭐</div>
                        <h3 className={style.featureTitle}>Đánh giá cao</h3>
                        <p className={style.featureDesc}>Được khách hàng đánh giá 4.8/5 sao</p>
                    </div>
                    <div className={style.featureCard}>
                        <div className={style.featureIcon}>🚀</div>
                        <h3 className={style.featureTitle}>Giao hàng nhanh</h3>
                        <p className={style.featureDesc}>Ưu tiên giao hàng cho best seller</p>
                    </div>
                    <div className={style.featureCard}>
                        <div className={style.featureIcon}>💝</div>
                        <h3 className={style.featureTitle}>Quà tặng độc quyền</h3>
                        <p className={style.featureDesc}>Nhận quà khi mua best seller</p>
                    </div>
                </div>
            </section>

            {/* Success Notification */}
            {showNotification && (
                <div className={style.notification}>
                    <FaCheckCircle className={style.notificationIcon} />
                    <span>Đã thêm vào giỏ hàng!</span>
                </div>
            )}


        </div>
    );
}

export default BestSeller;
