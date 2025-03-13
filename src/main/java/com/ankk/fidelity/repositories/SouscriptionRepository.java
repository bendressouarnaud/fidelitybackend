package com.ankk.fidelity.repositories;

import com.ankk.fidelity.model.Souscription;
import org.springframework.data.repository.CrudRepository;

public interface SouscriptionRepository extends CrudRepository<Souscription, Long> {
    Souscription findByNumPolice(String numPolice);
}
