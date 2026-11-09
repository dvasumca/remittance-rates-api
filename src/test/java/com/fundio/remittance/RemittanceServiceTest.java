
package com.fundio.remittance;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fundio.remittance.client.FrankfurterClient;
import com.fundio.remittance.client.FrankfurterClient.RateResponse;
import com.fundio.remittance.dto.CurrencyConversionResponse;
import com.fundio.remittance.dto.ExchangeRateResponse;
import com.fundio.remittance.service.RemittanceService;

class RemittanceServiceTest {

    private FrankfurterClient client;
    private RemittanceService service;

    private final LocalDate rateDate =
            LocalDate.of(2026, 11, 10);

    @BeforeEach
    void setUp() {
        client = mock(FrankfurterClient.class);
        service = new RemittanceService(client);
    }

    @Test
    void shouldReturnExchangeRate() {

        when(client.getRate("INR", "MYR"))
                .thenReturn(new RateResponse(
                        rateDate,
                        "INR",
                        "MYR",
                        new BigDecimal("0.043193")
                ));

        ExchangeRateResponse response =
                service.getRate("INR", "MYR");

        assertEquals("INR", response.fromCurrency());
        assertEquals("MYR", response.toCurrency());
        assertEquals(
                new BigDecimal("0.043193"),
                response.exchangeRate()
        );
        assertEquals(rateDate, response.rateDate());
        assertEquals("Frankfurter", response.provider());

        verify(client).getRate("INR", "MYR");
    }

    @Test
    void shouldNormalizeCurrencyCodes() {

        when(client.getRate("INR", "MYR"))
                .thenReturn(new RateResponse(
                        rateDate,
                        "INR",
                        "MYR",
                        new BigDecimal("0.043193")
                ));

        ExchangeRateResponse response =
                service.getRate("inr", "myr");

        assertEquals("INR", response.fromCurrency());
        assertEquals("MYR", response.toCurrency());

        verify(client).getRate("INR", "MYR");
    }

    @Test
    void shouldConvertCurrencySuccessfully() {

        when(client.getRate("INR", "MYR"))
                .thenReturn(new RateResponse(
                        rateDate,
                        "INR",
                        "MYR",
                        new BigDecimal("0.043193")
                ));

        CurrencyConversionResponse response =
                service.convert(
                        "INR",
                        "MYR",
                        new BigDecimal("10000")
                );

        assertEquals(
                new BigDecimal("431.93"),
                response.convertedAmount()
        );

        assertEquals("INR", response.fromCurrency());
        assertEquals("MYR", response.toCurrency());
    }

    @Test
    void shouldRoundConvertedAmountHalfUp() {

        when(client.getRate("INR", "MYR"))
                .thenReturn(new RateResponse(
                        rateDate,
                        "INR",
                        "MYR",
                        new BigDecimal("0.043195")
                ));

        CurrencyConversionResponse response =
                service.convert(
                        "INR",
                        "MYR",
                        new BigDecimal("100")
                );

        assertEquals(
                new BigDecimal("4.32"),
                response.convertedAmount()
        );
    }

    @Test
    void shouldRejectInvalidCurrency() {

        assertThrows(
                IllegalArgumentException.class,
                () -> service.getRate("IND", "MYR1")
        );

        verifyNoInteractions(client);
    }

    @Test
    void shouldRejectNullCurrency() {

        assertThrows(
                IllegalArgumentException.class,
                () -> service.getRate(null, "MYR")
        );

        verifyNoInteractions(client);
    }

    @Test
    void shouldRejectZeroAmount() {

        assertThrows(
                IllegalArgumentException.class,
                () -> service.convert(
                        "INR",
                        "MYR",
                        BigDecimal.ZERO
                )
        );

        verifyNoInteractions(client);
    }

    @Test
    void shouldRejectNegativeAmount() {

        assertThrows(
                IllegalArgumentException.class,
                () -> service.convert(
                        "INR",
                        "MYR",
                        new BigDecimal("-100")
                )
        );

        verifyNoInteractions(client);
    }

    @Test
    void shouldRejectNullAmount() {

        assertThrows(
                IllegalArgumentException.class,
                () -> service.convert(
                        "INR",
                        "MYR",
                        null
                )
        );

        verifyNoInteractions(client);
    }

    @Test
    void shouldPropagateProviderFailure() {

        when(client.getRate("INR", "MYR"))
                .thenThrow(
                        new IllegalStateException(
                                "Provider unavailable"
                        )
                );

        assertThrows(
                IllegalStateException.class,
                () -> service.getRate("INR", "MYR")
        );

        verify(client).getRate("INR", "MYR");
    }
}
