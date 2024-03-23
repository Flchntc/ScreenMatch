package br.com.alura.screenmatch.model;

import br.com.alura.screenmatch.service.ConsultaChatGPT;
import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;
import br.com.alura.screenmatch.model.*;

@Entity
@Table(name = "series")
public class Serie{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String title;
    private Integer totalSeasons;
    private Double rating;
    @Enumerated(EnumType.STRING)
    private Categoria genero;
    private String atores;
    private String sinopse;
    private String poster;
    @Transient
    private List<Episodio> episodios = new ArrayList<>();

    public Serie() {}
    public Serie(DadosSerie dadosSerie){
        this.title = dadosSerie.title();
        this.totalSeasons = dadosSerie.totalSeasons();
        this.rating= OptionalDouble.of(Double.valueOf(dadosSerie.rating())).orElse(0);
        this.genero = Categoria.fromString(dadosSerie.genero().split(",")[0].trim());
        this.atores = dadosSerie.atores();
        this.poster = dadosSerie.poster();
        this.sinopse = ConsultaChatGPT.obterTraducao(dadosSerie.sinopse()).trim();
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getTotalSeasons() {
        return totalSeasons;
    }

    public void setTotalSeasons(Integer totalSeasons) {
        this.totalSeasons = totalSeasons;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Categoria getGenero() {
        return genero;
    }

    public void setGenero(Categoria genero) {
        this.genero = genero;
    }

    public String getAtores() {
        return atores;
    }

    public void setAtores(String atores) {
        this.atores = atores;
    }

    public String getSinopse() {
        return sinopse;
    }

    public void setSinopse(String sinopse) {
        this.sinopse = sinopse;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    @Override
    public String toString() {
        return
                "gênero=" + genero +
                ", Título='" + title + '\'' +
                ", totalTemporadas=" + totalSeasons +
                ", avaliação=" + rating +
                ", atores='" + atores + '\'' +
                ", sinopse='" + sinopse + '\'' +
                ", poster='" + poster + '\'' ;
    }

    public List<Episodio> getEpisodios() {
        return episodios;
    }

    public void setEpisodios(List<Episodio> episodios) {
        this.episodios = episodios;
    }
}
