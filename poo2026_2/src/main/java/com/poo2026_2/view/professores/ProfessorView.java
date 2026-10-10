package com.poo2026_2.view.professores;

import javafx.animation.AnimationTimer;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;

public class ProfessorView extends ImageView {

    private Image[] sprites;
    private int quadroAtual = 0;

    private AnimationTimer animacao;

    private Image[] recortarSprites(Image imagem) {

        Image[] resultado = new Image[12];

        PixelReader leitor = imagem.getPixelReader();

        //int inicioX = 40;
        //int inicioY = 90;

        //int larguraSprite = 240;
        //int alturaSprite = 400;
         
        //int larguraSprite = 256;
        //int alturaSprite = 512;

        int larguraSprite = (int) imagem.getWidth() / 6;
        int alturaSprite = (int) imagem.getHeight() / 2;

        for (int linha = 0; linha < 2; linha++) {

            for (int coluna = 0; coluna < 6; coluna++) {

                int indice = linha * 6 + coluna;

                //int x = inicioX + coluna * larguraSprite;
                //int y = inicioY + linha * alturaSprite;

                int x = coluna * larguraSprite;
                int y = linha * alturaSprite;

                resultado[indice] = new WritableImage(
                    leitor,
                    x,
                    y,
                    larguraSprite,
                    alturaSprite
                );
            }
        }

        return resultado;
    }

    public ProfessorView(String nomeArquivo) {

        Image imagemOriginal = new Image(getClass().getResource("/images/professores/" + nomeArquivo).toExternalForm());

        sprites = recortarSprites(imagemOriginal);

        setImage(sprites[0]);

        //setFitWidth(90);
        //setFitHeight(110);

        setFitWidth(80);
        setFitHeight(130);
        setPreserveRatio(true);

        iniciarAnimacao();
    }

    private void iniciarAnimacao() {

        animacao = new AnimationTimer() {

            private long ultimoQuadro = 0;

            @Override
            public void handle(long agora) {

                if (agora - ultimoQuadro >= 120_000_000) {

                    quadroAtual++;

                    if (quadroAtual >= sprites.length) {
                        quadroAtual = 0;
                    }

                    setImage(sprites[quadroAtual]);

                    ultimoQuadro = agora;
                }
            }
        };

        animacao.start();
    }

    public void pararAnimacao() {

        if (animacao != null) {
            animacao.stop();
        }
    }
}