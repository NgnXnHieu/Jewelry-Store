package com.example.jewelrystore.Entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import io.micrometer.common.lang.NonNull;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "inventory_history")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Inventory_History {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @CreationTimestamp
    private LocalDateTime date;
    @NonNull
    private Long importQuantity;
    @NonNull
    private Long currentQuantity;
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Override
    public String toString() {
        return "Inventory_History [id=" + id + ", date=" + date + ", importQuantity=" + importQuantity
                + ", currentQuantity=" + currentQuantity + ", product=" + product + ", user=" + user + "]";
    }

}
