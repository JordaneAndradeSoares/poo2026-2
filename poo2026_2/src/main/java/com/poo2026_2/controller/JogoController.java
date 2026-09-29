package com.poo2026_2.controller;

import java.net.URL;
import java.util.ResourceBundle;
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

    // deve alinhar com a imagem
    // Coordenadas em pixels da imagem original (1672 x 941).
    private static final double LARGURA_PALCO = 1672;
    private static final double ALTURA_PALCO  = 941;

    private static final int LINHAS   = 5;
    private static final int COLUNAS  = 12;
    private static final double GRADE_X  = 240;  // canto superior esquerdo da grade
    private static final double GRADE_Y  = 255;
    private static final double CEL_LARG = 110;  // tamanho de cada casa
    private static final double CEL_ALT  = 130;
    // =================================================================

    @FXML private StackPane raiz;
    @FXML private Pane palco;
    @FXML private GridPane grade;
    @FXML private Pane camadaJogo;
    
    @FXML private Button btnVoltar;
    
    private GerenciadorDeTelas gerenciadorDeTelas;

    @Override
    public void setGerenciadorDeTelas(
            GerenciadorDeTelas gerenciadorDeTelas) {
        this.gerenciadorDeTelas = gerenciadorDeTelas;
    }

    // Placeholder da view: quais casas estao ocupadas (o modelo real fica no Tabuleiro)
    private final StackPane[][] celulas = new StackPane[LINHAS][COLUNAS];


    public void initialize(URL url, ResourceBundle rb) {
        montarGrade();
        ajustarEscala();
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
                celula.getStyleClass().add((l + c) % 2 == 0 ? "celula-clara" : "celula-escura");

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
        gerenciadorDeTelas.voltarMenu();
    }
}
