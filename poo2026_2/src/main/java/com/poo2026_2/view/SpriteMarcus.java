package com.poo2026_2.view;

import com.poo2026_2.audio.AudioManager;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

/**
 * Marcus (tanque): ao ser colocado toca os frames 1 -> 9 uma vez, fica
 * parado no 9 e so respira.
 */
public class SpriteMarcus {

    private static final double FPS = 12.0;
    private static final int FRAME_FINAL = 12; // numero do arquivo (marcus12.png)

    private final ImageView view = new ImageView();
    private final Image[] frames;
    private final Timeline entrada;
    private final Timeline respiracao;
    private final int ultimo; // indice (0-based) do frame onde ele para
    private int indice;

    public SpriteMarcus(String padrao, int quantidade) {
        this.frames = SpriteAnimado.carregarFrames(padrao, 1, quantidade);
        this.ultimo = Math.min(FRAME_FINAL, frames.length) - 1;

        view.setPreserveRatio(true);
        view.setSmooth(true);
        if (frames.length > 0) {
            view.setImage(frames[0]);
        }

        respiracao = Respiracao.criar(view);

        entrada = new Timeline(new KeyFrame(Duration.millis(1000.0 / FPS), e -> {
            if (indice < ultimo) {
                indice++;
                view.setImage(frames[indice]);
            }
        }));
        entrada.setCycleCount(Math.max(1, ultimo));
        entrada.setOnFinished(e -> respiracao.play());
    }

    public ImageView getView() {
        return view;
    }

    public Animation getTimeline() {
        return entrada;
    }

    public Animation getRespiracao() {
        return respiracao;
    }

    public void setCaixa(double largura, double altura) {
        view.setFitWidth(largura);
        view.setFitHeight(altura);
    }

    public void play() {
        AudioManager.tocarEfeito("/audio/tiros/lego.mp3");
        entrada.playFromStart();
    }
    public void stop() {
        entrada.stop();
        respiracao.stop();
    }
}