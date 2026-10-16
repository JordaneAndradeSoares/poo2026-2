package com.poo2026_2.controller;

import com.poo2026_2.audio.AudioManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class SelecaoNivelController implements ControladorTela {

    @FXML
    private Button btnNivel1;

    @FXML
    private Button btnNivel2;

    @FXML
    private Button btnNivel3;

    @FXML
    private Button btnVoltar;

    private GerenciadorDeTelas gerenciadorDeTelas;

    @Override
    public void setGerenciadorDeTelas(GerenciadorDeTelas gerenciadorDeTelas) {
        this.gerenciadorDeTelas = gerenciadorDeTelas;
    }

    @FXML
    private void initialize() {
        // Aplica o som ao passar o cursor sobre todos os botões
        configurarSomDeHover(btnNivel1, btnNivel2, btnNivel3, btnVoltar);
    }

    private void configurarSomDeHover(Button... botoes) {
        for (Button botao : botoes) {
            botao.setOnMouseEntered(
                    event -> AudioManager.tocarSomHoverBotao()
            );
        }
    }

    @FXML
    private void selecionarNivel1() {
        iniciarNivel(1);
    }

    @FXML
    private void selecionarNivel2() {
        iniciarNivel(2);
    }

    @FXML
    private void selecionarNivel3() {
        iniciarNivel(3);
    }

    private void iniciarNivel(int nivel) {
        // Redireciona para a tela do jogo passando o nível selecionado
        // (Ajuste o nome do arquivo FXML da tela do jogo conforme configurado no projeto)
        gerenciadorDeTelas.mudarTela("Jogo.fxml");
    }

    @FXML
    private void voltar() {
        gerenciadorDeTelas.mudarTela("Menu.fxml");
    }
}