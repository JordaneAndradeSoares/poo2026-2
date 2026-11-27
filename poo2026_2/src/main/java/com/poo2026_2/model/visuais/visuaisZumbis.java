package com.poo2026_2.model.visuais;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.animation.Timeline;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import com.poo2026_2.model.GerenciadorDeJogo;
import com.poo2026_2.model.zumbis.Viciado;
import com.poo2026_2.model.zumbis.Zumbi;
import com.poo2026_2.view.SpriteAnimado;

/**
 * Visual dos zumbis. Cada zumbi usa frames soltos (12 PNGs por animacao) em
 * /images/game/personagens/zumbis/<tipo>/<animacao>/<animacao>_01.png ... _12.png
 * tocados pelo SpriteAnimado, e troca de animacao conforme o estado:
 *
 *   andando  -> andar (Trote: correr)
 *   atacando -> bater
 *   Viciado: com celular anda e bate com o celular; ao quebrar o telefone
 *   (estaEstressado) passa a correr e a bater furioso (bater_fase2, 24 fps).
 *
 * Tamanho e pe: todos os zumbis sao desenhados com o corpo ~160 px de altura.
 * Como cada tipo tem um canvas de tamanho diferente, a largura, a altura e a
 * linha dos pes (pe) de cada um ficam na tabela VISUAIS abaixo, calculadas a
 * partir dos PNGs. Para mudar o tamanho de um zumbi, mexa so na linha dele.
 */
public class visuaisZumbis {

    private static final String BASE = "/images/game/personagens/zumbis/";

    /** 24 fps; segurar 2 ticks = 12 quadros por segundo; segurar 1 = 24 por segundo. */
    private static final double FPS = 24.0;
    private static final int FRAMES = 12;

    /** Onde ficam os pes dentro da casa (px a partir do topo da linha). */
    private static final double CHAO_NA_LINHA = 120;

    /** O zumbi e centrado a esta distancia (px) a direita do x do modelo. */
    private static final double DESLOCAMENTO_X = 30;

    private record Animacao(String padrao, int segurar) {}

    /**
     * @param largura  largura em que o canvas inteiro do frame e desenhado
     * @param altura   altura em que o canvas inteiro do frame e desenhado
     * @param pe       y (dentro da altura) onde ficam os pes
     * @param andar    animacao de andar
     * @param bater    animacao de bater
     * @param andarFuria / baterFuria  so o Viciado depois de quebrar o celular
     * @param baterComCelular  so o Viciado (fase 1); se os arquivos nao existirem usa andar
     */
    private record Visual(double largura, double altura, double pe,
                          Animacao andar, Animacao bater,
                          Animacao andarFuria, Animacao baterFuria,
                          Animacao baterComCelular) {

        Visual(double largura, double altura, double pe, Animacao andar, Animacao bater) {
            this(largura, altura, pe, andar, bater, null, null, null);
        }
    }

    private static String padrao(String pasta, String animacao) {
        return BASE + pasta + "/" + animacao + "/" + animacao + "_%02d.png";
    }

    private static Animacao normal(String pasta, String animacao) {
        return new Animacao(padrao(pasta, animacao), 2);
    }

    private static final Map<String, Visual> VISUAIS = new HashMap<>();

    static {
        //                                        largura altura   pe
        VISUAIS.put("Calouro",          new Visual(165.5, 170.5, 167.0, normal("calouro", "andar"),          normal("calouro", "bater")));
        VISUAIS.put("CalouroBlindado",  new Visual(147.5, 168.1, 164.7, normal("calouroBlindado", "andar"),  normal("calouroBlindado", "bater")));
        VISUAIS.put("CalouroProtegido", new Visual(148.8, 168.8, 165.5, normal("calouroProtegido", "andar"), normal("calouroProtegido", "bater")));
        VISUAIS.put("Doutorando",       new Visual(163.5, 171.5, 168.0, normal("doutorando", "andar"),       normal("doutorando", "bater")));
        VISUAIS.put("Formando",         new Visual(156.1, 169.8, 165.9, normal("formando", "andar"),         normal("formando", "bater")));
        VISUAIS.put("VibeCoder",        new Visual(150.9, 167.2, 163.8, normal("vibeCoder", "andar"),        normal("vibeCoder", "bater")));

        // Trote: so tem uma velocidade, e ela e a de corrida
        VISUAIS.put("Trote",            new Visual(178.7, 178.7, 175.2, normal("trote", "correr"),           normal("trote", "bater")));

        // Viciado: celular (fase 1) -> furioso sem celular (fase 2, bater a 24 fps)
        VISUAIS.put("Viciado", new Visual(202.3, 177.5, 173.9,
                normal("viciado", "andar_celular"),
                normal("viciado", "andar_celular"),                      // sem folha de bater com celular: ver baterComCelular
                normal("viciado", "correr"),
                new Animacao(padrao("viciado", "bater_fase2"), 1),       // 24 fps
                normal("viciado", "bater_celular")));                    // opcional: viciado/bater_celular/bater_celular_01..12.png
    }

    private static final Visual PADRAO = VISUAIS.get("Calouro");

    private static final Map<String, Boolean> EXISTE = new HashMap<>();

    private static boolean existe(Animacao a) {
        return EXISTE.computeIfAbsent(a.padrao(), p -> SpriteAnimado.existe(p, 1));
    }

    /** Qual animacao o zumbi deve estar tocando agora. */
    private Animacao escolher(Zumbi zumbi, Visual visual) {

        boolean atacando = zumbi.estaAtacando();

        if (zumbi instanceof Viciado viciado && viciado.estaEstressado() && visual.andarFuria() != null) {
            return atacando ? visual.baterFuria() : visual.andarFuria();
        }

        if (atacando) {
            if (visual.baterComCelular() != null && existe(visual.baterComCelular())) {
                return visual.baterComCelular();
            }
            return visual.bater();
        }

        return visual.andar();
    }

    // =================================================================

    /**
     * @param animacoes a animacao ativa de cada zumbi (qualquer estado); e
     *                  trocada aqui quando ele comeca/para de atacar ou fica furioso
     */
    public void atualizarVisuaisDosZumbis(Pane camadaJogo, GerenciadorDeJogo gerenciadorDeJogo, Map<Zumbi, ImageView> zumbisVisuais, Map<Zumbi, Timeline> animacoes, double gradeX, double gradeY, double celLarg, double celAlt) {

        List<Zumbi> zumbisAtivos = gerenciadorDeJogo.getZumbis();

        // Remove zumbis que sairam do jogo e interrompe a animacao deles.
        zumbisVisuais.entrySet().removeIf(entrada -> {
            Zumbi zumbi = entrada.getKey();

            if (!zumbisAtivos.contains(zumbi)) {
                camadaJogo.getChildren().remove(entrada.getValue());

                Timeline animacao = animacoes.remove(zumbi);
                if (animacao != null) {
                    animacao.stop();
                }
                return true;
            }

            return false;
        });

        for (Zumbi zumbi : zumbisAtivos) {

            Visual visual = VISUAIS.getOrDefault(zumbi.getClass().getSimpleName(), PADRAO);
            ImageView imagem = zumbisVisuais.get(zumbi);
            boolean novo = false;

            if (imagem == null) {
                imagem = new ImageView();
                imagem.setPreserveRatio(true);
                imagem.setFitWidth(visual.largura());
                imagem.setFitHeight(visual.altura());
                imagem.setMouseTransparent(true);

                zumbisVisuais.put(zumbi, imagem);
                camadaJogo.getChildren().add(imagem);
                novo = true;
            }

            // troca a animacao quando o estado muda (ou na criacao)
            Animacao desejada = escolher(zumbi, visual);

            if (novo || !desejada.padrao().equals(imagem.getProperties().get("padrao"))) {

                Timeline anterior = animacoes.remove(zumbi);
                if (anterior != null) {
                    anterior.stop();
                }

                // fase aleatoria so na criacao: os zumbis nao andam em marcha unida,
                // mas um ataque sempre comeca no frame 1
                SpriteAnimado sprite = new SpriteAnimado(
                        imagem, desejada.padrao(), 1, FRAMES,
                        FPS, desejada.segurar(), SpriteAnimado.Modo.LOOP, novo);

                imagem.getProperties().put("padrao", desejada.padrao());
                animacoes.put(zumbi, sprite.getTimeline());
                sprite.play();
            }

            // x: centrado na posicao do modelo; y: pes na linha do chao da casa
            imagem.setLayoutX(gradeX + zumbi.getPosicaoX() * celLarg + DESLOCAMENTO_X - visual.largura() / 2);
            imagem.setLayoutY(gradeY + zumbi.getLinha() * celAlt + CHAO_NA_LINHA - visual.pe());
        }
    }
}
