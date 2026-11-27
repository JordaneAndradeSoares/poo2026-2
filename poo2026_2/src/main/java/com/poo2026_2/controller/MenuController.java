package com.poo2026_2.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.poo2026_2.audio.AudioManager;
import com.poo2026_2.model.Progressao;
import com.poo2026_2.view.SpriteAnimado;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.util.Duration;

/**
 * Tela inicial: fundo fixo (sala), quatro personagens animados, fumaca nas tres
 * canecas e o ciclo de trovoes (fundo noturno + piscadas). Botoes empilhados no
 * centro e area reservada para o titulo (ver Menu.fxml).
 *
 * @author thoma
 */
public class MenuController implements ControladorTela {

    /**
     * Tamanho do palco = tamanho do fundo. Tudo no FXML e em pixels dele.
     */
    private static final double LARGURA_PALCO = 1920.0;
    private static final double ALTURA_PALCO = 1080.0;

    // ------------------------------------------------------------------
    // PERSONAGENS
    // ------------------------------------------------------------------
    private record PersonagemMenu(String pasta, double x, double y, double largura, double altura,
            String padraoAlternativo, int frames) {

        String padrao() {
            String proprio = "/images/menu/personagens/" + pasta + "/" + pasta + "%d.png";
            return SpriteAnimado.existe(proprio, 1) ? proprio : padraoAlternativo;
        }
    }

    private static final String JOGO = "/images/game/personagens/";

    /**
     * Quantos frames de cada personagem do menu. Frames que faltarem sao
     * ignorados.
     */
    private static final int QUANTIDADE_FRAMES = 12;

    private static final List<PersonagemMenu> PERSONAGENS = List.of(
            new PersonagemMenu("professora", 29, 475, 350, 480, JOGO + "professores/thais/thais%d.png", 12),
            new PersonagemMenu("professor", 348, 475, 400, 500, JOGO + "professores/daniel/daniel%d.png", 12),
            new PersonagemMenu("zumbi_cone", 1216, 420, 348, 534, JOGO + "zumbis/calouroProtegido/andar/andar_%02d.png", 1),
            new PersonagemMenu("zumbi_balde", 1560, 420, 350, 523, JOGO + "zumbis/calouroBlindado/andar/andar_%02d.png", 1)
    );

    // ------------------------------------------------------------------
    // FUMACA DAS CANECAS (mesma animacao de antes, em escala de caneca)
    // ------------------------------------------------------------------
    /**
     * Baforadas simultaneas em CADA caneca.
     */
    private static final int QUANTIDADE_PARTICULAS_VAPOR = 6;
    private static final double DURACAO_VAPOR_MS = 3600;

    /**
     * Tamanho de cada baforada (elipse) e do desfoque.
     */
    private static final double VAPOR_RAIO_X = 9;
    private static final double VAPOR_RAIO_Y = 13;
    private static final double VAPOR_BLUR = 9;

    /**
     * Quanto sobe (px do palco) na metade e no fim da vida; desvio lateral
     * maximo total.
     */
    private static final double VAPOR_SUBIDA_MEIO = -52;
    private static final double VAPOR_SUBIDA_FIM = -100;
    private static final double VAPOR_DESVIO_LATERAL = 60;

    /**
     * Opacidade no pico. A parede e clara, entao a fumaca e cinza-azulada e nao
     * branca.
     */
    private static final double VAPOR_OPACIDADE_PICO = 0.5;
    private static final Color VAPOR_COR = Color.web("#9fb1b8");

    // ------------------------------------------------------------------
    // TROVAO / FUNDO NOTURNO (mesmos tempos de antes)
    // ------------------------------------------------------------------
    /**
     * Intervalos (em segundos) entre um trovao e o proximo.
     */
    private static final int PRIMEIRO_TROVAO_MIN_S = 6;
    private static final int PRIMEIRO_TROVAO_MAX_S = 15;
    private static final int PROXIMO_TROVAO_MIN_S = 15;
    private static final int PROXIMO_TROVAO_MAX_S = 25;

    /**
     * Duracao de cada audio de trovao (ms).
     */
    private static final double DURACAO_TROVAO1_MS = 5300;
    private static final double DURACAO_TROVAO2_MS = 5950;

    /**
     * No ultimo 1s do trovao, o fundo pisca 5 vezes (10 trocas de 100ms).
     */
    private static final int TROCAS_FINAIS = 10;
    private static final double MS_POR_TROCA = 100;

    /**
     * Os personagens sao uma camada separada do fundo, entao no modo noite eles
     * escurecem com este efeito (matiz, saturacao, brilho, contraste). Ajuste o
     * brilho se ficar muito escuro/claro perto do fundo noturno.
     */
    private static final ColorAdjust EFEITO_NOITE = new ColorAdjust(0, -0.15, -0.30, 0);

    // ------------------------------------------------------------------
    private final List<SpriteAnimado> sprites = new ArrayList<>();
    private final List<Animation> animacoesVapor = new ArrayList<>();

    private final Random sorteio = new Random();
    private PauseTransition espera;
    private Timeline piscada;
    private boolean proximoEhTrovao1 = true;
    private static final String CAMINHO_TITULO = "/images/menu/titulo.png";

    @FXML
    private StackPane root;

    @FXML
    private Pane palco;

    @FXML
    private ImageView fundoNoite;

    @FXML
    private Pane camadaPersonagens;

    @FXML
    private Group vaporCaneca1;

    @FXML
    private Group vaporCaneca2;

    @FXML
    private Group vaporCaneca3;

    @FXML
    private ImageView imgTitulo;

    @FXML
    private Node tituloProvisorio;

    @FXML
    private Button btnJogar;

    @FXML
    private Button btnConfiguracoes;

    @FXML
    private Button btnCreditos;

    @FXML
    private Button btnSair;

    private GerenciadorDeTelas gerenciadorDeTelas;

    // ------------------------------------------------------------------
    // CHEAT CODE: apertar F10 seguidas vezes no menu desbloqueia tudo
    // ------------------------------------------------------------------
    private static final KeyCode TECLA_CHEAT = KeyCode.F10;
    private static final int APERTOS_PARA_CHEAT = 10;

    private int apertosSeguidos = 0;

    private final javafx.event.EventHandler<KeyEvent> aoApertarTecla = evento -> {
        if (evento.getCode() == KeyCode.ESCAPE) {
            apertosSeguidos = 0;
            evento.consume();
            AudioManager.tocarSomBotao();
            gerenciadorDeTelas.mudarTela("Extras.fxml");
            return;
        }
        if (evento.getCode() != TECLA_CHEAT) {
            apertosSeguidos = 0; // qualquer outra tecla quebra a sequencia
            return;
        }
        apertosSeguidos++;
        if (apertosSeguidos >= APERTOS_PARA_CHEAT) {
            apertosSeguidos = 0;
            boolean ligado = Progressao.alternarDesbloqueioTotal();
            AudioManager.tocarEfeito("/audio/faah.mp3");
            System.out.println(ligado ? "[CHEAT] Tudo desbloqueado." : "[CHEAT] Desligado: progresso reiniciado do zero.");
        }
        evento.consume();
    };

    private void configurarCheatCode() {
        root.sceneProperty().addListener((obs, antiga, nova) -> {
            if (antiga != null) {
                antiga.removeEventFilter(KeyEvent.KEY_PRESSED, aoApertarTecla);
            }
            if (nova != null) {
                apertosSeguidos = 0;
                nova.addEventFilter(KeyEvent.KEY_PRESSED, aoApertarTecla);
            }
        });
        if (root.getScene() != null) { // a cena ja pode existir quando o initialize roda
            root.getScene().addEventFilter(KeyEvent.KEY_PRESSED, aoApertarTecla);
        }
    }

    @Override
    public void setGerenciadorDeTelas(GerenciadorDeTelas gerenciadorDeTelas) {
        this.gerenciadorDeTelas = gerenciadorDeTelas;
    }

    @FXML
    private void initialize() {
        ajustarEscala();
        carregarTitulo();
        criarPersonagens();
        iniciarVaporCafe();
        configurarCicloDeAnimacoes();
        configurarCheatCode();

        AudioManager.tocarMusicaMenu();
        configurarSomDeHover(btnJogar, btnConfiguracoes, btnCreditos, btnSair);
        configurarClick(btnJogar, btnConfiguracoes, btnCreditos, btnSair);
    }

    private void configurarCicloDeAnimacoes() {
        root.sceneProperty().addListener((obs, antiga, nova) -> {
            if (nova != null) {
                sprites.forEach(SpriteAnimado::play);
                animacoesVapor.forEach(Animation::play);
                agendarProximoTrovao(PRIMEIRO_TROVAO_MIN_S, PRIMEIRO_TROVAO_MAX_S);
            } else {
                sprites.forEach(SpriteAnimado::stop);
                animacoesVapor.forEach(Animation::stop);
                pararCicloDeTrovoes();
            }
        });
    }

    private void carregarTitulo() {
        var url = getClass().getResource(CAMINHO_TITULO);

        if (url == null) {
            imgTitulo.setVisible(false);
            return;
        }

        imgTitulo.setImage(new Image(url.toExternalForm()));
        tituloProvisorio.setVisible(false);
    }

    /**
     * Escala o palco 1920x1080 para caber na janela sem esticar.
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

    private void criarPersonagens() {
        for (PersonagemMenu p : PERSONAGENS) {
            // faseAleatoria = true: os quatro nao se mexem exatamente juntos
            SpriteAnimado sprite = new SpriteAnimado(
                    p.padrao(), 1, p.frames(),
                    SpriteAnimado.FPS_PADRAO, SpriteAnimado.SEGURAR_PADRAO,
                    SpriteAnimado.Modo.LOOP, true);

            sprite.setCaixa(p.largura(), p.altura());

            // com preserveRatio a imagem pode ficar menor que a caixa: centraliza
            // na horizontal e apoia os pes na base da caixa
            double x = p.x();
            double y = p.y();
            Image primeiro = sprite.getView().getImage();
            if (primeiro != null) {
                double escala = Math.min(p.largura() / primeiro.getWidth(), p.altura() / primeiro.getHeight());
                x += (p.largura() - primeiro.getWidth() * escala) / 2;
                y += p.altura() - primeiro.getHeight() * escala;
            }
            sprite.getView().setLayoutX(x);
            sprite.getView().setLayoutY(y);
            sprite.getView().setMouseTransparent(true);

            camadaPersonagens.getChildren().add(sprite.getView());
            sprites.add(sprite);
            sprite.play();
        }
    }

    // ------------------------------------------------------------------
    // FUMACA
    // ------------------------------------------------------------------
    /**
     * Cria varias baforadas de vapor (elipses com blur) que sobem, crescem e
     * desaparecem em looping, saindo da boca de cada uma das tres canecas.
     */
    private void iniciarVaporCafe() {
        for (Group caneca : List.of(vaporCaneca1, vaporCaneca2, vaporCaneca3)) {
            for (int i = 0; i < QUANTIDADE_PARTICULAS_VAPOR; i++) {
                double atrasoMs = i * (DURACAO_VAPOR_MS / QUANTIDADE_PARTICULAS_VAPOR);
                criarParticulaVapor(caneca, atrasoMs);
            }
        }
    }

    private void criarParticulaVapor(Group grupoVapor, double atrasoMs) {
        Ellipse baforada = new Ellipse(0, 0, VAPOR_RAIO_X, VAPOR_RAIO_Y);
        baforada.setFill(VAPOR_COR);
        baforada.setOpacity(0);
        baforada.setEffect(new GaussianBlur(VAPOR_BLUR));

        grupoVapor.getChildren().add(baforada);

        // Desvio lateral maior = fumaca mais larga/espalhada pros lados.
        double desvioLateral = (Math.random() - 0.5) * VAPOR_DESVIO_LATERAL;

        Timeline animacao = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(baforada.translateXProperty(), 0),
                        new KeyValue(baforada.translateYProperty(), 0),
                        new KeyValue(baforada.opacityProperty(), 0),
                        new KeyValue(baforada.scaleXProperty(), 0.4),
                        new KeyValue(baforada.scaleYProperty(), 0.4)
                ),
                new KeyFrame(Duration.millis(DURACAO_VAPOR_MS * 0.2),
                        new KeyValue(baforada.opacityProperty(), VAPOR_OPACIDADE_PICO)
                ),
                new KeyFrame(Duration.millis(DURACAO_VAPOR_MS * 0.55),
                        new KeyValue(baforada.translateXProperty(), desvioLateral),
                        new KeyValue(baforada.translateYProperty(), VAPOR_SUBIDA_MEIO),
                        new KeyValue(baforada.scaleXProperty(), 1.0),
                        new KeyValue(baforada.scaleYProperty(), 1.0)
                ),
                new KeyFrame(Duration.millis(DURACAO_VAPOR_MS),
                        new KeyValue(baforada.translateXProperty(), -desvioLateral),
                        new KeyValue(baforada.translateYProperty(), VAPOR_SUBIDA_FIM),
                        new KeyValue(baforada.opacityProperty(), 0),
                        new KeyValue(baforada.scaleXProperty(), 1.7),
                        new KeyValue(baforada.scaleYProperty(), 1.7)
                )
        );

        animacao.setDelay(Duration.millis(atrasoMs));
        animacao.setCycleCount(Animation.INDEFINITE);
        animacao.play();

        animacoesVapor.add(animacao);
    }

    // ------------------------------------------------------------------
    // TROVAO
    // ------------------------------------------------------------------
    private void pararCicloDeTrovoes() {
        if (espera != null) {
            espera.stop();
        }
        if (piscada != null) {
            piscada.stop();
        }
        definirNoite(false);
    }

    /**
     * Liga/desliga o visual noturno: fundo da noite + personagens escurecidos.
     */
    private void definirNoite(boolean noite) {
        fundoNoite.setVisible(noite);
        camadaPersonagens.setEffect(noite ? EFEITO_NOITE : null);
    }

    /**
     * Espera um tempo aleatorio entre minS e maxS segundos e dispara o trovao.
     */
    private void agendarProximoTrovao(int minS, int maxS) {
        // nextInt(origem, limite): limite e exclusivo, por isso o +1
        int segundos = sorteio.nextInt((maxS - minS) + 1) + minS;

        espera = new PauseTransition(Duration.seconds(segundos));
        espera.setOnFinished(e -> executarTrovao());
        espera.play();
    }

    /**
     * Toca o som, troca pro fundo noturno e, no ultimo segundo do audio,
     * alterna entre noite/dia 5 vezes ate voltar ao fundo normal. Depois agenda
     * o proximo (alternando trovao1 e trovao2).
     */
    private void executarTrovao() {
        double duracaoMs;

        if (proximoEhTrovao1) {
            AudioManager.tocarSomTrovao1();
            duracaoMs = DURACAO_TROVAO1_MS;
        } else {
            AudioManager.tocarSomTrovao2();
            duracaoMs = DURACAO_TROVAO2_MS;
        }
        proximoEhTrovao1 = !proximoEhTrovao1;

        double inicioPiscadaMs = duracaoMs - TROCAS_FINAIS * MS_POR_TROCA;

        piscada = new Timeline();
        piscada.getKeyFrames().add(new KeyFrame(Duration.ZERO,
                e -> definirNoite(true)));

        for (int i = 0; i < TROCAS_FINAIS; i++) {
            boolean noite = (i % 2 == 0);   // 0=noite, 1=dia, 2=noite ... 9=dia
            piscada.getKeyFrames().add(new KeyFrame(
                    Duration.millis(inicioPiscadaMs + i * MS_POR_TROCA),
                    e -> definirNoite(noite)));
        }

        // Garante o fundo normal no fim e agenda o proximo trovao.
        piscada.getKeyFrames().add(new KeyFrame(Duration.millis(duracaoMs), e -> {
            definirNoite(false);
            agendarProximoTrovao(PROXIMO_TROVAO_MIN_S, PROXIMO_TROVAO_MAX_S);
        }));

        piscada.play();
    }

    // ------------------------------------------------------------------
    // SONS E BOTOES
    // ------------------------------------------------------------------
    /**
     * Toca um som quando o mouse entra em cada um dos botoes passados.
     */
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
    private void iniciarJogo(ActionEvent event) {
        gerenciadorDeTelas.mudarTela("SelecaoNivel.fxml");
    }

    @FXML
    private void abrirConfiguracoes(ActionEvent event) {
        gerenciadorDeTelas.mudarTela("Extras.fxml");
    }

    @FXML
    private void abrirCreditos(ActionEvent event) {
        gerenciadorDeTelas.mudarTela("WordAntigo.fxml");
    }

    @FXML
    private void sair(ActionEvent event) {
        Platform.exit();
    }
}