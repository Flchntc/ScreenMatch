package br.com.alura.screenmatch.principal;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.*;

import br.com.alura.screenmatch.model.DadosEpisodios;
import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;

public class Principal {

    private Scanner leitura = new Scanner(System.in);
    
    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();

    private final String ENDERECO= "https://www.omdbapi.com/?t=";
    
    private final String API_KEY= "&apikey=fcdf2dd7";

    public void exibeMenu(){
        
        System.out.println("Digite o nome da série para a busca:");
        var nomeSerie = leitura.nextLine();

		var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        
        System.out.println(dados);

        List<DadosTemporada> temporadas = new ArrayList<>();
		for (int i = 1; i <= dados.totalSeasons(); i++){
			json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + "&season="+ i + API_KEY);
			DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
			temporadas.add(dadosTemporada);
		}
		temporadas.forEach(System.out::println);

        temporadas.forEach(t -> t.episodes().forEach(e -> System.out.println(e.title())));


        
        List<DadosEpisodios> dadosEpisodios = temporadas.stream()
                .flatMap(t -> t.episodes().stream())
                .collect(Collectors.toList());

        // System.out.println("\nTOP 10 EPISÓDIOS");
        // dadosEpisodios.stream()
        //         .filter(e -> !e.rating().equalsIgnoreCase("N/A"))
        //         .peek(e -> System.out.println(" Primeiro filtro(N/A) " + e))
        //         .sorted(Comparator.comparing(DadosEpisodios::rating).reversed())
        //         .peek(e -> System.out.println(" Ordenação " + e))
        //         .limit(10)
        //         .peek(e -> System.out.println(" Limite " + e))
        //         .map(e -> e.title().toUpperCase())
        //         .peek(e -> System.out.println(" Mapeamento " + e))
        //         .forEach(System.out::println);
            
        List<Episodio> episodios = temporadas.stream().
                flatMap(t -> t.episodes().stream().
                    map(d -> new Episodio(t.seasonNum(), d))
                ).collect(Collectors.toList());

        episodios.forEach(System.out::println);

        // System.out.println("Digite o nome do episódio que deseja buscar: ");
        // var trechoTitulo = leitura.nextLine();
        // Optional<Episodio> epBuscado = episodios.stream()
        //         .filter(e -> e.getTitle().toUpperCase().contains(trechoTitulo.toUpperCase()))
        //         .findFirst();
        // if(epBuscado.isPresent()){
        //     System.out.println("Episódio encontrado!");
        //     System.out.println("Temporada: " + epBuscado.get().getTemporada());
        // } else {
        //     System.out.println("Episódio não encontrado!");
        // }

        // System.out.println("A partir de que ano voce deseja ver os episódios? ");
        // var ano = leitura.nextInt();
        // leitura.nextLine();

        // LocalDate dataBusca = LocalDate.of(ano,1,1);

        // DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        // episodios.stream() 
        //         .filter(e -> e.getReleaseDate() != null && e.getReleaseDate().isAfter(dataBusca))
        //         .forEach(e -> System.out.println(
        //                 "Temporada: " + e.getTemporada() +
        //                     " Episodio: " + e.getTitle() +
        //                     " Data lançamento: " + e.getReleaseDate().format(formatador)
        //         ));

        Map<Integer, Double> avaliacoesPorTemporada = episodios.stream()
                .filter(e -> e.getRating() > 0.0)
                .collect(Collectors.groupingBy(Episodio::getTemporada, 
                            Collectors.averagingDouble(Episodio::getRating)));
        System.out.println(avaliacoesPorTemporada);

        DoubleSummaryStatistics est = episodios.stream()
                .filter(e -> e.getRating() > 0.0)
                .collect(Collectors.summarizingDouble(Episodio::getRating));
        System.out.println("Média: " + est.getAverage());
        System.out.println("Melhor episódio: " + est.getMax());
        System.out.println("Pior episódio: " + est.getMin());
        System.out.println("Quantidade: " + est.getCount());
    }
}