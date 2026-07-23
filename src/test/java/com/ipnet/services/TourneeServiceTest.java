package com.ipnet.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ipnet.dto.TourneeDto;
import com.ipnet.dto.TourneeRequestDto;
import com.ipnet.entity.Colis;
import com.ipnet.entity.Tournee;
import com.ipnet.mappers.TourneeMapper;
import com.ipnet.repository.ColisRepository;
import com.ipnet.repository.TourneeRepository;
import com.ipnet.security.model.User;
import com.ipnet.security.repository.UserRepository;
import com.ipnet.services.implement.TourneeServiceImpl;

@ExtendWith(MockitoExtension.class)
public class TourneeServiceTest {

    @Mock
    private TourneeRepository tourneeRepository;

    @Mock
    private ColisRepository colisRepository;

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
    private Colis colis;

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
        tourneeRequestDto.setColisIds(new ArrayList<>());

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

        colis = new Colis();
        colis.setId(UUID.randomUUID());
        colis.setStatut(com.ipnet.enums.StatutColis.PRIS_EN_CHARGE);
    }

    @Test
    void testCreateTournee_Success() {
        when(userRepository.findById(any())).thenReturn(java.util.Optional.of(livreur));
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
        when(userRepository.findById(any())).thenReturn(java.util.Optional.empty());

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
    void testAddColisToTournee_Success() {
        when(tourneeRepository.findById(any())).thenReturn(java.util.Optional.of(tournee));
        when(colisRepository.findById(any())).thenReturn(java.util.Optional.of(colis));
        when(colisRepository.save(any(Colis.class))).thenReturn(colis);
        when(tourneeRepository.findById(any())).thenReturn(java.util.Optional.of(tournee));
        when(tourneeMapper.toDto(any(Tournee.class))).thenReturn(tourneeDto);

        TourneeDto result = tourneeService.addColisToTournee(tournee.getId(), colis.getId());

        assertNotNull(result);
        verify(colisRepository, times(1)).save(any(Colis.class));
    }

    @Test
    void testAddColisToTournee_ColisDejaAssigne() {
        colis.setTournee(tournee);

        when(tourneeRepository.findById(any())).thenReturn(java.util.Optional.of(tournee));
        when(colisRepository.findById(any())).thenReturn(java.util.Optional.of(colis));

        assertThrows(RuntimeException.class, () -> tourneeService.addColisToTournee(tournee.getId(), colis.getId()));
    }

    @Test
    void testRemoveColisFromTournee_Success() {
        colis.setTournee(tournee);

        when(tourneeRepository.findById(any())).thenReturn(java.util.Optional.of(tournee));
        when(colisRepository.findById(any())).thenReturn(java.util.Optional.of(colis));
        when(colisRepository.save(any(Colis.class))).thenReturn(colis);
        when(tourneeRepository.findById(any())).thenReturn(java.util.Optional.of(tournee));
        when(tourneeMapper.toDto(any(Tournee.class))).thenReturn(tourneeDto);

        TourneeDto result = tourneeService.removeColisFromTournee(tournee.getId(), colis.getId());

        assertNotNull(result);
        verify(colisRepository, times(1)).save(any(Colis.class));
    }

    @Test
    void testDeleteTournee_Success() {
        when(tourneeRepository.findById(any())).thenReturn(java.util.Optional.of(tournee));
        when(colisRepository.save(any(Colis.class))).thenReturn(colis);

        tourneeService.delete(tournee.getId());

        verify(tourneeRepository, times(1)).delete(any(Tournee.class));
    }
}
