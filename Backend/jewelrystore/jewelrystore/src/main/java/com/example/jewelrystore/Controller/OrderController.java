package com.example.jewelrystore.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.jewelrystore.DTO.OrderDTO;
import com.example.jewelrystore.DTO.OrderSumaryDTO;
import com.example.jewelrystore.Form.OrderForm.OrderCreateForm;
import com.example.jewelrystore.Form.OrderForm.OrderUpdateForm;
import com.example.jewelrystore.Service.OrderService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/orders")
@Validated
public class OrderController {
    @Autowired
    OrderService service;

    @GetMapping
    public Page<OrderDTO> getAll(Pageable pageable) {
        return service.getAllOrder(pageable);
    }

    @GetMapping("/orderSumaries")
    public Page<OrderSumaryDTO> getOrderSumarys(Pageable pageable) {
        return service.getAllOrderSumary(pageable);
    }

    @GetMapping("/{id}")
    public OrderDTO getOrderById(@PathVariable Integer id) {
        return service.getOrderById(id);
    }

    @PostMapping
    public ResponseEntity create(@RequestBody @Valid OrderCreateForm addressCreateForm) {
        OrderDTO created = service.createOrder(addressCreateForm);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity update(@PathVariable Integer id, @RequestBody OrderUpdateForm addressUpdateForm) {
        OrderDTO updated = service.updateOrder(id, addressUpdateForm);
        return (updated != null) ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Integer id) {
        service.deleteOrder(id);
        return "Xóa thành công";
    }

    @GetMapping("/myOrders")
    public List<OrderDTO> getodersByUsername(@AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) Integer cursor,
            @RequestParam(defaultValue = "10") int limit) {
        if (userDetails == null) {
            System.out.println("KHONG CO QUYEN");
            return null;
        }
        return service.getOrdersByUsername(userDetails.getUsername(), cursor, limit);
    }

    @PostMapping("/myOrder")
    public ResponseEntity createMyOrder(@RequestBody @Valid OrderCreateForm orderCreateForm,
            @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        OrderDTO created = service.createMyOrder(orderCreateForm, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/myOrdersByStatus")
    public List<OrderDTO> getMyOrderByStatus(@AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String status,
            @RequestParam(required = false) Integer cursor,
            @RequestParam(defaultValue = "10") int limit) {
        if (userDetails == null)
            return null;
        return service.getMyOrderByStatus(userDetails.getUsername(), status, cursor, limit);
    }

    // Lấy số lượng đơn hàng theo time unit (day, month, year)
    @GetMapping("/quantity/count/unitTime")
    public ResponseEntity<Long> getOrderCountByTime(@RequestParam("time") String timeUnit) {
        Long count = service.getcountOrdersByTimeUnit(timeUnit);
        return ResponseEntity.ok(count);
    }

    // Lấy tổng giá trị đơn hàng theo time unit (day, month, year)
    @GetMapping("/sumByUnitTime")
    public ResponseEntity<Double> getOrderSumByTime(@RequestParam("time") String timeUnit) {
        Double total = service.getTotalAmountByTimeUnit(timeUnit);
        return ResponseEntity.ok(total);
    }

    // Lấy giá của đơn hàng có giá cao nhất theo đơn vị thời gian
    @GetMapping("maxPriceOfOdersByTimeUnit")
    public Double getmaxPriceOfOdersByTimeUnit(@RequestParam("time") String timeUnit) {
        return service.getMaxPriceOfOrdersByTimeUnit(timeUnit);
    }

    // Lấy ra trung bình doanh thu/ngày
    @GetMapping("revenuePerDay")
    public Double getRevenuePerDay(@RequestParam String time) {
        return service.getRevenuePerDay(time);
    }

    // Đếm số đơn hàng/ngày
    @GetMapping("perDay/count/unitTime")
    public Double getCountOrderPerDay(@RequestParam String time) {
        return service.getCountOrderPerDayByUnitTime(time);
    }

    // Đếm số đơn hàng đang xử lý
    @GetMapping("/resolved/count/unitTime")
    public Long getcountResolvedOrdersByUnitTime(@RequestParam String time) {
        return service.getCountOrderDeliveredByUnitTime(time);
    }

    // Đếm số đơn hàng đã xử lý
    @GetMapping("/unresolved/count/unitTime")
    public Long getcountNotResolvedOrdersByUnitTime(@RequestParam String time) {
        return service.getCountOrderNotDeliveredByUnitTime(time);
    }

    // Lấy ra danh sách doanh thu theo 4 năm gần đây
    @GetMapping("sumTotalPricesByYears")
    public List<Double> getSumTotalPriceByYearByDateAndStatus() {
        return service.getSumTotalPriceByYearsByDateAndStatus();
    }

    // Lấy ra danh sách doanh thu theo các tháng trong năm
    @GetMapping("sumTotalPricesByMonths")
    public List<Double> getSumTotalPriceByMonthsByDateAndStatus() {
        return service.getSumTotalPriceByMonthsByDateAndStatus();
    }

    // Lấy ra danh sách doanh thu theo 7 ngày gần nhất
    @GetMapping("sumTotalPricesByDays")
    public List<Double> getSumTotalPriceByDaysByDateAndStatus() {
        return service.getSumTotalPriceByDaysByDateAndStatus();
    }

    // Lấy ra số lượng đơn hàng theo 4 năm gần đây
    @GetMapping("countOrdersByYears")
    public List<Long> getCountOrdersByYears() {
        return service.countOrdersByYears();
    }

    // Lấy ra số lượng đơn hàng theo 4 năm gần đây
    @GetMapping("countOrdersByMonths")
    public List<Long> getCountOrdersByMonths() {
        return service.countOrdersByMonths();
    }

    // Lấy ra số lượng đơn hàng theo 4 năm gần đây
    @GetMapping("countOrdersByDays")
    public List<Long> getCountOrdersByDays() {
        return service.countOrdersByDays();
    }

}
