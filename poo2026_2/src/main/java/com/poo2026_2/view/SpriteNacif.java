package com.poo2026_2.view;

import com.poo2026_2.model.utilizaveis.Nacif;
import com.poo2026_2.audio.AudioManager;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;


/**
 * Animacao do Nacif: toca os 12 frames UMA vez, do 1 ao 12, espalhados pelo
 * tempo ate a explosao (Nacif.TEMPO_ATE_EXPLODIR). O frame mostrado vem do
 * progresso do modelo, entao o sprite nunca sai de sincronia com a explosao,
 * nem quando o jogo roda a 1.5x ou 2x. No ultimo frame ele fica parado ate o
 * JogoController tirar o sprite e mostrar o boom.
 */
public class SpriteNacif {

    private static final double FPS = 24.0;

    private final ImageView view = new ImageView();
    private final Nacif nacif;
    private final Image[] frames;
    private final Timeline timeline;
    private int indice = 0;

    public SpriteNacif(Nacif nacif, String padrao, int quantidade) {
        this.nacif = nacif;
        AudioManager.tocarEfeito("/audio/digitando.mp3");
        this.frames = SpriteAnimado.carregarFrames(padrao, 1, quantidade);

        view.setPreserveRatio(true);
        view.setSmooth(true);
        if (frames.length > 0) {
            view.setImage(frames[0]);
        }

        timeline = new Timeline(new KeyFrame(Duration.millis(1000.0 / FPS), e -> passo()));
        timeline.setCycleCount(Timeline.INDEFINITE);
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

    private void passo() {
        if (frames.length == 0) {
            return;
        }
        int novo = Math.min(frames.length - 1, (int) (nacif.getProgressoDaAnimacao() * frames.length));
        if (novo != indice) {
            indice = novo;
            view.setImage(frames[indice]);
        }
    }
}
