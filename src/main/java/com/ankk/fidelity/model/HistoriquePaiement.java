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
        @Index(name = "souscription_historique_id_idx", columnList = "souscription_id")
    }
)
@NoArgsConstructor
public class HistoriquePaiement extends AbstractEntity{

    private int montant;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "souscription_id", foreignKey = @ForeignKey(name = "FK_souscription_historique"))
    private Souscription souscription;

}
