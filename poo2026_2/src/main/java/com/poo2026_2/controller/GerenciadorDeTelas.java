package com.poo2026_2.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class GerenciadorDeTelas {

    private final Stage stage;

    public GerenciadorDeTelas(Stage stage) {
        this.stage = stage;
    }

    public void mudarTela(String arquivoFXML) {
        try {
            Parent root = FXMLLoader.load(
                    getClass().getResource("/fxml/" + arquivoFXML)
            );

            Scene scene = new Scene(root);

            stage.setScene(scene);
            stage.show();

        } catch (IOException | NullPointerException e) {
            throw new RuntimeException(
                    "Erro ao carregar a tela: " + arquivoFXML, e
            );
        }
    }
}