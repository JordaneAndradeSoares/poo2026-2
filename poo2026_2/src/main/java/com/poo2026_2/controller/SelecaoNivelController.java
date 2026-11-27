package com.poo2026_2.controller;

import com.poo2026_2.audio.AudioManager;
import com.poo2026_2.model.CatalogoNiveis;
import com.poo2026_2.model.Progressao;
import com.poo2026_2.model.Progressao.InfoProfessor;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.util.Duration;

/**
 * Tela de selecao de fase. Cada no do caminho e um professor: vencer a fase N
 * libera o professor do no N (o Daniel ja comeca liberado). O conteudo de cada
 * fase (zumbis) vem de /niveis/niveis.json e as regras de desbloqueio estao em
 * Progressao.
 */
public class SelecaoNivelController implements ControladorTela {

    /**
     * Guarda a fase escolhida antes de abrir o jogo.
     */
    public static int faseSelecionada = 1;

    /**
     * Para testes: true libera todas as fases, sem precisar vencer as
     * anteriores.
     */
    private static final boolean DESBLOQUEAR_TODOS = false;

    private static final double LARGURA_PALCO = 1920.0;
    private static final double ALTURA_PALCO = 1080.0;
    private static final double RAIO_NO = 150.0;
    private static final String PASTA_PERFIS = "/images/game/personagens/profiles/";
    private static final String PASTA_ARMAS = "/images/weapons/";

    @FXML
    private StackPane root;
    @FXML
    private Pane palco;
    @FXML
    private Button btnVoltar;
    @FXML
    private VBox painelInfo;
    @FXML
    private Label infoTitulo;
    @FXML
    private Label infoDetalhe;
    @FXML
    private Label infoEstado;
    @FXML
    private ImageView infoArma;

    private GerenciadorDeTelas gerenciadorDeTelas;

    @Override
    public void setGerenciadorDeTelas(GerenciadorDeTelas gerenciadorDeTelas) {
        this.gerenciadorDeTelas = gerenciadorDeTelas;
    }

    @FXML
    private void initialize() {
        Font.loadFont(getClass().getResourceAsStream("/fonts/Poppins-Bold.ttf"), 12);
        Font.loadFont(getClass().getResourceAsStream("/fonts/Poppins-Regular.ttf"), 12);

        ajustarEscala();
        configurarNos();

        btnVoltar.setOnMouseEntered(e -> AudioManager.tocarSomHoverBotao());
        root.sceneProperty().addListener((obs, antiga, nova) -> {
            if (antiga != null) {
                antiga.removeEventFilter(KeyEvent.KEY_PRESSED, aoApertarTecla);
            }
            if (nova != null) {
                nova.addEventFilter(KeyEvent.KEY_PRESSED, aoApertarTecla);
            }
        });
    }
    private final EventHandler<KeyEvent> aoApertarTecla = e -> {
        if (e.getCode() == KeyCode.ESCAPE) {
            e.consume();
            voltar();
        }
    };

    /**
     * Escala o palco 1920x1080 para caber na janela sem esticar (igual ao
     * Menu).
     */
    private void ajustarEscala() {
        Runnable atualizar = () -> {
            double s = Math.min(root.getWidth() / LARGURA_PALCO, root.getHeight() / ALTURA_PALCO);
            if (s > 0) {
                palco.setScaleX(s);
                palco.setScaleY(s);
            }
        };

        root.widthProperty().addListener((o, a, b) -> atualizar.run());
        root.heightProperty().addListener((o, a, b) -> atualizar.run());
        atualizar.run();
    }

    // ===================== ESTADO DOS NOS =====================
    private void configurarNos() {
        int totalNos = Progressao.DESBLOQUEAVEIS.size(); // 7 professores = 7 nos
        int totalFases = Math.min(totalNos, CatalogoNiveis.quantidade());

        for (int n = 1; n <= totalNos; n++) {
            Node no = palco.lookup("#nivel" + n);
            Node linha = palco.lookup("#linha" + n); // liga o no n ao n+1

            if (n > totalFases) { // JSON com menos fases que professores
                no.setVisible(false);
                no.setManaged(false);
                if (linha != null) {
                    linha.setVisible(false);
                }
                continue;
            }

            boolean concluida = n <= Progressao.fasesConcluidas(); // professor ja desbloqueado
            boolean liberada = faseLiberada(n);

            no.getStyleClass().add(concluida ? "concluido" : liberada ? "atual" : "bloqueado");
            no.setCursor(liberada ? Cursor.HAND : Cursor.DEFAULT); // "pointer" nas fases jogaveis

            // a linha que chega neste no fica clara enquanto ele estiver bloqueado
            Node linhaAnterior = palco.lookup("#linha" + (n - 1));
            if (!liberada && linhaAnterior != null) {
                linhaAnterior.getStyleClass().add("linha-bloqueada");
            }

            ImageView foto = (ImageView) no.lookup(".nivel-foto");
            foto.setImage(carregarFoto(Progressao.DESBLOQUEAVEIS.get(n - 1)));

            if (!concluida) { // professor ainda nao desbloqueado: preto e branco
                ColorAdjust cinza = new ColorAdjust();
                cinza.setSaturation(-1);
                cinza.setBrightness(-0.15);
                foto.setEffect(cinza);
            }

            mostrar(no.lookup(".nivel-cadeado"), !liberada);
        }
    }

    private static boolean faseLiberada(int numero) {
        return DESBLOQUEAR_TODOS || Progressao.faseLiberada(numero);
    }

    private static void mostrar(Node node, boolean visivel) {
        node.setVisible(visivel);
        node.setManaged(visivel);
    }

    /**
     * Logo da linguagem do professor em images/weapons/<nome>.png.
     */
    private Image carregarArma(InfoProfessor professor) {
        var url = getClass().getResource(PASTA_ARMAS + professor.nome().toLowerCase() + ".png");
        return url == null ? null : new Image(url.toExternalForm(), 112, 112, true, true);
    }

    /**
     * Foto do professor em
     * images/game/personagens/profiles/<nome>-profile.(png|jpg).
     */
    private Image carregarFoto(InfoProfessor professor) {
        String base = PASTA_PERFIS + professor.nome().toLowerCase() + "-profile.";

        for (String extensao : new String[]{"png", "jpg"}) {
            var url = getClass().getResource(base + extensao);
            if (url != null) {
                // 600x600 e mais que suficiente e evita guardar PNGs de 1800px na memoria
                return new Image(url.toExternalForm(), 600, 600, true, true);
            }
        }
        return null;
    }

    // ===================== EVENTOS =====================
    @FXML
    private void aoEntrarNivel(MouseEvent evento) {
        Node no = (Node) evento.getSource();
        int n = numeroDe(no);
        InfoProfessor professor = Progressao.DESBLOQUEAVEIS.get(n - 1);

        if (faseLiberada(n)) {
            AudioManager.tocarSomHoverBotao();
        }

        infoTitulo.setText(professor.nome());
        infoDetalhe.setText(professor.poder());
        infoArma.setImage(carregarArma(professor));

        if (n <= Progressao.fasesConcluidas()) {
            infoEstado.setText("Desbloqueado");
        } else if (faseLiberada(n)) {
            infoEstado.setText("Vença a fase " + n + " para desbloquear");
        } else {
            infoEstado.setText("Bloqueado: vença a fase " + (n - 1) + " primeiro");
        }

        posicionarPopupOposto(no);
        painelInfo.setVisible(true);
    }

    @FXML
    private void aoSairNivel(MouseEvent evento) {
        painelInfo.setVisible(false);
    }

    @FXML
    private void aoClicarNivel(MouseEvent evento) {
        Node no = (Node) evento.getSource();
        int n = numeroDe(no);

        if (!faseLiberada(n)) {
            tremer(no);
            return;
        }

        faseSelecionada = n;

        // Vai direto para o jogo; o Jogo le a fase escolhida (faseSelecionada).
        gerenciadorDeTelas.mudarTela("Jogo.fxml");
    }

    private static int numeroDe(Node no) {
        return Integer.parseInt(String.valueOf(no.getUserData()));
    }

    private static final double LARGURA_POPUP = 520.0;
    private static final double ALTURA_POPUP = 190.0; // estimativa (titulo + 2 linhas + estado)
    private static final double FOLGA_POPUP = 16.0; // distancia entre o no e o pop-up

    /**
     * Coloca o pop-up COLADO no no, preferindo o lado oposto (no de cima ->
     * pop-up embaixo; no de baixo -> em cima), depois os lados. Escolhe o
     * primeiro lugar que nao cobre nenhum outro no.
     */
    private void posicionarPopupOposto(Node no) {
        double cx = no.getLayoutX() + RAIO_NO;
        double cy = no.getLayoutY() + RAIO_NO;
        boolean noEmCima = cy < ALTURA_PALCO / 2;

        double xCentrado = cx - LARGURA_POPUP / 2;
        double yLado = cy - ALTURA_POPUP / 2;
        double yAbaixo = cy + RAIO_NO + FOLGA_POPUP;
        double yAcima = cy - RAIO_NO - FOLGA_POPUP - ALTURA_POPUP;
        double xDireita = cx + RAIO_NO + FOLGA_POPUP;
        double xEsquerda = cx - RAIO_NO - FOLGA_POPUP - LARGURA_POPUP;

        // ultimos candidatos: deslocados para o lado, para quando o centro esta ocupado
        double xDesloc = cx + 60;
        double xDeslocE = cx - 60 - LARGURA_POPUP;

        double[][] candidatos = noEmCima
                ? new double[][]{{xCentrado, yAbaixo}, {xDireita, yLado}, {xEsquerda, yLado},
                {xCentrado, yAcima},
                {xDesloc, yAcima}, {xDeslocE, yAcima}, {xDesloc, yAbaixo}, {xDeslocE, yAbaixo}}
                : new double[][]{{xCentrado, yAcima}, {xDireita, yLado}, {xEsquerda, yLado},
                {xCentrado, yAbaixo},
                {xDesloc, yAbaixo}, {xDeslocE, yAbaixo}, {xDesloc, yAcima}, {xDeslocE, yAcima}};

        double[] escolhido = null;
        for (double[] c : candidatos) {
            double x = Math.max(16, Math.min(c[0], LARGURA_PALCO - LARGURA_POPUP - 16));
            double y = Math.max(16, Math.min(c[1], ALTURA_PALCO - ALTURA_POPUP - 16));
            if (!cobreOutroNo(x, y)) {
                escolhido = new double[]{x, y};
                break;
            }
        }
        if (escolhido == null) { // nenhum lugar livre: usa o primeiro (lado oposto)
            escolhido = new double[]{
                Math.max(16, Math.min(candidatos[0][0], LARGURA_PALCO - LARGURA_POPUP - 16)),
                Math.max(16, Math.min(candidatos[0][1], ALTURA_PALCO - ALTURA_POPUP - 16))};
        }

        painelInfo.setLayoutX(escolhido[0]);
        painelInfo.setLayoutY(escolhido[1]);
    }

    /**
     * true se o retangulo do pop-up invade o circulo de algum no visivel.
     */
    private boolean cobreOutroNo(double x, double y) {
        for (int n = 1; n <= Progressao.DESBLOQUEAVEIS.size(); n++) {
            Node outro = palco.lookup("#nivel" + n);
            if (outro == null || !outro.isVisible()) {
                continue;
            }
            double ox = outro.getLayoutX() + RAIO_NO;
            double oy = outro.getLayoutY() + RAIO_NO;

            // ponto do retangulo mais proximo do centro do circulo
            double px = Math.max(x, Math.min(ox, x + LARGURA_POPUP));
            double py = Math.max(y, Math.min(oy, y + ALTURA_POPUP));

            if (Math.hypot(ox - px, oy - py) < RAIO_NO) {
                return true;
            }
        }
        return false;
    }

    /**
     * Balancinho curto para avisar que a fase esta bloqueada.
     */
    private void tremer(Node no) {
        Timeline tremor = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(no.translateXProperty(), 0)),
                new KeyFrame(Duration.millis(60), new KeyValue(no.translateXProperty(), -10)),
                new KeyFrame(Duration.millis(120), new KeyValue(no.translateXProperty(), 10)),
                new KeyFrame(Duration.millis(180), new KeyValue(no.translateXProperty(), -6)),
                new KeyFrame(Duration.millis(240), new KeyValue(no.translateXProperty(), 0)));
        tremor.play();
    }

    /**
     * Voltar para o menu principal.
     */
    @FXML
    private void voltar() {
        gerenciadorDeTelas.mudarTela("Menu.fxml");
    }
}
