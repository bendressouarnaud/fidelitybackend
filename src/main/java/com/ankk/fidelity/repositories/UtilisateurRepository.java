package com.ankk.fidelity.repositories;

import com.ankk.fidelity.model.Utilisateur;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UtilisateurRepository extends CrudRepository<Utilisateur, Long> {
    Optional<Utilisateur> findByEmail(String email);
    Optional<Utilisateur> findByEmailAndPwdAndActive(String email, String pwd, int active);
}
