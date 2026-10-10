package com.poo2026_2;

import com.poo2026_2.controller.GerenciadorDeTelas;
import com.poo2026_2.model.ConfiguracoesJogo;
import javafx.application.Application;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class Poo2026_2 extends Application {

    private static final double ASPECTO = 16.0 / 9.0;

    @Override
    public void start(Stage stage) {

        stage.setTitle("Professores vs Zumbiversitários");

        // O usuário não redimensiona a janela manualmente.
        // A resolução é escolhida pelas configurações do jogo.
        stage.setResizable(false);

        double[] resolucao = obterResolucaoValida(
                ConfiguracoesJogo.getResolucao()
        );

        stage.setWidth(resolucao[0]);
        stage.setHeight(resolucao[1]);

        GerenciadorDeTelas gerenciadorDeTelas =
                new GerenciadorDeTelas(stage);

        gerenciadorDeTelas.mudarTela("Menu.fxml");

        stage.setFullScreen(ConfiguracoesJogo.isTelaCheia());

        stage.show();

        // Garante que a janela comece centralizada.
        if (!stage.isFullScreen()) {
            stage.centerOnScreen();
        }
    }

    /* Verifica se a resolução salva cabe no monitor.
     * Se não couber, utiliza a maior resolução 16:9 disponível.*/
    public static double[] obterResolucaoValida(String resolucao) {

        double larguraMonitor =
                Screen.getPrimary().getVisualBounds().getWidth();

        double alturaMonitor =
                Screen.getPrimary().getVisualBounds().getHeight();

        double largura;
        double altura;

        try {
            String[] partes = resolucao.split("x");

            largura = Double.parseDouble(partes[0]);
            altura = Double.parseDouble(partes[1]);

        } catch (Exception e) {

            largura = 1280;
            altura = 720;
        }

        /* Verifica se a resolução escolhida cabe no monitor. */
        if (largura <= larguraMonitor &&
            altura <= alturaMonitor) {

            return new double[]{largura, altura};
        }

        /* Se não couber, calcula a maior área 16:9 possível dentro do monitor. */
        double larguraMaxima = larguraMonitor;
        double alturaMaxima = larguraMaxima / ASPECTO;

        if (alturaMaxima > alturaMonitor) {

            alturaMaxima = alturaMonitor;
            larguraMaxima = alturaMaxima * ASPECTO;
        }

        return new double[]{
                Math.floor(larguraMaxima),
                Math.floor(alturaMaxima)
        };
    }

    public static void main(String[] args) {
        launch(args);
    }
}