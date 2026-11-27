package com.poo2026_2.model.visuais;

import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import com.poo2026_2.model.Entidade;
import com.poo2026_2.model.GerenciadorDeJogo;
import com.poo2026_2.model.TipoPersonagem;
import com.poo2026_2.model.utilizaveis.Lucas;
import com.poo2026_2.view.SpriteAnimado;
import com.poo2026_2.view.SpriteLucas;

public class visuaisProfessores {

    // remove do tabuleiro os professores/cafeteiras mortos e seus visuais
    public void removerProfessoresOuCafeteirasMortas(int COLUNAS, int LINHAS, StackPane[][] celulas, GerenciadorDeJogo gerenciadorDeJogo, Map<ImageView, Timeline> animacoes) {

        for (int linha = 0; linha < LINHAS; linha++) {
            for (int coluna = 0; coluna < COLUNAS; coluna++) {

                StackPane celula = celulas[linha][coluna];
                Entidade ocupante = gerenciadorDeJogo.getOcupante(linha, coluna);

                if (ocupante != null && !ocupante.estaVivo()) {
                    gerenciadorDeJogo.removerDoPiso(linha, coluna);
                    ocupante = null;
                }

                if (ocupante == null && !celula.getChildren().isEmpty()) {
                    for (javafx.scene.Node filho : new ArrayList<>(celula.getChildren())) {

                        if (filho instanceof ImageView) {

                            Timeline animacao = animacoes.remove((ImageView) filho);

                            if (animacao != null) {
                                animacao.stop();
                            }
                        }

                        celula.getChildren().remove(filho);
                    }
                }
            }
        }
    }

    // =================================================================

    /**
     * Coloca na casa o visual de um professor (a cafeteira tem o seu proprio
     * metodo). Cada personagem usa a caixa e o ajuste vertical do seu
     * TipoPersonagem; o Lucas usa a animacao guiada pelo estado do modelo.
     */
    public void adicionarProfessorVisual(StackPane celula, TipoPersonagem tipo, Entidade ocupante, Map<ImageView, Timeline> animacoes) {

        ImageView visual;
        Timeline timeline;

        if (ocupante instanceof Lucas lucas) {

            SpriteLucas sprite = new SpriteLucas(lucas);
            sprite.setCaixa(tipo.getLarguraCaixa(), tipo.getAlturaCaixa());
            visual = sprite.getView();
            timeline = sprite.getTimeline();
            sprite.play();

        } else {

            // loop 1->N, 24 fps, cada frame segurado 2 ticks
            SpriteAnimado sprite = new SpriteAnimado(
                    tipo.getPadraoFrames(), 1, tipo.getQuantidadeDeFrames());
            sprite.setCaixa(tipo.getLarguraCaixa(), tipo.getAlturaCaixa());
            visual = sprite.getView();
            timeline = sprite.getTimeline();
            sprite.play();
        }

        visual.setTranslateY(tipo.getAjusteY());

        celula.getChildren().add(visual);
        animacoes.put(visual, timeline);
    }

    // =================================================================

    /** Café entregue ao jogador a cada coleta. */
    private static final int CAFE_POR_COLETA = 50;
    /** Segundos até a cafeteira ficar pronta para coletar. */
    private static final double SEGUNDOS_ATE_PRONTA = 5;
    /** Segundos até a cafeteira "perder" o café não coletado e recomeçar. */
    private static final double SEGUNDOS_CICLO = 15;

    /**
     * A cafeteira fica sempre animada (12 frames, loop, 24 fps). Quando o café
     * fica pronto ela ganha um brilho dourado; clicar nela coleta o café.
     */
    public void adicionarCafeteiraVisual(StackPane celula, GerenciadorDeJogo gerenciadorDeJogo, Map<ImageView, Timeline> animacoes, Runnable atualizarQuantidadeDeCafe) {

        TipoPersonagem tipo = TipoPersonagem.CAFETEIRA;

        SpriteAnimado sprite = new SpriteAnimado(
                tipo.getPadraoFrames(), 1, tipo.getQuantidadeDeFrames());

        ImageView visual = sprite.getView();
        sprite.setCaixa(tipo.getLarguraCaixa(), tipo.getAlturaCaixa());
        visual.setTranslateY(tipo.getAjusteY());
        visual.setSmooth(false);

        celula.getChildren().add(visual);
        animacoes.put(visual, sprite.getTimeline());
        sprite.play();

        DropShadow brilho = new DropShadow(24, Color.GOLD);
        boolean[] pronta = {false};

        Timeline ciclo = new Timeline(

            new KeyFrame(Duration.seconds(SEGUNDOS_ATE_PRONTA), evento -> {
                pronta[0] = true;
                visual.setEffect(brilho);
            }),

            new KeyFrame(Duration.seconds(SEGUNDOS_CICLO), evento -> {
                pronta[0] = false;
                visual.setEffect(null);
            })
        );

        ciclo.setCycleCount(Timeline.INDEFINITE);
        ciclo.playFromStart();

        // o ciclo morre junto com o visual (cafeteira destruída ou casa limpa)
        visual.parentProperty().addListener((obs, antigo, novoPai) -> {
            if (novoPai == null) {
                ciclo.stop();
            }
        });

        visual.setOnMouseClicked(evento -> {

            // Impede que o clique chegue ao evento da célula.
            evento.consume();

            if (!pronta[0]) {
                return;
            }

            gerenciadorDeJogo.adicionarCafe(CAFE_POR_COLETA);

            // Reinicia a produção.
            pronta[0] = false;
            visual.setEffect(null);
            ciclo.playFromStart();

            atualizarQuantidadeDeCafe.run();
        });
    }
}