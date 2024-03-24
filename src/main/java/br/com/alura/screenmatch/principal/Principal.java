package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.*;
import br.com.alura.screenmatch.repository.SerieRepository;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;

public class Principal {

    private Scanner leitura = new Scanner(System.in);
    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=fcdf2dd7";

    private List<DadosSerie> dadosSeries = new ArrayList<>();

    private SerieRepository repositorio;

    private List<Serie> series = new ArrayList<>();

    private Optional<Serie> serieBusca;

    public Principal(SerieRepository repositorio) {
        this.repositorio = repositorio;
    }

    public void exibeMenu() {
        var opcao = -1;
        while (opcao != 0) {
            var menu = """
                    1 - Buscar séries
                    2 - Buscar episódios
                    3 - Listar series buscadas
                    4 - Buscar séries por titulo
                    5 - Buscar séries por ator
                    6 - Buscar top 5 séries
                    7 - Buscar séries por categoria
                    8 - Buscar séries por número de temporadas
                    9 - Buscar episódio por trecho do título
                    10 - Buscar top 5 episódios de determinada série
                    11 - Buscar episodios por data de lançamento
                                    
                    0 - Sair                                
                    """;

            System.out.println(menu);
            opcao = leitura.nextInt();
            leitura.nextLine();

            switch (opcao) {
                case 1:
                    buscarSerieWeb();
                    break;
                case 2:
                    buscarEpisodioPorSerie();
                    break;
                case 3:
                    listarSeriesBuscadas();
                    break;
                case 4:
                    buscarSeriePorTitulo();
                    break;
                case 5:
                    buscarSeriesPorAtor();
                    break;
                case 6:
                    buscarTopSeries();
                    break;
                case 7:
                    buscarSeriesPorCategoria();
                    break;
                case 8:
                    buscarSeriesPorTemporada();
                    break;
                case 9:
                    buscarEpisodioPorTrecho();
                    break;
                case 10:
                    topEpisodiosPorSerie();
                    break;
                case 11:
                    buscarEpisodiosPorData();
                    break;
                case 0:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção inválida");
            }
        }
    }

    private void buscarSerieWeb() {
        DadosSerie dados = getDadosSerie();
        Serie serie = new Serie(dados);
        //dadosSeries.add(dados);
        repositorio.save(serie);
        System.out.println(dados);
    }

    private DadosSerie getDadosSerie() {
        System.out.println("Digite o nome da série para busca");
        var nomeSerie = leitura.nextLine();
        var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        return dados;
    }

    private void buscarEpisodioPorSerie(){
        listarSeriesBuscadas();
        System.out.println("Escolha uma série pelo nome:");
        var nomeSerie = leitura.nextLine();

        Optional<Serie> serie = series.stream()
                .filter(s -> s.getTitle().toLowerCase().contains(nomeSerie.toLowerCase()))
                .findFirst();
        if(serie.isPresent()) {

            var serieEncontrada = serie.get();
            List<DadosTemporada> temporadas = new ArrayList<>();

            for (int i = 1; i <= serieEncontrada.getTotalSeasons(); i++) {
                var json = consumo.obterDados(ENDERECO + serieEncontrada.getTitle().replace(" ", "+") + "&season=" + i + API_KEY);
                DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
                temporadas.add(dadosTemporada);
            }
            temporadas.forEach(System.out::println);

            List<Episodio> episodios = temporadas.stream()
                    .flatMap(d -> d.episodes().stream()
                            .map(e -> new Episodio(d.seasonNum(), e)))
                    .collect(Collectors.toList());
            serieEncontrada.setEpisodios(episodios);
            repositorio.save(serieEncontrada);

        } else {
            System.out.println("Série não encontrada no banco de dados");
        }
    }
    private void listarSeriesBuscadas(){
        series = repositorio.findAll();
        series.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);
    }


    private void buscarSeriePorTitulo() {
        System.out.println("Digite o nome da serie para a busca dos episodios");
        var nomeSerie = leitura.nextLine();
        serieBusca = repositorio.findByTitleContainingIgnoreCase(nomeSerie);

        if(serieBusca.isPresent()){
            System.out.println("Dados da série: " + serieBusca.get());
        } else {
            System.out.println("Dados da série nao encontrados: ");
        }
    }

    private void buscarSeriesPorAtor() {
        System.out.println("Qual nome para busca?");
        var nomeAtor = leitura.nextLine();
        System.out.println("Avaliações a partir de que valor ?");
        var avaliacao = leitura.nextDouble();
        List<Serie> seriesEcontradas = repositorio.findByAtoresContainingIgnoreCaseAndRatingGreaterThanEqual(nomeAtor, avaliacao);
        System.out.println("Séries em que " +nomeAtor + "trabalhou");
        seriesEcontradas.forEach(s ->
                System.out.println(s.getTitle() + " avaliação: " + s.getRating()));

    }

    private void buscarTopSeries() {
        List<Serie> serieTop = repositorio.findTop5ByOrderByRatingDesc();
        serieTop.forEach(s ->
                System.out.println(s.getTitle() + " avaliação: " + s.getRating()));
    }

    private void buscarSeriesPorCategoria() {
        System.out.println("Deseja buscar séries de que categoria/gênero? ");
        var nomeGenero = leitura.nextLine();
        Categoria categoria = Categoria.fromPortugues(nomeGenero);
        List<Serie> seriesPorCategoria = repositorio.findByGenero(categoria);
        System.out.println("Series da categoria " + nomeGenero);
        seriesPorCategoria.forEach(System.out::println);
    }

    private void buscarSeriesPorTemporada() {
        System.out.println("Até quantas temporadas para a busca? ");
        var totalSeasons = leitura.nextInt();
        leitura.nextLine();
        System.out.println("Avaliações a partir de que valor ?");
        var avaliacao = leitura.nextDouble();

        List <Serie> seriesPorTemporada = repositorio.seriesPorTemporadaEAvaliacao(totalSeasons,avaliacao);
        System.out.println("Séries com até " +totalSeasons + " temporadas encontradas");
        seriesPorTemporada.forEach(s ->
                System.out.println(s.getTitle() + ", avaliação: " + s.getRating()));
    }

    private void buscarEpisodioPorTrecho() {
        System.out.println("Qual nome do episódio para busca?");
        var trechoEpisodio = leitura.nextLine();
        List<Episodio> episodiosEcontrados = repositorio.episodiosPorTrecho(trechoEpisodio);
        episodiosEcontrados.forEach(e ->
                System.out.printf("Série: %s Temporada %s - Episódio %s - %s\n",
                        e.getSerie().getTitle(), e.getTemporada(),
                        e.getEpNum(), e.getTitle()));
    }

    private void topEpisodiosPorSerie() {
        buscarSeriePorTitulo();
        if(serieBusca.isPresent()){
            Serie serie = serieBusca.get();
            List<Episodio> topEpisodios = repositorio.topEpisodiosPorSerie(serie);
            topEpisodios.forEach(e ->
                    System.out.printf("Série: %s Temporada %s - Episódio %s - %s - Avaliação %.1f\n",
                            e.getSerie().getTitle(), e.getTemporada(),
                            e.getEpNum(), e.getTitle(), e.getRating()));
        }
    }
    private void buscarEpisodiosPorData() {
        buscarSeriePorTitulo();
        if(serieBusca.isPresent()){
            Serie serie = serieBusca.get();
            System.out.println("Digite o ano limite de lançamento");
            var anoLancamento = leitura.nextInt();
            leitura.nextLine();

            List<Episodio> episodiosAno = repositorio.episodiosPorSerieEAno(serie, anoLancamento);
            episodiosAno.forEach(System.out::println);
        }
    }

}