package com.ipnet.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.ipnet.dto.ColisDto;
import com.ipnet.dto.ColisRequestDto;
import com.ipnet.dto.EstimationPrixDto;
import com.ipnet.entity.AgenceEntity;
import com.ipnet.entity.Colis;
import com.ipnet.entity.VilleEntity;
import com.ipnet.enums.ModeRemise;
import com.ipnet.enums.StatutColis;
import com.ipnet.enums.TranchePoids;
import com.ipnet.exception.ColisTransitionInvalideException;
import com.ipnet.mappers.ColisMapper;
import com.ipnet.mappers.HistoriqueColisMapper;
import com.ipnet.repository.AgenceRepository;
import com.ipnet.repository.ColisRepository;
import com.ipnet.repository.HistoriqueColisRepository;
import com.ipnet.repository.TrajetRepository;
import com.ipnet.security.UserDetailsImpl;
import com.ipnet.security.enums.StatutCompte;
import com.ipnet.security.model.User;
import com.ipnet.security.repository.UserRepository;
import com.ipnet.services.implement.ColisServiceImpl;
import com.ipnet.services.interfaces.NotificationServiceInterface;
import com.ipnet.services.interfaces.TarifExpeditionServiceInterface;

@ExtendWith(MockitoExtension.class)
public class ColisServiceTest {

    @Mock private ColisRepository colisRepository;
    @Mock private HistoriqueColisRepository historiqueColisRepository;
    @Mock private UserRepository userRepository;
    @Mock private AgenceRepository agenceRepository;
    @Mock private TrajetRepository trajetRepository;
    @Mock private TarifExpeditionServiceInterface tarifExpeditionService;
    @Mock private ColisMapper colisMapper;
    @Mock private HistoriqueColisMapper historiqueColisMapper;
    @Mock private NotificationServiceInterface notificationService;

    @InjectMocks
    private ColisServiceImpl colisService;

    private User agent;
    private AgenceEntity agenceDepart;
    private AgenceEntity agenceArrivee;
    private Colis colis;
    private ColisDto colisDto;

    @BeforeEach
    void setUp() {
        agent = new User();
        agent.setId(1L);
        agent.setPublicId(UUID.randomUUID());
        agent.setNom("Agent Test");
        agent.setTelephone("+261340000001");

        UserDetailsImpl principal = new UserDetailsImpl(
                agent.getPublicId(), agent.getNom(), agent.getTelephone(), "pwd",
                StatutCompte.ACTIF, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, Collections.emptyList()));
        lenient().when(userRepository.findByPublicId(agent.getPublicId())).thenReturn(Optional.of(agent));

        VilleEntity villeDepart = new VilleEntity();
        villeDepart.setId(UUID.randomUUID());
        VilleEntity villeArrivee = new VilleEntity();
        villeArrivee.setId(UUID.randomUUID());

        agenceDepart = new AgenceEntity();
        agenceDepart.setId(UUID.randomUUID());
        agenceDepart.setVille(villeDepart);

        agenceArrivee = new AgenceEntity();
        agenceArrivee.setId(UUID.randomUUID());
        agenceArrivee.setVille(villeArrivee);

        colis = new Colis();
        colis.setId(UUID.randomUUID());
        colis.setNumeroSuivi("TRS-000001");
        colis.setTranchePoids(TranchePoids.DE_1_A_5KG);
        colis.setModeRemise(ModeRemise.RETRAIT_AGENCE);
        colis.setAgenceDepart(agenceDepart);
        colis.setAgenceArrivee(agenceArrivee);
        colis.setFraisCollecte(0.0);
        colis.setFraisLivraison(0.0);

        colisDto = new ColisDto();
        colisDto.setId(colis.getId());
        colisDto.setStatut(colis.getStatut());
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void testEnregistrerColis_Success() {
        ColisRequestDto dto = new ColisRequestDto();
        dto.setDescription("Documents");
        dto.setTranchePoids(TranchePoids.DE_1_A_5KG);
        dto.setModeRemise(ModeRemise.RETRAIT_AGENCE);
        dto.setExpediteurNom("Expéditeur");
        dto.setExpediteurTelephone("+261340000010");
        dto.setDestinataireNom("Destinataire");
        dto.setDestinataireTelephone("+261340000020");
        dto.setAgenceDepartId(agenceDepart.getId());
        dto.setAgenceArriveeId(agenceArrivee.getId());

        when(agenceRepository.findById(agenceDepart.getId())).thenReturn(Optional.of(agenceDepart));
        when(agenceRepository.findById(agenceArrivee.getId())).thenReturn(Optional.of(agenceArrivee));
        when(tarifExpeditionService.estimerPrix(any(), any(), any(), any(), anyBoolean()))
                .thenReturn(new EstimationPrixDto(2000.0, 0.0, 0.0));
        when(colisRepository.findByNumeroSuivi(any())).thenReturn(Optional.empty());
        when(colisRepository.save(any(Colis.class))).thenReturn(colis);
        when(colisMapper.toDto(any(Colis.class))).thenReturn(colisDto);

        ColisDto result = colisService.enregistrerColis(dto);

        assertNotNull(result);
        verify(colisRepository, times(1)).save(any(Colis.class));
        verify(historiqueColisRepository, times(1)).save(any());
    }

    @Test
    void testEnregistrerColis_LivraisonDomicileSansAdresse_ThrowsException() {
        ColisRequestDto dto = new ColisRequestDto();
        dto.setModeRemise(ModeRemise.LIVRAISON_DOMICILE);

        assertThrows(IllegalArgumentException.class, () -> colisService.enregistrerColis(dto));
    }

    @Test
    void testConfirmerPeseeAjusterPrix_Success() {
        colis.setStatut(StatutColis.EN_ATTENTE_DEPOT);

        when(colisRepository.findById(colis.getId())).thenReturn(Optional.of(colis));
        when(tarifExpeditionService.estimerPrix(any(), any(), any(), any(), anyBoolean()))
                .thenReturn(new EstimationPrixDto(2000.0, 0.0, 0.0));
        when(colisRepository.save(any(Colis.class))).thenReturn(colis);
        when(colisMapper.toDto(any(Colis.class))).thenReturn(colisDto);

        ColisDto result = colisService.confirmerPeseeAjusterPrix(colis.getId(), 3.2, TranchePoids.DE_1_A_5KG);

        assertNotNull(result);
        assertEquals(StatutColis.DEPOSE_EN_AGENCE, colis.getStatut());
        verify(colisRepository, times(1)).save(any(Colis.class));
    }

    @Test
    void testConfirmerPeseeAjusterPrix_MauvaisStatut_ThrowsException() {
        colis.setStatut(StatutColis.LIVRE);

        when(colisRepository.findById(colis.getId())).thenReturn(Optional.of(colis));

        assertThrows(ColisTransitionInvalideException.class,
                () -> colisService.confirmerPeseeAjusterPrix(colis.getId(), 3.2, TranchePoids.DE_1_A_5KG));
    }

    @Test
    void testAnnulerColis_DejaLivre_ThrowsException() {
        colis.setStatut(StatutColis.LIVRE);
        when(colisRepository.findById(colis.getId())).thenReturn(Optional.of(colis));

        assertThrows(ColisTransitionInvalideException.class, () -> colisService.annulerColis(colis.getId()));
    }

    @Test
    void testAnnulerColis_Success() {
        colis.setStatut(StatutColis.EN_ATTENTE_DEPOT);
        when(colisRepository.findById(colis.getId())).thenReturn(Optional.of(colis));
        when(colisRepository.save(any(Colis.class))).thenReturn(colis);

        colisService.annulerColis(colis.getId());

        assertEquals(StatutColis.ANNULE, colis.getStatut());
        verify(colisRepository, times(1)).save(any(Colis.class));
    }

    @Test
    void testGetStatutColis_NotFound_ThrowsException() {
        when(colisRepository.findByNumeroSuivi("TRS-999999")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> colisService.getStatutColis("TRS-999999"));
    }
}
