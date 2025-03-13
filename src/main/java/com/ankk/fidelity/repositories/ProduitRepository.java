package com.ankk.fidelity.repositories;

import com.ankk.fidelity.model.Produit;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ProduitRepository extends CrudRepository<Produit, Long> {
    Optional<Produit> findByLibelle(String data);
}
