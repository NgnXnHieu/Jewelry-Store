package com.example.jewelrystore.Implement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.jewelrystore.DTO.PaymentDTO;
import com.example.jewelrystore.Entity.Payment;
import com.example.jewelrystore.Form.PaymentForm.PaymentCreateForm;
import com.example.jewelrystore.Form.PaymentForm.PaymentUpdateForm;
import com.example.jewelrystore.Mapper.PaymentMapper;
import com.example.jewelrystore.Repository.PaymentRepository;
import com.example.jewelrystore.Service.PaymentService;

@Service

public class PaymentImpl implements PaymentService {
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private PaymentMapper paymentMapper;

    @Override
    public PaymentDTO createPayment(PaymentCreateForm Form) {
        Payment payment = paymentMapper.toEntity(Form);
        paymentRepository.save(payment);
        return paymentMapper.toPaymentDTO(payment);
    }

    @Override
    public PaymentDTO updatePayment(Integer id, PaymentUpdateForm Form) {
        Payment existing = paymentRepository.findById(id).orElse(null);
        if (existing != null) {
            paymentMapper.updatePayment(Form, existing);
            paymentRepository.save(existing);
            return paymentMapper.toPaymentDTO(existing);
        }
        return null;
    }

    @Override
    public Page<PaymentDTO> getAllPayment(Pageable pageable) {
        return paymentRepository.findAll(pageable).map(paymentMapper::toPaymentDTO);
    }

    @Override
    public PaymentDTO getPaymentById(Integer id) {
        return paymentRepository.findById(id).map(paymentMapper::toPaymentDTO).orElse(null);
    }

    @Override
    public void deletePayment(Integer id) {
        paymentRepository.deleteById(id);
    }

}
