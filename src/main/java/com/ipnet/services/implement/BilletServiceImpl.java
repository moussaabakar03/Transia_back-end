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
        BilletEntity billet = trouverBilletEntity(qrCode);

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

    @Override
    public BilletDto rechercherParQrCode(String qrCode) {
        BilletEntity billet = trouverBilletEntity(qrCode);
        return billetMapper.toDto(billet);
    }

    private BilletEntity trouverBilletEntity(String qrCode) {
        if (qrCode == null || qrCode.trim().isEmpty()) {
            throw new ResourceNotFoundException("Billet introuvable pour ce QR code");
        }
        String cleanQr = qrCode.trim();

        // 1. Recherche directe par qrCode enregistré
        java.util.Optional<BilletEntity> opt = billetRepository.findByQrCode(cleanQr);
        if (opt.isPresent()) return opt.get();

        // 2. Recherche directe par ID de billet
        try {
            UUID id = UUID.fromString(cleanQr);
            opt = billetRepository.findById(id);
            if (opt.isPresent()) return opt.get();
        } catch (Exception ignored) {}

        // 3. Fallback pour chaînes composées avec RESERVATION_ID:
        if (cleanQr.contains("RESERVATION_ID:")) {
            try {
                int start = cleanQr.indexOf("RESERVATION_ID:") + "RESERVATION_ID:".length();
                int end = cleanQr.indexOf("|", start);
                String resIdStr = (end != -1) ? cleanQr.substring(start, end) : cleanQr.substring(start);
                UUID resId = UUID.fromString(resIdStr.trim());
                List<BilletEntity> list = billetRepository.findByReservation_Id(resId);
                if (!list.isEmpty()) {
                    return list.get(0);
                }
            } catch (Exception ignored) {}
        }

        throw new ResourceNotFoundException("Billet introuvable pour ce QR code");
    }

    @Override
    public List<BilletDto> getBilletsByTrajet(UUID trajetId) {
        return billetRepository.findByReservation_Trajet_Id(trajetId)
                .stream()
                .map(billetMapper::toDto)
                .toList();
    }
}