package com.poo2026_2.model.visuais;

import java.net.URL;

import com.poo2026_2.model.visuais.visuaisGeral;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

public class visuaisExplosao {

    private static final String CAMINHO_SPRITESHEET = "/images/explosao.png";

    private static final int COLUNAS = 3;
    private static final int LINHAS = 3;
    private static final int TOTAL_QUADROS = 8;

    private static final double TEMPO_QUADRO = 100;
    private static final int TAMANHO_EXPLOSAO = 3;

    private final visuaisGeral visuais = new visuaisGeral();

    // Exibe a animação da explosão na posição indicada
    public void mostrar(Pane camadaJogo, int linha, int coluna, double gradeX, double gradeY, double celLarg, double celAlt) {

        URL recurso = getClass().getResource(CAMINHO_SPRITESHEET);

        if (recurso == null) {
            System.err.println("Spritesheet não encontrado: " + CAMINHO_SPRITESHEET);
            return;
        }

        Image spritesheet = new Image(recurso.toExternalForm());

        if (spritesheet.isError()) {
            System.err.println("Erro ao carregar explosão.");
            return;
        }

        Image[] quadros = visuais.recortarSprites(
            spritesheet,
            COLUNAS,
            LINHAS,
            TOTAL_QUADROS
        );

        if (quadros == null) {
            return;
        }

        ImageView imagem = new ImageView(quadros[0]);

        imagem.setPreserveRatio(false);
        imagem.setSmooth(false);
        imagem.setMouseTransparent(true);
        imagem.setManaged(false);

        double largura = celLarg * TAMANHO_EXPLOSAO;
        double altura = celAlt * TAMANHO_EXPLOSAO;

        imagem.setFitWidth(largura);
        imagem.setFitHeight(altura);

        double centroX = gradeX + coluna * celLarg + celLarg / 2;
        double centroY = gradeY + linha * celAlt + celAlt / 2;

        imagem.setLayoutX(centroX - largura / 2);
        imagem.setLayoutY(centroY - altura / 2);

        camadaJogo.getChildren().add(imagem);

        final int[] quadroAtual = {0};

        Timeline animacao = new Timeline(
            new KeyFrame(Duration.millis(TEMPO_QUADRO), e -> {

                quadroAtual[0]++;

                if (quadroAtual[0] < TOTAL_QUADROS) {
                    imagem.setImage(quadros[quadroAtual[0]]);
                }
            })
        );

        animacao.setCycleCount(TOTAL_QUADROS - 1);

        animacao.setOnFinished(e -> {
            camadaJogo.getChildren().remove(imagem);
            animacao.stop();
        });

        animacao.play();
    }
}