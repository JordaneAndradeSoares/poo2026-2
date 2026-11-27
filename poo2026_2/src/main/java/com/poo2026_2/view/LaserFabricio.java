package com.poo2026_2.view;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;

/**
 * Desenho do laser do Fabricio: um feixe por alvo, cada um com uma borda
 * escura (#380d09) e um nucleo vermelho (#de3423) que pulsa de leve.
 * Ha no maximo 3 feixes (a linha dele, a de cima e a de baixo).
 */
public final class LaserFabricio {

    public static final Color COR_NUCLEO = Color.web("#de3423");
    public static final Color COR_BORDA = Color.web("#380d09");

    private static final int MAX_FEIXES = 3;
    private static final double LARGURA_BORDA = 16;
    private static final double LARGURA_NUCLEO = 7;
    private static final double RAIO_IMPACTO_BORDA = 15;
    private static final double RAIO_IMPACTO_NUCLEO = 8;
    private static final double VELOCIDADE_PULSO = 40;   // rad/s
    private static final double AMPLITUDE_PULSO = 0.15;

    private static final class Feixe {
        private final Group grupo = new Group();
        private final Line borda = new Line();
        private final Line nucleo = new Line();
        private final Circle impactoBorda = new Circle(RAIO_IMPACTO_BORDA, COR_BORDA);
        private final Circle impactoNucleo = new Circle(RAIO_IMPACTO_NUCLEO, COR_NUCLEO);

        private Feixe() {
            borda.setStroke(COR_BORDA);
            borda.setStrokeLineCap(StrokeLineCap.ROUND);
            nucleo.setStroke(COR_NUCLEO);
            nucleo.setStrokeLineCap(StrokeLineCap.ROUND);
            grupo.getChildren().addAll(borda, nucleo, impactoBorda, impactoNucleo);
            grupo.setMouseTransparent(true);
            grupo.setViewOrder(-1000);   // por cima de zumbis e professores
            grupo.setVisible(false);
        }
    }

    private final Pane camada;
    private final List<Feixe> feixes = new ArrayList<>();
    private double fase;

    public LaserFabricio(Pane camada) {
        this.camada = camada;
        for (int i = 0; i < MAX_FEIXES; i++) {
            Feixe feixe = new Feixe();
            feixes.add(feixe);
            camada.getChildren().add(feixe.grupo);
        }
    }

    /**
     * Mostra um feixe por segmento; os feixes que sobram ficam escondidos.
     *
     * @param segmentos cada item e {x0, y0, x1, y1}: da ponta do dedo ate o alvo
     */
    public void mostrar(List<double[]> segmentos, double deltaTime) {
        fase += deltaTime * VELOCIDADE_PULSO;
        double pulso = 1 + AMPLITUDE_PULSO * Math.sin(fase);

        for (int i = 0; i < feixes.size(); i++) {
            Feixe feixe = feixes.get(i);
            if (i >= segmentos.size()) {
                feixe.grupo.setVisible(false);
                continue;
            }
            double[] s = segmentos.get(i);

            feixe.borda.setStartX(s[0]);
            feixe.borda.setStartY(s[1]);
            feixe.borda.setEndX(s[2]);
            feixe.borda.setEndY(s[3]);
            feixe.borda.setStrokeWidth(LARGURA_BORDA * pulso);

            feixe.nucleo.setStartX(s[0]);
            feixe.nucleo.setStartY(s[1]);
            feixe.nucleo.setEndX(s[2]);
            feixe.nucleo.setEndY(s[3]);
            feixe.nucleo.setStrokeWidth(LARGURA_NUCLEO * pulso);

            feixe.impactoBorda.setCenterX(s[2]);
            feixe.impactoBorda.setCenterY(s[3]);
            feixe.impactoBorda.setRadius(RAIO_IMPACTO_BORDA * pulso);
            feixe.impactoNucleo.setCenterX(s[2]);
            feixe.impactoNucleo.setCenterY(s[3]);
            feixe.impactoNucleo.setRadius(RAIO_IMPACTO_NUCLEO * pulso);

            feixe.grupo.setVisible(true);
        }
    }

    public void esconder() {
        for (Feixe feixe : feixes) {
            feixe.grupo.setVisible(false);
        }
    }

    /** Tira os feixes da tela (professor removido). */
    public void remover() {
        for (Feixe feixe : feixes) {
            camada.getChildren().remove(feixe.grupo);
        }
    }
}
