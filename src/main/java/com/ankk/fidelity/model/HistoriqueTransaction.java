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
        @Index(name = "utilisateur_historique_transaction_id_idx", columnList = "utilisateur_id")
    }
)
@NoArgsConstructor
public class HistoriqueTransaction extends AbstractEntity{

    private String contenu;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "utilisateur_id", foreignKey = @ForeignKey(name = "FK_utilisateur_historique_transaction"))
    private Utilisateur utilisateur;

}
