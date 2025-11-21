package com.example.jewelrystore.Implement;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.jewelrystore.DTO.Order_DetailDTO;
import com.example.jewelrystore.Entity.Order;
import com.example.jewelrystore.Entity.Order_Detail;
import com.example.jewelrystore.Form.Order_DetailForm.Order_DetailCreateForm;
import com.example.jewelrystore.Form.Order_DetailForm.Order_DetailUpdateForm;
import com.example.jewelrystore.Mapper.Order_DetailMapper;
import com.example.jewelrystore.Repository.OrderRepository;
import com.example.jewelrystore.Repository.Order_DetailRepository;
import com.example.jewelrystore.Service.Order_DetailService;

@Service

public class Order_DetailImpl implements Order_DetailService {
    @Autowired
    private Order_DetailRepository order_DetailRepository;
    @Autowired
    private Order_DetailMapper order_DetailMapper;
    @Autowired
    private OrderRepository orderRepository;

    @Override
    public Order_DetailDTO createOrder_Detail(Order_DetailCreateForm createForm) {
        Order_Detail order_Detail = order_DetailMapper.toEntity(createForm);
        order_DetailRepository.save(order_Detail);
        return order_DetailMapper.toOrder_DetailDTO(order_Detail);
    }

    @Override
    public Order_DetailDTO updateOrder_Detail(Integer id, Order_DetailUpdateForm updateForm) {
        Order_Detail existing = order_DetailRepository.findById(id).orElse(null);
        if (existing != null) {
            order_DetailMapper.updateOrder_Detail(updateForm, existing);
            order_DetailRepository.save(existing);
            return order_DetailMapper.toOrder_DetailDTO(existing);
        }
        return null;
    }

    @Override
    public Page<Order_DetailDTO> getAllOrder_Detail(Pageable pageable) {
        return order_DetailRepository.findAll(pageable).map(order_DetailMapper::toOrder_DetailDTO);
    }

    @Override
    public Order_DetailDTO getOrder_DetailById(Integer id) {
        return order_DetailRepository.findById(id).map(order_DetailMapper::toOrder_DetailDTO).orElse(null);
    }

    @Override
    public void deleteOrder_Detail(Integer id) {
        order_DetailRepository.deleteById(id);
    }

    @Override
    public List<Order_DetailDTO> getOrder_DetailsByOrderId(Integer orderId) {
        return order_DetailRepository.findByOrderId(orderId).stream()
                .map(order_DetailMapper::toOrder_DetailDTO)
                .toList();
    }

}
