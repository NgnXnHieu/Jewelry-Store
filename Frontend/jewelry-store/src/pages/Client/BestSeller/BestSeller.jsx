// src/pages/BestSeller/BestSeller.jsx
import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import style from "./BestSeller.module.css"; // có thể dùng style của Home
import axios from "axios";

function BestSeller() {
    const [bestSellers, setBestSellers] = useState([]);
    const navigate = useNavigate();

    useEffect(() => {
        axios.get("http://localhost:8080/api/products/bestSeller")
            .then(res => {
                setBestSellers(res.data.content || res.data);
            })
            .catch(err => console.error("Lỗi khi tải dữ liệu:", err));
    }, []);

    const handleCardClick = (id) => {
        navigate(`/productdetail/${id}`);
    };

    const handleAddToCart = (product) => {
        console.log("Thêm vào giỏ:", product);
        // Gọi redux/context để thêm sản phẩm vào giỏ
    };

    return (
        <div className={style.container}>
            <section>
                <div className={style.display_horizontal}>
                    <h1>Best Seller</h1>
                </div>
                <div className={style.grid}>
                    {bestSellers.map(product => (
                        <div key={product.id} className={style.card}>
                            <img
                                src={product.image_url}
                                alt={product.name}
                                className={style.image}
                                onClick={() => handleCardClick(product.id)}
                                style={{ cursor: "pointer" }}
                            />
                            <h3
                                className={style.name}
                                onClick={() => handleCardClick(product.id)}
                                style={{ cursor: "pointer" }}
                            >
                                {product.name}
                            </h3>
                            <p className={style.price}>
                                {product.price.toLocaleString()}₫
                            </p>
                            <p className={style.sold}>
                                Lượt bán: {product.totalQuantity || 0}
                            </p>
                            <button
                                className={style.button}
                                onClick={() => handleAddToCart(product)}
                            >
                                Thêm vào giỏ
                            </button>
                        </div>
                    ))}
                </div>
            </section>
        </div>
    );
}

export default BestSeller;
