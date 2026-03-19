package com.ipnet.controller;

import com.ipnet.dto.TrajetRequestDto;
import com.ipnet.entity.TrajetEntity;
import com.ipnet.entity.VehiculeEntity;
import com.ipnet.entity.VilleEntity;
import com.ipnet.repository.TrajetRepository;
import com.ipnet.repository.VehiculeRepository;
import com.ipnet.repository.VilleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/trajet")
@CrossOrigin("*") 
public class TrajetController {

    @Autowired
    private TrajetRepository trajetRepository;

    @Autowired
    private VilleRepository villeRepository;

    @Autowired
    private VehiculeRepository vehiculeRepository;


    @GetMapping
    public List<TrajetEntity> getAllTrajets() {
        return trajetRepository.findAll();
    }


    @GetMapping("/{id}")
    public ResponseEntity<TrajetEntity> getTrajetById(@PathVariable Long id) {
        return trajetRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @PostMapping
    public ResponseEntity<?> createTrajet(@RequestBody TrajetRequestDto request) {
        try {
            TrajetEntity trajet = new TrajetEntity();

            
            VilleEntity depart = villeRepository.findById(request.getVilleDepartId())
                    .orElseThrow(() -> new RuntimeException("Ville de départ introuvable"));
            
            VilleEntity arrivee = villeRepository.findById(request.getVilleArriveeId())
                    .orElseThrow(() -> new RuntimeException("Ville d'arrivée introuvable"));
            
            VehiculeEntity vehicule = vehiculeRepository.findById(request.getVehiculeId())
                    .orElseThrow(() -> new RuntimeException("Véhicule introuvable"));

                    
            trajet.setVilleDepart(depart);
            trajet.setVilleArrivee(arrivee);
            trajet.setVehicule(vehicule);
            trajet.setDistance(request.getDistance());
            trajet.setDureeEstimee(request.getDureeEstimee());
            trajet.setTarif(request.getTarif());
            trajet.setDateDepart(request.getDateDepart());
            trajet.setHeureDepart(request.getHeureDepart());
            trajet.setStatut(request.getStatut());

            TrajetEntity savedTrajet = trajetRepository.save(trajet);
            return ResponseEntity.ok(savedTrajet);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur lors de la création : " + e.getMessage());
        }
    }

    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTrajet(@PathVariable Long id, @RequestBody TrajetRequestDto request) {
        return trajetRepository.findById(id).map(trajet -> {
            VilleEntity depart = villeRepository.findById(request.getVilleDepartId()).get();
            VilleEntity arrivee = villeRepository.findById(request.getVilleArriveeId()).get();
            
            trajet.setVilleDepart(depart);
            trajet.setVilleArrivee(arrivee);
            trajet.setTarif(request.getTarif());
            trajet.setStatut(request.getStatut());
            
            
            return ResponseEntity.ok(trajetRepository.save(trajet));
        }).orElse(ResponseEntity.notFound().build());
    }

    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTrajet(@PathVariable Long id) {
        return trajetRepository.findById(id).map(trajet -> {
            trajetRepository.delete(trajet);
            return ResponseEntity.ok().build();
        }).orElse(ResponseEntity.notFound().build());
    }
}