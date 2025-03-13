package com.ankk.fidelity.httpbeans;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FirebasePoliceObject {
    private String produit;
    private String numPolice;
    private int prime;
    private int id;
    private int echeance;
    private long dateSouscription;
    private long temps;
}
