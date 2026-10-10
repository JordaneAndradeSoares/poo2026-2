package com.poo2026_2.controller;

import com.poo2026_2.audio.AudioManager;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.util.Duration;

public class MenuController implements ControladorTela {

    /** Resolucao "de referencia" em que os botoes foram desenhados no FXML. */
    private static final double LARGURA_REFERENCIA = 1920.0;
    private static final double ALTURA_REFERENCIA = 1080.0;

    /** Quantidade de "baforadas" de vapor simultaneas (efeito mais denso). */
    private static final int QUANTIDADE_PARTICULAS_VAPOR = 7;

    @FXML
    private StackPane root;

    @FXML
    private AnchorPane painelBotoes;

    @FXML
    private Group vaporCafeEsquerda;

    @FXML
    private Group vaporCafeDireita;

    @FXML
    private Button btnJogar;

    @FXML
    private Button btnConfiguracoes;

    @FXML
    private Button btnCreditos;

    @FXML
    private Button btnSair;

    private GerenciadorDeTelas gerenciadorDeTelas;

    @Override
    public void setGerenciadorDeTelas(
            GerenciadorDeTelas gerenciadorDeTelas) {

        this.gerenciadorDeTelas = gerenciadorDeTelas;
    }

    @FXML
    private void initialize() {
        painelBotoes.scaleXProperty().bind(
                root.widthProperty().divide(LARGURA_REFERENCIA)
        );

        painelBotoes.scaleYProperty().bind(
                root.heightProperty().divide(ALTURA_REFERENCIA)
        );

        iniciarVaporCafe();

        AudioManager.tocarMusicaMenu();
        configurarSomDeHover(btnJogar, btnConfiguracoes, btnCreditos, btnSair);
    }

    /** Toca um som quando o mouse entra em cada um dos botoes passados. */
    private void configurarSomDeHover(Button... botoes) {
        for (Button botao : botoes) {
            botao.setOnMouseEntered(
                    event -> AudioManager.tocarSomHoverBotao()
            );
        }
    }

    /**
     * Cria varias "baforadas" de vapor (elipses com blur) que sobem,
     * crescem e desaparecem em looping, saindo da xicara na imagem
     * de fundo.
     */
    private void iniciarVaporCafe() {

    // Fumaça da esquerda
    for (int i = 0; i < QUANTIDADE_PARTICULAS_VAPOR; i++) {
        double atrasoMs = i * (3600.0 / QUANTIDADE_PARTICULAS_VAPOR);
        criarParticulaVapor(vaporCafeEsquerda, atrasoMs);
    }

    // Fumaça da direita
    for (int i = 0; i < QUANTIDADE_PARTICULAS_VAPOR; i++) {
        double atrasoMs = i * (3600.0 / QUANTIDADE_PARTICULAS_VAPOR);
        criarParticulaVapor(vaporCafeDireita, atrasoMs);
    }
}

    private void criarParticulaVapor(Group grupoVapor, double atrasoMs) {
        Ellipse baforada = new Ellipse(0, 0, 14, 20);
        baforada.setFill(Color.WHITE);
        baforada.setOpacity(0);
        baforada.setEffect(new GaussianBlur(14));

        grupoVapor.getChildren().add(baforada);

        double duracaoMs = 3600;

        // Desvio lateral maior = fumaca mais larga/espalhada pros lados.
        double desvioLateral = (Math.random() - 0.5) * 100;

        Timeline animacao = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(baforada.translateXProperty(), 0),
                        new KeyValue(baforada.translateYProperty(), 0),
                        new KeyValue(baforada.opacityProperty(), 0),
                        new KeyValue(baforada.scaleXProperty(), 0.4),
                        new KeyValue(baforada.scaleYProperty(), 0.4)
                ),
                new KeyFrame(Duration.millis(duracaoMs * 0.2),
                        // Pico de opacidade mais alto = fumaca mais visivel.
                        new KeyValue(baforada.opacityProperty(), 0.55)
                ),
                new KeyFrame(Duration.millis(duracaoMs * 0.55),
                        new KeyValue(baforada.translateXProperty(), desvioLateral),
                        new KeyValue(baforada.translateYProperty(), -75),
                        new KeyValue(baforada.scaleXProperty(), 1.0),
                        new KeyValue(baforada.scaleYProperty(), 1.0)
                ),
                new KeyFrame(Duration.millis(duracaoMs),
                        new KeyValue(baforada.translateXProperty(), -desvioLateral),
                        new KeyValue(baforada.translateYProperty(), -158),
                        new KeyValue(baforada.opacityProperty(), 0),
                        new KeyValue(baforada.scaleXProperty(), 1.7),
                        new KeyValue(baforada.scaleYProperty(), 1.7)
                )
        );

        animacao.setDelay(Duration.millis(atrasoMs));
        animacao.setCycleCount(Animation.INDEFINITE);
        animacao.play();
    }

    @FXML
    private void iniciarJogo(ActionEvent event) {
        gerenciadorDeTelas.mudarTela(
                "SelecaoNivel.fxml"
        );
    }

    @FXML
    private void abrirConfiguracoes(ActionEvent event) {
        gerenciadorDeTelas.mudarTela(
                "Extras.fxml"
        );
    }

    @FXML
    private void abrirCreditos(ActionEvent event) {
        gerenciadorDeTelas.mudarTela(
                "Creditos.fxml"
        );
    }

    @FXML
    private void sair(ActionEvent event) {
        Platform.exit();
    }
}