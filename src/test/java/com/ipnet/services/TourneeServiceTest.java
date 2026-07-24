package com.ipnet.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ipnet.dto.TourneeDto;
import com.ipnet.dto.TourneeRequestDto;
import com.ipnet.entity.DemandeCollecteEntity;
import com.ipnet.entity.Tournee;
import com.ipnet.mappers.TourneeMapper;
import com.ipnet.repository.DemandeCollecteRepository;
import com.ipnet.repository.TourneeRepository;
import com.ipnet.security.model.User;
import com.ipnet.security.repository.UserRepository;
import com.ipnet.services.implement.TourneeServiceImpl;

@ExtendWith(MockitoExtension.class)
public class TourneeServiceTest {

    @Mock
    private TourneeRepository tourneeRepository;

    @Mock
    private DemandeCollecteRepository demandeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TourneeMapper tourneeMapper;

    @InjectMocks
    private TourneeServiceImpl tourneeService;

    private TourneeRequestDto tourneeRequestDto;
    private User livreur;
    private Tournee tournee;
    private TourneeDto tourneeDto;
    private DemandeCollecteEntity demande;

    @BeforeEach
    void setUp() {
        livreur = new User();
        livreur.setId(1L);
        livreur.setPublicId(UUID.randomUUID());
        livreur.setNom("Livreur Test");
        livreur.setTelephone("+261340000002");

        tourneeRequestDto = new TourneeRequestDto();
        tourneeRequestDto.setDateTournee(LocalDate.now());
        tourneeRequestDto.setLivreurId(livreur.getPublicId());
        tourneeRequestDto.setZone("Nord");
        tourneeRequestDto.setDemandeIds(new ArrayList<>());

        tournee = new Tournee();
        tournee.setId(UUID.randomUUID());
        tournee.setLivreur(livreur);
        tournee.setDateTournee(LocalDate.now());
        tournee.setZone("Nord");
        tournee.setStatut("PLANIFIEE");

        tourneeDto = new TourneeDto();
        tourneeDto.setId(tournee.getId());
        tourneeDto.setDateTournee(LocalDate.now());
        tourneeDto.setZone("Nord");

        demande = new DemandeCollecteEntity();
        demande.setId(UUID.randomUUID());
    }

    @Test
    void testCreateTournee_Success() {
        when(userRepository.findByPublicId(any())).thenReturn(java.util.Optional.of(livreur));
        when(tourneeMapper.toEntity(any(TourneeRequestDto.class))).thenReturn(tournee);
        when(tourneeRepository.save(any(Tournee.class))).thenReturn(tournee);
        when(tourneeRepository.findById(any())).thenReturn(java.util.Optional.of(tournee));
        when(tourneeMapper.toDto(any(Tournee.class))).thenReturn(tourneeDto);

        TourneeDto result = tourneeService.create(tourneeRequestDto);

        assertNotNull(result);
        assertEquals("Nord", result.getZone());
        verify(tourneeRepository, times(1)).save(any(Tournee.class));
    }

    @Test
    void testCreateTournee_LivreurNotFound() {
        when(userRepository.findByPublicId(any())).thenReturn(java.util.Optional.empty());

        assertThrows(RuntimeException.class, () -> tourneeService.create(tourneeRequestDto));
    }

    @Test
    void testGetById_Success() {
        when(tourneeRepository.findById(any())).thenReturn(java.util.Optional.of(tournee));
        when(tourneeMapper.toDto(any(Tournee.class))).thenReturn(tourneeDto);

        TourneeDto result = tourneeService.getById(tournee.getId());

        assertNotNull(result);
        assertEquals(tournee.getId(), result.getId());
    }

    @Test
    void testGetById_NotFound() {
        when(tourneeRepository.findById(any())).thenReturn(java.util.Optional.empty());

        assertThrows(RuntimeException.class, () -> tourneeService.getById(UUID.randomUUID()));
    }

    @Test
    void testAddDemandeToTournee_Success() {
        when(tourneeRepository.findById(any())).thenReturn(java.util.Optional.of(tournee));
        when(demandeRepository.findById(any())).thenReturn(java.util.Optional.of(demande));
        when(demandeRepository.save(any(DemandeCollecteEntity.class))).thenReturn(demande);
        when(tourneeMapper.toDto(any(Tournee.class))).thenReturn(tourneeDto);

        TourneeDto result = tourneeService.addDemandeToTournee(tournee.getId(), demande.getId());

        assertNotNull(result);
        verify(demandeRepository, times(1)).save(any(DemandeCollecteEntity.class));
    }

    @Test
    void testAddDemandeToTournee_DemandeDejaAssignee() {
        demande.setTournee(tournee);

        when(tourneeRepository.findById(any())).thenReturn(java.util.Optional.of(tournee));
        when(demandeRepository.findById(any())).thenReturn(java.util.Optional.of(demande));

        assertThrows(RuntimeException.class,
                () -> tourneeService.addDemandeToTournee(tournee.getId(), demande.getId()));
    }

    @Test
    void testRemoveDemandeFromTournee_Success() {
        demande.setTournee(tournee);

        when(tourneeRepository.findById(any())).thenReturn(java.util.Optional.of(tournee));
        when(demandeRepository.findById(any())).thenReturn(java.util.Optional.of(demande));
        when(demandeRepository.save(any(DemandeCollecteEntity.class))).thenReturn(demande);
        when(tourneeMapper.toDto(any(Tournee.class))).thenReturn(tourneeDto);

        TourneeDto result = tourneeService.removeDemandeFromTournee(tournee.getId(), demande.getId());

        assertNotNull(result);
        verify(demandeRepository, times(1)).save(any(DemandeCollecteEntity.class));
    }

    @Test
    void testDeleteTournee_Success() {
        when(tourneeRepository.findById(any())).thenReturn(java.util.Optional.of(tournee));

        tourneeService.delete(tournee.getId());

        verify(tourneeRepository, times(1)).delete(any(Tournee.class));
    }
}
