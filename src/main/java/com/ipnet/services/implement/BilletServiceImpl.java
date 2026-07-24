package com.ipnet.services.implement;

import com.ipnet.dto.BilletDto;
import com.ipnet.entity.BilletEntity;
import com.ipnet.enums.StatutBillet;
import com.ipnet.exception.BilletNonValidableException;
import com.ipnet.mappers.BilletMapper;
import com.ipnet.repository.BilletRepository;
import com.ipnet.security.exception.ResourceNotFoundException;
import com.ipnet.services.interfaces.BilletServiceInterface;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class BilletServiceImpl implements BilletServiceInterface {

    private final BilletRepository billetRepository;
    private final BilletMapper billetMapper;

    public BilletServiceImpl(BilletRepository billetRepository, BilletMapper billetMapper) {
        this.billetRepository = billetRepository;
        this.billetMapper = billetMapper;
    }

    @Override
    @Transactional
    public BilletDto validerBillet(String qrCode) {
        BilletEntity billet = billetRepository.findByQrCode(qrCode)
                .orElseThrow(() -> new ResourceNotFoundException("Billet introuvable pour ce QR code"));

        if (billet.getStatut() == StatutBillet.ANNULE) {
            throw new BilletNonValidableException("Ce billet est annulé");
        }
        if (billet.getStatut() == StatutBillet.UTILISE) {
            throw new BilletNonValidableException("Ce billet a déjà été utilisé");
        }
        if (billet.getStatut() == StatutBillet.EN_ATTENTE) {
            throw new BilletNonValidableException("Ce billet n'est pas encore payé — paiement requis avant l'embarquement");
        }

        billet.setStatut(StatutBillet.UTILISE);
        return billetMapper.toDto(billetRepository.save(billet));
    }

    // Lecture seule, contrairement à validerBillet : sert au chauffeur pour comprendre pourquoi
    // un QR scanné n'appartient pas au trajet en cours (billet d'un autre trajet, pas encore payé...)
    // sans marquer le billet comme utilisé.
    @Override
    public BilletDto rechercherParQrCode(String qrCode) {
        BilletEntity billet = billetRepository.findByQrCode(qrCode)
                .orElseThrow(() -> new ResourceNotFoundException("Billet introuvable pour ce QR code"));
        return billetMapper.toDto(billet);
    }

    @Override
    public List<BilletDto> getBilletsByTrajet(UUID trajetId) {
        return billetRepository.findByReservation_Trajet_Id(trajetId)
                .stream()
                .map(billetMapper::toDto)
                .toList();
    }
}