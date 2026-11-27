package com.poo2026_2.view;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.transform.Scale;
import javafx.util.Duration;

/**
 * Animação por frames soltos (um PNG por frame), usada por professores,
 * cafeteira e personagens do menu. Substitui os Timelines copiados em cada
 * classe visual.
 *
 * Configuração padrão:
 *   - 12 frames, tocando 1 -> 2 -> ... -> 12 -> 1 -> 2 ... (loop, sem ida e volta)
 *   - relógio de 24 fps
 *   - cada frame é mostrado por 2 ticks (SEGURAR_PADRAO) -> 12 frames "de verdade"
 *     por segundo, ciclo completo de 1 segundo.
 *
 */
public class SpriteAnimado {

    public static final double FPS_PADRAO = 24.0;
    public static final int SEGURAR_PADRAO = 2;
    public static final int FRAMES_PADRAO = 12;

    /** LOOP: 1..12,1..12. IDA_E_VOLTA: 1..12,11..2,1.. (movimento espelhado). */
    public enum Modo { LOOP, IDA_E_VOLTA }

    private static final double RESPIRACAO_AMPLITUDE = 0.015;   // 1,5% de altura
    private static final double RESPIRACAO_PERIODO_S = 1.1;

    private static final Map<String, Image[]> CACHE = new HashMap<>();
    private static final Random SORTEIO = new Random();

    private final ImageView view;
    private final Image[] frames;
    private final int[] sequencia;
    private final Timeline timeline;
    private int tick;

    /** Padrão: 12 frames, 24 fps, segura 2 ticks, loop, todos começam no frame 1. */
    public SpriteAnimado(String padraoCaminho, int primeiroNumero) {
        this(padraoCaminho, primeiroNumero, FRAMES_PADRAO);
    }

    public SpriteAnimado(String padraoCaminho, int primeiroNumero, int quantidade) {
        this(padraoCaminho, primeiroNumero, quantidade,
                FPS_PADRAO, SEGURAR_PADRAO, Modo.LOOP, false);
    }

    /**
     * @param padraoCaminho  ex.: "/images/game/personagens/professores/philipe/philipe%d.png"
     * @param primeiroNumero número do primeiro arquivo (1 para philipe1.png)
     * @param quantidade     quantos frames existem (12)
     * @param fps            relógio da animação (24)
     * @param segurar        quantos ticks cada frame fica na tela (2)
     * @param modo           LOOP ou IDA_E_VOLTA
     * @param faseAleatoria  true: cada instância começa num ponto diferente do
     *                       ciclo (bom para zumbis não andarem em marcha unida)
     */
    public SpriteAnimado(String padraoCaminho, int primeiroNumero, int quantidade,
                         double fps, int segurar, Modo modo, boolean faseAleatoria) {
        this(new ImageView(), padraoCaminho, primeiroNumero, quantidade,
                fps, segurar, modo, faseAleatoria);
    }

    /**
     * Igual ao anterior, mas anima um ImageView que ja existe (o que o chamador
     * ja colocou na tela). Serve para trocar de animacao (andar -> bater) sem
     * trocar o no da cena.
     */
    public SpriteAnimado(ImageView destino, String padraoCaminho, int primeiroNumero, int quantidade,
                         double fps, int segurar, Modo modo, boolean faseAleatoria) {

        this.view = destino;

        if (fps <= 0 || segurar < 1 || quantidade < 1) {
            throw new IllegalArgumentException("fps, segurar e quantidade precisam ser positivos.");
        }

        this.frames = carregar(padraoCaminho, primeiroNumero, quantidade);
        this.sequencia = montarSequencia(frames.length, segurar, modo);

        view.setPreserveRatio(true);
        view.setSmooth(true);

        if (frames.length > 0) {
            view.setImage(frames[0]);
        }

        if (frames.length > 1) {
            tick = faseAleatoria ? SORTEIO.nextInt(sequencia.length) : 0;
            view.setImage(frames[sequencia[tick]]);

            timeline = new Timeline(new KeyFrame(Duration.millis(1000.0 / fps), e -> {
                tick = (tick + 1) % sequencia.length;
                view.setImage(frames[sequencia[tick]]);
            }));
            timeline.setCycleCount(Timeline.INDEFINITE);
        } else {
            timeline = criarRespiracao(faseAleatoria);
        }
    }

    // ------------------------------------------------------------------

    public ImageView getView() {
        return view;
    }

    /** Timeline em execução; guarde no mapa de animações para parar ao remover. */
    public Timeline getTimeline() {
        return timeline;
    }

    public int getQuantidadeDeFrames() {
        return frames.length;
    }

    public void play() {
        timeline.play();
    }

    public void stop() {
        timeline.stop();
    }

    /** Ajusta a caixa onde o sprite cabe (mantém a proporção dos frames). */
    public void setCaixa(double largura, double altura) {
        view.setFitWidth(largura);
        view.setFitHeight(altura);
    }

    // ------------------------------------------------------------------

    /** Frames ja carregados (cache compartilhado) para quem precisa controlar a sequencia sozinho. */
    public static Image[] carregarFrames(String padrao, int primeiro, int quantidade) {
        return carregar(padrao, primeiro, quantidade);
    }

    /** true se o arquivo do primeiro frame desse padrao existe nos resources. */
    public static boolean existe(String padrao, int primeiroNumero) {
        return SpriteAnimado.class.getResource(String.format(padrao, primeiroNumero)) != null;
    }

    private static Image[] carregar(String padrao, int primeiro, int quantidade) {
        String chave = padrao + "|" + primeiro + "|" + quantidade;

        return CACHE.computeIfAbsent(chave, k -> {
            java.util.List<Image> lidos = new java.util.ArrayList<>();

            for (int i = 0; i < quantidade; i++) {
                String caminho = String.format(padrao, primeiro + i);
                URL url = SpriteAnimado.class.getResource(caminho);

                if (url == null) {
                    System.err.println("Frame não encontrado: " + caminho);
                    continue;
                }
                lidos.add(new Image(url.toExternalForm()));
            }
            return lidos.toArray(new Image[0]);
        });
    }

    /** Índice do frame mostrado em cada tick do ciclo. */
    private static int[] montarSequencia(int n, int segurar, Modo modo) {
        if (n <= 1) {
            return new int[]{0};
        }

        int[] ordem;
        if (modo == Modo.IDA_E_VOLTA) {
            ordem = new int[2 * n - 2];
            for (int i = 0; i < n; i++) {
                ordem[i] = i;
            }
            for (int i = 1; i < n - 1; i++) {
                ordem[n - 1 + i] = n - 1 - i;
            }
        } else {
            ordem = new int[n];
            for (int i = 0; i < n; i++) {
                ordem[i] = i;
            }
        }

        int[] saida = new int[ordem.length * segurar];
        for (int i = 0; i < ordem.length; i++) {
            for (int s = 0; s < segurar; s++) {
                saida[i * segurar + s] = ordem[i];
            }
        }
        return saida;
    }

    /** Placeholder de 1 frame: estica/encolhe levemente a partir do pé. */
    private Timeline criarRespiracao(boolean faseAleatoria) {
        Scale escala = new Scale(1, 1);

        // pivô no centro-inferior do sprite, acompanhando o tamanho real
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
                new KeyFrame(Duration.seconds(RESPIRACAO_PERIODO_S),
                        new KeyValue(escala.yProperty(), 1.0 + RESPIRACAO_AMPLITUDE, Interpolator.EASE_BOTH),
                        new KeyValue(escala.xProperty(), 1.0 - RESPIRACAO_AMPLITUDE / 2, Interpolator.EASE_BOTH)));

        respirar.setAutoReverse(true);
        respirar.setCycleCount(Timeline.INDEFINITE);

        if (faseAleatoria) {
            respirar.setDelay(Duration.millis(SORTEIO.nextInt(1000)));
        }
        return respirar;
    }
}
