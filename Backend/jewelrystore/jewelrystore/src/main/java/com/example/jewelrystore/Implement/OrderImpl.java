package com.example.jewelrystore.Implement;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.jewelrystore.DTO.OrderDTO;
import com.example.jewelrystore.DTO.OrderSumaryDTO;
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
import jakarta.persistence.EntityNotFoundException;

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
    public List<OrderDTO> getOrdersByUsername(String username, Integer cursor, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User Not Found"));
        List<Order> orders = orderRepository.findMyOrdersCursor(user.getId(), cursor, pageable);
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
    public List<OrderDTO> getMyOrderByStatus(String username, String status, Integer cursor, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User Not Found"));
        List<Order> orders = orderRepository.findMyOrdersByStatusCursor(user.getId(), status, cursor, pageable);
        return orders.stream().map(orderMapper::toOrderDTO).toList();
    }

    @Override
    public Page<OrderSumaryDTO> getAllOrderSumary(Pageable pageable) {
        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "id"));
        return orderRepository.findAll(sortedPageable).map(orderMapper::toOrderSumaryDTO);
    }

    // Lấy tổng tiền các đơn hàng theo đơn vị(ngày, tháng, năm)(Doanh thu)
    @Override
    public Double getTotalAmountByTimeUnit(String time) {
        if (time.equalsIgnoreCase("year")) {
            int thisYear = LocalDate.now().getYear();
            LocalDateTime startDateTime = LocalDateTime.of(thisYear, 1, 1, 0, 0, 0);
            LocalDateTime endDateTime = LocalDateTime.of(thisYear, 12, 31, 23, 59, 59);
            return Objects.requireNonNullElse(
                    orderRepository.getSumTotalAmountByDateAndStatus(startDateTime, endDateTime, "Đã nhận hàng"),
                    0.0);
        } else if (time.equalsIgnoreCase("month")) {
            int thisYear = LocalDate.now().getYear();
            int thisMonth = LocalDate.now().getMonthValue();
            YearMonth yearMonth = YearMonth.of(thisYear, thisMonth);
            LocalDateTime startDateTime = yearMonth.atDay(1).atStartOfDay();
            LocalDateTime endDateTime = yearMonth.atEndOfMonth().atTime(23, 59, 59);
            return Objects.requireNonNullElse(
                    orderRepository.getSumTotalAmountByDateAndStatus(startDateTime, endDateTime, "Đã nhận hàng"),
                    0.0);
        } else if (time.equalsIgnoreCase("day")) {
            LocalDate today = LocalDate.now();
            LocalDateTime startDateTime = today.atStartOfDay();
            LocalDateTime endDateTime = today.atTime(23, 59, 59);
            return Objects.requireNonNullElse(
                    orderRepository.getSumTotalAmountByDateAndStatus(startDateTime, endDateTime, "Đã nhận hàng"),
                    0.0);
        }
        return 0.0;
    }

    // Lấy tổng số lượng đơn hàng theo đơn vị(ngày, tháng, năm)
    @Override
    public Long getcountOrdersByTimeUnit(String time) {
        if (time.equalsIgnoreCase("year")) {
            int thisYear = LocalDate.now().getYear();
            LocalDateTime startDateTime = LocalDateTime.of(thisYear, 1, 1, 0, 0, 0);
            LocalDateTime endDateTime = LocalDateTime.of(thisYear, 12, 31, 23, 59, 59);
            return orderRepository.getCountOrdersByDateAndNotStatus(startDateTime, endDateTime, "Hủy đơn hàng");
        } else if (time.equalsIgnoreCase("month")) {
            int thisYear = LocalDate.now().getYear();
            int thisMonth = LocalDate.now().getMonthValue();
            YearMonth yearMonth = YearMonth.of(thisYear, thisMonth);
            LocalDateTime startDateTime = yearMonth.atDay(1).atStartOfDay();
            LocalDateTime endDateTime = yearMonth.atEndOfMonth().atTime(23, 59, 59);
            return orderRepository.getCountOrdersByDateAndNotStatus(startDateTime, endDateTime, "Hủy đơn hàng");
        } else if (time.equalsIgnoreCase("day")) {
            LocalDate today = LocalDate.now();
            LocalDateTime startDateTime = today.atStartOfDay();
            LocalDateTime endDateTime = today.atTime(23, 59, 59);
            return orderRepository.getCountOrdersByDateAndNotStatus(startDateTime, endDateTime, "Hủy đơn hàng");
        }
        return 0L;
    }

    @Override
    public Double getMaxPriceOfOrdersByTimeUnit(String time) {
        if (time.equalsIgnoreCase("year")) {
            int thisYear = LocalDate.now().getYear();
            LocalDateTime startDateTime = LocalDateTime.of(thisYear, 1, 1, 0, 0, 0);
            LocalDateTime endDateTime = LocalDateTime.of(thisYear, 12, 31, 23, 59, 59);
            return Objects.requireNonNullElse(
                    orderRepository.findMaxTotalAmountByDateAndStatus(startDateTime, endDateTime, "Đã nhận hàng"),
                    0.0);
        } else if (time.equalsIgnoreCase("month")) {
            int thisYear = LocalDate.now().getYear();
            int thisMonth = LocalDate.now().getMonthValue();
            YearMonth yearMonth = YearMonth.of(thisYear, thisMonth);
            LocalDateTime startDateTime = yearMonth.atDay(1).atStartOfDay();
            LocalDateTime endDateTime = yearMonth.atEndOfMonth().atTime(23, 59, 59);
            return Objects.requireNonNullElse(
                    orderRepository.findMaxTotalAmountByDateAndStatus(startDateTime, endDateTime, "Đã nhận hàng"),
                    0.0);
        } else if (time.equalsIgnoreCase("day")) {
            LocalDate today = LocalDate.now();
            LocalDateTime startDateTime = today.atStartOfDay();
            LocalDateTime endDateTime = today.atTime(23, 59, 59);
            return Objects.requireNonNullElse(
                    orderRepository.findMaxTotalAmountByDateAndStatus(startDateTime, endDateTime, "Đã nhận hàng"),
                    0.0);
        }
        return 0.0;
    }

    @Override
    public Double getRevenuePerDay(String time) {
        if (time.equalsIgnoreCase("year")) {
            int thisYear = LocalDate.now().getYear();
            LocalDateTime startDateTime = LocalDateTime.of(thisYear, 1, 1, 0, 0, 0);
            LocalDateTime endDateTime = LocalDateTime.of(thisYear, 12, 31, 23, 59, 59);
            Double revenue = Objects.requireNonNullElse(
                    orderRepository.getSumTotalAmountByDateAndStatus(startDateTime, endDateTime, "Đã nhận hàng"),
                    0.0);
            long daysBetween = ChronoUnit.DAYS.between(startDateTime.toLocalDate(), LocalDate.now()) + 1;
            return revenue / daysBetween;
        } else if (time.equalsIgnoreCase("month")) {
            int thisYear = LocalDate.now().getYear();
            int thisMonth = LocalDate.now().getMonthValue();
            YearMonth yearMonth = YearMonth.of(thisYear, thisMonth);
            LocalDateTime startDateTime = yearMonth.atDay(1).atStartOfDay();
            LocalDateTime endDateTime = yearMonth.atEndOfMonth().atTime(LocalTime.MAX);
            Double revenue = Objects.requireNonNullElse(
                    orderRepository.getSumTotalAmountByDateAndStatus(startDateTime, endDateTime, "Đã nhận hàng"),
                    0.0);
            long daysBetween = ChronoUnit.DAYS.between(startDateTime.toLocalDate(), LocalDate.now()) + 1;
            return revenue / daysBetween;
        } else if (time.equalsIgnoreCase("day")) {
            LocalDate today = LocalDate.now();
            LocalDateTime startDateTime = today.atStartOfDay();
            LocalDateTime endDateTime = today.atTime(23, 59, 59);
            return Objects.requireNonNullElse(
                    orderRepository.getSumTotalAmountByDateAndStatus(startDateTime, endDateTime, "Đã nhận hàng"),
                    0.0);
        }
        return 0.0;
    }

    // Đếm đơn hàng chưa giao thành công
    @Override
    public Long getCountOrderNotDeliveredByUnitTime(String time) {
        if (time.equalsIgnoreCase("year")) {
            int thisYear = LocalDate.now().getYear();
            LocalDateTime starDateTime = LocalDate.of(thisYear, 1, 1).atStartOfDay();
            LocalDateTime endDateTime = LocalDate.of(thisYear, 12, 31).atTime(LocalTime.MAX);
            List<String> statuses = new ArrayList<>();
            statuses.add("Đang xử lý");
            statuses.add("Chờ xác nhận");
            statuses.add("Đã xác nhận");
            statuses.add("Đang giao hàng");
            return Objects.requireNonNullElse(
                    orderRepository.getCountOrdersByDateAndStatuses(starDateTime, endDateTime, statuses), 0L);
        } else if (time.equalsIgnoreCase("month")) {
            YearMonth yearMonth = YearMonth.now();
            LocalDateTime startDateTime = (yearMonth).atDay(1).atStartOfDay();
            LocalDateTime endDateTime = yearMonth.atEndOfMonth().atTime(23, 59, 59);
            List<String> statuses = new ArrayList<>();
            statuses.add("Đang xử lý");
            statuses.add("Chờ xác nhận");
            statuses.add("Đã xác nhận");
            statuses.add("Đang giao hàng");
            return Objects.requireNonNullElse(
                    orderRepository.getCountOrdersByDateAndStatuses(startDateTime, endDateTime, statuses), 0L);
        } else {
            LocalDate today = LocalDate.now();
            LocalDateTime startDateTime = today.atStartOfDay();
            LocalDateTime endDateTime = today.atTime(23, 59, 59);
            List<String> statuses = new ArrayList<>();
            statuses.add("Đang xử lý");
            statuses.add("Chờ xác nhận");
            statuses.add("Đã xác nhận");
            statuses.add("Đang giao hàng");
            return Objects.requireNonNullElse(
                    orderRepository.getCountOrdersByDateAndStatuses(startDateTime, endDateTime, statuses), 0L);
        }
    }

    // Đếm đơn hàng đã giao thành công
    @Override
    public Long getCountOrderDeliveredByUnitTime(String time) {
        if (time.equalsIgnoreCase("year")) {
            int thisYear = LocalDate.now().getYear();
            LocalDateTime starDateTime = LocalDate.of(thisYear, 1, 1).atStartOfDay();
            LocalDateTime endDateTime = LocalDate.of(thisYear, 12, 31).atTime(LocalTime.MAX);
            return Objects.requireNonNullElse(
                    orderRepository.getCountOrdersByDateAndStatus(starDateTime, endDateTime, "Đã nhận hàng"), 0L);
        } else if (time.equalsIgnoreCase("month")) {
            YearMonth yearMonth = YearMonth.now();
            LocalDateTime startDateTime = (yearMonth).atDay(1).atStartOfDay();
            LocalDateTime endDateTime = yearMonth.atEndOfMonth().atTime(23, 59, 59);
            return Objects.requireNonNullElse(
                    orderRepository.getCountOrdersByDateAndStatus(startDateTime, endDateTime, "Đã nhận hàng"), 0L);
        } else {
            LocalDate today = LocalDate.now();
            LocalDateTime startDateTime = today.atStartOfDay();
            LocalDateTime endDateTime = today.atTime(23, 59, 59);
            return Objects.requireNonNullElse(
                    orderRepository.getCountOrdersByDateAndNotStatus(startDateTime, endDateTime, "Đã nhận hàng"),
                    0L);
        }
    }

    // Đếm đơn hàng trên unit time
    @Override
    public Double getCountOrderPerDayByUnitTime(String time) {
        if (time.equalsIgnoreCase("year")) {
            int thisYear = LocalDate.now().getYear();
            LocalDateTime startDateTime = LocalDateTime.of(thisYear, 1, 1, 0, 0, 0);
            LocalDateTime endDateTime = LocalDateTime.of(thisYear, 12, 31, 23, 59, 59);
            Long count = Objects.requireNonNullElse(
                    orderRepository.getCountOrdersByDateAndNotStatus(startDateTime, endDateTime, "Đã nhận hàng"),
                    0L);
            long daysBetween = ChronoUnit.DAYS.between(startDateTime.toLocalDate(), LocalDate.now()) + 1;
            return (double) count / daysBetween;
        } else if (time.equalsIgnoreCase("month")) {
            int thisYear = LocalDate.now().getYear();
            int thisMonth = LocalDate.now().getMonthValue();
            YearMonth yearMonth = YearMonth.of(thisYear, thisMonth);
            LocalDateTime startDateTime = yearMonth.atDay(1).atStartOfDay();
            LocalDateTime endDateTime = yearMonth.atEndOfMonth().atTime(LocalTime.MAX);
            Long count = Objects.requireNonNullElse(
                    orderRepository.getCountOrdersByDateAndNotStatus(startDateTime, endDateTime, "Đã nhận hàng"),
                    0L);
            long daysBetween = ChronoUnit.DAYS.between(startDateTime.toLocalDate(), LocalDate.now()) + 1;
            return (double) count / daysBetween;
        } else if (time.equalsIgnoreCase("day")) {
            LocalDate today = LocalDate.now();
            LocalDateTime startDateTime = today.atStartOfDay();
            LocalDateTime endDateTime = today.atTime(23, 59, 59);
            return (double) Objects.requireNonNullElse(
                    orderRepository.getCountOrdersByDateAndNotStatus(startDateTime, endDateTime, "Đã nhận hàng"),
                    0L);
        }
        return 0.0;
    }

    @Override
    public List<Double> getSumTotalPriceByYearsByDateAndStatus() {
        int thisYear = LocalDate.now().getYear();
        List<Double> results = new ArrayList<>();
        for (int i = 3; i >= 0; i--) {
            LocalDateTime startDateTime = LocalDateTime.of(thisYear - i, 1, 1, 0, 0, 0);
            LocalDateTime endDateTime = LocalDateTime.of(thisYear - i, 12, 31, 23, 59, 59);
            results.add(Objects.requireNonNullElse(
                    orderRepository.getSumTotalAmountByDateAndStatus(startDateTime, endDateTime, "Đã nhận hàng"),
                    0.0));
        }
        return results;
    }

    @Override
    public List<Double> getSumTotalPriceByMonthsByDateAndStatus() {
        List<Double> result = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            YearMonth yearMonth = YearMonth.now().minusMonths(i);
            LocalDateTime startDateTime = yearMonth.atDay(1).atStartOfDay();
            LocalDateTime endDateTime = yearMonth.atEndOfMonth().atTime(23, 59, 59);
            result.add(Objects.requireNonNullElse(
                    orderRepository.getSumTotalAmountByDateAndStatus(startDateTime, endDateTime, "Đã nhận hàng"),
                    0.0));
        }
        Collections.reverse(result);
        return result;

    }

    @Override
    public List<Double> getSumTotalPriceByDaysByDateAndStatus() {
        List<Double> result = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            LocalDate thisDay = LocalDate.now().minusDays(i);
            LocalDateTime startDateTime = thisDay.atStartOfDay();
            LocalDateTime endDateTime = thisDay.atTime(23, 59, 59);
            result.add(Objects.requireNonNullElse(
                    orderRepository.getSumTotalAmountByDateAndStatus(startDateTime, endDateTime, "Đã nhận hàng"),
                    0.0));

        }
        Collections.reverse(result);
        return result;
    }

    @Override
    public List<Long> countOrdersByYears() {
        List<Long> results = new ArrayList<>();
        int thisYear = LocalDate.now().getYear();
        for (int i = 3; i >= 0; i--) {
            LocalDateTime startDateTime = LocalDateTime.of(thisYear - i, 1, 1, 0, 0, 0);
            LocalDateTime endDateTime = LocalDateTime.of(thisYear - i, 12, 31, 23, 59, 59);
            results.add(orderRepository.getCountOrdersByDateAndNotStatus(startDateTime, endDateTime, "Hủy đơn hàng"));
        }
        return results;
    }

    @Override
    public List<Long> countOrdersByMonths() {
        List<Long> results = new ArrayList<>();
        for (int i = 11; i >= 0; i--) {
            YearMonth yearMonth = YearMonth.now().minusMonths(i);
            LocalDateTime startDateTime = yearMonth.atDay(1).atStartOfDay();
            LocalDateTime endDateTime = yearMonth.atEndOfMonth().atTime(23, 59, 59);
            results.add(orderRepository.getCountOrdersByDateAndNotStatus(startDateTime, endDateTime, "Hủy đơn hàng"));
        }
        return results;
    }

    @Override
    public List<Long> countOrdersByDays() {
        List<Long> results = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate day = LocalDate.now().minusDays(i);
            LocalDateTime startDateTime = day.atStartOfDay();
            LocalDateTime endDateTime = day.atTime(23, 59, 59);
            results.add(orderRepository.getCountOrdersByDateAndNotStatus(startDateTime, endDateTime, "Hủy đơn hàng"));
        }
        return results;
    }

}
