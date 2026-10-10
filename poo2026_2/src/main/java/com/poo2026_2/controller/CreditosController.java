/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.poo2026_2.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;

import javafx.fxml.FXML;

import javafx.scene.control.Button;
import com.poo2026_2.audio.AudioManager;

/**
 * FXML Controller class
 *
 * @author thoma
 */

public class CreditosController implements Initializable, ControladorTela {

    private GerenciadorDeTelas gerenciadorDeTelas;

    @Override
    public void setGerenciadorDeTelas(GerenciadorDeTelas gerenciadorDeTelas) {

        this.gerenciadorDeTelas = gerenciadorDeTelas;

    }

    @FXML
    private Button btnVoltar;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        btnVoltar.setOnMouseEntered(event -> AudioManager.tocarSomHoverBotao() );
    }

    /* 
    @FXML
    private void voltar() {

        AudioManager.tocarEfeito("/audio/botao-hover.m4a"); 

        if (gerenciadorDeTelas == null) { 

            System.err.println( "Erro: GerenciadorDeTelas não foi configurado nos créditos."); 
            return; 
        }

        gerenciadorDeTelas.voltarMenu();

    }
    */

    @FXML
    private void voltar() {

        System.out.println("O método voltar() foi chamado.");

        AudioManager.tocarEfeito("/audio/botao-hover.m4a"); // efeito sonoro do botão

        if (gerenciadorDeTelas == null) {
            System.err.println("Erro: GerenciadorDeTelas está null.");
            return;
        }

        System.out.println("Chamando voltarMenu().");
        gerenciadorDeTelas.voltarMenu();
    }
}