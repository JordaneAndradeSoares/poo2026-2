package com.poo2026_2.model.visuais;

import java.net.URL;
import java.util.Map;
import java.util.Set;

import com.poo2026_2.audio.AudioManager;

import java.util.Collection;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.util.Duration;


public class visuaisBotoes {

    // Sons para os botões
    public void configurarSomDeHover(Button... botoes) {

        for (Button botao : botoes) {
            botao.setOnMouseEntered(event -> AudioManager.tocarSomHoverBotao());
        }
    }

    public void configurarClick(Button... botoes) {
        
        for (Button botao : botoes) {
            botao.setOnMousePressed(event -> AudioManager.tocarSomBotao());
        }
    }

    // =================================================================

    public void atualizarBotoesProfessores(Collection<String> professoresSelecionados, Button btnCafeteira, Button btnDaniel, Button btnFabricio, Button btnGlaucia, Button btnLucas, Button btnMarcus, Button btnNacif, Button btnPhilipe, Button btnThais) {

        btnCafeteira.setVisible(professoresSelecionados.contains("CAFETEIRA"));
        btnCafeteira.setManaged(btnCafeteira.isVisible());

        btnDaniel.setVisible(professoresSelecionados.contains("DANIEL"));
        btnDaniel.setManaged(btnDaniel.isVisible());

        btnFabricio.setVisible(professoresSelecionados.contains("FABRICIO"));
        btnFabricio.setManaged(btnFabricio.isVisible());

        btnGlaucia.setVisible(professoresSelecionados.contains("GLAUCIA"));
        btnGlaucia.setManaged(btnGlaucia.isVisible());

        btnLucas.setVisible(professoresSelecionados.contains("LUCAS"));
        btnLucas.setManaged(btnLucas.isVisible());

        btnMarcus.setVisible(professoresSelecionados.contains("MARCUS"));
        btnMarcus.setManaged(btnMarcus.isVisible());

        btnNacif.setVisible(professoresSelecionados.contains("NACIF"));
        btnNacif.setManaged(btnNacif.isVisible());

        btnPhilipe.setVisible(professoresSelecionados.contains("PHILIPE"));
        btnPhilipe.setManaged(btnPhilipe.isVisible());

        btnThais.setVisible(professoresSelecionados.contains("THAIS"));
        btnThais.setManaged(btnThais.isVisible());
    }
   
    // =================================================================

    public void configurarBotaoPersonagem(Button botao, String nome, String caminhoImagem, int custoCafe) {

        URL recurso = getClass().getResource(caminhoImagem);

        if (recurso == null) {
            System.err.println("Imagem não encontrada: " + caminhoImagem);
            return;
        }

        ImageView imagem = new ImageView(new Image(recurso.toExternalForm()));

        imagem.setFitWidth(80);
        imagem.setFitHeight(80);
        imagem.setPreserveRatio(true);
        imagem.setSmooth(true);

        Label lblNome = new Label(nome);
        lblNome.getStyleClass().add("nome-personagem");

        Label lblCusto = new Label("Custo: " + custoCafe + " cafés");
        lblCusto.getStyleClass().add("custo-personagem");

        VBox conteudo = new VBox(3, imagem, lblNome, lblCusto);
        conteudo.setAlignment(Pos.CENTER);
        conteudo.setMouseTransparent(true);

        botao.setText("");
        botao.setGraphic(conteudo);
        botao.setContentDisplay(javafx.scene.control.ContentDisplay.GRAPHIC_ONLY);
        botao.getStyleClass().add("botao-personagem");
        botao.setPrefWidth(150);
        botao.setPrefHeight(150);
        botao.setMinWidth(150);
        botao.setMinHeight(150);
    }
   
    // =================================================================
    
    // tempo de recarga dos botões dos professores e da cafeteira dentro da fase
    public void iniciarTempoDeRecarga(String nome, double segundos, Map<String, Button> botoesPersonagens, Map<String, Timeline> recargas) {

        Button botao = botoesPersonagens.get(nome);

        if (botao == null) {
            return;
        }

        // Interrompe uma recarga anterior desse botão, se existir.
        Timeline recargaAnterior = recargas.get(nome);

        if (recargaAnterior != null) {
            recargaAnterior.stop();
        }

        botao.setDisable(true);

        Timeline recarga = new Timeline(
            new KeyFrame(
                Duration.seconds(segundos),
                evento -> botao.setDisable(false)
            )
        );

        recargas.put(nome, recarga);
        recarga.play();
    }
}