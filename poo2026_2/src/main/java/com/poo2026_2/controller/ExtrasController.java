package com.poo2026_2.controller;

import com.poo2026_2.audio.AudioManager;
import com.poo2026_2.model.ConfiguracoesJogo;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
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
    private Slider sliderVozes;
    @FXML
    private CheckBox checkMudo;

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

        sliderVozes.setValue(
                ConfiguracoesJogo.getVolumeVozes() * 100
        );

        checkMudo.setSelected(
                ConfiguracoesJogo.isMudo()
        );
        comboResolucao.sceneProperty().addListener((obs, antiga, nova) -> {
            if (antiga != null) {
                antiga.removeEventFilter(KeyEvent.KEY_PRESSED, aoApertarTecla);
            }
            if (nova != null) {
                nova.addEventFilter(KeyEvent.KEY_PRESSED, aoApertarTecla);
            }
        });
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
            "3840x2160",
            "Tela cheia"
        };

        for (String resolucao : resolucoes) {

            if (resolucao.equals("Tela cheia")) {
                comboResolucao.getItems().add(resolucao);
                continue;
            }

            String[] partes = resolucao.split("x");

            double largura = Double.parseDouble(partes[0]);
            double altura = Double.parseDouble(partes[1]);

            if (largura <= larguraMonitor && altura <= alturaMonitor) {

                comboResolucao.getItems().add(resolucao);
            }
        }

        String salva = ConfiguracoesJogo.getResolucao();

        if (comboResolucao.getItems().contains(salva)) {

            comboResolucao.setValue(salva);

        } else if (!comboResolucao.getItems().isEmpty()) {

            comboResolucao.setValue(
                    comboResolucao.getItems()
                            .get(comboResolucao.getItems().size() - 1)
            );
        }
    }

    @FXML
    private void aplicar(ActionEvent event) {

        String escolha = comboResolucao.getValue();

        ConfiguracoesJogo.setVolumeMusica(sliderMusica.getValue() / 100.0);

        ConfiguracoesJogo.setVolumeEfeitos(sliderEfeitos.getValue() / 100.0);

        ConfiguracoesJogo.setVolumeVozes(sliderVozes.getValue() / 100.0);

        ConfiguracoesJogo.setMudo(checkMudo.isSelected());

        AudioManager.aplicarVolumeMusica();
        AudioManager.aplicarVolumeVozes();

        Stage stage = gerenciadorDeTelas.getStage();
        if (escolha.equals("Tela cheia")) {

            ConfiguracoesJogo.setTelaCheia(true);

            stage.setFullScreen(true);

        } else {

            ConfiguracoesJogo.setTelaCheia(false);

            String[] partes = escolha.split("x");

            double largura = Double.parseDouble(partes[0]);
            double altura = Double.parseDouble(partes[1]);

            ConfiguracoesJogo.setResolucao(escolha);

            stage.setFullScreen(false);

            stage.setWidth(largura);
            stage.setHeight(altura);

            stage.centerOnScreen();
        }
    }

    private final EventHandler<KeyEvent> aoApertarTecla = e -> {
        // se a lista de resolucoes esta aberta, o ESC so fecha a lista
        if (e.getCode() == KeyCode.ESCAPE && !comboResolucao.isShowing()) {
            e.consume();
            gerenciadorDeTelas.fecharConfiguracoes();
        }
    };

    @FXML
    private void voltar(ActionEvent event) {
        gerenciadorDeTelas.fecharConfiguracoes();
    }
}