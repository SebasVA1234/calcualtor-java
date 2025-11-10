package com.ecualand.calculator.dto;

/**
 * DTO representing the response of the calculation. It contains the
 * computed final amount (including commission and extra fee) and the
 * commission amount itself.
 */
public class CalculationResponse {

    /**
     * Total amount that should be charged to the user, after applying
     * commission and adding the extra fee.
     */
    private final double finalValue;

    /**
     * The commission amount applied (finalValue - value - extra).
     */
    private final double commissionAmount;

    public CalculationResponse(double finalValue, double commissionAmount) {
        this.finalValue = finalValue;
        this.commissionAmount = commissionAmount;
    }

    public double getFinalValue() {
        return finalValue;
    }

    public double getCommissionAmount() {
        return commissionAmount;
    }
}