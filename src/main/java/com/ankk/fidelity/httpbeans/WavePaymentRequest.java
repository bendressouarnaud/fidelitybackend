package com.ankk.fidelity.httpbeans;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;


@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WavePaymentRequest {
    private Integer amount;
    private String currency;
    @JsonProperty("error_url")
    private String errorUrl;
    @JsonProperty("success_url")
    private String successUrl;
    //
    private String numPolice;
}
