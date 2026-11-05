package com.poo2026_2.model.visuais;

import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import com.poo2026_2.model.Entidade;
import com.poo2026_2.model.GerenciadorDeJogo;
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

    public void adicionarCafeteiraVisual(StackPane celula, GerenciadorDeJogo gerenciadorDeJogo, Map<ImageView, Timeline> animacoes, Runnable atualizarQuantidadeDeCafe) {

        URL recursoVazia = getClass().getResource("/images/professores/cafeteiraVazia.png");
        URL recursoCheia = getClass().getResource("/images/professores/cafeteiraCheia.png");

        if (recursoVazia == null || recursoCheia == null) {

            System.err.println("Não foi possível encontrar as imagens da cafeteira.");
            return;
        }

        Image cafeteiraVazia = new Image(recursoVazia.toExternalForm());
        Image cafeteiraCheia = new Image(recursoCheia.toExternalForm());

        ImageView visual = new ImageView(cafeteiraVazia);
        visual.setFitWidth(90);
        visual.setFitHeight(110);
        visual.setPreserveRatio(true);
        visual.setSmooth(false);

        celula.getChildren().add(visual);

        visual.getProperties().put("cheia", false);

        Timeline ciclo = new Timeline(

            new KeyFrame(Duration.seconds(5), evento -> {
                visual.setImage(cafeteiraCheia);
                visual.getProperties().put("cheia", true);
            }),

            new KeyFrame(Duration.seconds(15), evento -> {
                visual.setImage(cafeteiraVazia);
                visual.getProperties().put("cheia", false);
            })
        );

        ciclo.setCycleCount(Timeline.INDEFINITE);
        ciclo.playFromStart();

        animacoes.put(visual, ciclo);

        visual.setOnMouseClicked(evento -> {

            boolean cheia = (boolean) visual.getProperties().get("cheia");

            if (!cheia) {
                evento.consume();
                return;
            }

            // Coleta do café.
            gerenciadorDeJogo.adicionarCafe(50);

            // Reinicia a produção.
            visual.setImage(cafeteiraVazia);
            visual.getProperties().put("cheia", false);
            ciclo.playFromStart();

            atualizarQuantidadeDeCafe.run();

            // Impede que o clique chegue ao evento da célula.
            evento.consume();
        });
    }
}