package com.poo2026_2.view;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

/**
 * Animacao da cafeteira: o cafe vai enchendo o jarro do frame 1 ao frame 12
 * (estagio final, com fumaca). O frame 12 aparece exatamente quando passam
 * {@code segundosAteOFinal} (24 s) e e nesse instante que a gota sai
 * ({@code aoFicarPronto}). O estagio final fica na tela por mais um instante e
 * a cafeteira volta ao frame 1 para um novo ciclo.
 */
public class SpriteCafeteira {

    public static final String PADRAO = "/images/game/personagens/professores/cafeteira/cafeteira%d.png";
    public static final int TOTAL_DE_FRAMES = 12;

    /** Quanto tempo o estagio final (frame 12) fica na tela antes de reiniciar. */
    private static final double SEGUNDOS_NO_ESTAGIO_FINAL = 1.0;

    private final ImageView view = new ImageView();
    private final Timeline timeline;

    public SpriteCafeteira(double segundosAteOFinal, Runnable aoFicarPronto) {
        Image[] frames = SpriteAnimado.carregarFrames(PADRAO, 1, TOTAL_DE_FRAMES);

        view.setPreserveRatio(true);
        view.setSmooth(true);
        if (frames.length > 0) {
            view.setImage(frames[0]);
        }

        timeline = new Timeline();
        int n = frames.length;
        // o frame i (0-based) aparece em i * (24 / 11) s: o ultimo cai nos 24 s
        double passo = n > 1 ? segundosAteOFinal / (n - 1) : segundosAteOFinal;

        for (int i = 0; i < n; i++) {
            final int indice = i;
            boolean ultimo = i == n - 1;
            timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(i * passo), e -> {
                view.setImage(frames[indice]);
                if (ultimo && aoFicarPronto != null) {
                    aoFicarPronto.run();
                }
            }));
        }
        // fim do ciclo: depois do estagio final, a Timeline recomeca no frame 1
        timeline.getKeyFrames().add(new KeyFrame(
                Duration.seconds(segundosAteOFinal + SEGUNDOS_NO_ESTAGIO_FINAL)));
        timeline.setCycleCount(Animation.INDEFINITE);
    }

    public ImageView getView() {
        return view;
    }

    public Timeline getTimeline() {
        return timeline;
    }

    public void setCaixa(double largura, double altura) {
        view.setFitWidth(largura);
        view.setFitHeight(altura);
    }

    public void play() {
        timeline.play();
    }

    public void stop() {
        timeline.stop();
    }
}
