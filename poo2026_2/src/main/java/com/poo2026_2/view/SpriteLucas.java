package com.poo2026_2.view;

import com.poo2026_2.model.utilizaveis.Lucas;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

/**
 * Animacao do Lucas (24 frames, lucas1.png ... lucas24.png), guiada pelo
 * estado do modelo em vez de ficar num loop fixo:
 *
 *   PRONTO        fica no frame 1, so respirando 1,5% (esperando um zumbi chegar)
 *   GOLPEANDO     frames 6-18: levanta o skate, impacto no ~11, solta o skate
 *   RECARREGANDO  fica ABAIXADO (frame 19) ate faltar pouco para a carga acabar
 *   LEVANTANDO    frames 20-24: pega o skate do chao e volta ao repouso
 *
 * O instante de levantar e calculado a partir de Lucas.getTempoRestanteDeCarga():
 * a animacao de levantar (5 frames a 12 fps) termina exatamente quando ele fica
 * pronto para a proxima skatada. Para mudar a pose em que ele fica abaixado,
 * troque FRAME_ABAIXADO.
 */
public class SpriteLucas {

    public static final String PADRAO = "/images/game/personagens/professores/lucas/lucas%d.png";
    public static final int TOTAL_DE_FRAMES = 24;

    /** 12 frames por segundo, como o resto dos professores (24 fps segurando 2). */
    private static final double FPS = 12.0;

    // indices 0-based dos frames (frame N do arquivo = indice N-1)
    private static final int FRAME_PRONTO = 0;        // lucas1.png
    private static final int INICIO_GOLPE = 5;        // lucas6.png
    private static final int FIM_GOLPE = 17;          // lucas18.png
    private static final int FRAME_ABAIXADO = 18;     // lucas19.png: agachado, skate no chao
    private static final int INICIO_LEVANTAR = 19;    // lucas20.png
    private static final int FIM_LEVANTAR = 23;       // lucas24.png

    private static final double SEGUNDOS_PARA_LEVANTAR =
            (FIM_LEVANTAR - INICIO_LEVANTAR + 1) / FPS;

    private enum Fase { PRONTO, GOLPEANDO, RECARREGANDO, LEVANTANDO }

    private final ImageView view = new ImageView();
    private final Lucas lucas;
    private final Image[] frames;
    private final Timeline timeline;
    private final Timeline respiracao;

    private Fase fase = Fase.PRONTO;
    private int indice = 0;

    public SpriteLucas(Lucas lucas) {
        this.lucas = lucas;
        this.frames = SpriteAnimado.carregarFrames(PADRAO, 1, TOTAL_DE_FRAMES);

        view.setPreserveRatio(true);
        view.setSmooth(true);
        respiracao = Respiracao.criar(view);

        if (frames.length > 0) {
            view.setImage(frames[0]);
        }

        if (frames.length < TOTAL_DE_FRAMES) {
            System.err.println("Lucas: esperava " + TOTAL_DE_FRAMES + " frames, encontrei "
                    + frames.length + ". Tocando o que existe em loop.");
            timeline = criarLoopSimples();
        } else {
            timeline = new Timeline(new KeyFrame(Duration.millis(1000.0 / FPS), e -> passo()));
            timeline.setCycleCount(Timeline.INDEFINITE);
        }
    }

    public ImageView getView() {
        return view;
    }

    public Timeline getTimeline() {
        return timeline;
    }

    public Timeline getRespiracao() {
        return respiracao;
    }

    public void play() {
        timeline.play();
        respiracao.play();
    }

    public void stop() {
        timeline.stop();
        respiracao.stop();
    }

    public void setCaixa(double largura, double altura) {
        view.setFitWidth(largura);
        view.setFitHeight(altura);
    }

    // ------------------------------------------------------------------

    private void passo() {
        double restante = lucas.getTempoRestanteDeCarga();

        switch (fase) {
            case PRONTO -> {
                if (lucas.estaGolpeando()) {
                    fase = Fase.GOLPEANDO;
                    indice = INICIO_GOLPE;
                } else {
                    indice = FRAME_PRONTO;
                }
            }
            case GOLPEANDO -> {
                indice++;
                if (indice > FIM_GOLPE) {
                    fase = Fase.RECARREGANDO;
                    indice = FRAME_ABAIXADO;
                }
            }
            case RECARREGANDO -> {
                indice = FRAME_ABAIXADO;
                if (restante <= SEGUNDOS_PARA_LEVANTAR) {
                    fase = Fase.LEVANTANDO;
                    indice = INICIO_LEVANTAR;
                }
            }
            case LEVANTANDO -> {
                indice++;
                if (indice > FIM_LEVANTAR) {
                    fase = Fase.PRONTO;
                    indice = FRAME_PRONTO;
                }
            }
        }

        view.setImage(frames[indice]);
    }

    private Timeline criarLoopSimples() {
        int[] atual = {0};
        Timeline t = new Timeline(new KeyFrame(Duration.millis(1000.0 / FPS), e -> {
            if (frames.length > 0) {
                atual[0] = (atual[0] + 1) % frames.length;
                view.setImage(frames[atual[0]]);
            }
        }));
        t.setCycleCount(Timeline.INDEFINITE);
        return t;
    }
}
