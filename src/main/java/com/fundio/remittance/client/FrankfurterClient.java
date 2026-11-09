
package com.fundio.remittance.client;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class FrankfurterClient {

    private final RestClient restClient;

    public FrankfurterClient(RestClient.Builder builder) {
        this.restClient = builder
                .baseUrl("https://api.frankfurter.dev")
                .build();
    }

    public record RateResponse(
            LocalDate date,
            String base,
            String quote,
            BigDecimal rate
    ) {}

    public RateResponse getRate(String from, String to) {
        return restClient.get()
                .uri("/v2/rate/{from}/{to}", from, to)
                .retrieve()
                .body(RateResponse.class);
    }
}
