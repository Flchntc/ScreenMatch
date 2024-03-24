package br.com.alura.screenmatch.repository;

import br.com.alura.screenmatch.model.Categoria;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.model.Serie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SerieRepository extends JpaRepository<Serie, Long> {
    Optional <Serie> findByTitleContainingIgnoreCase(String nomeSerie);

    List<Serie> findByAtoresContainingIgnoreCaseAndRatingGreaterThanEqual(String nomeAtor, Double rating);

    List<Serie> findTop5ByOrderByRatingDesc();

    List<Serie> findByGenero(Categoria categoria);

    @Query("SELECT s FROM Serie s WHERE s.totalSeasons <= :totalSeasons AND s.rating >= :rating")
    List<Serie> seriesPorTemporadaEAvaliacao(Integer totalSeasons, Double rating);

    @Query("SELECT e FROM Serie s JOIN s.episodios e WHERE e.title ILIKE %:trechoEpisodio%")
    List<Episodio> episodiosPorTrecho(String trechoEpisodio);

    @Query("SELECT e FROM Serie s JOIN s.episodios e WHERE s = :serie ORDER BY e.rating DESC LIMIT 5")
    List<Episodio> topEpisodiosPorSerie(Serie serie);

    @Query("SELECT e FROM Serie s JOIN s.episodios e WHERE s = :serie AND YEAR(e.releaseDate) >= :anoLancamento")
    List<Episodio> episodiosPorSerieEAno(Serie serie, int anoLancamento);
}
