package com.example.jewelrystore.Implement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.jewelrystore.DTO.Inventory_HistoryDTO;
import com.example.jewelrystore.Form.Invetory_HistoryForm.Inventory_HistoryCreateForm;
import com.example.jewelrystore.Form.Invetory_HistoryForm.Invetory_HistoryUpdateForm;
import com.example.jewelrystore.Mapper.Inventory_HistoryMapper;
import com.example.jewelrystore.Repository.Inventory_HistoryRepository;
import com.example.jewelrystore.Service.Inventory_HistoryService;
import com.example.jewelrystore.Entity.Inventory_History;

@Service

public class Inventory_HistoryImpl implements Inventory_HistoryService {
    @Autowired
    private Inventory_HistoryRepository inventory_HistoryRepository;
    @Autowired
    private Inventory_HistoryMapper inventory_HistoryMapper;

    @Override
    public Inventory_HistoryDTO createInventory_History(Inventory_HistoryCreateForm createForm) {
        Inventory_History inventory_History = inventory_HistoryMapper.toEntity(createForm);
        inventory_HistoryRepository.save(inventory_History);
        return inventory_HistoryMapper.toInventory_HistoryDTO(inventory_History);
    }

    @Override
    public Inventory_HistoryDTO updateInventory_History(Integer id, Invetory_HistoryUpdateForm updateForm) {
        Inventory_History existing = inventory_HistoryRepository.findById(id).orElse(null);
        if (existing != null) {
            inventory_HistoryMapper.updateInventory_History(updateForm, existing);
            inventory_HistoryRepository.save(existing);
            return inventory_HistoryMapper.toInventory_HistoryDTO(existing);
        }
        return null;
    }

    @Override
    public Page<Inventory_HistoryDTO> getAllInventory_History(Pageable pageable) {
        return inventory_HistoryRepository.findAll(pageable).map(inventory_HistoryMapper::toInventory_HistoryDTO);
    }

    @Override
    public Inventory_HistoryDTO getInventory_HistoryById(Integer id) {
        return inventory_HistoryRepository.findById(id).map(inventory_HistoryMapper::toInventory_HistoryDTO)
                .orElse(null);
    }

    @Override
    public void deleteInventory_History(Integer id) {
        inventory_HistoryRepository.deleteById(id);
    }

}
