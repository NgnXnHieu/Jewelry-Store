import { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import style from "./Category.module.css"; // dùng file CSS riêng
import { getProductsByCategory } from "../../../api/productApi";
import axiosInstance from "../../../api/axiosInstance";

function Category() {
    const { id } = useParams(); // Lấy categoryId từ URL
    const [products, setProducts] = useState([]);
    const [categoryName, setCategoryName] = useState("Sản phẩm");
    const navigate = useNavigate();
    useEffect(() => {
        const fetchCategoryName = async () => {
            try {
                const res = await axiosInstance.get(`categories/${id}`);
                // console.log("Dữ liệu trả về:", res.data);
                setCategoryName(res.data.name); // nếu backend trả về object có 'name'
            } catch (error) {
                console.error("Lỗi khi lấy category:", error);
            }
        };

        if (id) fetchCategoryName();
    }, [id]);

    useEffect(() => {
        if (!id) return;



        getProductsByCategory(id)
            .then((data) => {
                setProducts(data.content || []);

            })
            .catch((err) => console.error("Lỗi tải sản phẩm:", err));
    }, [id]);

    const handleCardClick = (productId) => {
        navigate(`/productdetail/${productId}`);
    };

    const handleAddToCart = (product) => {
        console.log("Thêm vào giỏ:", product);
    };

    return (
        <div className={style.container}>
            <section>
                <div className={style.display_horizontal}>
                    <h1>{categoryName}</h1>
                    <button className={style.buttonNoBorder}>
                        <img className={style.arrow_button} src="/image/arrow.png" alt="arrow" />
                    </button>
                </div>

                {products.length === 0 ? (
                    <p className={style.noProductText}>
                        Không có sản phẩm trong danh mục này.
                    </p>
                ) : (
                    <div className={style.grid}>
                        {products.map((product) => (
                            <div key={product.id} className={style.card}>
                                <img
                                    src={product.image_url}
                                    alt={product.name}
                                    className={style.image}
                                    onClick={() => handleCardClick(product.id)}
                                />
                                <h3
                                    className={style.name}
                                    onClick={() => handleCardClick(product.id)}
                                >
                                    {product.name}
                                </h3>
                                <p className={style.price}>
                                    {product.price.toLocaleString()}₫
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
                )}
            </section>
        </div>
    );
}

export default Category;
