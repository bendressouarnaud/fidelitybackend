package com.ankk.fidelity.model;

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
@NoArgsConstructor
public class Produit extends AbstractEntity{

    private String libelle;
    private int prime;

    @OneToMany(fetch = LAZY, mappedBy = "produit")
    private List<Souscription> souscriptions;
}
