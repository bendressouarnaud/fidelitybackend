package com.ankk.fidelity.model;

import com.ankk.fidelity.enums.PaiementState;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

import static jakarta.persistence.FetchType.LAZY;

@Getter
@Setter
@SuperBuilder
@Entity
@Table(
        indexes = {
                @Index(name = "utilisateur_souscription_id_idx", columnList = "utilisateur_id"),
                @Index(name = "produit_souscription_id_idx", columnList = "produit_id")
        }
)
@NoArgsConstructor
public class Souscription extends AbstractEntity{

    private String numPolice;
    private long dateSouscription;
    private int echeance; // Mois
    @Enumerated(EnumType.ORDINAL)
    private PaiementState paiementState;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "utilisateur_id", foreignKey = @ForeignKey(name = "FK_utilisateur_souscription"))
    private Utilisateur utilisateur;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "produit_id", foreignKey = @ForeignKey(name = "FK_produit_souscription"))
    private Produit produit;

    @OneToMany(fetch = LAZY, mappedBy = "souscription")
    private List<HistoriquePaiement> historiquePaiements;

}
