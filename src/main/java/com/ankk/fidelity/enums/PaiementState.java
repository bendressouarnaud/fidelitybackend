package com.ankk.fidelity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum PaiementState {
    DEFAULT(0),
    PAIEMENT_EN_COURS(1),
    PAIEMENT_EFFECTUE(2);
    private final int value;
}
