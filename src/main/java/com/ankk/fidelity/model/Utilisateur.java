package com.ankk.fidelity.model;

import com.ankk.fidelity.enums.SmartphoneType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Collection;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;

@Getter
@Setter
@SuperBuilder
@Entity
@NoArgsConstructor
public class Utilisateur extends AbstractEntity{
    private String nom;
    private String prenom;
    private String email;
    private String contact;
    private String adresse;
    private String pwd;
    private String fcmtoken;
    private Integer active;

    @Enumerated(EnumType.ORDINAL)
    private SmartphoneType smartphoneType;

    @OneToMany(fetch = LAZY, mappedBy = "utilisateur")
    private List<Souscription> souscriptions;

    @OneToOne
    @JoinColumn(name = "points_id",  foreignKey = @ForeignKey(name = "FK_utilisateur_points"))
    private Points points;
}
