package com.poo2026_2.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import com.poo2026_2.audio.AudioManager;

/**
 * FXML Controller class
 *
 * @author thoma
 */
public class CreditosController implements ControladorTela {

    @FXML
    private Button btnVoltar;

    private GerenciadorDeTelas gerenciadorDeTelas;

    @Override
    public void setGerenciadorDeTelas(GerenciadorDeTelas gerenciadorDeTelas) {
        this.gerenciadorDeTelas = gerenciadorDeTelas;
    }

    @FXML
    private void initialize() {
        configurarSomDeHover(btnVoltar);

        btnVoltar.sceneProperty().addListener((obs, cenaAntiga, cenaNova) -> {

            if (cenaNova != null) {

                cenaNova.widthProperty().addListener(
                        (obs2, valorAntigo, valorNovo) -> ajustarBotao()
                );

                cenaNova.heightProperty().addListener(
                        (obs2, valorAntigo, valorNovo) -> ajustarBotao()
                );

                ajustarBotao();
            }
        });
    }

    private void ajustarBotao() {

        if (btnVoltar.getScene() == null) {
            return;
        }

        double larguraTela = btnVoltar.getScene().getWidth();
        double alturaTela = btnVoltar.getScene().getHeight();

        double larguraBotao = larguraTela * 0.20;
        double alturaBotao = alturaTela * 0.12;

        larguraBotao = Math.min(larguraBotao, 384);
        alturaBotao = Math.min(alturaBotao, 112);

        larguraBotao = Math.max(larguraBotao, 160);
        alturaBotao = Math.max(alturaBotao, 50);

        btnVoltar.setPrefWidth(larguraBotao);
        btnVoltar.setPrefHeight(alturaBotao);
    }

    // Toca o mesmo som usado pelos botões do menu quando o mouse passa sobre o botão.
    private void configurarSomDeHover(Button... botoes) {
        for (Button botao : botoes) {
            botao.setOnMouseEntered(
                    event -> AudioManager.tocarSomHoverBotao()
            );
        }
    }

    @FXML
    private void voltar() {
        gerenciadorDeTelas.voltarMenu();
    }
}