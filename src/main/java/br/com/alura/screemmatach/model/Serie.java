package br.com.alura.screemmatach.model;

import br.com.alura.screemmatach.service.ConsultaChatGpt;
import br.com.alura.screemmatach.service.traducao.ConsultaMyMemory;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;

@Entity
@Table(name = "series")
public class Serie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true)
    private String titulo;
    private String anoDeLancamento;
    private Integer totalTemporadas;
    private Double avaliacao;

    @Enumerated(EnumType.STRING)
    private Categoria genero;

    private String atores;
    private String poster;
    private String sinopse;

    //mapeamento de um para muitos Serie<>Episodios
    @OneToMany(mappedBy = "serie", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Episodios> episodios = new ArrayList<>();

    //contrutor padrão
    public Serie() {
    }


    public Serie(DadosSerie dadosSerie) {
        this.titulo = dadosSerie.titulo();
        this.anoDeLancamento = dadosSerie.anoDeLancamento();
        this.totalTemporadas = dadosSerie.totalTemporadas();
        this.avaliacao = OptionalDouble.of(Double.valueOf(dadosSerie.avaliacao())).orElse(0.0);
        this.genero = Categoria.fromString(dadosSerie.genero().split(",")[0].trim());
        this.atores = dadosSerie.atores();
        this.poster = dadosSerie.poster();
        this.sinopse = ConsultaMyMemory.obterTraducao(dadosSerie.sinopse()).trim();

    }

    public List<Episodios> getEpisodios() {
        return episodios;
    }

    public void setEpisodios(List<Episodios> episodios) {
        episodios.forEach(e -> e.setSerie(this));
        this.episodios = episodios;
    }

    public long getId() {

        return id;
    }

    public void setId(long id) {

        this.id = id;
    }

    public String getTitulo() {

        return titulo;
    }

    public void setTitulo(String titulo) {

        this.titulo = titulo;
    }

    public String getAnoDeLancamento() {

        return anoDeLancamento;
    }

    public void setAnoDeLancamento(String anoDeLancamento) {

        this.anoDeLancamento = anoDeLancamento;
    }

    public Integer getTotalTemporadas() {

        return totalTemporadas;
    }

    public void setTotalTemporadas(Integer totalTemporadas) {

        this.totalTemporadas = totalTemporadas;
    }

    public Double getAvaliacao() {

        return avaliacao;
    }

    public void setAvaliacao(Double avaliacao) {

        this.avaliacao = avaliacao;
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

    public String getPoster() {

        return poster;
    }

    public void setPoster(String poster) {

        this.poster = poster;
    }

    public String getSinopse() {

        return sinopse;
    }

    public void setSinopse(String sinopse) {

        this.sinopse = sinopse;
    }

    @Override
    public String toString() {
        return
                " titulo = " + titulo + '\'' +
                        " genero = " + genero + '\'' +
                        ", episodios = " + episodios + '\'' +
                        ", anoDeLancamento = " + anoDeLancamento + '\'' +
                        ", totalTemporadas = " + totalTemporadas +
                        ", sinopse = " + sinopse + '\'' +
                        ", atores = " + atores + '\'' +
                        ", avaliacao = " + avaliacao +
                        ", poster = " + poster + '\'' +
                        '}';
    }
}
