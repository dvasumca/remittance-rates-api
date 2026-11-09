
package com.fundio.remittance.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Service;

import com.fundio.remittance.client.FrankfurterClient;
import com.fundio.remittance.dto.CurrencyConversionResponse;
import com.fundio.remittance.dto.ExchangeRateResponse;

@Service
public class RemittanceService {

    private final FrankfurterClient client;

    public RemittanceService(FrankfurterClient client) {
        this.client = client;
    }

    public ExchangeRateResponse getRate(String from, String to) {

        String source = normalize(from);
        String target = normalize(to);

        var response = client.getRate(source, target);
        BigDecimal rate = response.rate();

        return new ExchangeRateResponse(
                source, target, rate,
                response.date(), "Frankfurter"
        );
    }

    public CurrencyConversionResponse convert(
            String from, String to, BigDecimal amount) {

        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException(
                    "Amount must be greater than zero");
        }

        var rateResponse = getRate(from, to);

        BigDecimal converted = amount
                .multiply(rateResponse.exchangeRate())
                .setScale(2, RoundingMode.HALF_UP);

        return new CurrencyConversionResponse(
                rateResponse.fromCurrency(),
                rateResponse.toCurrency(),
                amount,
                rateResponse.exchangeRate(),
                converted,
                rateResponse.rateDate(),
                rateResponse.provider()
        );
    }

    private String normalize(String currency) {
        if (currency == null ||
                !currency.matches("(?i)[A-Z]{3}")) {
            throw new IllegalArgumentException(
                    "Currency must be a valid 3-letter code");
        }
        return currency.toUpperCase();
    }
    
    
    
}
