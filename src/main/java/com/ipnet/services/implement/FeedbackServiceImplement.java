package com.ipnet.services.implement;

import com.ipnet.dto.FeedbackDto;
import com.ipnet.entity.CommentaireEntity;
import com.ipnet.entity.NoteEntity;
import com.ipnet.entity.TrajetEntity;
import com.ipnet.mappers.FeedbackMapper;
import com.ipnet.repository.CommentaireRepository;
import com.ipnet.repository.NoteRepository;
import com.ipnet.repository.TrajetRepository;
import com.ipnet.services.interfaces.FeedbackServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FeedbackServiceImplement implements FeedbackServiceInterface {

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private CommentaireRepository commentaireRepository;

    @Autowired
    private TrajetRepository trajetRepository;

    @Autowired
    private FeedbackMapper feedbackMapper;

    @Override
    @Transactional(readOnly = true)
    public List<FeedbackDto> getAllFeedbacks() {
        List<NoteEntity> notes = noteRepository.findAll();

        return notes.stream().map(note -> {
            FeedbackDto dto = new FeedbackDto();
            dto.setNoteValeur(note.getValeur());
            dto.setTrajetId(note.getTrajet().getId());
            dto.setCreerPar(note.getCreerPar());

            CommentaireEntity commentaire = commentaireRepository
                    .findTopByTrajetAndCreerParOrderByDateCreationDesc(
                            note.getTrajet(),
                            note.getCreerPar()
                    )
                    .orElse(null);

            dto.setCommentaireTexte(
                    commentaire != null ? commentaire.getContenu() : null
            );

            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void saveFeedback(FeedbackDto dto) {
        TrajetEntity trajet = trajetRepository.findById(dto.getTrajetId())
                .orElseThrow(() ->
                        new RuntimeException("Erreur : Trajet " + dto.getTrajetId() + " introuvable.")
                );

        noteRepository.save(feedbackMapper.toNoteEntity(dto, trajet));
        commentaireRepository.save(feedbackMapper.toCommentaireEntity(dto, trajet));
    }
}