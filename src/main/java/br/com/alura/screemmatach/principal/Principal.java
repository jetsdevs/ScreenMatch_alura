package br.com.alura.screemmatach.principal;

import br.com.alura.screemmatach.model.*;
import br.com.alura.screemmatach.repository.SerieRepository;
import br.com.alura.screemmatach.service.ConsumoApi;
import br.com.alura.screemmatach.service.ConverteDados;

import java.util.*;
import java.util.stream.Collectors;

public class Principal {

    private Scanner leitura = new Scanner(System.in);
    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=d125a94b";

    private List<DadosSerie> dadosSerie = new ArrayList<>();

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
                    1  - Buscar Séries
                    2  - Buscar Titulo
                    3  - Buscar Episodios
                    4  - Listar serie buscada
                    5  - Buscar por Ator
                    6  - Top 5 series
                    7  - Busca por Categoria
                    8  - Filtrar Series
                    9  - Buscar Episodio por trecho
                    10 - Top Episodio por Serie
                    11 - Buscar Episodio por ano
                                    
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
                    buscarSerieTitulo();
                    break;
                case 3:
                    buscarEpisodioSerie();
                    break;
                case 4:
                    listarSeriesBuscadas();
                    break;
                case 5:
                    buscarSeirePorAtor();
                    break;
                case 6:
                    topCincoSeries();
                case 7:
                    buscarCategoria();
                    break;
                case 8:
                    FiltrarTemporadaAvaliacao();
                    break;
                case 9:
                    buscarTrechoEpisodio();
                    break;
                case 10:
                    topEpisodiosPorSerie();
                    break;
                case 11:
                    buscarEpisodioPorData();
                    break;
                case 0:
                    System.out.println("Agradecemos por utulizar nossos serviços");
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
        System.out.println("Digite o nome do serie: ");
        var nomeSerie = leitura.nextLine();

        var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        return dados;

    }

    private void buscarEpisodioSerie() {
        listarSeriesBuscadas();
        System.out.println("Digite o nome do serie: ");
        var nomeSerie = leitura.nextLine();

        Optional<Serie> serie = repositorio.findByTituloContainingIgnoreCase(nomeSerie);

        if (serie.isPresent()) {

            var serieEncontrada = serie.get();
            List<DadosTemporada> temporadas = new ArrayList<>();

            for (int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++) {
                var json = consumo.obterDados(ENDERECO + serieEncontrada.getTitulo().replace(" ", "+") + "&season=" + i + API_KEY);
                DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
                temporadas.add(dadosTemporada);
            }
            temporadas.forEach(System.out::println);

            List<Episodios> episodios = temporadas.stream()
                    .flatMap(d -> d.episodios().stream()
                            .map(e -> new Episodios(d.temporada(), e)))
                    .collect(Collectors.toList());

            serieEncontrada.setEpisodios(episodios);
            repositorio.save(serieEncontrada);
        } else {
            System.out.println("Serie não localizada!");
        }
    }

    private void listarSeriesBuscadas() {

        series = repositorio.findAll();
        series.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);


    }

    private void buscarSerieTitulo() {
        System.out.println("Digite o nome do serie: ");
        var nomeSerie = leitura.nextLine();

        serieBusca = repositorio.findByTituloContainingIgnoreCase(nomeSerie);

        if (serieBusca.isPresent()) {
            System.out.println("Serie encontrado" + serieBusca.get());
        } else {
            System.out.println("Serie não encontrada!");
        }
    }

    private void buscarSeirePorAtor() {
        System.out.println("Informe o nome do ator: ");
        var nomeAtor = leitura.nextLine();
        System.out.println("Informe o valor da avaliação: ");
        var avaliacao = leitura.nextDouble();

        List<Serie> seriesEcontradas = repositorio.findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(nomeAtor, avaliacao);
        System.out.println("O ator " + nomeAtor + " atuou em: ");
        seriesEcontradas.forEach(s ->
                System.out.println(s.getTitulo() + ": avaliações: " + s.getAvaliacao()));
    }

    private void topCincoSeries() {
        List<Serie> serieTop = repositorio.findTop5ByOrderByAvaliacaoDesc();
        serieTop.forEach(s ->
                System.out.println(s.getTitulo() + " avaliação: " + s.getAvaliacao()));
    }

    private void buscarCategoria() {
        System.out.println("Informe a Categoria | Genero: ");
        var nomeGenero = leitura.nextLine();
        Categoria categoria = Categoria.fromPortugues(nomeGenero);
        List<Serie> serieCategoria = repositorio.findByGenero(categoria);
        System.out.println("Series da Categoria " + nomeGenero);
        serieCategoria.forEach(System.out::println);

    }

    private void FiltrarTemporadaAvaliacao() {
        System.out.println("Infrome o numero maximo de temporadas: ");
        var totalTemporadas = leitura.nextInt();
        leitura.nextLine();
        System.out.println("Informe um valor maximo para avaliação: ");
        var avaliacao = leitura.nextDouble();
        leitura.nextLine();

        List<Serie> filtroSerie = repositorio.seriesPorTemporadasEAvaliacao(totalTemporadas, avaliacao);
        System.out.println("### Séries Filtradas ###");
        filtroSerie.forEach(s ->
                System.out.println(s.getTitulo() + " - Avaliação: " + s.getAvaliacao()));
    }

    private void buscarTrechoEpisodio() {
        System.out.println("Informe o nome do Episodio: ");
        var trechoEpisodio = leitura.nextLine();

        List<Episodios> episodiosEncontrados = repositorio.episodioPorTrecho(trechoEpisodio);
        episodiosEncontrados.forEach(e ->
                System.out.printf("Série: %s \nTemporada: %s  \nEpisódio: %s - %s\n\n",
                        e.getSerie().getTitulo(), e.getTemporada(),
                        e.getNumeroEpisodio(), e.getTitulo()));

    }

    private void topEpisodiosPorSerie() {
        buscarSerieTitulo();
        if (serieBusca.isPresent()) {
            Serie serie = serieBusca.get();
            List<Episodios> topEpisodios = repositorio.topEpisodiosPorSerie(serie);
            topEpisodios.forEach(e ->
                    System.out.printf("Série: %s \nTemporada: %s  \nEpisódio: %s - %s \nAvaliação: %s\n\n",
                            e.getSerie().getTitulo(), e.getTemporada(),
                            e.getNumeroEpisodio(), e.getTitulo(), e.getAvaliacao()));

        }
    }

    private void buscarEpisodioPorData() {
        buscarSerieTitulo();
        if (serieBusca.isPresent()) {
            Serie serie = serieBusca.get();
            System.out.println("Informe a data de lançamento: ");
            var anoLancamento = leitura.nextInt();
            leitura.nextLine();

            List<Episodios> episodiosAno = repositorio.episodioPorSerieAno(serie, anoLancamento);
            episodiosAno.forEach(System.out::println);
        }


    }

}


