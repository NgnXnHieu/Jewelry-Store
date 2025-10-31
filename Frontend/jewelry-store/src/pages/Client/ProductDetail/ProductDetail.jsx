import React, { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import styles from "./ProductDetail.module.css";
import { FaStar, FaRegStar, FaHeart } from "react-icons/fa";
import { getProductById, getRelatedProducts } from "../../../api/productApi"; // chỉ lấy chi tiết sản phẩm
import { addToCart } from "../../../api/cartApi";

function ProductDetail() {
    const { id } = useParams();
    const navigate = useNavigate();

    const [product, setProduct] = useState(null);
    const [relatedProducts, setRelatedProducts] = useState([]);
    const [favorite, setFavorite] = useState(false);
    const [quantity, setQuantity] = useState(1);





    useEffect(() => {
        getProductById(id)
            .then((data) => {
                console.log("Chi tiết sản phẩm từ backend:", data);
                setProduct(data);
            })
            .catch((err) => console.error("Lỗi khi tải sản phẩm:", err));

        // Lấy sản phẩm liên quan
        getRelatedProducts(id)
            .then((data) => {
                console.log("Dữ liệu sản phẩm liên quan từ backend:", data);
                if (data && data.content) {
                    setRelatedProducts(data.content);
                    console.log("relatedProducts đã set:", data.content);
                } else {
                    setRelatedProducts([]);
                    console.log("relatedProducts rỗng vì backend không trả về content");
                }
            })
            .catch((err) => console.error("Lỗi khi tải sản phẩm liên quan:", err));

    }, [id]);

    const handleAddToCart = async (product) => {
        // console.log("Thêm vào giỏ:", product);
        try {
            const res = await addToCart(product.id, quantity);
            console.log("Đã thêm vào giỏ hàng:", res);
            alert("✅ Đã thêm sản phẩm vào giỏ hàng!");
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
    //Xử lý nút mua
    const handleBuyNow = () => {
        // Tạo dữ liệu cần chuyển sang trang Checkout
        const productData = [
            {
                id: product.id,
                quantity: quantity,
            },
        ];

        // Dẫn hướng sang trang checkout, truyền dữ liệu qua state
        navigate("/checkout", { state: { items: productData } });
    };


    // Tăng/giảm số lượng
    const increase = () => {
        setQuantity((prev) => prev + 1);
    };

    const decrease = () => {
        setQuantity((prev) => (prev > 1 ? prev - 1 : 1));
    };


    if (!product) return <p>Đang tải sản phẩm...</p>;

    const specList = product.description ? product.description.split(",").map(s => s.trim()) : [];

    return (
        <div className={styles.container}>
            <div className={styles.mainSection}>
                <div className={styles.left}>
                    <div className={styles.imageBox}>
                        <img src={product.image_url} alt={product.name} className={styles.image} />
                    </div>
                    <div className={styles.buttons}>
                        <button className={styles.add} onClick={() => handleAddToCart(product)}>Thêm vào giỏ</button>
                        <button className={styles.buy} onClick={handleBuyNow}>Mua ngay</button>
                    </div>
                </div>

                <div className={styles.right}>
                    <h2 className={styles.productName}>{product.name}</h2>
                    <div className={styles.rating}>
                        {[...Array(4)].map((_, i) => <FaStar key={i} className={styles.star} />)}
                        <FaRegStar className={styles.star} />
                        <span>(0 đánh giá)</span>
                    </div>
                    <div className={styles.price}>{product.price.toLocaleString()}đ</div>
                    {/* 👇 Thêm khối tăng giảm số lượng */}
                    <div className={styles.quantityBox}>
                        <button onClick={decrease}>-</button>
                        <span>{quantity}</span>
                        <button onClick={increase}>+</button>
                    </div>
                    <div className={styles.infoRow}>
                        <span>Còn lại: {product.quantity}</span>

                    </div>



                    <h3 className={styles.sectionTitle}>Thông tin chi tiết</h3>
                    <ul className={styles.specList}>
                        {specList.map((spec, idx) => <li key={idx}>{spec}</li>)}
                    </ul>
                </div>
            </div>

            <div className={styles.relatedSection}>
                <h3 className={styles.relatedTitle}>Sản phẩm liên quan</h3>
                <div className={styles.relatedList}>
                    {relatedProducts.map((p) => (
                        <div key={p.id} className={styles.relatedItem}>
                            <img src={p.image_url} alt={p.name} />
                            <p className={styles.relatedName}>{p.name}</p>
                            <p className={styles.relatedPrice}>{p.price.toLocaleString()}đ</p>
                            <button
                                className={styles.viewButton}
                                onClick={() => navigate(`/productdetail/${p.id}`)}
                            >
                                Xem chi tiết
                            </button>
                        </div>
                    ))}
                </div>
            </div>
        </div>
    );
}

export default ProductDetail;
