package com.poo2026_2.model.visuais;

import java.util.function.BiConsumer;

import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

public class visuaisGrades {

    private final int LINHAS;
    private final int COLUNAS;

    private final double CEL_LARG;
    private final double CEL_ALT;

    private final double GRADE_X;
    private final double GRADE_Y;

    private StackPane[][] celulas;

    public visuaisGrades(int linhas, int colunas, double celLarg, double celAlt, double gradeX, double gradeY) {

        this.LINHAS = linhas;
        this.COLUNAS = colunas;
        this.CEL_LARG = celLarg;
        this.CEL_ALT = celAlt;
        this.GRADE_X = gradeX;
        this.GRADE_Y = gradeY;

        celulas = new StackPane[LINHAS][COLUNAS];
    }

    public StackPane[][] getCelulas() {
        return celulas;
    }

    // AJUSTE DA ESCALA DO PALCO
    // =========================================================

    public void ajustarEscala(StackPane raiz, Pane palco, double larguraPalco, double alturaPalco) {

        Runnable atualizar = () -> {

            double s = Math.min(raiz.getWidth() / larguraPalco, raiz.getHeight() / alturaPalco);

            if (s > 0) {
                palco.setScaleX(s);
                palco.setScaleY(s);
            }
        };

        raiz.widthProperty().addListener((o, a, b) -> atualizar.run());
        raiz.heightProperty().addListener((o, a, b) -> atualizar.run());

        atualizar.run();
    }

    // MONTAGEM DA GRADE
    // =========================================================

    public void montarGrade(GridPane grade, BiConsumer<Integer, Integer> acaoAoClicar) {

        grade.setLayoutX(GRADE_X);
        grade.setLayoutY(GRADE_Y);

        grade.getChildren().clear();

        for (int l = 0; l < LINHAS; l++) {

            for (int c = 0; c < COLUNAS; c++) {

                StackPane celula = new StackPane();

                celula.setPrefSize(CEL_LARG, CEL_ALT);
                celula.setMinSize(CEL_LARG, CEL_ALT);
                celula.setMaxSize(CEL_LARG, CEL_ALT);

                celula.getStyleClass().add("celula");

                final int linha = l;
                final int coluna = c;

                celula.setOnMouseClicked(event -> acaoAoClicar.accept(linha, coluna));

                celulas[l][c] = celula;

                grade.add(celula, c, l);
            }
        }
    }
}
