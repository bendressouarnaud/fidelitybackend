package com.ankk.fidelity.httpbeans;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UtilisateurBean {
    private String nom;
    private String prenom;
    private String email;
    private String contact;
    private String produit;
    private int montant;
    private long dateSouscription;
    private int echeance;
}
