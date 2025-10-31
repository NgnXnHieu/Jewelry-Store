import { useState, useEffect } from "react";
import { Link, useNavigate } from "react-router-dom";
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

    const [listProducts, setProducts] = useState([]);
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
    const handleAddToCart = (product) => {
        console.log("Thêm vào giỏ:", product);
        // ở đây bạn có thể gọi redux hoặc context để thêm sản phẩm vào giỏ
    };

    return (
        <div className={style.container}>
            {/* Top sản phẩm */}
            <section>
                <div className={style.display_horizontal}>
                    <h1
                        style={{ cursor: "pointer" }}
                        onClick={handleMoreButton}>Top sản phẩm</h1>
                    <button className={style.buttonNoBorder}>
                        <img className={style.arrow_button} src="/image/arrow.png" onClick={handleMoreButton} alt="arrow" />
                    </button>
                </div>
                <div className={style.grid}>
                    {products.map(product => (
                        <div key={product.id} className={style.card}>
                            <img
                                src={product.image}
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
                            <p className={style.price}>{product.price.toLocaleString()}₫</p>
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

            {/* Danh mục sản phẩm */}
            <section>
                <div className={style.display_horizontal}>
                    <h1>Danh mục sản phẩm</h1>
                    <button className={style.buttonNoBorder}>
                        <img className={style.arrow_button} src="/image/arrow.png" alt="arrow" />
                    </button>
                </div>
                <div className={style.grid}>
                    {listProducts.map(product => (
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
                            <p className={style.price}>{product.price.toLocaleString()}₫</p>
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

export default Home;
