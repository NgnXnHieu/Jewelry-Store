package com.example.jewelrystore.Repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.jewelrystore.DTO.BestSellerDTO;
import com.example.jewelrystore.DTO.ProductDTO;
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

}
