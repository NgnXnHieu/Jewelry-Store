package com.example.jewelrystore.Implement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.jewelrystore.DTO.OrderDTO;
import com.example.jewelrystore.Entity.Order;
import com.example.jewelrystore.Entity.Order_Detail;
import com.example.jewelrystore.Entity.Product;
import com.example.jewelrystore.Entity.User;
import com.example.jewelrystore.Form.OrderForm.OrderCreateForm;
import com.example.jewelrystore.Form.OrderForm.OrderUpdateForm;
import com.example.jewelrystore.Mapper.OrderMapper;
import com.example.jewelrystore.Repository.OrderRepository;
import com.example.jewelrystore.Repository.ProductRepository;
import com.example.jewelrystore.Repository.UserRepository;
import com.example.jewelrystore.Service.OrderService;

import io.micrometer.common.lang.NonNull;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Service

public class OrderImpl implements OrderService {
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    OrderMapper orderMapper;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ProductRepository productRepository;

    @Override
    public OrderDTO createOrder(OrderCreateForm orderCreateForm) {
        Order order = orderMapper.toEntity(orderCreateForm);
        orderRepository.save(order);
        return orderMapper.toOrderDTO(order);
    }

    @Override
    public OrderDTO updateOrder(Integer id, OrderUpdateForm orderUpdateForm) {
        Order existing = orderRepository.findById(id).orElse(null);
        if (existing != null) {
            orderMapper.updateOrder(orderUpdateForm, existing);
            orderRepository.save(existing);
            return orderMapper.toOrderDTO(existing);
        }
        return null;
    }

    @Override
    public Page<OrderDTO> getAllOrder(Pageable pageable) {
        return orderRepository.findAll(pageable).map(orderMapper::toOrderDTO);
    }

    @Override
    public OrderDTO getOrderById(Integer id) {
        return orderRepository.findById(id).map(orderMapper::toOrderDTO).orElse(null);
    }

    @Override
    public void deleteOrder(Integer id) {
        orderRepository.deleteById(id);
    }

    @Override
    public List<OrderDTO> getOrdersByUsername(String username) {
        List<Order> orders = orderRepository.findByUserUsername(username);
        return orders.stream().map(orderMapper::toOrderDTO).toList();
    }

    // Xử lý tạo đơn hàng khi mua hàng
    @Override
    public OrderDTO createMyOrder(OrderCreateForm orderCreateForm, String username) {
        Order order = orderMapper.toEntity(orderCreateForm);
        // Xử lý lấy danh sách order details từ idAndQuantityList và tính tổng giá,số
        // lượng của các order details
        List<Map> idAndQuantityList = orderCreateForm.getIdAndQuantityList();
        Long totalQuantity = 0L;
        Double totalAmount = 0.0;
        List<Order_Detail> orderDetails = new ArrayList<>();
        for (Map item : idAndQuantityList) {
            Integer productId = (Integer) item.get("productId");
            Long quantity = Long.valueOf((Integer) item.get("quantity"));
            // Tạo Order_Detail từ productId và quantity
            Order_Detail orderDetail = new Order_Detail();
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new EntityNotFoundException("Product not found"));
            orderDetail.setProduct(product);
            orderDetail.setQuantity(quantity);
            orderDetail.setOrder(order);
            Double price = product.getPrice();
            orderDetail.setPrice(price);
            orderDetail.setTotalPrice(price * quantity);
            // Add vào orderDetails
            orderDetails.add(orderDetail);
            // Tính tổng số lượng và tổng tiền
            totalQuantity += quantity;
            totalAmount += orderDetail.getTotalPrice();
        }
        order.setOrderDetails(orderDetails);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        order.setUser(user);
        order.setTotalAmount(totalAmount);
        order.setQuantity(totalQuantity);
        order.setStatus("Chờ xác nhận");
        orderRepository.save(order);
        return orderMapper.toOrderDTO(order);
    }

    @Override
    public List<OrderDTO> getMyOrderByStatus(String username, String status) {
        List<Order> orders = orderRepository.findByUserUsernameAndStatus(username, status);
        return orders.stream().map(orderMapper::toOrderDTO).toList();
    }

}
