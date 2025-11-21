package com.example.jewelrystore.Service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.jewelrystore.DTO.Order_DetailDTO;
import com.example.jewelrystore.Form.Order_DetailForm.Order_DetailCreateForm;
import com.example.jewelrystore.Form.Order_DetailForm.Order_DetailUpdateForm;

public interface Order_DetailService {
    Order_DetailDTO createOrder_Detail(Order_DetailCreateForm createForm);

    Order_DetailDTO updateOrder_Detail(Integer id, Order_DetailUpdateForm updateForm);

    List<Order_DetailDTO> getOrder_DetailsByOrderId(Integer orderId);

    Page<Order_DetailDTO> getAllOrder_Detail(Pageable pageable);

    Order_DetailDTO getOrder_DetailById(Integer id);

    void deleteOrder_Detail(Integer id);

}
