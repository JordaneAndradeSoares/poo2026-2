package com.poo2026_2.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class GerenciadorDeTelas {

    private final Stage stage;

    /*
     * Mantemos uma unica Scene viva durante toda a execucao do jogo e
     * apenas trocamos o "root" dela a cada mudanca de tela.
     *
     * Antes, cada chamada a mudarTela() criava uma Scene NOVA e chamava
     * stage.setScene(scene). Trocar a Scene inteira faz o JavaFX
     * reconfigurar a janela nativa, e isso derruba o modo tela cheia
     * (por isso o botao "voltar" do Extras saia do fullscreen).
     * Trocando so o root, a janela nunca e recriada e o fullscreen
     * (assim como o tamanho da janela) e preservado.
     */
    private Scene scene;

    public GerenciadorDeTelas(Stage stage) {
        this.stage = stage;
    }

    public Stage getStage() {
        return stage;
    }

    public void mudarTela(String arquivoFXML) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/" + arquivoFXML)
            );

            Parent root = loader.load();
            Object controller = loader.getController();

            if (controller instanceof ControladorTela tela) {
                tela.setGerenciadorDeTelas(this);
            }

            if (scene == null) {
                scene = new Scene(root, stage.getWidth(), stage.getHeight());
                stage.setScene(scene);
            } else {
                scene.setRoot(root);
            }

            stage.show();

        } catch (IOException | NullPointerException e) {
            throw new RuntimeException(
                    "Erro ao carregar a tela: " + arquivoFXML, e
            );
        }
    }

    public void voltarMenu() {
        mudarTela("Menu.fxml");
    }
}
