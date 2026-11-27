package com.poo2026_2.view;

import java.net.URL;

import com.poo2026_2.model.TipoPersonagem;
import javafx.animation.Animation;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.css.PseudoClass;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * Card de um personagem na barra de cima: foto, preço em café e, se o
 * personagem ainda não foi desbloqueado, cadeado. Depois de usado, uma cortina
 * escura "sobe" mostrando o tempo de recarga.
 */
public class CartaoPersonagem extends StackPane {

    public static final double TAMANHO = 156;
    private static final double BORDA = 4;
    private static final double INTERNO = TAMANHO - 2 * BORDA;

    private static final PseudoClass SELECIONADO = PseudoClass.getPseudoClass("selecionado");

    private final TipoPersonagem tipo;
    private final int custo;
    private final boolean bloqueado;
    private final Region cortina = new Region();

    private Timeline recarga;
    private double velocidade = 1.0;
    private boolean emRecarga;
    private boolean semCafe;

    public CartaoPersonagem(TipoPersonagem tipo, boolean bloqueado) {
        this.tipo = tipo;
        this.bloqueado = bloqueado;
        this.custo = tipo.getCusto();

        getStyleClass().add("cartao");
        setPrefSize(TAMANHO, TAMANHO);
        setMinSize(TAMANHO, TAMANHO);
        setMaxSize(TAMANHO, TAMANHO);

        ImageView foto = criarFoto();
        getChildren().add(foto);

        cortina.getStyleClass().add("cartao-cortina");
        cortina.setMaxWidth(INTERNO);
        cortina.setMaxHeight(0);
        cortina.setMouseTransparent(true);
        StackPane.setAlignment(cortina, Pos.TOP_CENTER);
        getChildren().add(cortina);

        if (bloqueado) {
            getStyleClass().add("cartao-bloqueado");
            foto.setEffect(new ColorAdjust(0, -1, -0.3, 0)); // preto e branco, mais escuro
            getChildren().add(criarCadeado());
        } else {
            getChildren().add(criarPreco());
        }
    }

    // ===================== ESTADO =====================
    public void pausar() {
        if (recarga != null && recarga.getStatus() == Animation.Status.RUNNING) {
            recarga.pause();
        }
    }

    public void retomar() {
        if (recarga != null && recarga.getStatus() == Animation.Status.PAUSED) {
            recarga.play();
        }
    }

    public TipoPersonagem getTipo() {
        return tipo;
    }

    public boolean isBloqueado() {
        return bloqueado;
    }

    /**
     * Pode ser escolhido agora: desbloqueado, fora de recarga e com café
     * suficiente.
     */
    public boolean isUtilizavel() {
        return !bloqueado && !emRecarga && !semCafe;
    }

    public void setSelecionado(boolean selecionado) {
        pseudoClassStateChanged(SELECIONADO, selecionado);
    }

    /**
     * Avisa o card quanto café o jogador tem agora (escurece se não der para
     * pagar).
     */
    public void atualizarCafe(int cafe) {
        semCafe = cafe < custo;
        atualizarAparencia();
    }

    public void iniciarRecarga(double segundos) {
        if (recarga != null) {
            recarga.stop();
        }
        emRecarga = true;
        cortina.setMaxHeight(INTERNO);

        recarga = new Timeline(new KeyFrame(Duration.seconds(segundos),
                new KeyValue(cortina.maxHeightProperty(), 0.0)));
        recarga.setOnFinished(e -> {
            emRecarga = false;
            atualizarAparencia();
        });
        recarga.setRate(velocidade);
        recarga.playFromStart();
        atualizarAparencia();
    }

    /**
     * Velocidade do jogo (1, 1.25, 1.5, 2): a recarga passa mais rapido junto
     * com o resto.
     */
    public void setVelocidade(double velocidade) {
        this.velocidade = velocidade;
        if (recarga != null && recarga.getRate() != velocidade) {
            recarga.setRate(velocidade);
        }
    }

    /**
     * Para a recarga (usado ao sair da tela).
     */
    public void parar() {
        if (recarga != null) {
            recarga.stop();
        }
    }

    private void atualizarAparencia() {
        setOpacity(!bloqueado && (semCafe || emRecarga) ? 0.6 : 1.0);
    }

    // ===================== VISUAL =====================
    private ImageView criarFoto() {
        ImageView foto = new ImageView();
        URL url = getClass().getResource(tipo.getCaminhoIcone());
        if (url != null) {
            foto.setImage(new Image(url.toExternalForm(), INTERNO * 2, INTERNO * 2, true, true));
        } else {
            System.err.println("Imagem não encontrada: " + tipo.getCaminhoIcone());
        }
        foto.setFitWidth(INTERNO);
        foto.setFitHeight(INTERNO);
        foto.setPreserveRatio(true);
        foto.setMouseTransparent(true);

        Rectangle recorte = new Rectangle(INTERNO, INTERNO);
        recorte.setArcWidth(12);
        recorte.setArcHeight(12);
        foto.setClip(recorte);
        return foto;
    }

    private Label criarPreco() {
        Label preco = new Label(String.valueOf(custo));
        preco.getStyleClass().add("cartao-preco");
        preco.setMouseTransparent(true);
        StackPane.setAlignment(preco, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(preco, new Insets(0, -14, -10, 0));
        return preco;
    }

    private ImageView criarCadeado() {
        ImageView cadeado = new ImageView();
        URL url = getClass().getResource("/images/game/itens/cadeado.png");
        if (url != null) {
            cadeado.setImage(new Image(url.toExternalForm(), 140, 140, true, true));
        }
        cadeado.setFitWidth(70);
        cadeado.setPreserveRatio(true);
        cadeado.setRotate(12);
        cadeado.setMouseTransparent(true);
        StackPane.setAlignment(cadeado, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(cadeado, new Insets(0, -18, -16, 0));
        return cadeado;
    }
}
