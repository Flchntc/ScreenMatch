package br.com.alura.screenmatch.dto;

import br.com.alura.screenmatch.model.Categoria;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

public record SerieDTO(Long id,
                       String title,
                       Integer totalSeasons,
                       Double rating,
                       Categoria genero,
                       String atores,
                       String poster,
                       String sinopse) {
}
