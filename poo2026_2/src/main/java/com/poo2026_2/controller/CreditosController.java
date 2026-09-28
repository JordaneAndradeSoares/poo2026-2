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