package com.poo2026_2.controller;

import com.poo2026_2.audio.AudioManager;
import com.poo2026_2.model.ConfiguracoesJogo;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.stage.Stage;
import javafx.stage.Screen;

public class ExtrasController implements ControladorTela {

    @FXML
    private ComboBox<String> comboResolucao;
    @FXML
    private Slider sliderMusica;
    @FXML
    private Slider sliderEfeitos;
    @FXML
    private CheckBox checkMudo;
    @FXML
    private CheckBox checkTelaCheia;

    private GerenciadorDeTelas gerenciadorDeTelas;

    @Override
    public void setGerenciadorDeTelas(GerenciadorDeTelas gerenciadorDeTelas) {
        this.gerenciadorDeTelas = gerenciadorDeTelas;
    }

    @FXML
    private void initialize() {

        carregarResolucionesDisponiveis();

        sliderMusica.setValue(
                ConfiguracoesJogo.getVolumeMusica() * 100
        );

        sliderEfeitos.setValue(
                ConfiguracoesJogo.getVolumeEfeitos() * 100
        );

        checkMudo.setSelected(
                ConfiguracoesJogo.isMudo()
        );

        checkTelaCheia.setSelected(
                ConfiguracoesJogo.isTelaCheia()
        );
    }

    private void carregarResolucionesDisponiveis() {

        double larguraMonitor
                = Screen.getPrimary()
                        .getVisualBounds()
                        .getWidth();

        double alturaMonitor
                = Screen.getPrimary()
                        .getVisualBounds()
                        .getHeight();

        String[] resolucoes = {
            "960x540",
            "1280x720",
            "1600x900",
            "1920x1080",
            "2560x1440",
            "3840x2160"
        };

        for (String resolucao : resolucoes) {

            String[] partes = resolucao.split("x");

            double largura
                    = Double.parseDouble(partes[0]);

            double altura
                    = Double.parseDouble(partes[1]);

            if (largura <= larguraMonitor
                    && altura <= alturaMonitor) {

                comboResolucao.getItems().add(resolucao);
            }
        }

        String salva = ConfiguracoesJogo.getResolucao();

        if (comboResolucao.getItems().contains(salva)) {

            comboResolucao.setValue(salva);

        } else if (!comboResolucao.getItems().isEmpty()) {

            // Se a resolução salva não cabe mais no monitor,
            // pega a maior disponível.
            comboResolucao.setValue(
                    comboResolucao.getItems()
                            .get(comboResolucao.getItems().size() - 1)
            );
        }
    }

    @FXML
    private void aplicar(ActionEvent event) {
        ConfiguracoesJogo.setResolucao(comboResolucao.getValue());
        ConfiguracoesJogo.setVolumeMusica(sliderMusica.getValue() / 100.0);
        ConfiguracoesJogo.setVolumeEfeitos(sliderEfeitos.getValue() / 100.0);
        ConfiguracoesJogo.setMudo(checkMudo.isSelected());
        ConfiguracoesJogo.setTelaCheia(checkTelaCheia.isSelected());

        // Caso alguma musica esteja tocando neste momento, atualiza o
        // volume dela na hora (sem precisar trocar de tela).
        AudioManager.aplicarVolumeMusica();

        Stage stage = gerenciadorDeTelas.getStage();
        String[] partes = comboResolucao.getValue().split("x");
        double largura = Double.parseDouble(partes[0]);
        double altura = Double.parseDouble(partes[1]);

        stage.setFullScreen(checkTelaCheia.isSelected());
        if (!checkTelaCheia.isSelected()) {
            stage.setWidth(largura);
            stage.setHeight(altura);
            stage.centerOnScreen();
        }
    }

    @FXML
    private void voltar(ActionEvent event) {
        gerenciadorDeTelas.voltarMenu();
    }
}
