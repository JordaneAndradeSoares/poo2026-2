package com.poo2026_2.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.DoubleConsumer;

import com.poo2026_2.model.Cronograma;
import com.poo2026_2.model.Onda;

import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

/**
 * Linha do tempo da fase (canto inferior direito): mostra o andamento da fase,
 * onde comeca cada onda e tem os botoes de velocidade do jogo (1x, 1.25x,
 * 1.5x e 2x).
 *
 * Tudo vem do Cronograma da fase (que vem do niveis.json). Passe o mouse em
 * cima da bandeira de uma onda para ver quais zumbis vem nela.
 */
public final class LinhaDoTempo extends Pane {

    /** As velocidades que o jogador pode escolher. */
    public static final double[] VELOCIDADES = {1.0, 1.25, 1.5, 2.0};

    public static final double LARGURA = 520;
    public static final double ALTURA = 112;

    private static final double MARGEM = 20;
    private static final double LARGURA_BARRA = LARGURA - 2 * MARGEM;
    private static final double Y_BARRA = 54;
    private static final double ALTURA_BARRA = 14;

    private static final Color COR_TRILHO = Color.web("#1c110a");
    private static final Color COR_PROGRESSO = Color.web("#f4a23a");
    private static final Color COR_BANDEIRA = Color.web("#fff3d6");
    private static final Color COR_BANDEIRA_GRANDE = Color.web("#de3423");

    private final Cronograma cronograma;

    private final Rectangle preenchimento = new Rectangle(0, ALTURA_BARRA);
    private final Circle marcador = new Circle(9, Color.web("#fff3d6"));
    private final Label lblOnda = new Label();
    private final Label lblTempo = new Label();
    private final ToggleGroup grupo = new ToggleGroup();
    private final List<ToggleButton> botoes = new ArrayList<>();

    private DoubleConsumer aoMudarVelocidade = v -> { };

    public LinhaDoTempo(Cronograma cronograma) {
        this.cronograma = cronograma;

        setPrefSize(LARGURA, ALTURA);
        setMinSize(LARGURA, ALTURA);
        setMaxSize(LARGURA, ALTURA);
        setPickOnBounds(false);   // so os botoes e as bandeiras pegam o mouse; o resto deixa clicar nas casas

        Region fundo = new Region();
        fundo.getStyleClass().add("linha-tempo");
        fundo.setPrefSize(LARGURA, ALTURA);
        fundo.setMouseTransparent(true);
        getChildren().add(fundo);

        montarTextos();
        montarBarra();
        montarBotoes();
        atualizar();
    }

    /** Chamado quando o jogador escolhe uma velocidade (ex.: 1.5). */
    public void setAoMudarVelocidade(DoubleConsumer aoMudar) {
        this.aoMudarVelocidade = aoMudar != null ? aoMudar : v -> { };
    }

    /** Marca o botao da velocidade informada (sem avisar o ouvinte). */
    public void setVelocidade(double velocidade) {
        for (int i = 0; i < VELOCIDADES.length; i++) {
            botoes.get(i).setSelected(Math.abs(VELOCIDADES[i] - velocidade) < 1e-9);
        }
    }

    /** Chame a cada tick: anda a barra, o marcador e os textos. */
    public void atualizar() {
        double duracao = Math.max(cronograma.getDuracao(), 0.001);
        double decorrido = Math.min(cronograma.getDecorrido(), duracao);
        double progresso = decorrido / duracao;

        preenchimento.setWidth(LARGURA_BARRA * progresso);
        marcador.setCenterX(MARGEM + LARGURA_BARRA * progresso);
        lblTempo.setText(formatar(decorrido) + " / " + formatar(duracao));
        lblOnda.setText(textoDaOnda());
    }

    // ===================== montagem =====================

    private void montarTextos() {
        lblOnda.getStyleClass().add("linha-tempo-texto");
        lblOnda.setLayoutX(MARGEM);
        lblOnda.setLayoutY(10);
        lblOnda.setPrefWidth(LARGURA_BARRA - 140);
        lblOnda.setMouseTransparent(true);

        lblTempo.getStyleClass().add("linha-tempo-texto");
        lblTempo.setLayoutX(LARGURA - MARGEM - 140);
        lblTempo.setLayoutY(10);
        lblTempo.setPrefWidth(140);
        lblTempo.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
        lblTempo.setMouseTransparent(true);

        getChildren().addAll(lblOnda, lblTempo);
    }

    private void montarBarra() {
        Rectangle trilho = new Rectangle(MARGEM, Y_BARRA, LARGURA_BARRA, ALTURA_BARRA);
        trilho.setArcWidth(ALTURA_BARRA);
        trilho.setArcHeight(ALTURA_BARRA);
        trilho.setFill(COR_TRILHO);
        trilho.setMouseTransparent(true);

        preenchimento.setX(MARGEM);
        preenchimento.setY(Y_BARRA);
        preenchimento.setArcWidth(ALTURA_BARRA);
        preenchimento.setArcHeight(ALTURA_BARRA);
        preenchimento.setFill(COR_PROGRESSO);
        preenchimento.setMouseTransparent(true);

        getChildren().addAll(trilho, preenchimento);

        // uma bandeira no comeco de cada onda
        double duracao = Math.max(cronograma.getDuracao(), 0.001);
        int numero = 1;
        for (Onda onda : cronograma.getOndas()) {
            double x = MARGEM + LARGURA_BARRA * Math.min(1.0, onda.getInicio() / duracao);
            Color cor = onda.isGrande() ? COR_BANDEIRA_GRANDE : COR_BANDEIRA;
            double raio = onda.isGrande() ? 8 : 6;

            Line haste = new Line(x, Y_BARRA - 6, x, Y_BARRA + ALTURA_BARRA + 3);
            haste.setStroke(cor);
            haste.setStrokeWidth(3);
            haste.setMouseTransparent(true);

            Circle bandeira = new Circle(x, Y_BARRA - 8, raio, cor);
            bandeira.setStroke(Color.web("#111111"));
            bandeira.setStrokeWidth(2);
            Tooltip.install(bandeira, new Tooltip(
                    "Onda " + numero + " - " + onda.getNome()
                    + "\nComeca em " + formatar(onda.getInicio())
                    + "\n" + onda.getResumoEmTexto()));

            getChildren().addAll(haste, bandeira);
            numero++;
        }

        marcador.setCenterY(Y_BARRA + ALTURA_BARRA / 2);
        marcador.setStroke(Color.web("#111111"));
        marcador.setStrokeWidth(3);
        marcador.setMouseTransparent(true);
        getChildren().add(marcador);
    }

    private void montarBotoes() {
        HBox linha = new HBox(8);
        linha.setLayoutX(MARGEM);
        linha.setLayoutY(78);
        linha.setPickOnBounds(false);

        for (double velocidade : VELOCIDADES) {
            ToggleButton botao = new ToggleButton(rotulo(velocidade));
            botao.getStyleClass().add("linha-tempo-botao");
            botao.setToggleGroup(grupo);
            botao.setFocusTraversable(false);
            botao.setMnemonicParsing(false);
            botao.setSelected(velocidade == 1.0);
            // clicar no botao ja selecionado nao pode deixar sem nenhum marcado
            botao.setOnAction(e -> {
                botao.setSelected(true);
                aoMudarVelocidade.accept(velocidade);
            });
            botoes.add(botao);
            linha.getChildren().add(botao);
        }
        getChildren().add(linha);
    }

    // ===================== textos =====================

    private String textoDaOnda() {
        if (cronograma.estaConcluido()) {
            return "Todos os zumbis chegaram!";
        }
        if (!cronograma.temOndas()) {
            return "Sem ondas: zumbis sem parar";
        }
        int indice = cronograma.getIndiceDaOndaAtual();
        if (indice < 0) {
            return "Preparem-se... " + (int) Math.ceil(cronograma.getSegundosAteOPrimeiro()) + "s";
        }
        Onda onda = cronograma.getOndas().get(indice);
        return onda.getNome() + " (" + (indice + 1) + "/" + cronograma.getOndas().size() + ")";
    }

    private static String rotulo(double velocidade) {
        return (velocidade == Math.rint(velocidade)
                ? String.valueOf((int) velocidade)
                : String.valueOf(velocidade)) + "x";
    }

    /** 75 -> "1:15". */
    private static String formatar(double segundos) {
        int total = (int) Math.floor(segundos + 1e-9);
        return String.format(Locale.ROOT, "%d:%02d", total / 60, total % 60);
    }
}
