package com.poo2026_2.controller;

import javafx.event.ActionEvent;
import javafx.stage.Stage;

public class MenuController {

    private GerenciadorDeTelas gerenciadorDeTelas;

    public void setGerenciadorDeTelas(GerenciadorDeTelas gerenciadorDeTelas) {
        this.gerenciadorDeTelas = gerenciadorDeTelas;
    }

    public void iniciarJogo(ActionEvent event) {
        gerenciadorDeTelas.mudarTela("SelecaoNivel.fxml");
    }
}