package com.example.jewelrystore.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.jewelrystore.DTO.Inventory_HistoryDTO;
import com.example.jewelrystore.Form.Invetory_HistoryForm.Inventory_HistoryCreateForm;
import com.example.jewelrystore.Form.Invetory_HistoryForm.Invetory_HistoryUpdateForm;

public interface Inventory_HistoryService {
    Inventory_HistoryDTO createInventory_History(Inventory_HistoryCreateForm createForm, String username);

    Inventory_HistoryDTO updateInventory_History(Integer id, Invetory_HistoryUpdateForm updateForm);

    Page<Inventory_HistoryDTO> getAllInventory_History(Pageable pageable);

    Inventory_HistoryDTO getInventory_HistoryById(Integer id);

    void deleteInventory_History(Integer id);
}
