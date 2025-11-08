package com.example.jewelrystore.Service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.jewelrystore.DTO.OrderDTO;
import com.example.jewelrystore.DTO.OrderSumaryDTO;
import com.example.jewelrystore.Form.OrderForm.OrderCreateForm;
import com.example.jewelrystore.Form.OrderForm.OrderUpdateForm;

public interface OrderService {
    OrderDTO createOrder(OrderCreateForm orderCreateForm);

    OrderDTO updateOrder(Integer id, OrderUpdateForm orderUpdateForm);

    Page<OrderDTO> getAllOrder(Pageable pageable);

    Page<OrderSumaryDTO> getAllOrderSumary(Pageable pageable);

    OrderDTO getOrderById(Integer id);

    void deleteOrder(Integer id);

    List<OrderDTO> getOrdersByUsername(String username);

    OrderDTO createMyOrder(OrderCreateForm orderCreateForm, String username);

    List<OrderDTO> getMyOrderByStatus(String username, String status);
}
