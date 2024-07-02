package br.com.alura.screemmatach.principal;

import br.com.alura.screemmatach.model.DadosSerie;
import br.com.alura.screemmatach.model.DadosTemporada;
import br.com.alura.screemmatach.model.Episodios;
import br.com.alura.screemmatach.model.Serie;
import br.com.alura.screemmatach.repository.SerieRepository;
import br.com.alura.screemmatach.service.ConsumoApi;
import br.com.alura.screemmatach.service.ConverteDados;
import org.springframework.beans.factory.annotation.Autowired;

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

    public Principal(SerieRepository repositorio) {
        this.repositorio = repositorio;
    }


    public void exibeMenu() {
        var opcao = -1;

        while (opcao != 0) {
            var menu = """
                    1 - Buscar Séries
                    2 - Buscar Titulo
                    3 - Buscar Episodios
                    4 - Listar serie buscada
                    5 - Buscar por Ator
                    6 - Top 5 series
                                    
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

        Optional<Serie> serieBuscada = repositorio.findByTituloContainingIgnoreCase(nomeSerie);

        if (serieBuscada.isPresent()) {
            System.out.println("Serie encontrado" + serieBuscada.get());
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
                System.out.println(s.getTitulo() + " avaliação: " +s.getAvaliacao()));
    }

}
//             Todo codigo abaixo é referente a aula de lambida:

//        System.out.println("\nDigite o nome da serie para busca: \n");
//        var nomeSerie = leitura.nextLine();
//        var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
//        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
//        System.out.println(dados);
//
//        List<DadosTemporada> temporadas = new ArrayList<>();
//
//        for (int i = 1; i <= dados.totalTemporadas(); i++) {
//            json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + "&season=" + i + API_KEY);
//            DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
//            temporadas.add(dadosTemporada);
//        }
//        temporadas.forEach(System.out::println);

//        for (int i = 0; i < dados.totalTemporadas(); i++) {
//            List<DadosEpisodios> episodiosTemporada = temporadas.get(i).episodios();
//            for (int j = 0; j < episodiosTemporada.size(); j++) {
//                System.out.println(episodiosTemporada.get(j).titulo());
//            }
//        }

//        temporadas.forEach(t -> t.episodios().forEach(e -> System.out.println(e.titulo())));
//
//        List<DadosEpisodios> dadosEpisodios = temporadas.stream()
//                .flatMap(t -> t.episodios().stream())
//                .collect(Collectors.toList());
//
//        System.out.println("\nTop 5 Episodios:\n");
//        dadosEpisodios.stream()
//                .filter(e -> !e.avaliacao().equalsIgnoreCase("N/A"))
//                .sorted(Comparator.comparing(DadosEpisodios::avaliacao).reversed())
//                .limit(5)
//                .map(e -> e.titulo().toUpperCase())
//                .forEach(System.out::println);
//
//        List<Episodios> episodios = temporadas.stream()
//                .flatMap(t -> t.episodios().stream()
//                        .map(d -> new Episodios(t.temporada(), d))
//                ).collect(Collectors.toList());
//
//        episodios.forEach(System.out::println);
//
//        System.out.println("Informe o trecho do episodio desejado: ");
//        var trechoTitulo = leitura.nextLine();
//        Optional<Episodios> episodiosBucar = episodios.stream()
//                .filter(e -> e.getTitulo().toUpperCase().contains(trechoTitulo.toUpperCase()))
//                .findFirst();
//        if (episodiosBucar.isPresent()) {
//            System.out.println("Episodio encontrado");
//            System.out.println("Temporada: " + episodiosBucar.get().getTemporada());
//        } else {
//            System.out.println("Episodio não encontrado!");
//        }

//
//        System.out.println("Informe o ano do episodio desejado: ");
//        var ano = leitura.nextInt();
//        leitura.nextLine();
//
//        LocalDate dataBusca = LocalDate.of(ano, 1, 1);
//
//        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");
//        episodios.stream()
//                .filter(e -> e.getDataLancamento() != null && e.getDataLancamento().isAfter(dataBusca))
//                .forEach(e -> System.out.println(
//                        "Temporada: " + e.getTemporada() +
//                                " Episodio: " + e.getTitulo() +
//                                "Data dfe Lançamento: " + e.getDataLancamento().format(formatador)
//                ));

//        Map<Integer, Double> avaliacoesPorTemporada = episodios.stream()
//                .filter(e -> e.getAvaliacao() > 0.0)
//                .collect(Collectors.groupingBy(Episodios::getTemporada,
//                        Collectors.averagingDouble(Episodios::getAvaliacao)));
//        System.out.println(avaliacoesPorTemporada);
//
//        DoubleSummaryStatistics est = episodios.stream()
//                .filter(e -> e.getAvaliacao() > 0.0)
//                .collect(Collectors.summarizingDouble(Episodios::getAvaliacao));
//        System.out.println("Media: " + est.getAverage());
//        System.out.println("Melhor episodio: " + est.getMax());
//        System.out.println("Pior episodio: " + est.getMin());
//        System.out.println("Quantidade: " + est.getCount());
//
//

