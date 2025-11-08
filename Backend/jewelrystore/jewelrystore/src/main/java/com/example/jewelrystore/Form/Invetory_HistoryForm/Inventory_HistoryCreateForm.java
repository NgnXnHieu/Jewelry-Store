package com.example.jewelrystore.Form.Invetory_HistoryForm;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Inventory_HistoryCreateForm {

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Long importQuantity;

    @NotNull(message = "Product ID is required")
    private Integer productId;

    @NotNull(message = "User ID is required")
    private Integer userId;

}
