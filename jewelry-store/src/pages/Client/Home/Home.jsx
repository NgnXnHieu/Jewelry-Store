import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import style from "./Home.module.css";
import { getAllProducts } from "../../../api/productApi";

function Home() {
    const products = [
        { id: 1, name: "Vòng tay bạc", price: 250000, image: "https://tse4.mm.bing.net/th/id/OIP.zoycwLWxDNMCqiynRvp_hAHaEO?cb=12&rs=1&pid=ImgDetMain&o=7&rm=3" },
        { id: 2, name: "Nhẫn kim cương", price: 1200000, image: "https://tse3.mm.bing.net/th/id/OIP.pxMHQIF04UVpDJAn8UufXwHaE8?cb=12&w=1024&h=683&rs=1&pid=ImgDetMain&o=7&rm=3" },
        { id: 3, name: "Dây chuyền vàng", price: 950000, image: "https://tse2.mm.bing.net/th/id/OIP.axmcZGzGUEemYiHG9r99_gHaFA?cb=12&rs=1&pid=ImgDetMain&o=7&rm=3" },
        { id: 4, name: "Bông tai ngọc trai", price: 550000, image: "https://wallpapers.com/images/hd/jewelry-pictures-wyd23ar9xagseo7d.jpg" },
        { id: 5, name: "Bông tai ngọc trai", price: 550000, image: "https://wallpapers.com/images/hd/jewelry-pictures-wyd23ar9xagseo7d.jpg" },
        { id: 6, name: "Bông tai ngọc trai", price: 550000, image: "https://wallpapers.com/images/hd/jewelry-pictures-wyd23ar9xagseo7d.jpg" }
    ];

    const categories = [
        { id: 1, name: "Nhẫn", icon: "💍", count: 125, color: "#FFD700" },
        { id: 2, name: "Dây chuyền", icon: "📿", count: 89, color: "#C0C0C0" },
        { id: 3, name: "Vòng tay", icon: "⌚", count: 156, color: "#B87333" },
        { id: 4, name: "Bông tai", icon: "💎", count: 98, color: "#E5E4E2" },
        { id: 5, name: "Lắc chân", icon: "✨", count: 45, color: "#FFE5B4" },
        { id: 6, name: "Phụ kiện", icon: "👑", count: 67, color: "#FFDAB9" }
    ];

    const [listProducts, setProducts] = useState([]);
    const [activeFilter, setActiveFilter] = useState("all");
    const [wishlist, setWishlist] = useState([]);
    const navigate = useNavigate();

    useEffect(() => {
        getAllProducts()
            .then(data => setProducts(data.content))
            .catch(err => console.error("Lỗi khi tải dữ liệu:", err));
    }, []);

    const handleCardClick = (id) => {
        navigate(`/productdetail/${id}`);
    };

    const handleMoreButton = () => {
        navigate(`/bestSeller`);
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

    const handleCategoryClick = (categoryName) => {
        console.log("Danh mục được chọn:", categoryName);
        // Navigate to category page or filter products
    };

    return (
        <div className={style.container}>
            {/* Hero Banner */}
            <section className={style.heroBanner}>
                <div className={style.heroContent}>
                    <div className={style.heroText}>
                        <span className={style.heroSubtitle}>✨ Bộ sưu tập mới 2024</span>
                        <h1 className={style.heroTitle}>Vẻ Đẹp Vượt Thời Gian</h1>
                        <p className={style.heroDescription}>
                            Khám phá những thiết kế trang sức sang trọng, tinh tế được chế tác thủ công từ những nghệ nhân tài ba
                        </p>
                        <div className={style.heroButtons}>
                            <button className={style.primaryButton} onClick={handleMoreButton}>
                                Khám phá ngay
                            </button>
                            <button className={style.secondaryButton}>
                                Xem bộ sưu tập
                            </button>
                        </div>
                    </div>
                    <div className={style.heroImage}>
                        <div className={style.heroImageWrapper}>
                            <img
                                src="https://images.unsplash.com/photo-1515562141207-7a88fb7ce338?w=800&q=80"
                                alt="Hero Jewelry"
                                className={style.heroImg}
                            />
                            <div className={style.heroBadge}>
                                <span className={style.badgeText}>Sale 30%</span>
                            </div>
                        </div>
                    </div>
                </div>
            </section>

            {/* Featured Categories */}
            <section className={style.categoriesSection}>
                <div className={style.sectionHeader}>
                    <h2 className={style.sectionTitle}>Danh mục nổi bật</h2>
                    <p className={style.sectionSubtitle}>Tìm kiếm theo loại trang sức yêu thích</p>
                </div>
                <div className={style.categoriesGrid}>
                    {categories.map(category => (
                        <div
                            key={category.id}
                            className={style.categoryCard}
                            onClick={() => handleCategoryClick(category.name)}
                            style={{ '--category-color': category.color }}
                        >
                            <div className={style.categoryIcon}>{category.icon}</div>
                            <h3 className={style.categoryName}>{category.name}</h3>
                            <p className={style.categoryCount}>{category.count} sản phẩm</p>
                        </div>
                    ))}
                </div>
            </section>

            {/* Promotional Banner */}
            <section className={style.promoBanner}>
                <div className={style.promoContent}>
                    <div className={style.promoText}>
                        <h2>🎁 Ưu đãi đặc biệt</h2>
                        <p>Giảm giá lên đến 50% cho bộ sưu tập mùa hè</p>
                    </div>
                    <button className={style.promoButton} onClick={handleMoreButton}>
                        Mua ngay
                    </button>
                </div>
            </section>

            {/* Top Products */}
            <section className={style.productsSection}>
                <div className={style.display_horizontal}>
                    <div>
                        <h2 className={style.sectionTitle}>✨ Top sản phẩm bán chạy</h2>
                        <p className={style.sectionSubtitle}>Những sản phẩm được yêu thích nhất</p>
                    </div>
                    <button className={style.viewAllButton} onClick={handleMoreButton}>
                        <span>Xem tất cả</span>
                        <img className={style.arrow_button} src="/image/arrow.png" alt="arrow" />
                    </button>
                </div>

                {/* Product Filter */}
                <div className={style.filterBar}>
                    <button
                        className={`${style.filterButton} ${activeFilter === 'all' ? style.activeFilter : ''}`}
                        onClick={() => setActiveFilter('all')}
                    >
                        Tất cả
                    </button>
                    <button
                        className={`${style.filterButton} ${activeFilter === 'new' ? style.activeFilter : ''}`}
                        onClick={() => setActiveFilter('new')}
                    >
                        Mới nhất
                    </button>
                    <button
                        className={`${style.filterButton} ${activeFilter === 'popular' ? style.activeFilter : ''}`}
                        onClick={() => setActiveFilter('popular')}
                    >
                        Phổ biến
                    </button>
                    <button
                        className={`${style.filterButton} ${activeFilter === 'sale' ? style.activeFilter : ''}`}
                        onClick={() => setActiveFilter('sale')}
                    >
                        Giảm giá
                    </button>
                </div>

                <div className={style.grid}>
                    {products.map(product => (
                        <div
                            key={product.id}
                            className={style.card}
                            onClick={() => handleCardClick(product.id)}
                        >
                            <div className={style.cardImageWrapper}>
                                <img
                                    src={product.image}
                                    alt={product.name}
                                    className={style.image}
                                />
                                <div className={style.cardOverlay}>
                                    <button
                                        className={style.quickViewButton}
                                        onClick={(e) => {
                                            e.stopPropagation();
                                            console.log("Quick view");
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
                                        <p className={style.originalPrice}>{product.originalPrice.toLocaleString()}₫</p>
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
            </section>

            {/* Product Categories from API */}
            {listProducts.length > 0 && (
                <section className={style.productsSection}>
                    <div className={style.display_horizontal}>
                        <div>
                            <h2 className={style.sectionTitle}>🏆 Tất cả sản phẩm</h2>
                            <p className={style.sectionSubtitle}>Khám phá bộ sưu tập đa dạng của chúng tôi</p>
                        </div>
                        <button className={style.viewAllButton}>
                            <span>Xem tất cả</span>
                            <img className={style.arrow_button} src="/image/arrow.png" alt="arrow" />
                        </button>
                    </div>
                    <div className={style.grid}>
                        {listProducts.map(product => (
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
                                            onClick={(e) => {
                                                e.stopPropagation();
                                                console.log("Quick view");
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
                                </div>
                                <div className={style.cardContent}>
                                    <h3 className={style.name}>{product.name}</h3>
                                    <div className={style.rating}>
                                        ⭐⭐⭐⭐⭐ <span className={style.ratingCount}>(4.8)</span>
                                    </div>
                                    <p className={style.price}>{product.price.toLocaleString()}₫</p>
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
                </section>
            )}

            {/* Why Choose Us Section */}
            <section className={style.featuresSection}>
                <div className={style.featuresGrid}>
                    <div className={style.featureCard}>
                        <div className={style.featureIcon}>🚚</div>
                        <h3 className={style.featureTitle}>Miễn phí vận chuyển</h3>
                        <p className={style.featureDesc}>Đơn hàng từ 500.000₫</p>
                    </div>
                    <div className={style.featureCard}>
                        <div className={style.featureIcon}>💎</div>
                        <h3 className={style.featureTitle}>Chất lượng đảm bảo</h3>
                        <p className={style.featureDesc}>Hàng chính hãng 100%</p>
                    </div>
                    <div className={style.featureCard}>
                        <div className={style.featureIcon}>🔄</div>
                        <h3 className={style.featureTitle}>Đổi trả dễ dàng</h3>
                        <p className={style.featureDesc}>Trong vòng 30 ngày</p>
                    </div>
                    <div className={style.featureCard}>
                        <div className={style.featureIcon}>💳</div>
                        <h3 className={style.featureTitle}>Thanh toán an toàn</h3>
                        <p className={style.featureDesc}>Nhiều hình thức thanh toán</p>
                    </div>
                </div>
            </section>

            {/* Newsletter Section */}
            <section className={style.newsletterSection}>
                <div className={style.newsletterContent}>
                    <h2 className={style.newsletterTitle}>💌 Đăng ký nhận tin</h2>
                    <p className={style.newsletterDesc}>Nhận thông tin về sản phẩm mới và ưu đãi đặc biệt</p>
                    <div className={style.newsletterForm}>
                        <input
                            type="email"
                            placeholder="Nhập email của bạn..."
                            className={style.newsletterInput}
                        />
                        <button className={style.newsletterButton}>Đăng ký</button>
                    </div>
                </div>
            </section>
        </div>
    );
}

export default Home;
