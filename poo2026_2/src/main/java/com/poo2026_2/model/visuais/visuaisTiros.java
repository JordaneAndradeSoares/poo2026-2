package com.poo2026_2.model.visuais;

import java.net.URL;
import java.util.List;
import java.util.Map;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import com.poo2026_2.audio.AudioManager;
import com.poo2026_2.model.GerenciadorDeJogo;
import com.poo2026_2.model.Tiro;

public class visuaisTiros {
    
    public void atualizarVisuaisDosTiros(double CEL_LARG, double CEL_ALT, double GRADE_X, double GRADE_Y, Pane camadaJogo, GerenciadorDeJogo gerenciadorDeJogo, Map<Tiro, ImageView> tirosVisuais) {

        List<Tiro> tirosAtivos = gerenciadorDeJogo.getTiros();

        // remove tiros que já desapareceram do modelo.
        tirosVisuais.entrySet().removeIf(entrada -> {

            if (!tirosAtivos.contains(entrada.getKey())) {
                camadaJogo.getChildren().remove(entrada.getValue());
                return true;
            }

            return false;
        });

        for (Tiro tiro : tirosAtivos) {

            ImageView imagem = tirosVisuais.get(tiro);

            if (imagem == null) {

                String caminho = tiro.getSprite();
                URL recurso = getClass().getResource(caminho);

                if (recurso == null) {
                    System.err.println("Imagem de tiro não encontrada: " + caminho);
                    continue;
                }

                imagem = new ImageView(new Image(recurso.toExternalForm()));
                imagem.setFitWidth(40);
                imagem.setFitHeight(40);
                imagem.setPreserveRatio(true);
                imagem.setMouseTransparent(true);

                tirosVisuais.put(tiro, imagem);
                camadaJogo.getChildren().add(imagem);

                if (tiro.getSom() != null && !tiro.getSom().isBlank()) {
                    AudioManager.tocarEfeito(tiro.getSom());
                }
            }

            imagem.setLayoutX(GRADE_X + tiro.getX() * CEL_LARG);
            imagem.setLayoutY(GRADE_Y + tiro.getY() * CEL_ALT + CEL_ALT * 0.4);
        }
    }
}