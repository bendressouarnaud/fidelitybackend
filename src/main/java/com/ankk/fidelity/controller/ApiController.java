package com.ankk.fidelity.controller;

import com.ankk.fidelity.enums.PaiementState;
import com.ankk.fidelity.httpbeans.*;
import com.ankk.fidelity.meservices.Firebasemessage;
import com.ankk.fidelity.model.*;
import com.ankk.fidelity.repositories.*;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

@RestController
@RequiredArgsConstructor
public class ApiController {

    // Attribute :
    private final UtilisateurRepository utilisateurRepository;
    private final ProduitRepository produitRepository;
    private final SouscriptionRepository souscriptionRepository;
    private final HistoriqueTransactionRepository historiqueTransactionRepository;
    private final HistoriquePaiementRepository historiquePaiementRepository;
    private final Firebasemessage firebasemessage;
    @Value("${app.firebase-config}")
    private String firebaseConfig;
    FirebaseApp firebaseApp;
    @Value("${sfp.wave.token}")
    private String waveToken;
    @Value("${sfp.wave.apiurl}")
    private String waveUrl;
    @Value("${backend.web.url}")
    private String backendWebUrl;


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
                .paiementState(PaiementState.DEFAULT)
                .build();
        souscriptionRepository.save(souscription);

        //
        HistoriqueTransaction historiqueTransaction = HistoriqueTransaction.builder()
                .contenu("Souscription au produit " + data.getProduit())
                .utilisateur(utilisateur)
                .build();
        historiqueTransactionRepository.save(historiqueTransaction);

        if(userExistAlready.get() && utilisateur.getFcmtoken() != null && !utilisateur.getFcmtoken().isEmpty()){
            // Send NEW PRODUCT and the CALENDAR :
            FirebasePoliceObject firebasePoliceObject = FirebasePoliceObject.builder()
                .produit(produit.getLibelle())
                    .numPolice(souscription.getNumPolice())
                    .prime(produit.getPrime())
                    .id(produit.getId().intValue())
                    .echeance(souscription.getEcheance())
                    .dateSouscription(souscription.getDateSouscription())
                    .temps(historiqueTransaction.getCreationDatetime().toInstant().toEpochMilli())
                    .build();
            firebasemessage.notifyClientAboutNewPolices(firebasePoliceObject, utilisateur.getFcmtoken());
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


    @CrossOrigin("*")
    @PostMapping(path = "/generatewaveid")
    public ResponseEntity<?> generatewaveid(
            @RequestBody WavePaymentRequest data,
            HttpServletRequest request
    )
    {
        // New LINE :
        Souscription souscription = souscriptionRepository.findByNumPolice(data.getNumPolice());
        souscription.setPaiementState(PaiementState.PAIEMENT_EN_COURS);
        souscriptionRepository.save(souscription);
        // Hit Historique
        HistoriquePaiement historiquePaiement = HistoriquePaiement.builder()
                .montant(data.getAmount())
                .souscription(souscription)
                .build();
        historiquePaiementRepository.save(historiquePaiement);

        Map<String, Object> stringMap = new HashMap<>();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + waveToken);
        headers.add("Content-Type", "application/json");

        try {
            // Call WEB Services :
            RestTemplate restTemplate = new RestTemplate();
            WavePaymentOriginalRequest objectRequest = new WavePaymentOriginalRequest();
            objectRequest.setAmount(data.getAmount());
            objectRequest.setCurrency(data.getCurrency());
            objectRequest.setErrorUrl(
                    backendWebUrl + "trobackend/invalidation/" +
                            souscription.getId());
            objectRequest.setSuccessUrl(
                    backendWebUrl + "trobackend/validation/" +
                            souscription.getId()
            );

            HttpEntity<WavePaymentOriginalRequest> entity = new HttpEntity<>(objectRequest, headers);
            ResponseEntity<WavePaymentResponse> responseEntity = restTemplate.postForEntity(waveUrl,
                    entity, WavePaymentResponse.class);
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                // Persist :
                WavePaymentResponse wavePaymentResponse = responseEntity.getBody();
                stringMap.put("id", wavePaymentResponse.getId());
                stringMap.put("wave_launch_url", wavePaymentResponse.getWaveLaunchUrl());
                stringMap.put("reserve", 0);
                return ResponseEntity.ok(stringMap);
            }
        } catch (Exception exc) {
            System.out.println("Exception (generatewaveid) : " + exc.toString());
        }

        stringMap.put("id", "");
        stringMap.put("wave_launch_url", "");
        stringMap.put("reserve", 0);

        return ResponseEntity.ok(stringMap);
    }


    // M E T H O D S
    @GetMapping("/validation/{souscriptionId}")
    public ModelAndView validation(@PathVariable long souscriptionId) {
        // Find RESERVATION :
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("validation");

        Souscription souscription = souscriptionRepository.findById(souscriptionId).orElse(null);
        souscription.setPaiementState(PaiementState.PAIEMENT_EFFECTUE);
        souscriptionRepository.save(souscription);

        // Find UTILISATEUR :
        Utilisateur utilisateur = souscription.getUtilisateur();
        // Find MONTANT :
        int montant = souscription.getHistoriquePaiements().get(souscription.getHistoriquePaiements().size() - 1).getMontant();

        DecimalFormat formatter = new DecimalFormat("###,###,###"); // ###,###,###.00
        String resultAmount = formatter.format(montant);
        modelAndView.addObject(
                "client",
                ("Felicitations " + utilisateur.getNom() + " " +
                        utilisateur.getPrenom())
        );
        modelAndView.addObject("montant", (resultAmount + " est effectif !"));
        modelAndView.addObject("date",
                OffsetDateTime.now(Clock.systemUTC()).
                        truncatedTo(ChronoUnit.SECONDS).
                        format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        // Add members :
        //emailService.addMembersToChannels(Stream.of(utilisateur, owner).toList(), channel_ID);

        // Notify PUBLICATION's curent suscriber :
        firebasemessage.notifyClientAboutPrimePayment(
                souscription.getProduit().getLibelle(),
                souscription.getNumPolice(),
                utilisateur.getFcmtoken()
                );
        return modelAndView;
    }

}
