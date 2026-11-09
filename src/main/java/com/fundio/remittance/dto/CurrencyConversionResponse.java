
package com.fundio.remittance.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CurrencyConversionResponse(
        String fromCurrency,
        String toCurrency,
        BigDecimal amount,
        BigDecimal exchangeRate,
        BigDecimal convertedAmount,
        LocalDate rateDate,
        String provider
) {
}
