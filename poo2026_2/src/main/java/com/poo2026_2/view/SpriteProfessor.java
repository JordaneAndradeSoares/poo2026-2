package com.poo2026_2.view;

import java.util.List;
import java.util.function.BooleanSupplier;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

/**
 * Animacao dos professores que atiram (Daniel, Thais, Philipe, Glaucia,
 * Fabricio) e dos que so ficam parados (Marcus). O Nacif tem o SpriteNacif.
 *
 *   PARADO    fica no FRAME 1, so respirando (1,5%).
 *   ATIRANDO  toca os 12 frames UMA vez, quando aparece um zumbi na mira. O
 *             tiro sai no FRAME_DO_TIRO (o braco esticado) e, no fim, o
 *             professor volta ao frame 1.
 *   SUSTENTADO  como ATIRANDO, mas no frame do tiro a animacao fica parada
 *             enquanto o professor tiver alvo (laser do Fabricio). Quando nao ha
 *             mais alvo, os frames seguintes tocam ate o fim.
 */
public class SpriteProfessor {

    /** 12 frames por segundo (o ciclo completo dura 1 segundo). */
    private static final double FPS = 12.0;

    /** Indice (0-based) do frame em que o tiro sai: frame 6 do arquivo. */
    public static final int FRAME_DO_TIRO = 5;

    private final ImageView view = new ImageView();
    private final Image[] frames;
    private final Timeline ciclo;
    private final Timeline respiracao;

    private int indice;
    private boolean atirando;
    private Runnable aoSairOTiro;

    /** Se nao for null, o sprite fica no frame do tiro enquanto devolver true. */
    private BooleanSupplier manterTiro;
    private boolean sustentando;

    public SpriteProfessor(String padrao, int quantidade) {
        this.frames = SpriteAnimado.carregarFrames(padrao, 1, quantidade);

        view.setPreserveRatio(true);
        view.setSmooth(true);
        if (frames.length > 0) {
            view.setImage(frames[0]);
        }

        ciclo = new Timeline(new KeyFrame(Duration.millis(1000.0 / FPS), e -> passo()));
        ciclo.setCycleCount(Animation.INDEFINITE);

        respiracao = Respiracao.criar(view);
    }

    public ImageView getView() {
        return view;
    }

    public List<Animation> getAnimacoes() {
        return List.of(ciclo, respiracao);
    }

    public boolean estaAtirando() {
        return atirando;
    }

    /** true enquanto o sprite esta segurando o frame do tiro (laser ligado). */
    public boolean estaSustentando() {
        return sustentando;
    }

    public void setCaixa(double largura, double altura) {
        view.setFitWidth(largura);
        view.setFitHeight(altura);
    }

    /** Comeca a respirar no frame 1. */
    public void play() {
        respiracao.play();
    }

    public void stop() {
        ciclo.stop();
        respiracao.stop();
    }

    /**
     * Toca a animacao de atirar uma vez.
     *
     * @param aoSairOTiro chamado no frame do disparo
     */
    public void atirar(Runnable aoSairOTiro) {
        if (atirando || frames.length < 2) {
            return;
        }
        this.aoSairOTiro = aoSairOTiro;
        atirando = true;
        indice = 0;
        ciclo.playFromStart();
    }

    /**
     * Toca a animacao de atirar, mas segura o frame do tiro enquanto
     * manterTiro devolver true. Quando devolver false, a animacao continua
     * dos frames seguintes e termina normalmente.
     *
     * @param aoSairOTiro chamado ao chegar no frame do disparo
     * @param manterTiro  perguntado a cada passo, enquanto o sprite esta no frame do disparo
     */
    public void atirarSustentado(Runnable aoSairOTiro, BooleanSupplier manterTiro) {
        if (atirando || frames.length < 2) {
            return;
        }
        this.manterTiro = manterTiro;
        atirar(aoSairOTiro);
    }

    private int frameDoTiro() {
        return Math.min(FRAME_DO_TIRO, frames.length - 1);
    }

    private void passo() {
        if (sustentando) {
            if (manterTiro != null && manterTiro.getAsBoolean()) {
                return;   // continua no frame do tiro
            }
            sustentando = false;   // acabaram os alvos: segue para os proximos frames
        }

        indice++;

        if (indice >= frames.length) {
            terminar();
            return;
        }

        view.setImage(frames[indice]);

        if (indice == frameDoTiro()) {
            sustentando = manterTiro != null;
            if (aoSairOTiro != null) {
                Runnable disparo = aoSairOTiro;
                aoSairOTiro = null;
                disparo.run();
            }
        }
    }

    private void terminar() {
        ciclo.stop();
        atirando = false;
        sustentando = false;
        manterTiro = null;
        aoSairOTiro = null;
        indice = 0;
        view.setImage(frames[0]);
    }
}
