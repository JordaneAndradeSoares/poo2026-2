package com.poo2026_2;

import com.poo2026_2.controller.GerenciadorDeTelas;
import com.poo2026_2.controller.MenuController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Poo2026_2 extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/Menu.fxml")
        );

        Parent root = loader.load();

        MenuController controller = loader.getController();

        GerenciadorDeTelas gerenciadorDeTelas =
                new GerenciadorDeTelas(stage);

        controller.setGerenciadorDeTelas(gerenciadorDeTelas);

        Scene scene = new Scene(root, 800, 600);

        stage.setTitle("POO 2026-2");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}