package com.poo2026_2.model.visuais;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class visuaisGeral{

    public Image[] recortarSprites(Image imagem, int colunas, int linhas, int totalQuadros) {

        PixelReader leitor = imagem.getPixelReader();

        if (leitor == null) {
            throw new IllegalArgumentException("Não foi possível ler o spritesheet");
        }

        int larguraQuadro = (int) imagem.getWidth() / colunas;
        int alturaQuadro = (int) imagem.getHeight() / linhas;

        List<Image> quadros = new ArrayList<>();

        for (int i = 0; i < totalQuadros; i++) {
            int xInicial = (i % colunas) * larguraQuadro;
            int yInicial = (i / colunas) * alturaQuadro;

            int minX = larguraQuadro;
            int minY = alturaQuadro;
            int maxX = -1;
            int maxY = -1;

            for (int y = 0; y < alturaQuadro; y++) {
                for (int x = 0; x < larguraQuadro; x++) {
                    if (leitor.getColor(
                            xInicial + x, yInicial + y
                        ).getOpacity() > 0.05) {

                        minX = Math.min(minX, x);
                        minY = Math.min(minY, y);
                        maxX = Math.max(maxX, x);
                        maxY = Math.max(maxY, y);
                    }
                }
            }

            // Ignora quadros totalmente transparentes
            if (maxX < minX || maxY < minY) {
                continue;
            }

            int margem = 3;

            minX = Math.max(0, minX - margem);
            minY = Math.max(0, minY - margem);
            maxX = Math.min(larguraQuadro - 1, maxX + margem);
            maxY = Math.min(alturaQuadro - 1, maxY + margem);

            quadros.add(new WritableImage(
                leitor,
                xInicial + minX,
                yInicial + minY,
                maxX - minX + 1,
                maxY - minY + 1
            ));
        }

        return quadros.toArray(new Image[0]);
    }
    
    // =================================================================

    public void adicionarSpriteAnimado(StackPane celula, String caminhoSpritesheet, int colunas, int linhas, int totalQuadros, Map<ImageView, Timeline> animacoes) {

        URL recurso = getClass().getResource(caminhoSpritesheet);

        if (recurso == null) {
            System.err.println("Spritesheet de professor não encontrado: " + caminhoSpritesheet);
            return;
        }

        Image spritesheet = new Image(recurso.toExternalForm());
        Image[] quadros = recortarSprites(spritesheet, colunas, linhas, totalQuadros);

        if (quadros.length == 0) {
            System.err.println("Nenhum quadro encontrado no spritesheet: " + caminhoSpritesheet);
            return;
        }

        ImageView imagem = new ImageView(quadros[0]);
        imagem.setPreserveRatio(true);
        imagem.setFitWidth(140);
        imagem.setFitHeight(200);
        imagem.setSmooth(false);

        celula.getChildren().add(imagem);

        Timeline animacao = new Timeline(new KeyFrame(Duration.millis(100), event -> {

                int proximo = (Integer) imagem.getProperties().getOrDefault("quadroAtual", 0);

                proximo = (proximo + 1) % quadros.length;

                imagem.setImage(quadros[proximo]);
                imagem.getProperties().put("quadroAtual", proximo);
            })
        );

        animacao.setCycleCount(Timeline.INDEFINITE);
        animacao.play();

        animacoes.put(imagem, animacao);
    }
}