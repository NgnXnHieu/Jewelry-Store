package com.example.jewelrystore.Form.OrderForm;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderUpdateForm {

    // @NotNull(message = "Status is required")
    private String status;
    private String address;
    private String phone;

}
