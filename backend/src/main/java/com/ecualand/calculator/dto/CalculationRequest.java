package com.ecualand.calculator.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * DTO representing a request to calculate the final amount and commission.
 * It contains the original value to send, the commission percentage to charge
 * and any fixed extra fee (transaction cost).
 */
public class CalculationRequest {

    /**
     * Amount the user wishes to send (value before applying commission).
     */
    @NotNull
    @PositiveOrZero(message = "Value must be positive")
    private Double value;

    /**
     * Commission percentage to charge on the transaction (0–100).
     */
    @NotNull
    @PositiveOrZero(message = "Commission must be positive")
    private Double commission;

    /**
     * Fixed transactional fee (extra amount added before computing commission).
     */
    @NotNull
    @PositiveOrZero(message = "Extra fee must be positive")
    private Double extra;

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Double getCommission() {
        return commission;
    }

    public void setCommission(Double commission) {
        this.commission = commission;
    }

    public Double getExtra() {
        return extra;
    }

    public void setExtra(Double extra) {
        this.extra = extra;
    }
}