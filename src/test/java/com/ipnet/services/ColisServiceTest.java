package com.ipnet.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ipnet.dto.ColisDto;
import com.ipnet.dto.ColisRequestDto;
import com.ipnet.entity.Colis;
import com.ipnet.entity.HistoriqueColis;
import com.ipnet.enums.ModeDepot;
import com.ipnet.enums.StatutColis;
import com.ipnet.mappers.ColisMapper;
import com.ipnet.mappers.HistoriqueColisMapper;
import com.ipnet.repository.ColisRepository;
import com.ipnet.repository.HistoriqueColisRepository;
import com.ipnet.security.model.User;
import com.ipnet.security.repository.UserRepository;
import com.ipnet.services.implement.ColisServiceImpl;

@ExtendWith(MockitoExtension.class)
public class ColisServiceTest {

    @Mock
    private ColisRepository colisRepository;

    @Mock
    private HistoriqueColisRepository historiqueColisRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ColisMapper colisMapper;

    @Mock
    private HistoriqueColisMapper historiqueColisMapper;

    @InjectMocks
    private ColisServiceImpl colisService;

    private ColisRequestDto colisRequestDto;
    private User expediteur;
    private User livreur;
    private Colis colis;
    private ColisDto colisDto;

    @BeforeEach
    void setUp() {
        expediteur = new User();
        expediteur.setId(1L);
        expediteur.setPublicId(UUID.randomUUID());
        expediteur.setNom("Expediteur Test");
        expediteur.setTelephone("+261340000001");

        livreur = new User();
        livreur.setId(2L);
        livreur.setPublicId(UUID.randomUUID());
        livreur.setNom("Livreur Test");
        livreur.setTelephone("+261340000002");

        colisRequestDto = new ColisRequestDto();
        colisRequestDto.setExpediteurId(expediteur.getPublicId());
        colisRequestDto.setNomDestinataire("Jean Dupont");
        colisRequestDto.setAdresseDestinataire("123 Rue de Paris");
        colisRequestDto.setTelephoneDestinataire("0123456789");
        colisRequestDto.setPoids(5.0);
        colisRequestDto.setLongueur(30.0);
        colisRequestDto.setLargeur(20.0);
        colisRequestDto.setHauteur(15.0);
        colisRequestDto.setModeDepot(ModeDepot.DEPOT_AGENCE);

        colis = new Colis();
        colis.setId(UUID.randomUUID());
        colis.setExpediteur(expediteur);
        colis.setStatut(StatutColis.EN_ATTENTE_COLLECTE);
        colis.setDateCreationColis(LocalDateTime.now());

        colisDto = new ColisDto();
        colisDto.setId(colis.getId());
        colisDto.setStatut(StatutColis.EN_ATTENTE_COLLECTE);
    }

    @Test
    void testCreateColis_Success() {
        when(userRepository.findById(any())).thenReturn(java.util.Optional.of(expediteur));
        when(colisMapper.toEntity(any(ColisRequestDto.class))).thenReturn(colis);
        when(colisRepository.save(any(Colis.class))).thenReturn(colis);
        when(colisMapper.toDto(any(Colis.class))).thenReturn(colisDto);
        when(colisRepository.count()).thenReturn(0L);

        ColisDto result = colisService.create(colisRequestDto);

        assertNotNull(result);
        assertEquals(StatutColis.EN_ATTENTE_COLLECTE, result.getStatut());
        verify(colisRepository, times(1)).save(any(Colis.class));
    }

    @Test
    void testCreateColis_ExpediteurNotFound() {
        when(userRepository.findById(any())).thenReturn(java.util.Optional.empty());

        assertThrows(RuntimeException.class, () -> colisService.create(colisRequestDto));
    }

    @Test
    void testAssignerLivreur_Success() {
        when(colisRepository.findById(any())).thenReturn(java.util.Optional.of(colis));
        when(userRepository.findById(any())).thenReturn(java.util.Optional.of(livreur));
        when(colisRepository.save(any(Colis.class))).thenReturn(colis);
        when(colisMapper.toDto(any(Colis.class))).thenReturn(colisDto);

        ColisDto result = colisService.assignerLivreur(colis.getId(), livreur.getPublicId());

        assertNotNull(result);
        verify(colisRepository, times(1)).save(any(Colis.class));
    }

    @Test
    void testCollecter_Success() {
        colis.setLivreur(livreur);
        colis.setStatut(StatutColis.PRIS_EN_CHARGE);

        when(colisRepository.findById(any())).thenReturn(java.util.Optional.of(colis));
        when(colisRepository.save(any(Colis.class))).thenReturn(colis);
        when(colisMapper.toDto(any(Colis.class))).thenReturn(colisDto);

        ColisDto result = colisService.collecter(colis.getId(), "Collecte effectuée");

        assertNotNull(result);
        verify(colisRepository, times(1)).save(any(Colis.class));
    }

    @Test
    void testCollecter_SansLivreur() {
        colis.setLivreur(null);

        when(colisRepository.findById(any())).thenReturn(java.util.Optional.of(colis));

        assertThrows(RuntimeException.class, () -> colisService.collecter(colis.getId(), "Test"));
    }

    @Test
    void testLivrer_Success() {
        colis.setLivreur(livreur);
        colis.setStatut(StatutColis.COLLECTE_EFFECTUEE);

        when(colisRepository.findById(any())).thenReturn(java.util.Optional.of(colis));
        when(colisRepository.save(any(Colis.class))).thenReturn(colis);
        when(colisMapper.toDto(any(Colis.class))).thenReturn(colisDto);

        ColisDto result = colisService.livrer(colis.getId(), "Livraison effectuée");

        assertNotNull(result);
        verify(colisRepository, times(1)).save(any(Colis.class));
    }

    @Test
    void testAnnulerColis_Success() {
        colis.setStatut(StatutColis.EN_ATTENTE_COLLECTE);

        when(colisRepository.findById(any())).thenReturn(java.util.Optional.of(colis));
        when(colisRepository.save(any(Colis.class))).thenReturn(colis);

        colisService.annulerColis(colis.getId());

        verify(colisRepository, times(1)).save(any(Colis.class));
    }

    @Test
    void testAnnulerColis_DejaLivré() {
        colis.setStatut(StatutColis.LIVRE);

        when(colisRepository.findById(any())).thenReturn(java.util.Optional.of(colis));

        assertThrows(RuntimeException.class, () -> colisService.annulerColis(colis.getId()));
    }

    @Test
    void testGenerateNumeroSuivi() {
        when(colisRepository.count()).thenReturn(5L);

        String numero = colisService.generateNumeroSuivi();

        assertNotNull(numero);
        assertTrue(numero.startsWith("COL-"));
    }
}
