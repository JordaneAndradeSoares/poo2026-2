package com.poo2026_2.model.visuais;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import com.poo2026_2.model.GerenciadorDeJogo;
import com.poo2026_2.model.visuais.visuaisGeral;
import com.poo2026_2.model.zumbis.Zumbi;

public class visuaisZumbis {

    private final visuaisGeral visualGeral = new visuaisGeral();

    public String obterCaminhoCaminhada(Zumbi zumbi) {
        String tipo = zumbi.getClass().getSimpleName();

        switch (tipo) {
            case "CalouroBlindado":
                return "/images/zumbis/caminhada/calouroBlindadoCaminhada.png";

            case "CalouroProtegido":
                return "/images/zumbis/caminhada/calouroProtegidoCaminhada.png";

            case "Doutorando":
                return "/images/zumbis/caminhada/doutorandoCaminhada.png";

            case "Formando":
                return "/images/zumbis/caminhada/formandoCaminhada.png";

            case "Trote":
                return "/images/zumbis/caminhada/troteCaminhada.png";

            case "VibeCoder":
                return "/images/zumbis/caminhada/vibeCoderCaminhada.png";

            case "Viciado":
                return "/images/zumbis/caminhada/viciadoCaminhada.png";

            default:
                return "/images/zumbis/caminhada/calouroCaminhada.png";
        }
    }

    public String obterCaminhoAtaque(Zumbi zumbi) {
        String tipo = zumbi.getClass().getSimpleName();

        switch (tipo) {
            case "CalouroBlindado":
                return "/images/zumbis/ataque/calouroBlindadoAtaque.png";

            case "CalouroProtegido":
                return "/images/zumbis/ataque/calouroProtegidoAtaque.png";

            case "Doutorando":
                return "/images/zumbis/ataque/doutorandoAtaque.png";

            case "Formando":
                return "/images/zumbis/ataque/formandoAtaque.png";

            case "Trote":
                return "/images/zumbis/ataque/troteAtaque.png";

            case "VibeCoder":
                return "/images/zumbis/ataque/vibeCoderAtaque.png";

            case "Viciado":
                return "/images/zumbis/ataque/viciadoAtaque.png";

            default:
                return "/images/zumbis/ataque/calouroAtaque.png";
        }
    }

    // =================================================================

    public void atualizarVisuaisDosZumbis(Pane camadaJogo, GerenciadorDeJogo gerenciadorDeJogo, Map<Zumbi, ImageView> zumbisVisuais, Map<Zumbi, Timeline> animacoesCaminhada, Map<Zumbi, Timeline> animacoesAtaque, double gradeX, double gradeY, double celLarg, double celAlt) {

        List<Zumbi> zumbisAtivos = gerenciadorDeJogo.getZumbis();

        // Remove zumbis que saíram do jogo e interrompe as duas animações.
        zumbisVisuais.entrySet().removeIf(entrada -> {
            Zumbi zumbi = entrada.getKey();

            if (!zumbisAtivos.contains(zumbi)) {
                camadaJogo.getChildren().remove(entrada.getValue());

                Timeline caminhada = animacoesCaminhada.remove(zumbi);
                if (caminhada != null) {
                    caminhada.stop();
                }

                Timeline ataque = animacoesAtaque.remove(zumbi);
                if (ataque != null) {
                    ataque.stop();
                }

                return true;
            }

            return false;
        });

        for (Zumbi zumbi : zumbisAtivos) {
            ImageView imagem = zumbisVisuais.get(zumbi);

            // cria o visual do zumbi
            if (imagem == null) {

                String caminho = obterCaminhoCaminhada(zumbi);
                URL recurso = getClass().getResource(caminho);

                if (recurso == null) {
                    System.err.println("Spritesheet de caminhada não encontrado: " + caminho);
                    continue;
                }

                Image spritesheet = new Image(recurso.toExternalForm());
                Image[] quadros = visualGeral.recortarSprites(spritesheet, 3, 3, 8);

                if (quadros.length == 0) {
                    System.err.println("Nenhum quadro encontrado: " + caminho);
                    continue;
                }

                imagem = new ImageView(quadros[0]);
                imagem.setFitWidth(140);
                imagem.setFitHeight(200);
                imagem.setTranslateY(-100);
                imagem.setPreserveRatio(true);
                imagem.setSmooth(false);
                imagem.setMouseTransparent(true);

                // guarda os quadros para restaurar a caminhada após o ataque
                imagem.getProperties().put("quadrosCaminhada", quadros);
                imagem.getProperties().put("quadroCaminhada", 0);
                imagem.getProperties().put("mostrandoAtaque", false);

                zumbisVisuais.put(zumbi, imagem);
                camadaJogo.getChildren().add(imagem);

                final ImageView imagemAnimada = imagem;

                    Timeline animacao = new Timeline(
                        new KeyFrame(Duration.millis(120), evento -> {

                            int atual = (Integer) imagemAnimada.getProperties().getOrDefault("quadroCaminhada", 0);
                            int proximo = (atual + 1) % quadros.length;

                            imagemAnimada.setImage(quadros[proximo]);
                            imagemAnimada.getProperties().put("quadroCaminhada", proximo);
                        })
                    );

                    animacao.setCycleCount(Timeline.INDEFINITE);
                    animacoesCaminhada.put(zumbi, animacao);
                    animacao.playFromStart();
                }

                Timeline animacao = animacoesCaminhada.get(zumbi);

                boolean atacando = zumbi.estaAtacando();
                boolean mostrandoAtaque = Boolean.TRUE.equals(imagem.getProperties().get("mostrandoAtaque"));

                // Zumbi começou a atacar (carrega o spritesheet e troca a animação)
                if (atacando && !mostrandoAtaque) {

                    String caminhoAtaque = obterCaminhoAtaque(zumbi);
                    URL recursoAtaque = getClass().getResource(caminhoAtaque);

                    if (recursoAtaque == null) {
                        System.err.println("Spritesheet de ataque não encontrado: " + caminhoAtaque);
                    } else {
                        Image spritesheetAtaque = new Image(recursoAtaque.toExternalForm());
                        Image[] quadrosAtaque = visualGeral.recortarSprites(spritesheetAtaque, 3, 3, 8);

                        if (quadrosAtaque.length > 0) {
                            if (animacao != null) {
                                animacao.stop();
                            }

                            imagem.setImage(quadrosAtaque[0]);
                            imagem.getProperties().put("mostrandoAtaque", true);
                            imagem.getProperties().put("quadroAtaque", 0);

                            // Referência final para ser usada dentro da expressão lambda
                            final ImageView imagemAnimadaAtaque = imagem;

                            Timeline animacaoAtaque = new Timeline(
                                new KeyFrame(Duration.millis(120), evento -> {
                                    int atual = (Integer) imagemAnimadaAtaque.getProperties()
                                        .getOrDefault("quadroAtaque", 0);
                                    int proximo = (atual + 1) % quadrosAtaque.length;

                                    imagemAnimadaAtaque.setImage(quadrosAtaque[proximo]);
                                    imagemAnimadaAtaque.getProperties().put("quadroAtaque", proximo);
                                })
                            );

                            animacaoAtaque.setCycleCount(Timeline.INDEFINITE);

                            Timeline anterior = animacoesAtaque.put(zumbi, animacaoAtaque);
                            if (anterior != null) {
                                anterior.stop();
                            }

                            animacaoAtaque.playFromStart();
                        } else {
                            System.err.println("Nenhum quadro de ataque encontrado: " + caminhoAtaque);
                        }
                    }
                }

                // Parou de atacar (restaura a caminhada) 
                else if (!atacando && mostrandoAtaque) {

                    Timeline ataque = animacoesAtaque.remove(zumbi);

                    if (ataque != null) {
                        ataque.stop();
                    }

                    Image[] quadros = (Image[]) imagem.getProperties()
                        .get("quadrosCaminhada");

                    int quadroAtual = (Integer) imagem.getProperties()
                        .getOrDefault("quadroCaminhada", 0);

                    if (quadros != null && quadros.length > 0) {
                        quadroAtual = quadroAtual % quadros.length;
                        imagem.setImage(quadros[quadroAtual]);
                    }

                    imagem.getProperties().put("mostrandoAtaque", false);

                    if (animacao != null) {
                        animacao.playFromStart();
                    }
                }

            // Mantém o zumbi na posição correspondente ao modelo
            imagem.setLayoutX(gradeX + zumbi.getPosicaoX() * celLarg - 40);
            imagem.setLayoutY(gradeY + zumbi.getLinha() * celAlt + 15);
        }
    }
}