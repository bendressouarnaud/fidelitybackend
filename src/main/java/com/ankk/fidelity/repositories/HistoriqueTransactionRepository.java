package com.ankk.fidelity.repositories;

import com.ankk.fidelity.model.HistoriqueTransaction;
import com.ankk.fidelity.model.Utilisateur;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface HistoriqueTransactionRepository extends CrudRepository<HistoriqueTransaction, Long> {
    List<HistoriqueTransaction> findAllByUtilisateur(Utilisateur utilisateur);
}
