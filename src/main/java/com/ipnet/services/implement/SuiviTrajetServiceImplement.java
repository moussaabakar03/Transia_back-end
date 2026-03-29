package com.ipnet.services.implement;

import com.ipnet.dto.SuiviTrajetDto;
import com.ipnet.entity.SuiviTrajetEntity;
import com.ipnet.entity.TrajetEntity;
import com.ipnet.repository.SuiviTrajetRepository;
import com.ipnet.repository.TrajetRepository;
import com.ipnet.services.interfaces.SuiviTrajetServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SuiviTrajetServiceImplement implements SuiviTrajetServiceInterface {

    @Autowired private SuiviTrajetRepository suiviRepo;
    @Autowired private TrajetRepository trajetRepo;

    @Override
    public SuiviTrajetDto demarrerSuivi(Long trajetId) {
        TrajetEntity trajet = trajetRepo.findById(trajetId)
            .orElseThrow(() -> new RuntimeException("Trajet non trouvé"));
        
        SuiviTrajetEntity suivi = new SuiviTrajetEntity();
        suivi.setTrajet(trajet);
        suivi.setStatut("EN_ROUTE");
        
        SuiviTrajetEntity saved = suiviRepo.save(suivi);
        SuiviTrajetDto dto = new SuiviTrajetDto();
        dto.setId(saved.getId());
        dto.setStatut(saved.getStatut());
        dto.setTrajetId(trajetId);
        return dto;
    }

    @Override
    public SuiviTrajetDto getSuiviParTrajet(Long trajetId) {
        return suiviRepo.findByTrajetId(trajetId)
            .map(s -> {
                SuiviTrajetDto dto = new SuiviTrajetDto();
                dto.setId(s.getId());
                dto.setStatut(s.getStatut());
                return dto;
            }).orElse(null);
    }

    @Override
    public SuiviTrajetDto mettreAJourStatut(Long suiviId, String nouveauStatut) {
        SuiviTrajetEntity suivi = suiviRepo.findById(suiviId).orElseThrow();
        suivi.setStatut(nouveauStatut);
        suiviRepo.save(suivi);
        return getSuiviParTrajet(suivi.getTrajet().getId());
    }
}