package com.ankk.fidelity.httpbeans;

import lombok.Data;

@Data
public class CalendrierBean {
    private int id;
    private int produitId;
    private int montant;
    private int mois;
    private int annee;
    private int paiementEffectue;
}
