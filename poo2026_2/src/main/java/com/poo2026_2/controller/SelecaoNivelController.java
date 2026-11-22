/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.poo2026_2.controller;

import com.poo2026_2.audio.AudioManager;

import javafx.scene.control.Button;
import javafx.fxml.FXML;

/**
 * FXML Controller class
 *
 * @author thoma
 */

public class SelecaoNivelController implements ControladorTela {

    @FXML
    private Button btnVoltar;

    @FXML
    private Button btnNivel1;

    @FXML
    private Button btnNivel2;

    @FXML
    private Button btnNivel3;

    private GerenciadorDeTelas gerenciadorDeTelas;

    //////////////////////////////////////////////////////////////////

    // Guarda a fase escolhida antes de abrir o jogo
    public static int faseSelecionada = 1;

    // Guarda o progresso apenas enquanto o jogo estiver aberto
    private static int faseMaximaDesbloqueada = 1;

    public static int getFaseMaximaDesbloqueada() {
        return faseMaximaDesbloqueada;
    }

    public static void desbloquearProximaFase(int faseAtual) {

        int proximaFase = faseAtual + 1;

        if (proximaFase > faseMaximaDesbloqueada && proximaFase <= 3) {
            faseMaximaDesbloqueada = proximaFase;
        }
    }

    private void atualizarBotoesFases() {
        int faseDesbloqueada = getFaseMaximaDesbloqueada();

        btnNivel1.setDisable(false);
        btnNivel2.setDisable(faseDesbloqueada < 2);
        btnNivel3.setDisable(faseDesbloqueada < 3);
    }

    //////////////////////////////////////////////////////////////////

    @Override
    public void setGerenciadorDeTelas(GerenciadorDeTelas gerenciadorDeTelas) {

        this.gerenciadorDeTelas = gerenciadorDeTelas;
    }

    @FXML
    private void initialize() {

        atualizarBotoesFases();

        configurarSomDeHover(
            btnVoltar,
            btnNivel1,
            btnNivel2,
            btnNivel3
        );

        configurarClick(
            btnVoltar,
            btnNivel1,
            btnNivel2,
            btnNivel3
        );
    }

    // Toca o mesmo som usado pelos botões do menu quando o mouse passa sobre o botão.
    private void configurarSomDeHover(Button... botoes) {

        for (Button botao : botoes) {

            botao.setOnMouseEntered(event -> AudioManager.tocarSomHoverBotao());
        }
    }

    private void configurarClick(Button... botoes) {

        for (Button botao : botoes) {

            botao.setOnMousePressed(event -> AudioManager.tocarSomBotao());
        }
    }

    @FXML
    private void selecionarFase1() {

        iniciarFase(1); // fase 1
    }

    @FXML
    private void selecionarFase2() {

        iniciarFase(2); // fase 2
    }

    @FXML
    private void selecionarFase3() {

        iniciarFase(3); // fase 3
    }

    private void iniciarFase(int numero) {

        // Impede iniciar fases bloqueadas
        if (numero > getFaseMaximaDesbloqueada()) {
            return;
        }

        faseSelecionada = numero;
        gerenciadorDeTelas.mudarTela("SelecaoProfessor.fxml");
    }

    // Voltar para o menu principal.
    @FXML
    private void voltar() {

        gerenciadorDeTelas.mudarTela("Menu.fxml");
    }
}