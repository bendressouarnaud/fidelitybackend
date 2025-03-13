package com.ankk.fidelity.meservices;

import com.ankk.fidelity.model.Utilisateur;
import com.ankk.fidelity.repositories.UtilisateurRepository;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class Firebasemessage {

    // attributes
    private final UtilisateurRepository utilisateurRepository;

    @Async
    public void notifySuscriberAboutCible(Long iduser){
        Utilisateur utilisateur = utilisateurRepository.findById(iduser).orElse(null);
        if(utilisateur != null){
            Notification builder = new Notification("Nouvelle souscription",
                    "Annonce modifiée");
            // ANDROID  :
            /*Message me = Message.builder()
                    .setNotification(builder)
                    .setToken(userToken.getToken())
                    .putData("type", typeMessage)
                    .putData("sujet", "1")  // Subject
                    .putData("id", publication.getId().toString())  // Feed 'Magasin' table :
                    .putData("userid", String.valueOf(publication.getUtilisateur().getId()))
                    .putData("villedepart", String.valueOf(publication.getVilleDepart().getId()))
                    .putData("villedestination", String.valueOf(publication.getVilleDestination().getId()))
                    .putData("datevoyage", String.valueOf(publication.getDateVoyage().toString()))
                    .putData("datepublication", String.valueOf(publication.getCreationDatetime().toString()))
                    .putData("reserve", String.valueOf(publication.getReserve()))
                    .putData("identifiant", publication.getIdentifiant())
                    .putData("prix", String.valueOf(publication.getPrix()))
                    .putData("devise", String.valueOf(publication.getDevise().getId()))
                    .build();
            try {
                FirebaseMessaging.getInstance().send(me);
            } catch (FirebaseMessagingException e) {
                System.out.println("FirebaseMessagingException ANDROID : " + e.getMessage());
            }*/
        }
    }

}
