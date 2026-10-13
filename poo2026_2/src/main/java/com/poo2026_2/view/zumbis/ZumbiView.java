package com.poo2026_2.view.zumbis;

import javafx.animation.AnimationTimer;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;

import com.poo2026_2.model.zumbis.Zumbi;

public class ZumbiView extends ImageView {

    //private Image[] sprites;
    private Image[] spritesCaminhada;
    private Image[] spritesAtaque;

    private int quadroAtual = 0;
    
    private boolean estavaAtacando = false;

    private AnimationTimer animacao;

    private Zumbi zumbi;

    /*
    private Image[] recortarSprites(Image imagem) {

        Image[] resultado = new Image[12];

        PixelReader leitor = imagem.getPixelReader();
         
        int larguraSprite = (int) imagem.getWidth() / 6;
        int alturaSprite = (int) imagem.getHeight() / 2;

        for (int linha = 0; linha < 2; linha++) {

            for (int coluna = 0; coluna < 6; coluna++) {

                int indice = linha * 6 + coluna;

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
    */

    private Image[] recortarSprites(Image imagem) {

        int numeroDeColunas = 3;
        int numeroDeLinhas = 3;
        int totalDeQuadros = 8;

        Image[] resultado = new Image[totalDeQuadros];

        PixelReader leitor = imagem.getPixelReader();

        int larguraSprite = (int) imagem.getWidth() / numeroDeColunas;
        int alturaSprite = (int) imagem.getHeight() / numeroDeLinhas;

        for (int indice = 0; indice < totalDeQuadros; indice++) {

            int linha = indice / numeroDeColunas;
            int coluna = indice % numeroDeColunas;

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

        return resultado;
    }

    //public ZumbiView(String nomeArquivo) {
    /*
    public ZumbiView(String nomeArquivo, Zumbi zumbi) {

        this.zumbi = zumbi;

        Image imagemOriginal = new Image(getClass().getResource("/images/zumbis/" + nomeArquivo).toExternalForm());

        spritesCaminhada = recortarSpritesCaminhada(imagemOriginal);
        spritesAtaque = recortarSpritesAtaque(imagemOriginal);

        setImage(sprites[0]);

        setFitWidth(300);
        setFitHeight(400);
        
        setPreserveRatio(true);

        setTranslateY(-130); // desloca o visual em pixels para a cima
        //setTranslateX(-15); // desloca o visual em pixels para a esquerda

        atualizarAnimacao();
    }
    */

    public ZumbiView(String arquivo, Zumbi zumbi) {
        this.zumbi = zumbi;

        Image imagemCaminhada = new Image(
            getClass().getResource(
                "/images/zumbis/caminhada/" + arquivo + "Caminhada.png"
            ).toExternalForm()
        );

        Image imagemAtaque = new Image(
            getClass().getResource(
                "/images/zumbis/ataque/" + arquivo + "Ataque.png"
            ).toExternalForm()
        );

        spritesCaminhada = recortarSprites(imagemCaminhada);
        spritesAtaque = recortarSprites(imagemAtaque);

        setImage(spritesCaminhada[0]);

        setFitWidth(300);
        setFitHeight(400);
        setPreserveRatio(true);
        setTranslateY(-130);

        atualizarAnimacao();
    }
    
    /* 
    public void atualizarAnimacao() {

        /*
        versão 1
        if (zumbi.zumbiEstaVivo() && !zumbi.zumbiEstaAtacando()) {

        if (animacao == null) {
                iniciarAnimacao();
            }
        } else {
            pararAnimacao();
            animacaoDeAtaque();
        }
        
        versão 2
        if (!zumbi.zumbiEstaVivo()) {
            pararAnimacao();
            return;
        }

        if (zumbi.zumbiEstaAtacando()) {
           animacaoDeAtaque();
        } else {
            animacaoDeCaminhada();
        }
    }
    */
    
    /* 
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
    */

    /*
    public void animacaoDeAtaque(){
        
        animacao = new AnimationTimer() {
            private long ultimoQuadro = 0;
            
            @Override
            public void handle(long agora) {

                if (!zumbi.zumbiEstaVivo()) {

                    pararAnimacao();
                    return;
                }

                if (!zumbi.zumbiEstaAtacando()) {
                    
                    animacaoDeCaminhada();
                    return;
                }

                if (agora - ultimoQuadro >= 120_000_000L) {
                    quadroAtual = (quadroAtual + 1) % spritesAtaque.length;
                    setImage(spritesAtaque[quadroAtual]);
                    ultimoQuadro = agora;
                }
            }
        };

        animacao.start();
    }

    public void animacaoDeCaminhada() {

        animacao = new AnimationTimer() {
            private long ultimoQuadro = 0;

            @Override
            public void handle(long agora) {
                if (!zumbi.zumbiEstaVivo()) {
                    pararAnimacao();
                    return;
                }

                if (zumbi.zumbiEstaAtacando()) {
                    //quadroAtual = 0;
                    //setImage(sprites[0]);
                    //ultimoQuadro = agora;

                    animacaoDeAtaque();
                    return;
                }

                if (agora - ultimoQuadro >= 120_000_000L) {
                    quadroAtual = (quadroAtual + 1) % spritesCaminhada.length;
                    setImage(spritesCaminhada[quadroAtual]);
                    ultimoQuadro = agora;
                }
            }
        };

        animacao.start();
    }
    */

    public void atualizarAnimacao() {
        if (!zumbi.zumbiEstaVivo()) {
            pararAnimacao();
            return;
        }

        if (animacao != null) {
            return;
        }

        animacao = new AnimationTimer() {
            private long ultimoQuadro = 0;

            @Override
            public void handle(long agora) {
                if (!zumbi.zumbiEstaVivo()) {
                    pararAnimacao();
                    return;
                }

                boolean atacando = zumbi.zumbiEstaAtacando();

                Image[] sequencia = atacando ? spritesAtaque : spritesCaminhada;

                if (atacando != estavaAtacando) {
                    quadroAtual = 0;
                    estavaAtacando = atacando;
                    setImage(sequencia[0]);
                    ultimoQuadro = agora;
                    return;
                }

                if (agora - ultimoQuadro >= 200_000_000L) {
                    quadroAtual = (quadroAtual + 1) % sequencia.length;
                    setImage(sequencia[quadroAtual]);
                    ultimoQuadro = agora;
                }
            }
        };

        animacao.start();
    }
    
    public void pararAnimacao() {

        if (animacao != null) {
            animacao.stop();
            animacao = null;
        }

        quadroAtual = 0;
        estavaAtacando = false;

        if (spritesCaminhada != null && spritesCaminhada.length > 0) {
            setImage(spritesCaminhada[0]);
        }
    }
}