package com.example.jewelrystore.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.jewelrystore.DTO.PaymentDTO;
import com.example.jewelrystore.Form.PaymentForm.PaymentCreateForm;
import com.example.jewelrystore.Form.PaymentForm.PaymentUpdateForm;

public interface PaymentService {
    PaymentDTO createPayment(PaymentCreateForm Form);

    PaymentDTO updatePayment(Integer id, PaymentUpdateForm Form);

    Page<PaymentDTO> getAllPayment(Pageable pageable);

    PaymentDTO getPaymentById(Integer id);

    void deletePayment(Integer id);
}
