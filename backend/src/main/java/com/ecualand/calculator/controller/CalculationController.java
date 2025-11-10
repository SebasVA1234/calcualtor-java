package com.ecualand.calculator.controller;

import com.ecualand.calculator.dto.CalculationRequest;
import com.ecualand.calculator.dto.CalculationResponse;
import com.ecualand.calculator.service.CalculationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller exposing endpoints for performing financial calculations.
 * Clients send a JSON payload with the base value, commission percentage and
 * extra fee; the server returns the final amount and commission.
 */
@RestController
@RequestMapping("/api")
public class CalculationController {

    private final CalculationService calculationService;

    public CalculationController(CalculationService calculationService) {
        this.calculationService = calculationService;
    }

    @PostMapping("/calculate")
    public CalculationResponse calculate(@Valid @RequestBody CalculationRequest request) {
        double[] values = calculationService.calculate(request.getValue(), request.getCommission(), request.getExtra());
        double finalValue = Math.round(values[0] * 100.0) / 100.0;
        double commissionAmount = Math.round(values[1] * 100.0) / 100.0;
        return new CalculationResponse(finalValue, commissionAmount);
    }
}