package com.ipnet.services.implement;

import java.util.List;

import com.ipnet.dto.FeedbackDto;
import com.ipnet.entity.NoteEntity;
import com.ipnet.entity.TrajetEntity;
import com.ipnet.mappers.FeedbackMapper;
import com.ipnet.repository.CommentaireRepository;
import com.ipnet.repository.NoteRepository;
import com.ipnet.repository.TrajetRepository;
import com.ipnet.services.interfaces.FeedbackServiceInterface;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ipnet.services.interfaces.FeedbackServiceInterface;

@Service
public class FeedbackServiceImplement implements FeedbackServiceInterface {

    @Autowired private NoteRepository noteRepository;
    @Autowired private CommentaireRepository commentaireRepository;
    @Autowired private TrajetRepository trajetRepository;
    @Autowired private FeedbackMapper feedbackMapper;

    

    @Override
    @Transactional
    public List<FeedbackDto> getAllFeedbacks() {
        List<NoteEntity> notes = noteRepository.findAll();
        // On transforme la liste des notes en liste de DTO pour l'affichage
        return notes.stream().map(note -> {
            FeedbackDto d = new FeedbackDto();
            d.setNoteValeur(note.getValeur());
            d.setTrajetId(note.getTrajet().getId());
            d.setCreerPar(note.getCreerPar());
            return d;
        }).collect(Collectors.toList());
    }
    public void saveFeedback(FeedbackDto dto) {
        TrajetEntity trajet = trajetRepository.findById(dto.getTrajetId())
                .orElseThrow(() -> new RuntimeException("Erreur : Trajet " + dto.getTrajetId() + " introuvable."));

        // Utilisation du Mapper pour créer les entités
        noteRepository.save(feedbackMapper.toNoteEntity(dto, trajet));
        commentaireRepository.save(feedbackMapper.toCommentaireEntity(dto, trajet));
    }
}