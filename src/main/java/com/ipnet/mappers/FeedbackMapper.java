package com.ipnet.mappers;

import com.ipnet.dto.FeedbackDto;
import com.ipnet.entity.CommentaireEntity;
import com.ipnet.entity.NoteEntity;
import com.ipnet.entity.TrajetEntity;
import org.springframework.stereotype.Component;

@Component
public class FeedbackMapper {

    public NoteEntity toNoteEntity(FeedbackDto dto, TrajetEntity trajet) {
        if (dto == null) return null;
        NoteEntity note = new NoteEntity();
        note.setValeur(dto.getNoteValeur());
        note.setTrajet(trajet);
        note.setCreerPar(dto.getCreerPar());
        return note;
    }

    public CommentaireEntity toCommentaireEntity(FeedbackDto dto, TrajetEntity trajet) {
        if (dto == null) return null;
        CommentaireEntity com = new CommentaireEntity();
        com.setContenu(dto.getCommentaireTexte());
        com.setTrajet(trajet);
        com.setCreerPar(dto.getCreerPar());
        return com;
    }
}