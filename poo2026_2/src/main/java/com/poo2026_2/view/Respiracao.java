package com.poo2026_2.view;

import java.util.Random;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.scene.image.ImageView;
import javafx.scene.transform.Scale;
import javafx.util.Duration;

/**
 * Respiracao dos professores parados: o sprite cresce 1,5% na altura (e
 * encolhe 0,75% na largura) a partir dos pes e volta, sem trocar de frame.
 */
public final class Respiracao {

    /** 1,5% de altura. */
    public static final double AMPLITUDE = 0.02;
    public static final double PERIODO_S = 1.1;

    private static final Random SORTEIO = new Random();

    private Respiracao() { }

    /** Aplica a escala no ImageView e devolve a Timeline (ainda parada). */
    public static Timeline criar(ImageView view) {
        Scale escala = new Scale(1, 1);

        // pivo no centro-inferior do sprite: os pes ficam no chao
        escala.pivotXProperty().bind(Bindings.createDoubleBinding(
                () -> (view.getBoundsInLocal().getMinX() + view.getBoundsInLocal().getMaxX()) / 2,
                view.boundsInLocalProperty()));
        escala.pivotYProperty().bind(Bindings.createDoubleBinding(
                () -> view.getBoundsInLocal().getMaxY(),
                view.boundsInLocalProperty()));
        view.getTransforms().add(escala);

        Timeline respirar = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(escala.yProperty(), 1.0, Interpolator.EASE_BOTH),
                        new KeyValue(escala.xProperty(), 1.0, Interpolator.EASE_BOTH)),
                new KeyFrame(Duration.seconds(PERIODO_S),
                        new KeyValue(escala.yProperty(), 1.0 + AMPLITUDE, Interpolator.EASE_BOTH),
                        new KeyValue(escala.xProperty(), 1.0 - AMPLITUDE / 2, Interpolator.EASE_BOTH)));
        respirar.setAutoReverse(true);
        respirar.setCycleCount(Timeline.INDEFINITE);
        respirar.setDelay(Duration.millis(SORTEIO.nextInt(1000))); // cada professor respira num ritmo
        return respirar;
    }
}
