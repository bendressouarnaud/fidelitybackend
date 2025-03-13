package com.ankk.fidelity.httpbeans;

import lombok.Data;

@Data
public class ProduitBean {
    private int id;
    private String libelle;
    private String numPolice;
    private int prime;
    private long dateSouscription;
    private int echeance; // Mois
}
