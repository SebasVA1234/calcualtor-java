package com.ecualand.calculator.service;

import org.springframework.stereotype.Service;

/**
 * Service responsible for the core calculation logic of the Ecualand
 * calculator. It computes the final amount and commission based on the
 * user's inputs: original value, commission percentage and extra fee.
 */
@Service
public class CalculationService {

    /**
     * Calculates the final value after applying the commission percentage and
     * adding the extra fee. The formula used is:
     * <pre>
     * finalValue = (value + extra) / (1 - commissionPercentage)
     * commissionAmount = finalValue - value - extra
     * </pre>
     * where commissionPercentage = commission / 100.
     *
     * @param value      the original amount the user wants to send
     * @param commission the commission percentage (e.g. 15 for 15%)
     * @param extra      the fixed extra fee
     * @return an array of two doubles: [finalValue, commissionAmount]
     */
    public double[] calculate(double value, double commission, double extra) {
        double commissionPercentage = commission / 100.0;
        double finalValue = (value + extra) / (1.0 - commissionPercentage);
        double commissionAmount = finalValue - value - extra;
        return new double[]{finalValue, commissionAmount};
    }
}