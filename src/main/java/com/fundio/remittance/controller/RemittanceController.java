
package com.fundio.remittance.controller;

import java.math.BigDecimal;

import org.springframework.web.bind.annotation.*;

import com.fundio.remittance.dto.CurrencyConversionResponse;
import com.fundio.remittance.dto.ExchangeRateResponse;
import com.fundio.remittance.service.RemittanceService;

@RestController
@RequestMapping("/api/v1/remittance")
public class RemittanceController {

    private final RemittanceService service;

    public RemittanceController(RemittanceService service) {
        this.service = service;
    }

    @GetMapping("/health")
    public String health() {
        return "Remittance Rates API is running!";
    }

    @GetMapping("/rate")
    public ExchangeRateResponse getRate(
            @RequestParam String from,
            @RequestParam String to) {

        return service.getRate(from, to);
    }

    @GetMapping("/convert")
    public CurrencyConversionResponse convert(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam BigDecimal amount) {

        return service.convert(from, to, amount);
    }
}
