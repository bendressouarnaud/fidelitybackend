package com.ankk.fidelity.meservices;

import com.ankk.fidelity.httpbeans.FirebasePoliceObject;
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
    public void notifyClientAboutNewPolices(FirebasePoliceObject object, String fcmToken){
        Notification builder = new Notification("Nouvelle souscription",
                "Police d'assurance " + object.getProduit());
        // ANDROID  :
        Message me = Message.builder()
                .setNotification(builder)
                .setToken(fcmToken)
                .putData("sujet", "1")
                .putData("id", String.valueOf(object.getId()))
                .putData("echeance", String.valueOf(object.getEcheance()))
                .putData("dateSouscription", String.valueOf(object.getDateSouscription()))
                .putData("produit", object.getProduit())
                .putData("numPolice", object.getNumPolice())  // Subject
                .putData("prime", String.valueOf(object.getPrime()))
                .putData("temps", String.valueOf(object.getTemps()))
                .build();
        try {
            FirebaseMessaging.getInstance().send(me);
        } catch (FirebaseMessagingException e) {
            System.out.println("FirebaseMessagingException ANDROID : " + e.getMessage());
        }
    }

    @Async
    public void notifyClientAboutPrimePayment(String produit, String numPolice, String fcmToken){
        Notification builder = new Notification("Paiement effectué",
                "Police d'assurance " + produit);
        // ANDROID  :
        Message me = Message.builder()
                .setNotification(builder)
                .setToken(fcmToken)
                .putData("sujet", "2")
                .putData("numPolice", numPolice)
                .build();
        try {
            FirebaseMessaging.getInstance().send(me);
        } catch (FirebaseMessagingException e) {
            System.out.println("FirebaseMessagingException ANDROID : " + e.getMessage());
        }
    }

}
