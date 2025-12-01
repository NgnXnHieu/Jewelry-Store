import React, { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import styles from "./ProductDetail.module.css";
import { FaStar, FaRegStar, FaHeart, FaRegHeart, FaShoppingCart, FaTruck, FaShieldAlt, FaUndo, FaCheckCircle } from "react-icons/fa";
import { getProductById, getRelatedProducts } from "../../../api/productApi";
import { addToCart } from "../../../api/cartApi";
import defaultUrl from "../../../api/defaultUrl";
import { useBuyNow } from "../../../hook/useBuyNow";
function ProductDetail() {
    const { id } = useParams();
    const navigate = useNavigate();

    const [product, setProduct] = useState(null);
    const [relatedProducts, setRelatedProducts] = useState([]);
    const [favorite, setFavorite] = useState(false);
    const [quantity, setQuantity] = useState(1);
    const [selectedImage, setSelectedImage] = useState(0);
    const [activeTab, setActiveTab] = useState("description");
    const [showNotification, setShowNotification] = useState(false);

    useEffect(() => {
        window.scrollTo(0, 0);

        getProductById(id)
            .then((data) => {
                console.log("Chi tiết sản phẩm từ backend:", data);
                setProduct(data);
                setSelectedImage(0);
            })
            .catch((err) => console.error("Lỗi khi tải sản phẩm:", err));

        getRelatedProducts(id)
            .then((data) => {
                console.log("Dữ liệu sản phẩm liên quan từ backend:", data);
                if (data && data.content) {
                    setRelatedProducts(data.content);
                } else {
                    setRelatedProducts([]);
                }
            })
            .catch((err) => console.error("Lỗi khi tải sản phẩm liên quan:", err));
    }, [id]);

    const handleAddToCart = async (product) => {
        try {
            const res = await addToCart(product.id, quantity);
            console.log("Đã thêm vào giỏ hàng:", res);
            setShowNotification(true);
            setTimeout(() => setShowNotification(false), 3000);
        } catch (err) {
            console.error("Lỗi khi thêm vào giỏ:", err);
            if (err.response?.status === 401) {
                alert("❌ Bạn cần đăng nhập trước khi thêm vào giỏ!");
                navigate("/login");
            } else {
                alert("❌ Không thể thêm sản phẩm vào giỏ!");
            }
        }
    };
    const { buyNow, isLoading } = useBuyNow();
    const handleBuyNow = () => {
        const convertedItem = {
            id: product.id,
            quantity: quantity,
        };
        buyNow(convertedItem);
    };

    const increase = () => {
        setQuantity((prev) => prev + 1);
    };

    const decrease = () => {
        setQuantity((prev) => (prev > 1 ? prev - 1 : 1));
    };

    const toggleFavorite = () => {
        setFavorite(!favorite);
    };

    if (!product) {
        return (
            <div className={styles.loadingContainer}>
                <div className={styles.loader}></div>
                <p>Đang tải sản phẩm...</p>
            </div>
        );
    }

    const specList = product.description ? product.description.split(",").map(s => s.trim()) : [];

    // Mock multiple images for gallery (in real app, get from backend)
    const productImages = [
        product.image_url,
        product.image_url,
        product.image_url,
    ];

    return (
        <div className={styles.container}>
            {/* Breadcrumb */}
            <div className={styles.breadcrumb}>
                <span onClick={() => navigate("/")}>Trang chủ</span>
                <span className={styles.separator}>/</span>
                <span onClick={() => navigate(-1)}>Sản phẩm</span>
                <span className={styles.separator}>/</span>
                <span className={styles.currentPage}>{product.name}</span>
            </div>

            {/* Main Product Section */}
            <div className={styles.mainSection}>
                {/* Left - Image Gallery */}
                <div className={styles.left}>
                    <div className={styles.imageGallery}>
                        <div className={styles.mainImageBox}>
                            <img
                                src={`${defaultUrl}/images/${product.image_url}`}
                                alt={product.name}
                                className={styles.mainImage}
                            />
                            <button
                                className={`${styles.favoriteButton} ${favorite ? styles.favorited : ""}`}
                                onClick={toggleFavorite}
                            >
                                {favorite ? <FaHeart /> : <FaRegHeart />}
                            </button>
                            {product.discount && (
                                <div className={styles.discountBadge}>-{product.discount}%</div>
                            )}
                        </div>
                        <div className={styles.thumbnailList}>
                            {productImages.map((img, index) => (
                                <div
                                    key={index}
                                    className={`${styles.thumbnail} ${selectedImage === index ? styles.activeThumbnail : ""}`}
                                    onClick={() => setSelectedImage(index)}
                                >
                                    {/* các ảnh góc khác của sản phẩm */}
                                    <img src={`${defaultUrl}/images/${product.image_url}`} alt={`${product.name} ${index + 1}`} />
                                </div>
                            ))}
                        </div>
                    </div>
                </div>

                {/* Right - Product Info */}
                <div className={styles.right}>
                    <h1 className={styles.productName}>{product.name}</h1>

                    <div className={styles.ratingSection}>
                        <div className={styles.stars}>
                            {[...Array(4)].map((_, i) => <FaStar key={i} className={styles.starFilled} />)}
                            <FaRegStar className={styles.starEmpty} />
                        </div>
                        <span className={styles.ratingText}>(4.8/5)</span>
                        <span className={styles.reviewCount}>156 đánh giá</span>
                        <span className={styles.soldCount}>•</span>
                        <span className={styles.soldCount}>Đã bán 1,234</span>
                    </div>

                    <div className={styles.priceSection}>
                        <div className={styles.currentPrice}>{product.price.toLocaleString()}₫</div>
                        {product.originalPrice && (
                            <>
                                <div className={styles.originalPrice}>{product.originalPrice.toLocaleString()}₫</div>
                                <div className={styles.saveAmount}>Tiết kiệm {(product.originalPrice - product.price).toLocaleString()}₫</div>
                            </>
                        )}
                    </div>

                    <div className={styles.stockInfo}>
                        <FaCheckCircle className={styles.stockIcon} />
                        <span>Còn lại: <strong>{product.quantity}</strong> sản phẩm</span>
                    </div>

                    <div className={styles.quantitySection}>
                        <label className={styles.label}>Số lượng:</label>
                        <div className={styles.quantityControls}>
                            <button onClick={decrease} className={styles.quantityBtn}>−</button>
                            <input
                                type="number"
                                value={quantity}
                                readOnly
                                className={styles.quantityInput}
                            />
                            <button onClick={increase} className={styles.quantityBtn}>+</button>
                        </div>
                    </div>

                    <div className={styles.actionButtons}>
                        <button className={styles.addToCartBtn} onClick={() => handleAddToCart(product)}>
                            <FaShoppingCart />
                            Thêm vào giỏ
                        </button>
                        <button className={styles.buyNowBtn} onClick={handleBuyNow}>
                            Mua ngay
                        </button>
                    </div>

                    {/* Features */}
                    <div className={styles.features}>
                        <div className={styles.featureItem}>
                            <FaTruck className={styles.featureIcon} />
                            <div>
                                <strong>Miễn phí vận chuyển</strong>
                                <p>Đơn hàng từ 500.000₫</p>
                            </div>
                        </div>
                        <div className={styles.featureItem}>
                            <FaShieldAlt className={styles.featureIcon} />
                            <div>
                                <strong>Bảo hành chính hãng</strong>
                                <p>12 tháng bảo hành</p>
                            </div>
                        </div>
                        <div className={styles.featureItem}>
                            <FaUndo className={styles.featureIcon} />
                            <div>
                                <strong>Đổi trả dễ dàng</strong>
                                <p>Trong vòng 30 ngày</p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            {/* Product Details Tabs */}
            <div className={styles.tabsSection}>
                <div className={styles.tabButtons}>
                    <button
                        className={`${styles.tabButton} ${activeTab === "description" ? styles.activeTab : ""}`}
                        onClick={() => setActiveTab("description")}
                    >
                        Mô tả sản phẩm
                    </button>
                    <button
                        className={`${styles.tabButton} ${activeTab === "specs" ? styles.activeTab : ""}`}
                        onClick={() => setActiveTab("specs")}
                    >
                        Thông số kỹ thuật
                    </button>
                    <button
                        className={`${styles.tabButton} ${activeTab === "reviews" ? styles.activeTab : ""}`}
                        onClick={() => setActiveTab("reviews")}
                    >
                        Đánh giá (156)
                    </button>
                </div>

                <div className={styles.tabContent}>
                    {activeTab === "description" && (
                        <div className={styles.descriptionContent}>
                            <h3>Thông tin sản phẩm</h3>
                            <p>
                                Sản phẩm được chế tác tỉ mỉ từ những nghệ nhân tài ba với chất liệu cao cấp,
                                mang đến vẻ đẹp sang trọng và đẳng cấp. Thiết kế tinh xảo, phù hợp cho mọi dịp
                                từ hằng ngày đến những sự kiện đặc biệt.
                            </p>
                            {specList.length > 0 && (
                                <ul className={styles.specList}>
                                    {specList.map((spec, idx) => (
                                        <li key={idx}>{spec}</li>
                                    ))}
                                </ul>
                            )}
                        </div>
                    )}
                    {activeTab === "specs" && (
                        <div className={styles.specsContent}>
                            <h3>Thông số chi tiết</h3>
                            <table className={styles.specsTable}>
                                <tbody>
                                    <tr>
                                        <td>Chất liệu</td>
                                        <td>Bạc cao cấp</td>
                                    </tr>
                                    <tr>
                                        <td>Trọng lượng</td>
                                        <td>15g</td>
                                    </tr>
                                    <tr>
                                        <td>Kích thước</td>
                                        <td>18mm x 25mm</td>
                                    </tr>
                                    <tr>
                                        <td>Xuất xứ</td>
                                        <td>Việt Nam</td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                    )}
                    {activeTab === "reviews" && (
                        <div className={styles.reviewsContent}>
                            <h3>Đánh giá từ khách hàng</h3>
                            <div className={styles.reviewItem}>
                                <div className={styles.reviewHeader}>
                                    <div className={styles.reviewUser}>
                                        <div className={styles.avatar}>N</div>
                                        <div>
                                            <strong>Nguyễn Văn A</strong>
                                            <div className={styles.reviewStars}>
                                                {[...Array(5)].map((_, i) => <FaStar key={i} />)}
                                            </div>
                                        </div>
                                    </div>
                                    <span className={styles.reviewDate}>23/10/2024</span>
                                </div>
                                <p className={styles.reviewText}>
                                    Sản phẩm rất đẹp, chất lượng tốt. Giao hàng nhanh, đóng gói cẩn thận.
                                    Rất hài lòng với sản phẩm!
                                </p>
                            </div>
                        </div>
                    )}
                </div>
            </div>

            {/* Related Products */}
            {relatedProducts.length > 0 && (
                <div className={styles.relatedSection}>
                    <h2 className={styles.relatedTitle}>Sản phẩm liên quan</h2>
                    <div className={styles.relatedGrid}>
                        {relatedProducts.map((p) => (
                            <div key={p.id} className={styles.relatedCard}>
                                <div className={styles.relatedImageWrapper}>
                                    <img src={`${defaultUrl}/images/${p.image_url}`} alt={p.name} />

                                </div>
                                <div className={styles.relatedContent}>
                                    <h3 className={styles.relatedName}>{p.name}</h3>
                                    <div className={styles.relatedRating}>
                                        {[...Array(5)].map((_, i) => (
                                            <FaStar key={i} className={styles.relatedStar} />
                                        ))}
                                        <span>(4.8)</span>
                                    </div>
                                    <div className={styles.relatedPrice}>
                                        {p.price.toLocaleString()}₫
                                    </div>
                                    <button
                                        className={styles.relatedButton}
                                        onClick={() => navigate(`/productdetail/${p.id}`)}
                                    >
                                        Xem chi tiết
                                    </button>
                                </div>
                            </div>
                        ))}
                    </div>
                </div>
            )}

            {/* Success Notification */}
            {showNotification && (
                <div className={styles.notification}>
                    <FaCheckCircle className={styles.notificationIcon} />
                    <span>Đã thêm vào giỏ hàng!</span>
                </div>
            )}
        </div>
    );
}

export default ProductDetail;
