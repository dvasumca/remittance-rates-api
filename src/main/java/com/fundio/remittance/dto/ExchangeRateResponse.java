
package com.fundio.remittance.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExchangeRateResponse(
        String fromCurrency,
        String toCurrency,
        BigDecimal exchangeRate,
        LocalDate rateDate,
        String provider
) {
}
