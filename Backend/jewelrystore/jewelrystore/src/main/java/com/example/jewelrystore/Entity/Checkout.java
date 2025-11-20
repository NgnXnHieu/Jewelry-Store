package com.example.jewelrystore.Entity;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.context.annotation.Primary;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "checkout")
public class Checkout {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer id;
    public String qr;
    @Enumerated(EnumType.STRING)
    public CheckoutStatus status;
    @CreationTimestamp
    private LocalDateTime createAt;
    private LocalDateTime qr_create_at;
    @ManyToOne
    @JoinColumn(name = "user_id")
    public User user;
    @OneToMany(mappedBy = "checkout", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Checkout_Item> checkout_Items;
    @OneToOne
    @JoinColumn(name = "order_id")
    private Order order;
    @ManyToOne
    @JoinColumn(name = "address_id")
    private Address address;

    @Enumerated(EnumType.STRING)
    private ProductSource products_source;

    public enum ProductSource {
        FROM_CART,
        DIRECT_BUY
    }

    public enum CheckoutStatus {
        PENDING,
        COMPLETED,
        CANCELED,
        PAYMENT_PENDING
    }
}
