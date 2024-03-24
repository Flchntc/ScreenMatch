package br.com.alura.screenmatch.model;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.persistence.*;

@Entity
@Table(name = "episodios")
public class Episodio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer temporada;
    private String title;
    private Integer epNum;
    private Double rating;
    private LocalDate releaseDate;
    @ManyToOne
    private Serie serie;

    public Episodio(){}
    public Episodio(Integer seasonNum, DadosEpisodios dadosEpisodios) {
        this.temporada = seasonNum;
        this.title = dadosEpisodios.title();
        this.epNum = dadosEpisodios.epNum();
        try {
            this.rating = Double.valueOf(dadosEpisodios.rating());
        } catch(NumberFormatException ex){
            this.rating = 0.0;
        }
        try {
            this.releaseDate = LocalDate.parse(dadosEpisodios.releaseDate());
        } catch (DateTimeParseException ex) {
            this.releaseDate = null;
        }
        
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Serie getSerie() {
        return serie;
    }

    public void setSerie(Serie serie) {
        this.serie = serie;
    }

    public Integer getEpNum() {
        return epNum;
    }

    public Double getRating() {
        return rating;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public Integer getTemporada() {
        return temporada;
    }

    public String getTitle() {
        return title;
    }

    public void setEpNum(Integer epNum) {
        this.epNum = epNum;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public void setTemporada(Integer temporada) {
        this.temporada = temporada;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "temporada = " + temporada +
                ", titulo ='" + title + '\''+
                ", episodio = " + epNum +
                ", avaliacao = " + rating +
                ", data de lancamento= " + releaseDate;
    }

}
