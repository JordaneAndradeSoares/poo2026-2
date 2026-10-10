/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.poo2026_2.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

/**
 * FXML Controller class
 *
 * @author thoma
 */
public class SelecaoProfessorController implements ControladorTela, Initializable {

    /**
     * Initializes the controller class.
     */

    private String professorSelecionado;
    public String getProfessorSelecionado() {return professorSelecionado;}
    private GerenciadorDeTelas gerenciadorDeTelas;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }   
    
    @Override
    public void setGerenciadorDeTelas(GerenciadorDeTelas gerenciadorDeTelas) {
        this.gerenciadorDeTelas = gerenciadorDeTelas;
    }
    
    @FXML
    private void selecionarDaniel(ActionEvent event) {
        professorSelecionado = "Daniel";
        System.out.println("Professor escolhido: " + professorSelecionado);
    }

    @FXML
    private void selecionarFabricio(ActionEvent event) {
        professorSelecionado = "Fabricio";
        System.out.println("Professor escolhido: " + professorSelecionado);
    }
}
