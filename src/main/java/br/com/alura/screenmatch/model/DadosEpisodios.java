package br.com.alura.screenmatch.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DadosEpisodios(@JsonAlias("Title") String title,
                             @JsonAlias("Episode") Integer epNum,
                             @JsonAlias("imdbRating") String rating,
                             @JsonAlias("Released") String releaseDate) {
    
}
