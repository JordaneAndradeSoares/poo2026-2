package com.poo2026_2.controller;

import com.poo2026_2.audio.AudioManager;
import com.poo2026_2.model.Fase;
import com.poo2026_2.model.GerenciadorDeJogo;
import com.poo2026_2.model.Tabuleiro;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

/**
 * FXML Controller class
 *
 * @author thoma
 */
public class JogoController implements ControladorTela, Initializable {

    private GerenciadorDeJogo gerenciadorDeJogo;
    private AnimationTimer timer;

    // deve alinhar com a imagem
    // Coordenadas em pixels da imagem original (1672 x 941).
    private static final double LARGURA_PALCO = 1672;
    private static final double ALTURA_PALCO = 941;

    private static final int LINHAS = 5;
    private static final int COLUNAS = 12;
    private static final double GRADE_X = 240;  // canto superior esquerdo da grade
    private static final double GRADE_Y = 255;
    private static final double CEL_LARG = 110;  // tamanho de cada casa
    private static final double CEL_ALT = 130;
    // =================================================================

    @FXML
    private StackPane raiz;
    @FXML
    private Pane palco;
    @FXML
    private GridPane grade;
    @FXML
    private Pane camadaJogo;

    @FXML
    private Button btnVoltar;

    private GerenciadorDeTelas gerenciadorDeTelas;

    private void iniciarPartida() {
        Fase fase = new Fase(1, "Fase 1", 150);   // provisório
        Tabuleiro tabuleiro = new Tabuleiro(LINHAS, COLUNAS);
        gerenciadorDeJogo = new GerenciadorDeJogo(fase, tabuleiro);
        gerenciadorDeJogo.iniciarJogo();

        timer = new AnimationTimer() {
            private long ultimo = 0;

            @Override
            public void handle(long agora) {
                if (ultimo != 0) {
                    double delta = (agora - ultimo) / 1_000_000_000.0;
                    gerenciadorDeJogo.atualizar(Math.min(delta, 0.1));
                    // redesenhar sprites aqui
                }
                ultimo = agora;
            }
        };
        timer.start();
    }

    @Override
    public void setGerenciadorDeTelas(
            GerenciadorDeTelas gerenciadorDeTelas) {
        this.gerenciadorDeTelas = gerenciadorDeTelas;
    }

    // Placeholder da view: quais casas estao ocupadas (o modelo real fica no Tabuleiro)
    private final StackPane[][] celulas = new StackPane[LINHAS][COLUNAS];

    private void configurarSomDeHover(Button... botoes) {
        for (Button botao : botoes) {
            botao.setOnMouseEntered(
                    event -> AudioManager.tocarSomHoverBotao()
            );
        }
    }

    private void configurarClick(Button... botoes) {
        for (Button botao : botoes) {
            botao.setOnMousePressed(
                    event -> AudioManager.tocarSomBotao()
            );
        }
    }

    public void initialize(URL url, ResourceBundle rb) {
        montarGrade();
        ajustarEscala();
        configurarSomDeHover(btnVoltar);
        configurarClick(btnVoltar);
        iniciarPartida(); 
    }

    private void montarGrade() {
        grade.setLayoutX(GRADE_X);
        grade.setLayoutY(GRADE_Y);

        for (int l = 0; l < LINHAS; l++) {
            for (int c = 0; c < COLUNAS; c++) {
                StackPane celula = new StackPane();
                celula.setPrefSize(CEL_LARG, CEL_ALT);
                celula.setMinSize(CEL_LARG, CEL_ALT);
                celula.setMaxSize(CEL_LARG, CEL_ALT);
                celula.getStyleClass().add("celula");

                final int linha = l, coluna = c;
                celula.setOnMouseClicked(e -> aoClicarNaCelula(linha, coluna));

                celulas[l][c] = celula;
                grade.add(celula, c, l);
            }
        }
    }

    // Por enquanto so coloca/tira um "P" de teste. Depois: chamar o GerenciadorDeJogo.
    private void aoClicarNaCelula(int linha, int coluna) {
        StackPane celula = celulas[linha][coluna];
        if (celula.getChildren().isEmpty()) {
            Label sprite = new Label("P");
            sprite.getStyleClass().add("sprite-teste");
            celula.getChildren().add(sprite);
        } else {
            celula.getChildren().clear();
        }
        System.out.println("Casa clicada: linha " + linha + ", coluna " + coluna);
    }

    // Escala o palco (1672x941) para caber na janela, mantendo a grade alinhada.
    private void ajustarEscala() {
        Runnable atualizar = () -> {
            double s = Math.min(raiz.getWidth() / LARGURA_PALCO,
                    raiz.getHeight() / ALTURA_PALCO);
            if (s > 0) {
                palco.setScaleX(s);
                palco.setScaleY(s);
            }
        };
        raiz.widthProperty().addListener((o, a, b) -> atualizar.run());
        raiz.heightProperty().addListener((o, a, b) -> atualizar.run());
    }

    @FXML
    private void voltar(ActionEvent event) {
        timer.stop(); 
        gerenciadorDeJogo.encerrar();
        gerenciadorDeTelas.voltarMenu();
    }
}
