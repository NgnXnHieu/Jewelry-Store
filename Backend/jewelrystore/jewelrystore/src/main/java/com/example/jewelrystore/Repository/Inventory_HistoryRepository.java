package com.example.jewelrystore.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.jewelrystore.Entity.Inventory_History;

@Repository
public interface Inventory_HistoryRepository extends JpaRepository<Inventory_History, Integer> {

}
