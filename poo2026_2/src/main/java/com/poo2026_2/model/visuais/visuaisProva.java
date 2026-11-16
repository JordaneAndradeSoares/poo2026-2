package com.poo2026_2.model.visuais;

import java.net.URL;

import com.poo2026_2.audio.AudioManager;
import com.poo2026_2.model.GerenciadorDeJogo;
import com.poo2026_2.model.zumbis.Zumbi;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class visuaisProva {

    // =========================================================
    // CONFIGURACOES

    private static final double LARGURA_PROVA = 70;
    private static final double ALTURA_PROVA = 94;

    // Velocidade em casas por segundo.
    private static final double VELOCIDADE_PROVA = 8.0;

    // Distancia do zumbi para ativar a prova.
    private static final double LIMITE_ATIVACAO = 0.85;

    // Distancia para considerar uma colisao.
    private static final double DISTANCIA_COLISAO = 0.55;

    // =========================================================
    // ATRIBUTOS

    private Image imagemProva;

    private ImageView[] imagensProvas;

    private double[] posicoesX;

    private boolean[] provasArmadas;

    private boolean[] provasUsadas;

    private Pane camadaJogo;

    private int linhas;
    private int colunas;

    private double gradeX;
    private double gradeY;

    private double celLarg;
    private double celAlt;

    // =========================================================
    // CARREGAMENTO DA IMAGEM

    private boolean carregarImagem() {

        if (imagemProva != null) {
            return true;
        }

        // site usado para remover o fundo da imagem: https://www.remove.bg/pt-br/upload
        URL recurso = getClass().getResource("/images/prova.png");

        if (recurso == null) {
            System.err.println("Erro: imagem /images/prova.png nao encontrada.");
            return false;
        }

        imagemProva = new Image(recurso.toExternalForm());

        return true;
    }

    // =========================================================
    // CRIAR E ARMAR AS PROVAS

    public void armarProva(Pane camadaJogo, int linhas, int colunas, double gradeX, double gradeY, double celLarg, double celAlt) {

        if (!carregarImagem()) {
            return;
        }

        this.camadaJogo = camadaJogo;
        this.linhas = linhas;
        this.colunas = colunas;

        this.gradeX = gradeX;
        this.gradeY = gradeY;

        this.celLarg = celLarg;
        this.celAlt = celAlt;

        // Remove as imagens anteriores, caso existam.
        if (imagensProvas != null) {
            for (ImageView imagem : imagensProvas) {
                camadaJogo.getChildren().remove(imagem);
            }
        }

        imagensProvas = new ImageView[linhas];
        posicoesX = new double[linhas];
        provasArmadas = new boolean[linhas];
        provasUsadas = new boolean[linhas];

        // Cria uma prova para cada linha.
        for (int linha = 0; linha < linhas; linha++) {

            ImageView imagem = new ImageView(imagemProva);

            imagem.setFitWidth(LARGURA_PROVA);
            imagem.setFitHeight(ALTURA_PROVA);

            imagem.setPreserveRatio(true);
            imagem.setSmooth(true);
            imagem.setMouseTransparent(true);

            imagensProvas[linha] = imagem;

            // Posição inicial, antes da primeira coluna.
            posicoesX[linha] = -0.25;

            provasArmadas[linha] = false;
            provasUsadas[linha] = false;

            atualizarPosicaoVisual(linha);

            camadaJogo.getChildren().add(imagem);
        }

        System.out.println("Provas posicionadas: " + linhas);
    }

    // =========================================================
    // ATUALIZAR A POSICAO VISUAL

    private void atualizarPosicaoVisual(int linha) {

        ImageView imagem = imagensProvas[linha];

        double x = gradeX + posicoesX[linha] * celLarg - LARGURA_PROVA / 2;
        double y = gradeY + linha * celAlt + (celAlt - ALTURA_PROVA) / 2;

        imagem.setLayoutX(x);
        imagem.setLayoutY(y);
    }

    // =========================================================
    // MOVIMENTAR E ATIVAR AS PROVAS

    public void moveProva(double deltaTime, GerenciadorDeJogo gerenciadorDeJogo) {

        if (imagensProvas == null || gerenciadorDeJogo == null || deltaTime <= 0) {
            return;
        }

        for (int linha = 0; linha < linhas; linha++) {

            // Verifica se existe algum zumbi próximo.
            if (!provasUsadas[linha]) {

                for (Zumbi zumbi : gerenciadorDeJogo.getZumbis()) {

                    if (!zumbi.estaVivo()) {
                        continue;
                    }

                    if (zumbi.getLinha() != linha) {
                        continue;
                    }

                    if (zumbi.getPosicaoX() <= LIMITE_ATIVACAO) {

                        provasArmadas[linha] = true;
                        provasUsadas[linha] = true;

                        AudioManager.tocarEfeito("/audio/prova.mp3");
                        System.out.println("Prova ativada na linha " + linha);

                        break;
                    }
                }
            }

            // Se a prova nao estiver ativa, nao se movimenta.
            if (!provasArmadas[linha]) {
                continue;
            }

            // Movimenta a prova para a direita.
            posicoesX[linha] += VELOCIDADE_PROVA * deltaTime;

            // Verifica as colisoes com os zumbis.
            for (Zumbi zumbi : gerenciadorDeJogo.getZumbis()) {

                if (!zumbi.estaVivo()) {
                    continue;
                }

                if (zumbi.getLinha() != linha) {
                    continue;
                }

                double distancia = Math.abs(zumbi.getPosicaoX() - posicoesX[linha]);

                if (distancia <= DISTANCIA_COLISAO) {
                    zumbi.receberDano(zumbi.getVida());
                    System.out.println("Prova eliminou um zumbi na linha " + linha);
                }
            }

            // Atualiza a imagem na tela.
            atualizarPosicaoVisual(linha);

            imagensProvas[linha].toFront();

            // Quando ultrapassa o tabuleiro, desaparece.
            if (posicoesX[linha] > colunas + 0.6) {

                provasArmadas[linha] = false;
                imagensProvas[linha].setVisible(false);
                System.out.println("Prova utilizada na linha " + linha);
            }
        }
    }

    // =========================================================
    // VERIFICAR SE ALGUMA PROVA ESTA ARMADA

    public boolean verificaSeAProvaEstaArmada() {

        if (provasArmadas == null) {
            return false;
        }

        for (boolean armada : provasArmadas) {
            if (armada) {
                return true;
            }
        }

        return false;
    }

    // Verifica uma linha especifica.
    public boolean verificaSeAProvaEstaArmada(int linha) {

        if (provasArmadas == null || linha < 0 || linha >= linhas) {
            return false;
        }

        return provasArmadas[linha];
    }

    // =========================================================
    // VERIFICAR SE A PROVA JA FOI UTILIZADA

    public boolean provaFoiUsada(int linha) {

        if (provasUsadas == null || linha < 0 || linha >= linhas) {
            return false;
        }

        return provasUsadas[linha];
    }
}