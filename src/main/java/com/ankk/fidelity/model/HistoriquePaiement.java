package com.ankk.fidelity.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import static jakarta.persistence.FetchType.LAZY;

@Getter
@Setter
@SuperBuilder
@Entity
@Table(
        indexes = {
                @Index(name = "utilisateur_historique_id_idx", columnList = "utilisateur_id"),
                @Index(name = "produit_historique_id_idx", columnList = "produit_id")
        }
)
@NoArgsConstructor
public class HistoriquePaiement extends AbstractEntity{

    private int montant;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "utilisateur_id", foreignKey = @ForeignKey(name = "FK_utilisateur_historique"))
    private Utilisateur utilisateur;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "produit_id", foreignKey = @ForeignKey(name = "FK_produit_historique"))
    private Produit produit;

}
