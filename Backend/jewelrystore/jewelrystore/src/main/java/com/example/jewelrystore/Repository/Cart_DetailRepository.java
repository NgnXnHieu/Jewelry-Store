package com.example.jewelrystore.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.jewelrystore.Entity.Cart_Detail;

@Repository
public interface Cart_DetailRepository extends JpaRepository<Cart_Detail, Integer> {
    Page<Cart_Detail> findByCartId(Integer cartId, Pageable pageable);

    Optional<Cart_Detail> findByCartIdAndProductId(Integer cart_id, Integer product_id);

    // "Tìm cho tao những thằng nào thuộc giỏ hàng này VÀ có ID sản phẩm nằm trong
    // danh sách này"
    List<Cart_Detail> findByCartIdAndProductIdIn(Integer cartId, List<Integer> productIds);
}
