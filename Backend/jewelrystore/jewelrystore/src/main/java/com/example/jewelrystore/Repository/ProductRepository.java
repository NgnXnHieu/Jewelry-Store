package com.example.jewelrystore.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.jewelrystore.DTO.BestSellerDTO;
import com.example.jewelrystore.Entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    // Optional<Product> findById(Integer id);
    Page<Product> findByCategoryIdAndIdNot(Integer categoryId, Integer excludeId, Pageable pageable);

    Page<Product> findByCategoryId(Integer categoryId, Pageable pageable);

    @Query("""
                    SELECT new  com.example.jewelrystore.DTO.BestSellerDTO(
                p.id, p.name, p.price, p.image_url, SUM(od.quantity) )
            FROM Product p
            JOIN p.orderDetails od
            JOIN od.order o
            WHERE o.status = 'đã nhận hàng'
            GROUP BY p
            HAVING SUM(od.quantity) > 0
            ORDER BY SUM(od.quantity) DESC

                        """)
    Page<BestSellerDTO> findBestSellerProducts(Pageable pageable);

    // Tìm procduct có quantity trong khoảng (A,B)
    Page<Product> findByQuantityGreaterThanAndQuantityLessThan(Long min, Long max, Pageable pageable);

    // Tìm product có quantity = ?
    Page<Product> findByQuantity(Long quantity, Pageable pageable);

    // Tìm product có quantity > ?
    Page<Product> findByQuantityGreaterThan(Long quantity, Pageable pageable);

    // Lấy ra tổng số lượng sản phẩm
    @Query("SELECT SUM(p.quantity) FROM Product p")
    Long getTotalQuantity();

    // Đếm tổng số sản phẩm sản phẩm có quantity trong khoảng (A,B)
    @Query("SELECT COUNT(p) FROM Product p WHERE p.quantity > :minQuantity AND p.quantity < :maxQuantity")
    Long countProductByQuantityBetween(@Param("minQuantity") Long minQuantity, @Param("maxQuantity") Long maxQuantity);

    // Đếm số sản phẩm có quantity đúng bằng X
    @Query("SELECT COUNT(p) FROM Product p WHERE p.quantity = :quantity")
    Long countProductByQuantityBySpecificQuantity(@Param("quantity") Long quantity);

    // Đếm số sản phẩm có quantity lớn hơn X
    @Query("SELECT COUNT(p) FROM Product p WHERE p.quantity > :quantity")
    Long countProductByQuantityGreaterSpecificQuantity(@Param("quantity") Long quantity);

    // Đếm tổng số sản phẩm
    @Query("SELECT COUNT(p) FROM Product p")
    Long countAllProducts();

}
