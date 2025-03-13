package com.ankk.fidelity.controller;

import com.ankk.fidelity.httpbeans.BeanAuthentification;
import com.ankk.fidelity.httpbeans.HistoriqueBean;
import com.ankk.fidelity.httpbeans.ProduitBean;
import com.ankk.fidelity.httpbeans.UtilisateurBean;
import com.ankk.fidelity.model.HistoriqueTransaction;
import com.ankk.fidelity.model.Produit;
import com.ankk.fidelity.model.Souscription;
import com.ankk.fidelity.model.Utilisateur;
import com.ankk.fidelity.repositories.HistoriqueTransactionRepository;
import com.ankk.fidelity.repositories.ProduitRepository;
import com.ankk.fidelity.repositories.SouscriptionRepository;
import com.ankk.fidelity.repositories.UtilisateurRepository;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

@RestController
@RequiredArgsConstructor
public class ApiController {

    // Attribute :
    private final UtilisateurRepository utilisateurRepository;
    private final ProduitRepository produitRepository;
    private final SouscriptionRepository souscriptionRepository;
    private final HistoriqueTransactionRepository historiqueTransactionRepository;
    @Value("${app.firebase-config}")
    private String firebaseConfig;
    FirebaseApp firebaseApp;


    // Method
    @PostConstruct
    private void initialize(){
        try {
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(
                            new ClassPathResource(firebaseConfig).
                                    getInputStream())).build();
            if (FirebaseApp.getApps().isEmpty()) {
                this.firebaseApp = FirebaseApp.initializeApp(options);
            } else {
                this.firebaseApp = FirebaseApp.getInstance();
            }
        } catch (IOException e) {
            System.out.println("Create FirebaseApp Error : " + e.getMessage());
        }
    }


    @CrossOrigin("*")
    @PostMapping(path = "/createUser")
    public void createUser(
            @RequestBody UtilisateurBean data,
            HttpServletRequest request
    ){
        // Create USER if needed :
        AtomicBoolean userExistAlready = new AtomicBoolean(true);
        Utilisateur utilisateur = utilisateurRepository.findByEmail(data.getEmail()).orElseGet(
                () -> {
                    Utilisateur us1 = new Utilisateur();
                    us1.setFcmtoken("");
                    us1.setNom(data.getNom());
                    us1.setPrenom(data.getPrenom());
                    us1.setEmail(data.getEmail());
                    us1.setContact(data.getContact());
                    us1.setActive(0);
                    us1.setAdresse("");
                    // Generate PWD
                    OffsetDateTime offsetDateTime = OffsetDateTime.now();
                    us1.setPwd(String.valueOf(offsetDateTime.getYear()) + String.valueOf(offsetDateTime.getMonthValue()) +
                            String.valueOf(offsetDateTime.getSecond()));
                    userExistAlready.set(false);
                    return utilisateurRepository.save(us1);
                }
        );

        // Add produit :
        Produit produit = produitRepository.findByLibelle(data.getProduit()).orElseGet(
                () -> {
                    return produitRepository.save(Produit.builder()
                            .libelle(data.getProduit())
                            .prime(data.getMontant())
                            .build());
                }
        );
        produitRepository.save(produit);

        // Link to :
        Souscription souscription = Souscription.builder()
                .numPolice(getNumPolice(utilisateur, data.getProduit()))
                .dateSouscription(data.getDateSouscription())
                .echeance(data.getEcheance())
                .utilisateur(utilisateur)
                .produit(produit)
                .build();
        souscriptionRepository.save(souscription);

        //
        HistoriqueTransaction historiqueTransaction = HistoriqueTransaction.builder()
                .contenu("Souscription au produit " + data.getProduit())
                .utilisateur(utilisateur)
                .build();
        historiqueTransactionRepository.save(historiqueTransaction);

        if(userExistAlready.get()){
            // Send NEW PRODUCT and the CALENDAR :
        }

        // Add
        System.out.println("Data : " + data);
    }


    private String getNumPolice(Utilisateur utilisateur, String produit){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(utilisateur.getNom().charAt(0));
        stringBuilder.append(utilisateur.getPrenom().charAt(0));
        stringBuilder.append(produit.charAt(0));
        stringBuilder.append(String.valueOf(utilisateur.getId()));
        return stringBuilder.toString();
    }


    @CrossOrigin("*")
    @PostMapping(value={"/authenticate"})
    private ResponseEntity<?> authenticate(@RequestBody BeanAuthentification data){
        // Check
        Utilisateur ur = utilisateurRepository.
                findByEmailAndPwdAndActive(data.getMail().trim(),
                        data.getPwd().trim(), 0).orElse(null);
        Map<String, Object> stringMap = new HashMap<>();
        stringMap.put("id", ur != null ? ur.getId() : 0);
        stringMap.put("nom", ur != null ? ur.getNom() : "");
        stringMap.put("prenom", ur != null ? ur.getPrenom() : "");
        stringMap.put("email", ur != null ? ur.getEmail() : "");
        stringMap.put("numero", ur != null ? ur.getContact() : "");
        stringMap.put("adresse", ur != null ? ur.getAdresse() : "");
        stringMap.put("fcmtoken", "");
        stringMap.put("pwd", "");
        // For each product,
        List<ProduitBean> lesProduits = new ArrayList<>();
        if(ur != null){
            List<Souscription> lesSouscriptions =
                    ur.getSouscriptions();
            for(Souscription souscription : lesSouscriptions){
                // Add Product :
                ProduitBean produitBean = new ProduitBean();
                produitBean.setId(souscription.getProduit().getId().intValue());
                produitBean.setLibelle(souscription.getProduit().getLibelle());
                produitBean.setPrime(souscription.getProduit().getPrime());
                produitBean.setDateSouscription(souscription.getDateSouscription());
                produitBean.setNumPolice(souscription.getNumPolice());
                produitBean.setEcheance(souscription.getEcheance());
                lesProduits.add(produitBean);
            }
        }
        stringMap.put("produits", lesProduits);

        // Pick Historique :
        List<HistoriqueBean> lesHistoriques = new ArrayList<>();
        historiqueTransactionRepository.findAllByUtilisateur(ur).forEach(
                d -> {
                    lesHistoriques.add(HistoriqueBean.builder()
                            .id(d.getId().intValue())
                            .temps(d.getCreationDatetime().toInstant().toEpochMilli())
                            .build());
                }
        );
        //
        stringMap.put("historiques", lesHistoriques);

        return ur != null ?
                ResponseEntity.ok(stringMap) :
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
