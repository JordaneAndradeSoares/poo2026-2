/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.poo2026_2.controller;

import com.poo2026_2.audio.AudioManager;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import javafx.event.ActionEvent;

public class SelecaoNivelController
        implements ControladorTela {

    @FXML
    private Button btnVoltar;

    private GerenciadorDeTelas gerenciadorDeTelas;

    @Override
    public void setGerenciadorDeTelas(GerenciadorDeTelas gerenciadorDeTelas) {

        this.gerenciadorDeTelas = gerenciadorDeTelas;
    }

    @FXML
    private void fase1() {

        gerenciadorDeTelas.mudarTela("Jogo.fxml");
    }

    @FXML
    private void initialize() {

        configurarSomDeHover(btnVoltar);
    }

    // Toca o mesmo som usado pelos botões do menu quando o mouse passa sobre o botão.
    private void configurarSomDeHover(Button... botoes) {

        for (Button botao : botoes) {

            botao.setOnMouseEntered(event -> AudioManager.tocarSomHoverBotao());
        }
    }

    // Volta para o menu principal.
    @FXML
    private void voltar() {

        gerenciadorDeTelas.mudarTela("Menu.fxml");
    }
}
