package com.poo2026_2.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import com.poo2026_2.audio.AudioManager;
import com.poo2026_2.model.Entidade;
import com.poo2026_2.model.ConfiguracoesJogo;
import com.poo2026_2.model.Cronograma;
import com.poo2026_2.model.Fase;
import com.poo2026_2.model.GerenciadorDeJogo;
import com.poo2026_2.model.Professor;
import com.poo2026_2.model.SorteadorDeFileira;
import com.poo2026_2.model.Progressao;
import com.poo2026_2.model.Tabuleiro;
import com.poo2026_2.model.TipoPersonagem;
import com.poo2026_2.model.TipoZumbi;
import com.poo2026_2.model.Tiro;
import com.poo2026_2.model.utilizaveis.Cafeteira;
import com.poo2026_2.model.utilizaveis.Fabricio;
import com.poo2026_2.model.utilizaveis.Glaucia;
import com.poo2026_2.model.utilizaveis.Lucas;
import com.poo2026_2.model.utilizaveis.Nacif;
import com.poo2026_2.model.zumbis.Calouro;
import com.poo2026_2.model.zumbis.CalouroBlindado;
import com.poo2026_2.model.zumbis.CalouroProtegido;
import com.poo2026_2.model.zumbis.Doutorando;
import com.poo2026_2.model.zumbis.Formando;
import com.poo2026_2.model.zumbis.Trote;
import com.poo2026_2.model.zumbis.VibeCoder;
import com.poo2026_2.model.zumbis.Viciado;
import com.poo2026_2.model.zumbis.Zumbi;
import com.poo2026_2.view.CartaoPersonagem;
import com.poo2026_2.view.LaserFabricio;
import com.poo2026_2.view.LinhaDoTempo;
import com.poo2026_2.view.SpriteAnimado;
import com.poo2026_2.view.SpriteCafeteira;
import com.poo2026_2.view.SpriteLucas;
import com.poo2026_2.view.SpriteNacif;
import com.poo2026_2.view.SpriteProfessor;
import javafx.scene.media.MediaPlayer;
import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.ColorInput;
import javafx.scene.effect.Effect;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.paint.LinearGradient;
import javafx.scene.transform.Scale;
import javafx.scene.shape.Circle;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.QuadCurveTo;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import com.poo2026_2.model.utilizaveis.Marcus;
import com.poo2026_2.view.SpriteMarcus;
import com.poo2026_2.model.utilizaveis.Marcus;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

/**
 * Tela do jogo. Carrega a fase escolhida na seleção de níveis (café inicial e
 * professores liberados) e deixa o jogador colocar professores e cafeteiras no
 * tabuleiro: clica no card, depois clica na casa. Botão direito cancela.
 *
 * O palco tem tamanho fixo (1920x1080, o mesmo do fundo-game.png) e é escalado
 * para a janela, então a grade sempre fica alinhada com o fundo.
 */
public class JogoController implements ControladorTela, Initializable {

    // ---- medidas em pixels do fundo-game.png (1920 x 1080) ----
    private static final double LARGURA_PALCO = 1920;
    private static final double ALTURA_PALCO = 1080;

    private static final int LINHAS = 5;
    private static final int COLUNAS = 10;
    private static final double GRADE_X = 203; // onde começa a primeira casa
    private static final double GRADE_Y = 190;
    private static final double CEL_LARG = 160;
    private static final double CEL_ALT = 178;

    @FXML
    private Button btnConfig;
    @FXML
    private Button btnPausar;
    @FXML
    private SVGPath iconePausar;

    private static final String ICONE_PAUSAR = "M6 19h4V5H6v14zm8-14v14h4V5h-4z";
    private static final String ICONE_RETOMAR = "M8 5v14l11-7z";

    private StackPane camadaConfirmacao;
    private boolean confirmacaoAberta;
    private boolean estavaPausadoAntes;

    // ---- introducao da fase ----
    private StackPane camadaIntro;
    private boolean introAtiva;
    private Animation animacaoIntro;
    /**
     * True so quando o jogador clica em "Tentar de novo": a intro mostra apenas a
     * contagem.
     */
    private static boolean repetirSoContagem;

    /**
     * Os tamanhos do TipoPersonagem foram feitos para casas menores; isto
     * ajusta.
     */
    private static final double ESCALA_PERSONAGEM = 1.25;

    private static final String IMG_SAIDA = "/images/game/itens/saida.png";
    private static final String IMG_CARRINHO = "/images/game/itens/carrinho.png";
    private static final String IMG_GOTA = "/images/game/itens/cafe-gota.png";

    // ---- gota que cai do ceu ----
    private static final double INTERVALO_GOTA_CEU = 8.0; // segundos (tempo de jogo)
    private static final int CAFE_GOTA_CEU = 25;
    private static final int CAFE_GOTA_CAFETEIRA = 50;
    private static final double GOTA_CEU_LARGURA = 62;
    private static final double GOTA_CEU_ALTURA_MAX = 88; // folga p/ a gota nao passar da grade
    private static final double GOTA_CEU_QUEDA = 6.0; // segundos caindo
    private static final double GOTA_CEU_DURACAO = 10.0; // segundos ate sumir
    private static final String CHAVE_GOTA = "gota";
    private static final double GOTA_VOO_DURACAO = 2.0;

    /**
     * Opacidade do boneco que segue o mouse (menor = mais invisível).
     */
    private static final double OPACIDADE_FANTASMA = 0.35;

    private ImageView fantasma;
    private double mouseXPalco = -1000;
    private double mouseYPalco = -1000;

    private double tempoGotaCeu;
    private Pane camadaGotas;

    // ---- explosao do Nacif ----
    private static final String IMG_BOOM = "/images/game/itens/boom.png";
    /**
     * Largura do boom: um pouco mais que as 3 casas da explosao (3 x CEL_LARG).
     */
    private static final double BOOM_LARGURA = CEL_LARG * 3.4;
    private static final double BOOM_DURACAO = 0.8; // segundos
    /**
     * Zumbi atingido: clareia (flash) e escurece ate virar carvao...
     */
    private static final double CHAMUSCAR_DURACAO = 0.25;
    /**
     * ...fica parado como carvao ate este instante...
     */
    private static final double CARVAO_ATE = 0.9;
    /**
     * ...e some (desvanece e sobe um pouco) ate este.
     */
    private static final double SUMIR_ATE = 1.6;
    // marcus
    private static final String IMG_MARCUS = "/images/game/personagens/professores/marcus/marcus9.png";
    /**
     * Menor = o zumbi para mais longe do Marcus (0.5 e o padrao dos outros).
     */
    private static final double MORDIDA_DESLOCAMENTO_MARCUS = 0.2;

    // ---- zumbis, tiros e carrinhos ----
    private static final String PASTA_ZUMBIS = "/images/game/personagens/zumbis/";
    private static final double ZUMBI_CAIXA = 190;
    /**
     * Viciado: sprite mais largo que alto, entao tem caixa propria (+20 px de
     * altura).
     */
    private static final double ZUMBI_CAIXA_VICIADO = 215;
    /** Y do viciado dentro da linha: pes 12 px mais abaixo que antes. */
    private static final double VICIADO_AJUSTE_Y = -21.5;
    /**
     * Onde o zumbi nasce (em casas, contando da esquerda da grade): logo depois
     * da ultima coluna.
     */
    private static final double X_INICIAL_ZUMBI = COLUNAS + 0.5;
    private static final double TIRO_LARGURA = 48;
    /**
     * Altura de saida do tiro: fracao da altura do professor (0 = cabeca, 1 =
     * pes).
     */
    private static final double ALTURA_DO_TIRO = 0.30;
    private static final double AJUSTE_Y_TIRO_THAIS = 20;
    private static final double AJUSTE_Y_TIRO_PHILIPE = 9;
    private static final double ALTURA_DO_TIRO_GLAUCIA = 0.50;
    private static final double X_DO_TIRO_GLAUCIA = 0.80; // fração da casa (0 = esquerda, 1 = direita)
    /**
     * Quantos ticks (de 24 por segundo) cada frame dos PASSOS fica na tela.
     * Padrao = 2 (ciclo de 1 s). Maior = passos mais lentos: 3 = ciclo de 1,5
     * s.
     */
    private static final int SEGURAR_PASSOS = 3;
    private static final double CARRINHO_X = 94;
    private static final double CARRINHO_VELOCIDADE = 1100; // px por segundo
    private static final double DURACAO_DO_PULO = 1.0; // segundos (12 frames a 12 fps)
    private static final double DISTANCIA_DO_PULO = 1.6; // casas
    private static final double MORDIDA_DESLOCAMENTO = 0.5;
    private static final double X_DERROTA = -1.3; // zumbi passou da porta
    private static final double DT_MAXIMO = 0.05;

    // ---- laser do Fabricio ----
    private static final String SOM_LASER = "/audio/tiros/laser.mp3";
    private static final double FRAME_LADO_PX = 288; // cada frame do sprite e 288 x 288
    private static final double[] DEDO_DE_CIMA = { 249, 93 }; // ponta do indicador de cima (frame 6)
    private static final double[] VISAO_CENTRAL = { 175, 90 };
    private static final double[] DEDO_DE_BAIXO = { 223, 169 }; // ponta do indicador de baixo (frame 6)
    private static final double ALTURA_DO_PEITO_ZUMBI = -6; // onde o feixe acerta, relativo ao centro da casa

    private boolean pausado;
    private final List<Animation> pausadasPeloEsc = new ArrayList<>();
    private static final String IMG_SAIDA_ABERTA = "/images/game/itens/saida-aberta.png";
    private static final String SOM_PORTA = "/audio/porta-aberta.mp3";
    private static final String SOM_CARRINHO = "/audio/carrinho.mp3";
    /**
     * Zumbi com posicaoX abaixo disto (em casas) abre a porta da linha dele. Menor
     * = so quando quase encosta.
     */

    private static final double ZUMBI_ABRE_PORTA = -1.0;

    private final ImageView[] portas = new ImageView[LINHAS];
    private final boolean[] portaAberta = new boolean[LINHAS];
    private Image imagemPortaFechada;
    private Image imagemPortaAberta;

    // ---- demissao (contrato) ----
    private static final String IMG_CONTRATO = "/images/game/itens/contrato.png";
    private static final String SOM_DEMITIDO = "/audio/voicelines/demitido.mp3";
    private static final double DEMISSAO_X = 152;
    private static final double DEMISSAO_Y = 100;
    private static final double DEMISSAO_LARGURA = 66;

    private ImageView botaoDemissao;
    private boolean demissaoAtiva;

    private static final String SOM_INTRO = "/audio/321go.mp3";

    // =================================================================
    @FXML
    private StackPane raiz;
    @FXML
    private Pane palco;
    @FXML
    private Pane faixaSaidas;
    @FXML
    private GridPane grade;
    @FXML
    private HBox cartoes;
    @FXML
    private Label lblCafe;
    @FXML
    private Button btnVoltar;
    @FXML
    private Pane camadaAcao;
    @FXML
    private Pane camadaLinhaDoTempo;

    private GerenciadorDeTelas gerenciadorDeTelas;
    private GerenciadorDeJogo gerenciador;

    private final StackPane[][] celulas = new StackPane[LINHAS][COLUNAS];
    private final List<CartaoPersonagem> cartoesDaBarra = new ArrayList<>();
    private final List<Animation> animacoes = new ArrayList<>();
    private CartaoPersonagem selecionado;

    // ---- estado da partida ----
    private final Random sorteio = new Random();
    /** Fileira dos zumbis: aleatoria, mas sem juntar quase todos numa so. */
    private final SorteadorDeFileira sorteadorDeFileira =
            new SorteadorDeFileira(LINHAS, 8, 2.0, sorteio);
    private final List<Zumbi> zumbis = new ArrayList<>();
    private final Map<Zumbi, VisualZumbi> visuaisZumbis = new IdentityHashMap<>();
    private final List<TiroEmCampo> tiros = new ArrayList<>();
    private final Map<Entidade, List<Animation>> animacoesDaEntidade = new IdentityHashMap<>();
    private final Map<Professor, SpriteProfessor> spritesDosProfessores = new IdentityHashMap<>();
    private final Map<Fabricio, LaserFabricio> lasers = new IdentityHashMap<>();
    private MediaPlayer somLaser;
    private final ImageView[] carrinhos = new ImageView[LINHAS];
    private final boolean[] carrinhoUsado = new boolean[LINHAS];
    private final boolean[] carrinhoRodando = new boolean[LINHAS];
    private AnimationTimer relogio;
    private LinhaDoTempo linhaDoTempo;

    @FXML
    private StackPane camadaResultado;
    @FXML
    private Label lblRostoResultado;
    @FXML
    private Label lblTituloResultado;
    @FXML
    private Label lblMensagemResultado;
    @FXML
    private Label mensagemLabel;
    @FXML
    private ImageView QRCode;
    @FXML
    private Button btnJogarNovamente;
    @FXML
    private Button btnProximaFase;
    @FXML
    private Button btnVoltarResultado;
    private Timeline digitacao;

    private static final int TOTAL_FASES = 8;

    /**
     * Velocidade do jogo (1, 1.25, 1.5 ou 2). E estatica para valer tambem na
     * proxima fase e no "tentar de novo".
     */
    private static double velocidadeDoJogo = 1.0;
    private long ultimoTick;
    private boolean partidaEncerrada;

    // ---- botoes redondos ----

    @FXML
    private void aoClicarVoltar() {
        if (introAtiva || partidaEncerrada || confirmacaoAberta) {
            return;
        }
        desselecionar();
        estavaPausadoAntes = pausado;
        if (!pausado) {
            pausarPartida();
        }
        confirmacaoAberta = true;
        camadaConfirmacao.setVisible(true);
    }

    private void fecharConfirmacao() {
        camadaConfirmacao.setVisible(false);
        confirmacaoAberta = false;
        if (!estavaPausadoAntes) {
            retomarPartida();
        }
    }

    @FXML
    private void aoClicarConfig() {
        if (introAtiva) {
            return;
        }
        abrirConfiguracoes();
    }

    @FXML
    private void aoClicarPausar() {
        if (introAtiva || partidaEncerrada || confirmacaoAberta) {
            return;
        }
        if (pausado) {
            retomarPartida();
        } else {
            desselecionar();
            pausarPartida();
        }
    }

    /**
     * Abre as configs pausando a partida (se ja estava pausada, continua pausada ao
     * voltar).
     */
    private void abrirConfiguracoes() {
        if (partidaEncerrada || confirmacaoAberta) {
            return;
        }
        desselecionar();
        boolean jaPausado = pausado;
        if (!jaPausado) {
            pausarPartida();
        }
        gerenciadorDeTelas.abrirConfiguracoesSobre(() -> {
            if (!jaPausado) {
                retomarPartida();
            }
        });
    }

    private void atualizarIconePausa() {
        iconePausar.setContent(pausado ? ICONE_RETOMAR : ICONE_PAUSAR);
    }

    /**
     * Tela preta "Tem certeza?" (comeca escondida; invisivel nao recebe clique).
     */
    private void montarConfirmacao() {
        Label pergunta = new Label("Tem certeza?");
        pergunta.setStyle("-fx-text-fill: white; -fx-font-family: 'Poppins'; "
                + "-fx-font-weight: bold; -fx-font-size: 72px;");
        Label aviso = new Label("Seu progresso NÃO será salvo");
        aviso.setStyle("-fx-text-fill: #f4a23a; -fx-font-family: 'Poppins'; "
                + "-fx-font-weight: bold; -fx-font-size: 40px;");

        Button sim = new Button("Sim, voltar");
        Button nao = new Button("Continuar jogando");
        for (Button botao : new Button[] { sim, nao }) {
            botao.getStyleClass().add("botao-voltar");
            botao.setPrefSize(300, 64);
            botao.setMnemonicParsing(false);
            botao.setOnMouseEntered(e -> AudioManager.tocarSomHoverBotao());
        }
        sim.setOnAction(e -> {
            sairDaPartida();
        });
        nao.setOnAction(e -> {
            fecharConfirmacao();
        });

        HBox botoes = new HBox(32, sim, nao);
        botoes.setAlignment(Pos.CENTER);
        javafx.scene.layout.VBox caixa = new javafx.scene.layout.VBox(24, pergunta, aviso, botoes);
        caixa.setAlignment(Pos.CENTER);

        camadaConfirmacao = new StackPane(caixa);
        camadaConfirmacao.setStyle("-fx-background-color: rgba(0, 0, 0, 0.93);");
        camadaConfirmacao.setPrefSize(LARGURA_PALCO, ALTURA_PALCO);
        camadaConfirmacao.setMinSize(LARGURA_PALCO, ALTURA_PALCO);
        camadaConfirmacao.setMaxSize(LARGURA_PALCO, ALTURA_PALCO);
        camadaConfirmacao.setVisible(false);
        palco.getChildren().add(camadaConfirmacao); // por ultimo: cobre tudo
    }

    /**
     * Sprite do zumbi e em qual animacao ele esta (andar, bater, correr, pulo).
     */
    private static final class VisualZumbi {

        private final ImageView view = new ImageView();
        private SpriteAnimado animacao;
        private String estado = "";
        private boolean pulou;
        private double puloRestante;
        private boolean congelado; // paralisado: animacao pausada e cor azulada
        private boolean carbonizado; // morreu na explosao do Nacif: o sprite fica na tela queimando e some
        private boolean pegandoFogo;
    }

    /**
     * Aparencia do zumbi paralisado (matiz, saturacao, brilho, contraste).
     */
    private static final ColorAdjust EFEITO_PARALISIA = new ColorAdjust(-0.5, 0.2, 0.1, 0);
    /**
     * Zumbi pegando fogo: tinge so os pixels do sprite de vermelho (SRC_ATOP
     * respeita a silhueta).
     */
    private static final Effect EFEITO_QUEIMADO = new Blend(BlendMode.SRC_ATOP, null,
            new ColorInput(0, 0, 400, 400, Color.rgb(255, 40, 0, 0.55)));
    private static final Effect EFEITO_GELO = new Blend(BlendMode.SRC_ATOP, null,
            new ColorInput(0, 0, 400, 400, Color.rgb(90, 190, 255, 0.5)));

    private static final double FOGO_SOM_MAXIMO = 1.5; // segundos de fogo.mp3 por tiro
    private static final double JATO_PONTA = 60; // sobra além da cabeça do tiro
    private static final double JATO_MIN = 60; // comprimento inicial

    private static final class TiroEmCampo {

        private final Tiro tiro;
        private final Node view;
        private final double centroYNaCasa;
        private final Animation tremor; // só o fogo tem (senão null)
        private final JatoDeFogo jato; // só o fogo tem (senão null)
        private final double origemX; // x do tiro quando nasceu (em casas)
        private final double alcanceMaximo; // em casas; infinito para quem não limita

        private TiroEmCampo(Tiro tiro, Node view, double centroYNaCasa, Animation tremor,
                JatoDeFogo jato, double alcanceMaximo) {
            this.tiro = tiro;
            this.view = view;
            this.centroYNaCasa = centroYNaCasa;
            this.tremor = tremor;
            this.jato = jato;
            this.origemX = tiro.getX();
            this.alcanceMaximo = alcanceMaximo;
        }
    }

    /**
     * Chama comprida: cauda, corpo e miolo esticam conforme o comprimento.
     */
    private static final class JatoDeFogo {

        private final Group grupo;
        private final Rectangle caixa = new Rectangle(0, 0, JATO_MIN, 40);
        private final Ellipse cauda = new Ellipse(0, 20, 0, 14);
        private final Ellipse corpo = new Ellipse(0, 20, 0, 18);
        private final Ellipse miolo = new Ellipse(0, 20, 0, 9);

        private JatoDeFogo() {
            caixa.setFill(Color.TRANSPARENT);
            cauda.setFill(gradienteDeFogo(Color.rgb(255, 110, 0, 0.75), Color.rgb(200, 30, 0, 0)));
            corpo.setFill(gradienteDeFogo(Color.rgb(255, 150, 20, 1), Color.rgb(220, 40, 0, 0)));
            miolo.setFill(gradienteDeFogo(Color.rgb(255, 250, 190, 1), Color.rgb(255, 190, 40, 0)));
            grupo = new Group(caixa, cauda, corpo, miolo);
            grupo.setMouseTransparent(true);
            definirComprimento(JATO_MIN);
        }

        private void definirComprimento(double l) {
            caixa.setWidth(l);
            cauda.setCenterX(l * 0.5);
            cauda.setRadiusX(l * 0.5);
            corpo.setCenterX(l * 0.62);
            corpo.setRadiusX(l * 0.38);
            miolo.setCenterX(l * 0.75);
            miolo.setRadiusX(l * 0.22);
        }
    }

    /**
     * Pastas e padroes de nome dos frames de cada zumbi (12 frames cada).
     */
    private record SpritesZumbi(String andar, String bater, String correr, String pulo) {

    }

    @Override
    public void setGerenciadorDeTelas(GerenciadorDeTelas gerenciadorDeTelas) {
        this.gerenciadorDeTelas = gerenciadorDeTelas;
    }

    // =================================================================
    // INICIALIZAÇÃO
    // =================================================================
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Fase fase = Fase.criarFaseSelecionada();
        gerenciador = new GerenciadorDeJogo(fase, new Tabuleiro(LINHAS, COLUNAS));

        montarBarra(fase);
        montarSaidas();
        montarGrade();
        montarLinhaDoTempo();
        atualizarCafe();
        ajustarEscala();
        montarCamadaGotas();
        montarDemissao();
        configurarFantasma();
        configurarTeclado();

        for (Button b : new Button[] { btnVoltar, btnConfig, btnPausar }) {
            b.setOnMouseEntered(e -> AudioManager.tocarSomHoverBotao());
        }
        montarConfirmacao();
        iniciarIntroducao();
    }

    /**
     * Cafeteira + os professores liberados na fase + o próximo bloqueado (com
     * cadeado). Os demais ficam escondidos até serem desbloqueados.
     */
    private void montarBarra(Fase fase) {
        adicionarCartao(TipoPersonagem.CAFETEIRA, false);

        List<Progressao.InfoProfessor> ordem = new ArrayList<>();
        ordem.add(Progressao.INICIAL);
        ordem.addAll(Progressao.DESBLOQUEAVEIS);

        boolean mostrouBloqueado = false;
        for (Progressao.InfoProfessor info : ordem) {
            boolean liberado = fase.getProfessorDisponiveis().contains(info.classe());
            if (!liberado) {
                if (mostrouBloqueado) {
                    break;
                }
                mostrouBloqueado = true;
            }
            TipoPersonagem tipo = TipoPersonagem.valueOf(
                    info.classe().getSimpleName().toUpperCase(Locale.ROOT));
            adicionarCartao(tipo, !liberado);
        }
    }

    private void adicionarCartao(TipoPersonagem tipo, boolean bloqueado) {
        CartaoPersonagem cartao = new CartaoPersonagem(tipo, bloqueado);
        cartao.setOnMouseEntered(e -> {
            if (!bloqueado) {
                AudioManager.tocarSomHoverBotao();
            }
        });
        cartao.setOnMouseClicked(e -> aoClicarNoCartao(cartao));
        cartoes.getChildren().add(cartao);
        cartoesDaBarra.add(cartao);
    }

    /**
     * Porta (saída de emergência) + carrinho no começo de cada linha.
     */
    private void montarSaidas() {
        imagemPortaAberta = carregarImagem(IMG_SAIDA_ABERTA, 0, 165).getImage();

        for (int l = 0; l < LINHAS; l++) {
            ImageView porta = carregarImagem(IMG_SAIDA, 0, 165);
            portas[l] = porta;
            porta.setLayoutX(12);
            porta.setLayoutY(l * CEL_ALT + 13);

            ImageView carrinho = carregarImagem(IMG_CARRINHO, 108, 0);
            carrinhos[l] = carrinho;
            carrinho.setLayoutX(CARRINHO_X);
            carrinho.setLayoutY(l * CEL_ALT + 66);

            faixaSaidas.getChildren().addAll(porta, carrinho);
        }
        imagemPortaFechada = portas[0].getImage();
    }

    /**
     * Porta aberta enquanto houver zumbi vivo quase nela; o som toca so na hora que
     * abre.
     */
    private void atualizarPortas() {
        if (imagemPortaAberta == null) {
            return; // saida-aberta.png faltando: carregarImagem ja avisou no console
        }
        boolean[] perto = new boolean[LINHAS];
        for (Zumbi zumbi : zumbis) {
            if (zumbi.estaVivo()
                    && zumbi.getPosicaoX() < ZUMBI_ABRE_PORTA
                    && carrinhoUsado[zumbi.getLinha()]) { // so abre se nao tem mais carrinho
                perto[zumbi.getLinha()] = true;
            }
        }
        for (int l = 0; l < LINHAS; l++) {
            if (perto[l] == portaAberta[l]) {
                continue;
            }
            portaAberta[l] = perto[l];
            portas[l].setImage(perto[l] ? imagemPortaAberta : imagemPortaFechada);
            if (perto[l] && getClass().getResource(SOM_PORTA) != null) {
                AudioManager.tocarEfeito(SOM_PORTA);
            }
        }
    }

    /**
     * Casas transparentes por cima do fundo; o fundo é quem desenha o xadrez.
     */
    private void montarGrade() {
        grade.getChildren().clear();

        for (int l = 0; l < LINHAS; l++) {
            for (int c = 0; c < COLUNAS; c++) {
                StackPane celula = new StackPane();
                celula.setPrefSize(CEL_LARG, CEL_ALT);
                celula.setMinSize(CEL_LARG, CEL_ALT);
                celula.setMaxSize(CEL_LARG, CEL_ALT);
                celula.getStyleClass().add("celula");

                final int linha = l;
                final int coluna = c;
                celula.setOnMouseClicked(e -> {
                    if (e.getButton() == MouseButton.SECONDARY) {
                        desselecionar();
                    } else {
                        aoClicarNaCelula(linha, coluna);
                    }
                });

                celulas[l][c] = celula;
                grade.add(celula, c, l);
            }
        }
    }

    private void ajustarEscala() {
        Runnable atualizar = () -> {
            double s = Math.min(raiz.getWidth() / LARGURA_PALCO, raiz.getHeight() / ALTURA_PALCO);
            if (s > 0) {
                palco.setScaleX(s);
                palco.setScaleY(s);
            }
        };
        raiz.widthProperty().addListener((o, a, b) -> atualizar.run());
        raiz.heightProperty().addListener((o, a, b) -> atualizar.run());
        atualizar.run();
    }

    // =================================================================
    // SELEÇÃO E COLOCAÇÃO
    // =================================================================
    private void aoClicarNoCartao(CartaoPersonagem cartao) {
        if (pausado || introAtiva) {
            return;
        }
        if (!cartao.isUtilizavel()) {
            return;
        }

        boolean eraEste = selecionado == cartao;
        desselecionar();
        if (eraEste) {
            return;
        }

        String som = SOM_DO_CARTAO.get(cartao.getTipo());
        if (som != null && getClass().getResource(som) != null) {
            AudioManager.tocarEfeito(som);
        }

        selecionado = cartao;
        cartao.setSelecionado(true);
        mostrarFantasma(cartao.getTipo());
        marcarCasasComoAlvo(true);
    }

    private void desselecionar() {
        if (selecionado != null) {
            selecionado.setSelecionado(false);
            selecionado = null;
        }
        if (demissaoAtiva) {
            demissaoAtiva = false;
            botaoDemissao.setScaleX(1.0);
            botaoDemissao.setScaleY(1.0);
        }
        if (fantasma != null) {
            fantasma.setVisible(false);
        }
        marcarCasasComoAlvo(false);
    }

    /**
     * Enquanto há um card escolhido, as casas ganham destaque ao passar o
     * mouse.
     */
    private void marcarCasasComoAlvo(boolean alvo) {
        for (StackPane[] linha : celulas) {
            for (StackPane celula : linha) {
                if (alvo) {
                    if (!celula.getStyleClass().contains("celula-alvo")) {
                        celula.getStyleClass().add("celula-alvo");
                    }
                } else {
                    celula.getStyleClass().remove("celula-alvo");
                }
            }
        }
    }

    private void aoClicarNaCelula(int linha, int coluna) {
        if (pausado || introAtiva) {
            return;
        }
        if (demissaoAtiva) {
            Entidade ocupante = gerenciador.getOcupante(linha, coluna);
            if (ocupante != null) {
                demitir(ocupante);
            }
            return; // casa vazia: continua com o contrato na mao
        }
        if (selecionado == null || gerenciador.getOcupante(linha, coluna) != null) {
            return;
        }

        TipoPersonagem tipo = selecionado.getTipo();
        Entidade novo = tipo.criar(linha, coluna);

        boolean instalou = (novo instanceof Professor professor)
                ? gerenciador.adicionarProfessor(professor, linha, coluna)
                : gerenciador.adicionarCafeteira((Cafeteira) novo, linha, coluna);

        if (!instalou) {
            return; // sem café suficiente: nada muda
        }

        StackPane celula = celulas[linha][coluna];
        celula.getChildren().add(criarSprite(tipo, novo, celula));

        selecionado.iniciarRecarga(tipo.getTempoDeRecarga());
        desselecionar();
        atualizarCafe();
    }

    // =================================================================
    // VISUAIS
    // ===========================================s======================
    /**
     * Cria o visual do personagem recem colocado: - Cafeteira: enche do frame 1
     * ao 12 em 24 s e, no frame 12, solta a gota; - Lucas: SpriteLucas (parado
     * no frame 1 ate um zumbi chegar perto); - Nacif: SpriteNacif (toca os 12
     * frames uma vez e explode no fim); - demais professores: parados no frame
     * 1 (so respirando) ate aparecer um zumbi na linha deles.
     */
    private ImageView criarSprite(TipoPersonagem tipo, Entidade entidade, StackPane celula) {

        double largura = tipo.getLarguraCaixa() * ESCALA_PERSONAGEM;
        double altura = tipo.getAlturaCaixa() * ESCALA_PERSONAGEM;
        List<Animation> animacoesDele = new ArrayList<>();
        ImageView visual;

        if (entidade instanceof Cafeteira cafeteira) {
            SpriteCafeteira sprite = new SpriteCafeteira(cafeteira.getIntervaloProducao(),
                    () -> soltarGota(celula, CAFE_GOTA_CAFETEIRA));
            sprite.setCaixa(largura, altura);
            sprite.play();
            animacoesDele.add(sprite.getTimeline());
            visual = sprite.getView();
        } else if (entidade instanceof Lucas lucas) {
            SpriteLucas sprite = new SpriteLucas(lucas);
            sprite.setCaixa(largura, altura);
            sprite.play();
            animacoesDele.add(sprite.getTimeline());
            animacoesDele.add(sprite.getRespiracao());
            visual = sprite.getView();
        } else if (entidade instanceof Nacif nacif) {
            SpriteNacif sprite = new SpriteNacif(nacif,
                    tipo.getPadraoFrames(), tipo.getQuantidadeDeFrames());
            sprite.setCaixa(largura, altura);
            sprite.play();
            animacoesDele.add(sprite.getTimeline());
            visual = sprite.getView();
        } else if (entidade instanceof Marcus) {
            SpriteMarcus sprite = new SpriteMarcus(
                    tipo.getPadraoFrames(), tipo.getQuantidadeDeFrames());
            sprite.setCaixa(largura, altura);
            sprite.play();
            animacoesDele.add(sprite.getTimeline());
            animacoesDele.add(sprite.getRespiracao());
            visual = sprite.getView();
        } else {
            SpriteProfessor sprite = new SpriteProfessor(
                    tipo.getPadraoFrames(), tipo.getQuantidadeDeFrames());
            sprite.setCaixa(largura, altura);
            sprite.play();
            animacoesDele.addAll(sprite.getAnimacoes());
            spritesDosProfessores.put((Professor) entidade, sprite);
            visual = sprite.getView();
        }

        visual.setTranslateY(tipo.getAjusteY() * ESCALA_PERSONAGEM);
        visual.setMouseTransparent(true); // o clique cai na casa
        animacoesDaEntidade.put(entidade, animacoesDele);
        return visual;
    }

    private void voarGotaParaCafe(ImageView gota) {
    if (gota.getImage() == null || gota.getScene() == null || camadaGotas == null) {
        return;
    }
    // posicoes em coordenadas do palco (ja desconta a escala da janela)
    Bounds origem = camadaGotas.sceneToLocal(gota.localToScene(gota.getBoundsInLocal()));
    Bounds destino = camadaGotas.sceneToLocal(lblCafe.localToScene(lblCafe.getBoundsInLocal()));
    if (origem == null || destino == null) {
        return;
    }

    ImageView voo = new ImageView(gota.getImage());
    voo.setPreserveRatio(true);
    voo.setSmooth(true);
    voo.setFitWidth(gota.getFitWidth());
    voo.setMouseTransparent(true);
    voo.setOpacity(OPACIDADE_FANTASMA);
    voo.setLayoutX(origem.getMinX());
    voo.setLayoutY(origem.getMinY());
    voo.setViewOrder(-3000); // por cima de tudo

    double dx = destino.getCenterX() - origem.getCenterX();
    double dy = destino.getCenterY() - origem.getCenterY();

    camadaGotas.getChildren().add(voo);

    Timeline voar = new Timeline(
            new KeyFrame(Duration.ZERO,
                    new KeyValue(voo.translateXProperty(), 0.0),
                    new KeyValue(voo.translateYProperty(), 0.0),
                    new KeyValue(voo.scaleXProperty(), 1.0),
                    new KeyValue(voo.scaleYProperty(), 1.0),
                    new KeyValue(voo.opacityProperty(), OPACIDADE_FANTASMA)),
            new KeyFrame(Duration.seconds(GOTA_VOO_DURACAO * 0.75),
                    new KeyValue(voo.opacityProperty(), OPACIDADE_FANTASMA)),
            new KeyFrame(Duration.seconds(GOTA_VOO_DURACAO),
                    new KeyValue(voo.translateXProperty(), dx, Interpolator.EASE_BOTH),
                    new KeyValue(voo.translateYProperty(), dy, Interpolator.EASE_BOTH),
                    new KeyValue(voo.scaleXProperty(), 0.4, Interpolator.EASE_BOTH),
                    new KeyValue(voo.scaleYProperty(), 0.4, Interpolator.EASE_BOTH),
                    new KeyValue(voo.opacityProperty(), 0.0, Interpolator.EASE_IN)));
    iniciarEfeito(voar, () -> camadaGotas.getChildren().remove(voo));
}

    private void soltarGota(StackPane celula, int quantidade) {
        if (pausado || introAtiva || partidaEncerrada)
            return;
        if (celula.getProperties().containsKey(CHAVE_GOTA)) {
            return; // já tem uma gota esperando
        }

        ImageView gota = carregarImagem(IMG_GOTA, 46, 0);
        gota.setMouseTransparent(false);
        gota.setCursor(Cursor.HAND);
        StackPane.setAlignment(gota, Pos.TOP_RIGHT);
        StackPane.setMargin(gota, new Insets(10, -6, 0, 0));

        TranslateTransition flutuar = new TranslateTransition(Duration.millis(700), gota);
        flutuar.setByY(8);
        flutuar.setAutoReverse(true);
        flutuar.setCycleCount(Animation.INDEFINITE);
        flutuar.play();
        animacoes.add(flutuar);

        gota.setPickOnBounds(false);
        gota.setOnMouseEntered(e -> {
            voarGotaParaCafe(gota);
            flutuar.stop();
            animacoes.remove(flutuar);
            celula.getChildren().remove(gota);
            celula.getProperties().remove(CHAVE_GOTA);
            gerenciador.adicionarCafe(quantidade);
            atualizarCafe();
        });

        celula.getProperties().put(CHAVE_GOTA, gota);
        celula.getChildren().add(gota);
    }

    /**
     * A cada INTERVALO_GOTA_CEU segundos de jogo, cai uma gota em um ponto
     * qualquer da grade.
     */
    /**
     * Camada por cima de tudo no palco; so a propria gota recebe clique.
     */
    private void montarCamadaGotas() {
        camadaGotas = new Pane();
        camadaGotas.setPickOnBounds(false);
        camadaGotas.setPrefSize(LARGURA_PALCO, ALTURA_PALCO);
        camadaGotas.setMinSize(LARGURA_PALCO, ALTURA_PALCO);
        camadaGotas.setMaxSize(LARGURA_PALCO, ALTURA_PALCO);
        palco.getChildren().add(camadaGotas);
    }

    private void limparGotas() {
        // gotas do céu: para as animações e some com a camada
        for (Node n : new ArrayList<>(camadaGotas.getChildren())) {
            n.setMouseTransparent(true);
        }
        camadaGotas.getChildren().clear();
        camadaGotas.setVisible(false);

        // gotas das cafeteiras
        for (StackPane[] linha : celulas) {
            for (StackPane celula : linha) {
                Object gota = celula.getProperties().remove(CHAVE_GOTA);
                if (gota instanceof Node no) {
                    celula.getChildren().remove(no);
                }
            }
        }

        // para as animações soltas (flutuar, cair, etc.)
        animacoes.forEach(Animation::stop);
        animacoes.clear();
    }

    /**
     * A cada INTERVALO_GOTA_CEU segundos de jogo, cai uma gota num ponto
     * qualquer da grade.
     */
    private void gerarGotaDoCeu(double dt) {
        if (pausado || introAtiva || partidaEncerrada)
            return;
        tempoGotaCeu += dt;
        if (tempoGotaCeu < INTERVALO_GOTA_CEU) {
            return;
        }
        tempoGotaCeu -= INTERVALO_GOTA_CEU;

        double x = GRADE_X + sorteio.nextDouble() * (COLUNAS * CEL_LARG - GOTA_CEU_LARGURA);
        double y = GRADE_Y + sorteio.nextDouble() * (LINHAS * CEL_ALT - GOTA_CEU_ALTURA_MAX);

        ImageView gota = carregarImagem(IMG_GOTA, GOTA_CEU_LARGURA, 0);
        gota.setMouseTransparent(false);
        gota.setPickOnBounds(false);
        gota.setCursor(Cursor.HAND);
        gota.setLayoutX(x);
        gota.setLayoutY(y);
        gota.setTranslateY(-(y + GOTA_CEU_ALTURA_MAX)); // comeca logo acima da tela

        TranslateTransition cair = new TranslateTransition(Duration.seconds(GOTA_CEU_QUEDA), gota);
        cair.setToY(0);
        cair.setInterpolator(Interpolator.EASE_IN);

        PauseTransition esperar = new PauseTransition(Duration.seconds(GOTA_CEU_DURACAO));

        FadeTransition sumir = new FadeTransition(Duration.millis(500), gota);
        sumir.setToValue(0);

        SequentialTransition ciclo = new SequentialTransition(cair, esperar, sumir);
        ciclo.setOnFinished(e -> {
            animacoes.remove(ciclo);
            camadaGotas.getChildren().remove(gota);
        });

        gota.setOnMouseEntered(e -> {
            voarGotaParaCafe(gota);
            ciclo.stop();
            animacoes.remove(ciclo);
            camadaGotas.getChildren().remove(gota);
            gerenciador.adicionarCafe(CAFE_GOTA_CEU);
            atualizarCafe();
        });

        camadaGotas.getChildren().add(gota);
        animacoes.add(ciclo);
        ciclo.play();
    }

    private void atualizarCafe() {
        int cafe = gerenciador.getCafe();
        lblCafe.setText(String.valueOf(cafe));
        for (CartaoPersonagem cartao : cartoesDaBarra) {
            cartao.atualizarCafe(cafe);
        }
    }

    private void montarDemissao() {
        botaoDemissao = carregarImagem(IMG_CONTRATO, DEMISSAO_LARGURA, 0);
        if (botaoDemissao.getImage() == null) {
            return; // contrato.png faltando: carregarImagem ja avisou no console
        }
        botaoDemissao.setLayoutX(DEMISSAO_X);
        botaoDemissao.setLayoutY(DEMISSAO_Y);
        botaoDemissao.setMouseTransparent(false);
        botaoDemissao.setPickOnBounds(true);
        botaoDemissao.setCursor(Cursor.HAND);
        botaoDemissao.setOnMouseEntered(e -> AudioManager.tocarSomHoverBotao());
        botaoDemissao.setOnMouseClicked(e -> {
            e.consume();
            aoClicarDemissao();
        });
        palco.getChildren().add(botaoDemissao); // por cima da barra
    }

    private void aoClicarDemissao() {
        if (pausado || introAtiva || partidaEncerrada) {
            return;
        }
        boolean eraAtiva = demissaoAtiva;
        desselecionar(); // tira card escolhido ou a demissao anterior
        if (eraAtiva) {
            return; // clicar de novo no contrato cancela
        }
        demissaoAtiva = true;
        botaoDemissao.setScaleX(1.15);
        botaoDemissao.setScaleY(1.15);
        mostrarFantasmaDemissao();
        marcarCasasComoAlvo(true);
    }

    /** O contrato (semitransparente) acompanha o mouse, como o boneco dos cards. */
    private void mostrarFantasmaDemissao() {
        fantasma.setImage(botaoDemissao.getImage());
        fantasma.setFitWidth(DEMISSAO_LARGURA * 1.4);
        fantasma.setFitHeight(DEMISSAO_LARGURA * 1.4 * 1.15);
        posicionarFantasma();
        fantasma.setVisible(true);
    }

    private void demitir(Entidade ocupante) {
        removerDoTabuleiro(ocupante);
        desselecionar();
        if (getClass().getResource(SOM_DEMITIDO) != null) {
            AudioManager.tocarEfeito(SOM_DEMITIDO);
        }
    }

    /**
     * Carrega uma imagem dos resources; se faltar, avisa e devolve um ImageView
     * vazio.
     */
    private ImageView carregarImagem(String caminho, double largura, double altura) {
        ImageView visual = new ImageView();
        URL url = getClass().getResource(caminho);
        if (url == null) {
            System.err.println("Imagem não encontrada: " + caminho);
            return visual;
        }
        visual.setImage(new Image(url.toExternalForm(), largura * 2, altura * 2, true, true));
        visual.setPreserveRatio(true);
        visual.setSmooth(true);
        if (largura > 0) {
            visual.setFitWidth(largura);
        }
        if (altura > 0) {
            visual.setFitHeight(altura);
        }
        visual.setMouseTransparent(true);
        return visual;
    }

    // =================================================================
    // PARTIDA: ONDAS, ZUMBIS, TIROS, CARRINHOS
    // =================================================================
    private void iniciarPartida() {
        relogio = new AnimationTimer() {
            @Override
            public void handle(long agora) {
                if (ultimoTick == 0) {
                    ultimoTick = agora;
                    return;
                }
                // o limite vale para o tempo real; a velocidade do jogo (1x a 2x) multiplica
                // depois
                double dt = Math.min((agora - ultimoTick) / 1_000_000_000.0, DT_MAXIMO) * velocidadeDoJogo;
                ultimoTick = agora;
                atualizar(dt);
            }
        };
        relogio.start();
    }

    private void atualizar(double dt) {
        if (partidaEncerrada) {
            return;
        }
        gerarZumbis(dt);
        gerarGotaDoCeu(dt);
        atualizarProfessores(dt);
        atualizarLasers(dt);
        atualizarTiros(dt);
        atualizarZumbis(dt);
        atualizarCarrinhos(dt);
        atualizarPortas();
        removerMortos();
        sincronizarVelocidade();
        linhaDoTempo.atualizar();
        verificarFim();
    }

    // ------------------------ linha do tempo e velocidade ------------------------
    /**
     * Linha do tempo no canto inferior direito, com os botoes 1x / 1.25x / 1.5x
     * / 2x.
     */
    private void montarLinhaDoTempo() {
        linhaDoTempo = new LinhaDoTempo(gerenciador.getFase().getCronograma());
        linhaDoTempo.setVelocidade(velocidadeDoJogo);
        linhaDoTempo.setAoMudarVelocidade(velocidade -> {
            velocidadeDoJogo = velocidade;
            sincronizarVelocidade();
        });
        camadaLinhaDoTempo.getChildren().add(linhaDoTempo);
    }

    /**
     * O tempo do modelo (zumbis, tiros, lasers, cronograma) ja anda na
     * velocidade escolhida porque o dt e multiplicado. Aqui as animacoes do
     * JavaFX (sprites, cafeteira, gotas, recarga dos cards) acompanham.
     */
    private void sincronizarVelocidade() {
        animacoes.forEach(this::aplicarVelocidade);
        animacoesDaEntidade.values().forEach(lista -> lista.forEach(this::aplicarVelocidade));
        visuaisZumbis.values().forEach(v -> {
            if (v.animacao != null) {
                aplicarVelocidade(v.animacao.getTimeline());
            }
        });
        cartoesDaBarra.forEach(c -> c.setVelocidade(velocidadeDoJogo));
    }

    private void aplicarVelocidade(Animation animacao) {
        if (animacao.getRate() != velocidadeDoJogo) {
            animacao.setRate(velocidadeDoJogo);
        }
    }

    // ---------------------------- ondas ----------------------------
    /**
     * O cronograma da fase (niveis.json) diz quais zumbis nascem agora; a linha
     * e sorteada.
     */
    private void gerarZumbis(double dt) {
        Cronograma cronograma = gerenciador.getFase().getCronograma();
        for (TipoZumbi tipo : cronograma.avancar(dt)) {
            Zumbi zumbi = tipo.getCriador().apply(sorteadorDeFileira.proxima());
            zumbi.setPosicaoX(X_INICIAL_ZUMBI);
            adicionarZumbi(zumbi);
        }
    }

    private void adicionarZumbi(Zumbi zumbi) {
        VisualZumbi visual = new VisualZumbi();
        visual.view.setMouseTransparent(true);
        double caixa = (zumbi instanceof Viciado) ? ZUMBI_CAIXA_VICIADO : ZUMBI_CAIXA;
        visual.view.setFitWidth(caixa);
        visual.view.setFitHeight(caixa);
        visual.view.setViewOrder(-zumbi.getLinha()); // linhas de baixo ficam na frente

        zumbis.add(zumbi);
        visuaisZumbis.put(zumbi, visual);
        camadaAcao.getChildren().add(visual.view);
        trocarAnimacao(zumbi, visual, estadoDoZumbi(zumbi, visual));
        posicionarZumbi(zumbi, visual);
    }

    // -------------------------- professores --------------------------
    /**
     * Os professores ficam parados (frame 1) e so comecam o movimento de atirar
     * quando ha um zumbi na linha deles.
     */
    private void atualizarProfessores(double dt) {
        for (Professor professor : gerenciador.getTabuleiro().getProfessores()) {
            if (!professor.estaVivo()) {
                continue;
            }
            professor.atualizarTempo(dt);

            if (professor instanceof Nacif nacif && nacif.estaArmada()) {
                explodirNacif(nacif); // fim da animacao: boom
                continue;
            }

            professor.agir(zumbis); // Lucas (skatada)

            SpriteProfessor sprite = spritesDosProfessores.get(professor);

            if (professor instanceof Fabricio fabricio) {
                // laser: o sprite segura o frame 6 enquanto houver zumbi na mira
                if (sprite != null && !sprite.estaAtirando() && temZumbiNaMira(fabricio)) {
                    sprite.atirarSustentado(
                            null,
                            () -> !partidaEncerrada && fabricio.estaVivo() && temZumbiNaMira(fabricio));
                }
                continue;
            }

            if (sprite != null && !sprite.estaAtirando()
                    && professor.podeAtacar() && temZumbiNaMira(professor)) {
                sprite.atirar(() -> disparar(professor));
            }
        }
    }

    // ------------------------- explosao do Nacif -------------------------
    /**
     * Mata os zumbis da area 3x3 (regra no Nacif) e mostra o boom e os zumbis
     * queimados.
     */
    private void explodirNacif(Nacif nacif) {
        for (Zumbi zumbi : nacif.explodir(zumbis, COLUNAS)) {
            carbonizar(zumbi);
        }
        mostrarExplosao(nacif);
    }

    /**
     * O zumbi morto pela explosao nao some na hora: a animacao para, ele
     * clareia num flash, escurece ate virar carvao, fica um instante assim e
     * desvanece subindo um pouco. So depois o sprite sai da tela.
     */
    private void carbonizar(Zumbi zumbi) {
        VisualZumbi visual = visuaisZumbis.get(zumbi);
        if (visual == null) {
            return;
        }
        visual.carbonizado = true;
        if (visual.animacao != null) {
            visual.animacao.stop();
        }

        ImageView view = visual.view;
        ColorAdjust queimado = new ColorAdjust(0, 0.6, 0.5, 0);
        view.setEffect(queimado);

        Timeline queima = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(queimado.brightnessProperty(), 0.5),
                        new KeyValue(queimado.saturationProperty(), 0.6)),
                new KeyFrame(Duration.seconds(CHAMUSCAR_DURACAO),
                        new KeyValue(queimado.brightnessProperty(), -0.85, Interpolator.EASE_OUT),
                        new KeyValue(queimado.saturationProperty(), -0.7, Interpolator.EASE_OUT)),
                new KeyFrame(Duration.seconds(CARVAO_ATE),
                        new KeyValue(view.opacityProperty(), 1.0),
                        new KeyValue(view.translateYProperty(), 0.0)),
                new KeyFrame(Duration.seconds(SUMIR_ATE),
                        new KeyValue(view.opacityProperty(), 0.0, Interpolator.EASE_IN),
                        new KeyValue(view.translateYProperty(), -16.0, Interpolator.EASE_IN)));
        iniciarEfeito(queima, () -> camadaAcao.getChildren().remove(view));
    }

    /**
     * boom.png no centro da explosao: aparece crescendo, segura um pouco e
     * desvanece.
     */
    private void mostrarExplosao(Nacif nacif) {
        ImageView boom = carregarImagem(IMG_BOOM, BOOM_LARGURA, 0);
        if (boom.getImage() == null) {
            return; // imagem faltando: carregarImagem ja avisou no console
        }
        Bounds caixa = boom.getLayoutBounds();
        boom.setLayoutX((nacif.getColuna() + 0.5) * CEL_LARG - caixa.getWidth() / 2);
        boom.setLayoutY((nacif.getLinha() + 0.5) * CEL_ALT - caixa.getHeight() / 2);
        boom.setViewOrder(-2000); // na frente de zumbis e tiros
        camadaAcao.getChildren().add(boom);

        Timeline aparecer = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(boom.scaleXProperty(), 0.3),
                        new KeyValue(boom.scaleYProperty(), 0.3),
                        new KeyValue(boom.opacityProperty(), 1.0)),
                new KeyFrame(Duration.seconds(0.12),
                        new KeyValue(boom.scaleXProperty(), 1.05, Interpolator.EASE_OUT),
                        new KeyValue(boom.scaleYProperty(), 1.05, Interpolator.EASE_OUT)),
                new KeyFrame(Duration.seconds(0.2),
                        new KeyValue(boom.scaleXProperty(), 1.0),
                        new KeyValue(boom.scaleYProperty(), 1.0)),
                new KeyFrame(Duration.seconds(BOOM_DURACAO * 0.55),
                        new KeyValue(boom.opacityProperty(), 1.0)),
                new KeyFrame(Duration.seconds(BOOM_DURACAO),
                        new KeyValue(boom.opacityProperty(), 0.0, Interpolator.EASE_IN)));
        iniciarEfeito(aparecer, () -> camadaAcao.getChildren().remove(boom));
    }

    /**
     * Toca um efeito curto de tela, na velocidade do jogo, e limpa quando
     * acabar.
     */
    private void iniciarEfeito(Timeline efeito, Runnable aoTerminar) {
        efeito.setOnFinished(e -> {
            animacoes.remove(efeito);
            aoTerminar.run();
        });
        animacoes.add(efeito);
        aplicarVelocidade(efeito);
        efeito.play();
    }

    // ----------------------------- lasers -----------------------------
    /**
     * Laser do Fabricio: enquanto o sprite esta segurando o frame 6, sai um
     * feixe da ponta do dedo para o primeiro zumbi de cada linha (a dele e as
     * vizinhas) que tiver zumbi. Linha sem zumbi nao ganha feixe.
     */
    private void atualizarLasers(double dt) {
        boolean algumLaserLigado = false;
        for (Professor professor : gerenciador.getTabuleiro().getProfessores()) {
            if (!(professor instanceof Fabricio fabricio)) {
                continue;
            }
            SpriteProfessor sprite = spritesDosProfessores.get(fabricio);
            LaserFabricio laser = lasers.get(fabricio);

            boolean ligado = sprite != null && fabricio.estaVivo() && sprite.estaSustentando();
            List<Zumbi> alvos = ligado ? fabricio.escolherAlvos(zumbis, COLUNAS) : List.of();

            if (alvos.isEmpty()) {
                fabricio.pararLaser();
                if (laser != null) {
                    laser.esconder();
                }
                continue;
            }
            algumLaserLigado = true;

            if (laser == null) {
                laser = new LaserFabricio(camadaAcao);
                lasers.put(fabricio, laser);
            }

            fabricio.aplicarLaser(alvos, dt);

            List<double[]> segmentos = new ArrayList<>();
            for (Zumbi alvo : alvos) {
                double[] origem = pontaDoDedo(fabricio, alvo.getLinha());
                double destinoX = (alvo.getPosicaoX() + 0.5) * CEL_LARG;
                double destinoY = (alvo.getLinha() + 0.5) * CEL_ALT + ALTURA_DO_PEITO_ZUMBI;
                segmentos.add(new double[] { origem[0], origem[1], destinoX, destinoY });
            }
            laser.mostrar(segmentos, dt);
        }
        atualizarSomLaser(algumLaserLigado);
    }

    /**
     * Um unico som para todos os lasers: toca enquanto existir algum ligado.
     */
    private void atualizarSomLaser(boolean ligado) {
        if (ligado && somLaser == null) {
            somLaser = AudioManager.iniciarEfeitoEmLoop(SOM_LASER);
        } else if (!ligado && somLaser != null) {
            pararSomLaser();
        }
    }

    private void pararSomLaser() {
        AudioManager.pararEfeitoEmLoop(somLaser);
        somLaser = null;
    }

    /**
     * De onde o feixe sai (em coordenadas da camadaAcao): do indicador de cima
     * para quem esta numa linha acima, do de baixo para quem esta abaixo, e do
     * meio entre os dois para quem esta na mesma linha.
     */
    private double[] pontaDoDedo(Fabricio fabricio, int linhaDoAlvo) {
        double[] dedo;
        if (linhaDoAlvo < fabricio.getLinha()) {
            dedo = DEDO_DE_CIMA;
        } else if (linhaDoAlvo > fabricio.getLinha()) {
            dedo = DEDO_DE_BAIXO;
        } else {
            dedo = VISAO_CENTRAL;
        }

        TipoPersonagem tipo = TipoPersonagem.FABRICIO;
        double escala = tipo.getLarguraCaixa() * ESCALA_PERSONAGEM / FRAME_LADO_PX;
        double centroX = (fabricio.getColuna() + 0.5) * CEL_LARG;
        double centroY = (fabricio.getLinha() + 0.5) * CEL_ALT + tipo.getAjusteY() * ESCALA_PERSONAGEM;

        return new double[] {
                centroX + (dedo[0] - FRAME_LADO_PX / 2) * escala,
                centroY + (dedo[1] - FRAME_LADO_PX / 2) * escala
        };
    }

    /**
     * Ha zumbi vivo, ja na tela, a frente do professor e dentro do alcance das
     * linhas dele?
     */
    private boolean temZumbiNaMira(Professor professor) {
        int alcance = (professor instanceof Glaucia) ? 1 : professor.getAlcanceEmLinhas();
        double limiteX = COLUNAS;
        if (professor instanceof Glaucia glaucia) {
            limiteX = Math.min(COLUNAS, professor.getColuna() + glaucia.getAlcanceEmColunas() + 1);
        }
        for (Zumbi zumbi : zumbis) {
            if (zumbi.estaVivo()
                    && Math.abs(zumbi.getLinha() - professor.getLinha()) <= alcance
                    && zumbi.getPosicaoX() >= professor.getColuna()
                    && zumbi.getPosicaoX() < limiteX) {
                return true;
            }
        }
        return false;
    }

    private static RadialGradient gradienteDeFogo(Color centro, Color borda) {
        return new RadialGradient(0, 0, 0.5, 0.5, 0.5, true, CycleMethod.NO_CYCLE,
                new Stop(0, centro), new Stop(1, borda));
    }

    /**
     * Bola de fogo de 60x40 px: rastro atras, corpo laranja e miolo amarelo
     * (anda para a direita).
     */
    private Group criarBolaDeFogo() {
        Rectangle caixa = new Rectangle(0, 0, 60, 40); // fixa o tamanho do grupo
        caixa.setFill(Color.TRANSPARENT);

        Ellipse rastro = new Ellipse(18, 20, 18, 9);
        rastro.setFill(gradienteDeFogo(Color.rgb(255, 110, 0, 0.85), Color.rgb(200, 30, 0, 0)));

        Ellipse corpo = new Ellipse(38, 20, 20, 16);
        corpo.setFill(gradienteDeFogo(Color.rgb(255, 150, 20, 1), Color.rgb(220, 40, 0, 0)));

        Ellipse miolo = new Ellipse(42, 20, 10, 8);
        miolo.setFill(gradienteDeFogo(Color.rgb(255, 250, 190, 1), Color.rgb(255, 190, 40, 0)));

        Group fogo = new Group(caixa, rastro, corpo, miolo);
        fogo.setMouseTransparent(true);
        return fogo;
    }

    /**
     * Chama tremulando; entra em `animacoes` para acompanhar a velocidade do
     * jogo.
     */
    private Animation criarTremorDoFogo(Group fogo) {
        Timeline tremor = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(fogo.scaleXProperty(), 1.0),
                        new KeyValue(fogo.scaleYProperty(), 0.88)),
                new KeyFrame(Duration.millis(110),
                        new KeyValue(fogo.scaleXProperty(), 1.0),
                        new KeyValue(fogo.scaleYProperty(), 1.12)));
        tremor.setAutoReverse(true);
        tremor.setCycleCount(Animation.INDEFINITE);
        animacoes.add(tremor);
        tremor.play();
        return tremor;
    }

    /**
     * Chamado no frame do disparo da animacao.
     */
    private void disparar(Professor professor) {
        if (partidaEncerrada || !professor.estaVivo()
                || !professor.podeAtacar() || !temZumbiNaMira(professor)) {
            return;
        }
        if (professor instanceof Glaucia glaucia) {
            dispararCone(glaucia);
            return;
        }

        boolean primeiro = true;
        for (Tiro tiro : professor.atacar()) {
            if (tiro.getY() < 0 || tiro.getY() > LINHAS - 1) {
                continue;
            }

            Node view;
            Animation tremor = null;
            JatoDeFogo jato = null;
            boolean fogo = "fogo".equals(tiro.getEfeitoVisual());
            if (fogo) {
                jato = new JatoDeFogo();
                tremor = criarTremorDoFogo(jato.grupo);
                view = jato.grupo;
            } else {
                view = carregarImagem(tiro.getSprite(), TIRO_LARGURA, 0);
            }
            view.setViewOrder(-1000);
            camadaAcao.getChildren().add(view);

            TipoPersonagem tipo = TipoPersonagem.valueOf(
                    professor.getClass().getSimpleName().toUpperCase(Locale.ROOT));
            double altura = (professor instanceof Glaucia) ? ALTURA_DO_TIRO_GLAUCIA : ALTURA_DO_TIRO;
            double centroY = tipo.yNaCasa(altura, CEL_ALT, ESCALA_PERSONAGEM);
            if (tipo == TipoPersonagem.THAIS) {
                centroY += AJUSTE_Y_TIRO_THAIS;
            } else if (tipo == TipoPersonagem.PHILIPE) {
                centroY += AJUSTE_Y_TIRO_PHILIPE;
            }
            double alcance = (professor instanceof Glaucia g) ? g.getAlcanceEmColunas() : Double.MAX_VALUE;

            TiroEmCampo emCampo = new TiroEmCampo(tiro, view, centroY, tremor, jato, alcance);
            tiros.add(emCampo);
            posicionarTiro(emCampo);

            if (primeiro) {
                if (fogo) {
                    AudioManager.tocarEfeito(tiro.getSom(), FOGO_SOM_MAXIMO);
                } else {
                    AudioManager.tocarEfeito(tiro.getSom());
                }
                primeiro = false;
            }
        }
    }

    /**
     * Cone de fogo da Glaucia: dano em area (regra na Glaucia) + visual.
     */
    private void dispararCone(Glaucia glaucia) {
        String som = null;
        for (Tiro t : glaucia.atacar()) { // so para gastar a recarga e pegar o som
            if (som == null) {
                som = t.getSom();
            }
        }
        glaucia.incendiar(zumbis, COLUNAS);
        mostrarCone(glaucia);
        if (som != null) {
            AudioManager.tocarEfeito(som, FOGO_SOM_MAXIMO);
        }
    }

    /**
     * Fogo da Glaucia: linguas de chama curvas que balancam, brasas e um clarao
     * na boca.
     */
    private void mostrarCone(Glaucia glaucia) {
        int linha = glaucia.getLinha();
        double xBoca = (glaucia.getColuna() + X_DO_TIRO_GLAUCIA) * CEL_LARG;
        double yBoca = linha * CEL_ALT
                + TipoPersonagem.GLAUCIA.yNaCasa(ALTURA_DO_TIRO_GLAUCIA, CEL_ALT, ESCALA_PERSONAGEM);
        double fim = Math.min(COLUNAS, glaucia.getColuna() + 1 + glaucia.getAlcanceEmColunas());
        double alcancePx = (fim + 0.5) * CEL_LARG - xBoca;
        double topo = Math.max(0, linha - 1) * CEL_ALT;
        double base = (Math.min(LINHAS - 1, linha + 1) + 1) * CEL_ALT;
        double angTopo = Math.atan2(topo - yBoca, alcancePx);
        double angBase = Math.atan2(base - yBoca, alcancePx);

        Group cone = new Group();
        cone.setMouseTransparent(true);
        cone.setViewOrder(-1000);
        List<KeyFrame> frames = new ArrayList<>();

        // clarao na boca
        Circle brilho = new Circle(xBoca, yBoca, 46);
        brilho.setFill(gradienteDeFogo(Color.rgb(255, 245, 190, 0.95), Color.rgb(255, 120, 0, 0)));
        cone.getChildren().add(brilho);
        frames.add(new KeyFrame(Duration.ZERO,
                new KeyValue(brilho.scaleXProperty(), 0.6),
                new KeyValue(brilho.scaleYProperty(), 0.6),
                new KeyValue(brilho.opacityProperty(), 1.0)));
        frames.add(new KeyFrame(Duration.seconds(0.15),
                new KeyValue(brilho.scaleXProperty(), 1.4, Interpolator.EASE_OUT),
                new KeyValue(brilho.scaleYProperty(), 1.4, Interpolator.EASE_OUT)));
        frames.add(new KeyFrame(Duration.seconds(0.8),
                new KeyValue(brilho.opacityProperty(), 0.0, Interpolator.EASE_IN)));

        // linguas de fogo
        int linguas = 16;
        for (int i = 0; i < linguas; i++) {
            double f = (i + sorteio.nextDouble()) / linguas;
            double ang = angTopo + (angBase - angTopo) * f;
            double comp = alcancePx / Math.cos(ang) * (0.7 + sorteio.nextDouble() * 0.35);
            double larg = 34 + sorteio.nextDouble() * 34;

            Path lingua = new Path(
                    new MoveTo(0, -larg / 2),
                    new QuadCurveTo(comp * 0.45, -larg * 0.9, comp, 0),
                    new QuadCurveTo(comp * 0.45, larg * 0.9, 0, larg / 2),
                    new ClosePath());
            lingua.setStroke(null);
            lingua.setFill(new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                    new Stop(0, Color.rgb(255, 235, 140, 0.95)),
                    new Stop(0.35, Color.rgb(255, 140, 10, 0.8)),
                    new Stop(1, Color.rgb(200, 30, 0, 0.0))));

            double graus = Math.toDegrees(ang);
            Rotate giro = new Rotate(graus, 0, 0);
            Scale esc = new Scale(0.05, 0.6, 0, 0);
            // ordem: escala, depois gira, depois leva para a boca
            lingua.getTransforms().addAll(new Translate(xBoca, yBoca), giro, esc);
            lingua.setOpacity(0);
            cone.getChildren().add(lingua);

            double atraso = sorteio.nextDouble() * 0.12;
            double bal = 6 + sorteio.nextDouble() * 8; // graus de balanco
            frames.add(new KeyFrame(Duration.ZERO,
                    new KeyValue(lingua.opacityProperty(), 0.0)));
            frames.add(new KeyFrame(Duration.seconds(atraso + 0.02),
                    new KeyValue(lingua.opacityProperty(), 1.0)));
            frames.add(new KeyFrame(Duration.seconds(atraso + 0.25),
                    new KeyValue(esc.xProperty(), 1.0, Interpolator.EASE_OUT),
                    new KeyValue(esc.yProperty(), 1.15),
                    new KeyValue(giro.angleProperty(), graus + bal)));
            frames.add(new KeyFrame(Duration.seconds(atraso + 0.45),
                    new KeyValue(esc.yProperty(), 0.9),
                    new KeyValue(giro.angleProperty(), graus - bal)));
            frames.add(new KeyFrame(Duration.seconds(atraso + 0.65),
                    new KeyValue(esc.yProperty(), 1.1),
                    new KeyValue(giro.angleProperty(), graus + bal / 2),
                    new KeyValue(lingua.opacityProperty(), 0.9)));
            frames.add(new KeyFrame(Duration.seconds(atraso + 0.85),
                    new KeyValue(esc.xProperty(), 1.05, Interpolator.EASE_IN),
                    new KeyValue(esc.yProperty(), 0.5, Interpolator.EASE_IN),
                    new KeyValue(lingua.opacityProperty(), 0.0, Interpolator.EASE_IN)));
        }

        // brasas voando (sobem um pouco, como fagulhas)
        int brasas = 22;
        for (int i = 0; i < brasas; i++) {
            Circle brasa = new Circle(xBoca, yBoca, 2 + sorteio.nextDouble() * 3.5);
            brasa.setFill(sorteio.nextBoolean() ? Color.rgb(255, 200, 60) : Color.rgb(255, 110, 10));
            brasa.setOpacity(0);
            cone.getChildren().add(brasa);

            double ang = angTopo + (angBase - angTopo) * sorteio.nextDouble();
            double dist = alcancePx / Math.cos(ang) * (0.4 + sorteio.nextDouble() * 0.8);
            double dx = Math.cos(ang) * dist;
            double dy = Math.sin(ang) * dist - (20 + sorteio.nextDouble() * 60);
            double atraso = sorteio.nextDouble() * 0.35;
            double dur = 0.5 + sorteio.nextDouble() * 0.5;

            frames.add(new KeyFrame(Duration.ZERO,
                    new KeyValue(brasa.opacityProperty(), 0.0)));
            frames.add(new KeyFrame(Duration.seconds(atraso),
                    new KeyValue(brasa.opacityProperty(), 1.0),
                    new KeyValue(brasa.translateXProperty(), 0.0),
                    new KeyValue(brasa.translateYProperty(), 0.0)));
            frames.add(new KeyFrame(Duration.seconds(atraso + dur),
                    new KeyValue(brasa.translateXProperty(), dx, Interpolator.EASE_OUT),
                    new KeyValue(brasa.translateYProperty(), dy, Interpolator.EASE_OUT),
                    new KeyValue(brasa.scaleXProperty(), 0.3),
                    new KeyValue(brasa.scaleYProperty(), 0.3),
                    new KeyValue(brasa.opacityProperty(), 0.0, Interpolator.EASE_IN)));
        }

        camadaAcao.getChildren().add(cone);
        Timeline fogo = new Timeline(frames.toArray(new KeyFrame[0]));
        iniciarEfeito(fogo, () -> camadaAcao.getChildren().remove(cone));
    }
    // ----------------------------- tiros -----------------------------

    private void atualizarTiros(double dt) {
        Iterator<TiroEmCampo> it = tiros.iterator();
        while (it.hasNext()) {
            TiroEmCampo emCampo = it.next();
            Tiro tiro = emCampo.tiro;
            tiro.mover(dt);

            Zumbi alvo = tiro.isAtivo() ? zumbiAtingido(tiro) : null;
            if (alvo != null) {
                tiro.atingir(alvo);
            }

            boolean passouDoAlcance = tiro.getX() - emCampo.origemX > emCampo.alcanceMaximo;
            if (!tiro.isAtivo() || tiro.getX() > COLUNAS + 1 || passouDoAlcance) {
                if (emCampo.tremor != null) {
                    emCampo.tremor.stop();
                    animacoes.remove(emCampo.tremor);
                }
                removerVisualDoTiro(emCampo);
                it.remove();
            } else {
                posicionarTiro(emCampo);
            }
        }
    }

    /**
     * O jato some num fade rápido; os outros tiros somem na hora.
     */
    private void removerVisualDoTiro(TiroEmCampo emCampo) {
        if (emCampo.jato == null) {
            camadaAcao.getChildren().remove(emCampo.view);
            return;
        }
        FadeTransition sumir = new FadeTransition(Duration.millis(150), emCampo.view);
        sumir.setToValue(0);
        sumir.setOnFinished(e -> {
            animacoes.remove(sumir);
            camadaAcao.getChildren().remove(emCampo.view);
        });
        animacoes.add(sumir);
        sumir.play();
    }

    private void posicionarTiro(TiroEmCampo t) {
        Tiro tiro = t.tiro;
        if (t.jato != null) {
            double inicio = (t.origemX + 0.75) * CEL_LARG;
            double fim = (tiro.getX() + 0.75) * CEL_LARG + JATO_PONTA;
            t.jato.definirComprimento(Math.max(JATO_MIN, fim - inicio));
            t.view.setLayoutX(inicio);
        } else {
            t.view.setLayoutX((tiro.getX() + 0.75) * CEL_LARG);
        }
        t.view.setLayoutY(tiro.getY() * CEL_ALT + t.centroYNaCasa
                - t.view.getLayoutBounds().getHeight() / 2);
    }

    /**
     * O zumbi vivo mais proximo, na linha do tiro, que o tiro ja alcancou.
     */
    private Zumbi zumbiAtingido(Tiro tiro) {
        int linha = (int) Math.round(tiro.getY());
        Zumbi alvo = null;
        for (Zumbi zumbi : zumbis) {
            if (zumbi.estaVivo() && zumbi.getLinha() == linha
                    && zumbi.getPosicaoX() < COLUNAS
                    && tiro.getX() + 1.0 >= zumbi.getPosicaoX()
                    && tiro.getX() <= zumbi.getPosicaoX() + 1.0
                    && (alvo == null || zumbi.getPosicaoX() < alvo.getPosicaoX())) {
                alvo = zumbi;
            }
        }
        return alvo;
    }

    private void posicionarTiro(Tiro tiro, Node view, double centroYNaCasa) {
        view.setLayoutX((tiro.getX() + 0.75) * CEL_LARG);
        view.setLayoutY(tiro.getY() * CEL_ALT + centroYNaCasa - view.getLayoutBounds().getHeight() / 2);
    }

    // ----------------------------- zumbis -----------------------------
    private void atualizarZumbis(double dt) {
        for (Zumbi zumbi : new ArrayList<>(zumbis)) {
            if (!zumbi.estaVivo()) {
                continue;
            }
            VisualZumbi visual = visuaisZumbis.get(zumbi);

            if (zumbi.estaParalisado()) {
                zumbi.mover(dt); // so passa o tempo dos efeitos: paralisado nao anda nem morde
                congelarVisual(visual, true);
                continue;
            }
            congelarVisual(visual, false);
            atualizarTinta(zumbi, visual);

            if (visual.puloRestante > 0) {
                saltar(zumbi, visual, dt);
            } else {
                Entidade alvo = gerenciador.getOcupante(zumbi.getLinha(),
                        (int) Math.floor(zumbi.getPosicaoX() + MORDIDA_DESLOCAMENTO));
                if (alvo == null) {
                    // so o Marcus e mordido de mais longe (sprite largo)
                    Entidade proximo = gerenciador.getOcupante(zumbi.getLinha(),
                            (int) Math.floor(zumbi.getPosicaoX() + MORDIDA_DESLOCAMENTO_MARCUS));
                    if (proximo instanceof Marcus) {
                        alvo = proximo;
                    }
                }

                if (alvo != null && alvo.estaVivo()) {
                    if (zumbi instanceof Trote && !visual.pulou) {
                        visual.pulou = true;
                        visual.puloRestante = DURACAO_DO_PULO; // o Trote pula o primeiro obstaculo
                    } else {
                        zumbi.iniciarAtaque();
                        zumbi.atacar(alvo, dt);
                    }
                } else {
                    zumbi.pararAtaque();
                }
                zumbi.mover(dt); // anda (se nao estiver mordendo) e aplica lentidao/queimadura
            }

            trocarAnimacao(zumbi, visual, estadoDoZumbi(zumbi, visual));
            posicionarZumbi(zumbi, visual);
            verificarPorta(zumbi);
        }
    }

    /**
     * Enquanto queima, o zumbi fica avermelhado; a paralisia (azul) tem
     * prioridade.
     */
    /**
     * Decide a tinta do zumbi: fogo (vermelho) > lentidao (azul gelo) > nada.
     * A paralisia (azul escuro) e a carbonizacao tem prioridade e ficam fora daqui.
     */
    private void atualizarTinta(Zumbi zumbi, VisualZumbi visual) {
        if (visual.carbonizado || visual.congelado) {
            return;
        }
        Effect desejado = zumbi.estaQueimando() ? EFEITO_QUEIMADO
                : zumbi.estaLento() ? EFEITO_GELO
                        : null;
        if (visual.view.getEffect() != desejado) {
            visual.view.setEffect(desejado);
        }
    }

    /**
     * Pausa a animacao e tinge o zumbi de azul enquanto ele esta paralisado.
     */
    private void congelarVisual(VisualZumbi visual, boolean congelar) {
        if (visual.congelado == congelar) {
            return;
        }

        visual.pegandoFogo = false;
        visual.congelado = congelar;
        visual.view.setEffect(congelar ? EFEITO_PARALISIA : null);
        if (visual.animacao != null) {
            if (congelar) {
                visual.animacao.getTimeline().pause();
            } else {
                visual.animacao.getTimeline().play();
            }
        }
    }

    /**
     * Durante o pulo o zumbi nao morde: avanca por cima do professor.
     */
    private void saltar(Zumbi zumbi, VisualZumbi visual, double dt) {
        zumbi.iniciarAtaque(); // trava o andar normal; mover() so aplica os efeitos
        zumbi.mover(dt);
        zumbi.setPosicaoX(zumbi.getPosicaoX() - DISTANCIA_DO_PULO * dt / DURACAO_DO_PULO);
        visual.puloRestante -= dt;
        if (visual.puloRestante <= 0) {
            zumbi.pararAtaque();
        }
    }

    private String estadoDoZumbi(Zumbi zumbi, VisualZumbi visual) {
        if (visual.puloRestante > 0) {
            return "pulo";
        }
        if (zumbi.estaAtacando()) {
            return "bater";
        }
        if (zumbi instanceof Viciado viciado && viciado.estaEstressado()) {
            return "correr";
        }
        return "andar";
    }

    /**
     * Troca o conjunto de frames no mesmo ImageView (andar -> bater -> ...).
     */
    private void trocarAnimacao(Zumbi zumbi, VisualZumbi visual, String estado) {
        if (estado.equals(visual.estado)) {
            return;
        }
        SpritesZumbi sprites = spritesDe(zumbi);
        String padrao = switch (estado) {
            case "bater" ->
                sprites.bater();
            case "correr" ->
                sprites.correr();
            case "pulo" ->
                sprites.pulo();
            default ->
                sprites.andar();
        };
        if (visual.animacao != null) {
            visual.animacao.stop();
        }
        visual.estado = estado;
        boolean passos = estado.equals("andar") || estado.equals("correr");
        visual.animacao = new SpriteAnimado(visual.view, padrao, 1, 12,
                SpriteAnimado.FPS_PADRAO,
                passos ? SEGURAR_PASSOS : SpriteAnimado.SEGURAR_PADRAO,
                SpriteAnimado.Modo.LOOP, visual.animacao == null);
        visual.animacao.play();
    }

    private void posicionarZumbi(Zumbi zumbi, VisualZumbi visual) {
        boolean viciado = zumbi instanceof Viciado;
        double caixa = viciado ? ZUMBI_CAIXA_VICIADO : ZUMBI_CAIXA;
        double y = viciado ? VICIADO_AJUSTE_Y : (CEL_ALT - ZUMBI_CAIXA) / 2 - 6;

        visual.view.setLayoutX((zumbi.getPosicaoX() + 0.5) * CEL_LARG - caixa / 2);
        visual.view.setLayoutY(zumbi.getLinha() * CEL_ALT + y);
    }

    private static SpritesZumbi spritesDe(Zumbi zumbi) {
        if (zumbi instanceof Viciado) {
            return new SpritesZumbi(
                    PASTA_ZUMBIS + "viciado/andar_celular/andar_celular_%02d.png",
                    PASTA_ZUMBIS + "viciado/bater_fase2/bater_fase2_%02d.png",
                    PASTA_ZUMBIS + "viciado/correr/correr_%02d.png",
                    null);
        }
        if (zumbi instanceof Trote) {
            String correr = PASTA_ZUMBIS + "trote/correr/correr_%02d.png";
            return new SpritesZumbi(correr,
                    PASTA_ZUMBIS + "trote/bater/bater_%02d.png",
                    correr,
                    PASTA_ZUMBIS + "trote/pulo/pulo_%02d.png");
        }
        String pasta;
        if (zumbi instanceof CalouroBlindado) {
            pasta = "calouroBlindado";
        } else if (zumbi instanceof CalouroProtegido) {
            pasta = "calouroProtegido";
        } else if (zumbi instanceof VibeCoder) {
            pasta = "vibeCoder";
        } else if (zumbi instanceof Formando) {
            pasta = "formando";
        } else if (zumbi instanceof Doutorando) {
            pasta = "doutorando";
        } else if (zumbi instanceof Calouro) {
            pasta = "calouro";
        } else {
            throw new IllegalArgumentException("Zumbi sem sprite: " + zumbi.getClass().getSimpleName());
        }
        String andar = PASTA_ZUMBIS + pasta + "/andar/andar_%02d.png";
        return new SpritesZumbi(andar, PASTA_ZUMBIS + pasta + "/bater/bater_%02d.png", andar, null);
    }

    // ---------------- porta de saida e carrinho de cada linha ----------------
    /**
     * Zumbi chegou na porta: o carrinho da linha leva todo mundo; se ja foi
     * usado, e derrota.
     */
    private void verificarPorta(Zumbi zumbi) {
        int linha = zumbi.getLinha();
        if (zumbi.getPosicaoX() < 0.1 && !carrinhoUsado[linha]) {
            carrinhoUsado[linha] = true;
            carrinhoRodando[linha] = true;
            AudioManager.tocarEfeito(SOM_CARRINHO);
        } else if (zumbi.getPosicaoX() < X_DERROTA && !carrinhoRodando[linha]) {
            terminarPartida(false);
        }
    }

    private void atualizarCarrinhos(double dt) {
        for (int linha = 0; linha < LINHAS; linha++) {
            if (!carrinhoRodando[linha]) {
                continue;
            }
            ImageView carrinho = carrinhos[linha];
            carrinho.setLayoutX(carrinho.getLayoutX() + CARRINHO_VELOCIDADE * dt);

            // frente do carrinho, em casas contadas a partir da esquerda da grade
            double frente = (carrinho.getLayoutX() + carrinho.getBoundsInLocal().getWidth() - GRADE_X) / CEL_LARG;
            for (Zumbi zumbi : zumbis) {
                if (zumbi.estaVivo() && zumbi.getLinha() == linha && zumbi.getPosicaoX() <= frente) {
                    zumbi.receberDano(zumbi.getVida());
                }
            }

            if (carrinho.getLayoutX() > LARGURA_PALCO) {
                carrinhoRodando[linha] = false;
                carrinho.setVisible(false);
            }
        }
    }

    // -------------------- mortes, vitoria e derrota --------------------
    private void removerMortos() {
        Iterator<Zumbi> it = zumbis.iterator();
        while (it.hasNext()) {
            Zumbi zumbi = it.next();
            if (!zumbi.estaVivo()) {
                VisualZumbi visual = visuaisZumbis.remove(zumbi);
                if (visual != null) {
                    if (visual.animacao != null) {
                        visual.animacao.stop();
                    }
                    if (!visual.carbonizado) { // carbonizado: a propria animacao tira o sprite no fim
                        camadaAcao.getChildren().remove(visual.view);
                    }
                }
                it.remove();
            }
        }

        // professores e cafeteiras que morreram (mordidas) ou se gastaram (Nacif
        // explodiu)
        List<Entidade> ocupantes = new ArrayList<>(gerenciador.getTabuleiro().getProfessores());
        ocupantes.addAll(gerenciador.getTabuleiro().getCafeteiras());
        for (Entidade ocupante : ocupantes) {
            if (!ocupante.estaVivo()) {
                removerDoTabuleiro(ocupante);
            }
        }
    }

    private void removerDoTabuleiro(Entidade ocupante) {
        int linha = ocupante.getLinha();
        int coluna = ocupante.getColuna();

        List<Animation> dele = animacoesDaEntidade.remove(ocupante);
        if (dele != null) {
            dele.forEach(Animation::stop);
        }
        if (ocupante instanceof Professor professor) {
            spritesDosProfessores.remove(professor);
        }
        if (ocupante instanceof Fabricio fabricio) {
            LaserFabricio laser = lasers.remove(fabricio);
            if (laser != null) {
                laser.remover();
            }
        }

        gerenciador.removerDoPiso(linha, coluna);
        StackPane celula = celulas[linha][coluna];
        celula.getChildren().clear();
        celula.getProperties().remove(CHAVE_GOTA);
    }

    private void verificarFim() {
        if (!partidaEncerrada && zumbis.isEmpty() && gerenciador.getFase().estaConcluida()) {
            terminarPartida(true);
        }
    }

    private void terminarPartida(boolean vitoria) {
        if (partidaEncerrada) {
            return;
        }
        partidaEncerrada = true;
        relogio.stop();
        pararSomLaser();
        lasers.values().forEach(LaserFabricio::esconder);
        desselecionar();
        esconderHudDeJogo();
        limparGotas();
        if (vitoria) {
            ConfiguracoesJogo.registrarNivelConcluido(gerenciador.getFase().getNumero());
            mostrarVitoria();
        } else {
            mostrarDerrota();
        }
    }

    // ================================================================

    private void sairDaPartida() {
        encerrarTela();
        gerenciadorDeTelas.voltarMenu(); // para ir a selecao de fases: mudarTela("SelecaoNivel.fxml")
    }

    /**
     * Para o relogio, todas as animacoes e as recargas dos cards.
     */
    private void encerrarTela() {
        if (relogio != null) {
            relogio.stop();
        }
        if (animacaoIntro != null) {
            animacaoIntro.stop();
        }
        pararSomLaser();
        animacoes.forEach(Animation::stop);
        animacoes.clear();
        animacoesDaEntidade.values().forEach(lista -> lista.forEach(Animation::stop));
        animacoesDaEntidade.clear();
        visuaisZumbis.values().forEach(v -> {
            if (v.animacao != null) {
                v.animacao.stop();
            }
        });
        cartoesDaBarra.forEach(CartaoPersonagem::parar);
    }

    // ---- som ao escolher o card ----
    private static final Map<TipoPersonagem, String> SOM_DO_CARTAO = new EnumMap<>(TipoPersonagem.class);

    static {
        String pasta = "/audio/voicelines/";
        SOM_DO_CARTAO.put(TipoPersonagem.CAFETEIRA, pasta + "cafeteira.mp3");
        SOM_DO_CARTAO.put(TipoPersonagem.DANIEL, pasta + "daniel.mp3");
        SOM_DO_CARTAO.put(TipoPersonagem.THAIS, pasta + "thais.mp3");
        SOM_DO_CARTAO.put(TipoPersonagem.PHILIPE, pasta + "philipe.mp3");
        SOM_DO_CARTAO.put(TipoPersonagem.GLAUCIA, pasta + "glaucia.mp3");
        SOM_DO_CARTAO.put(TipoPersonagem.FABRICIO, pasta + "fabricio.mp3");
        SOM_DO_CARTAO.put(TipoPersonagem.LUCAS, pasta + "lucas.mp3");
        SOM_DO_CARTAO.put(TipoPersonagem.NACIF, pasta + "nacif.mp3");
        SOM_DO_CARTAO.put(TipoPersonagem.MARCUS, pasta + "marcus.mp3");
    }

    /**
     * Boneco (frame 1) que acompanha o mouse enquanto um card esta escolhido.
     */
    private void configurarFantasma() {
        fantasma = new ImageView();
        fantasma.setMouseTransparent(true); // o clique cai na casa
        fantasma.setPreserveRatio(true);
        fantasma.setSmooth(true);
        fantasma.setOpacity(OPACIDADE_FANTASMA);
        fantasma.setVisible(false);
        palco.getChildren().add(fantasma); // por ultimo: fica por cima de tudo

        // filtro na raiz: acompanha o mouse mesmo por cima dos cards e das casas
        raiz.addEventFilter(MouseEvent.MOUSE_MOVED, this::moverFantasma);
        raiz.addEventFilter(MouseEvent.MOUSE_DRAGGED, this::moverFantasma);
    }

    private void moverFantasma(MouseEvent e) {
        Point2D p = palco.sceneToLocal(e.getSceneX(), e.getSceneY()); // desconta a escala do palco
        mouseXPalco = p.getX();
        mouseYPalco = p.getY();
        posicionarFantasma();
    }

    private void posicionarFantasma() {
        fantasma.setLayoutX(mouseXPalco - fantasma.getFitWidth() / 2);
        fantasma.setLayoutY(mouseYPalco - fantasma.getFitHeight() / 2);
    }

    private void mostrarFantasma(TipoPersonagem tipo) {
        Image[] frames = SpriteAnimado.carregarFrames(tipo.getPadraoFrames(), 1, 1);
        if (frames.length == 0) {
            return;
        }
        fantasma.setImage(frames[0]);
        fantasma.setFitWidth(tipo.getLarguraCaixa() * ESCALA_PERSONAGEM);
        fantasma.setFitHeight(tipo.getAlturaCaixa() * ESCALA_PERSONAGEM);
        posicionarFantasma();
        fantasma.setVisible(true);
    }

    private final EventHandler<KeyEvent> aoApertarTecla = e -> {
        if (e.getCode() != KeyCode.ESCAPE) {
            return;
        }
        e.consume();
        if (partidaEncerrada || introAtiva) {
            return;
        }
        if (confirmacaoAberta) {
            fecharConfirmacao(); // ESC na tela preta = continuar jogando
        } else if (selecionado != null || demissaoAtiva) {
            desselecionar(); // tira o fantasminha
        } else {
            abrirConfiguracoes();
        }
    };

    private void configurarTeclado() {
        // o filtro fica na cena e sai junto com esta tela (nao pega o ESC das configs)
        raiz.sceneProperty().addListener((obs, antiga, nova) -> {
            if (antiga != null) {
                antiga.removeEventFilter(KeyEvent.KEY_PRESSED, aoApertarTecla);
            }
            if (nova != null) {
                nova.addEventFilter(KeyEvent.KEY_PRESSED, aoApertarTecla);
            }
        });
        if (raiz.getScene() != null) {
            raiz.getScene().addEventFilter(KeyEvent.KEY_PRESSED, aoApertarTecla);
        }
    }

    private void pausarPartida() {
        pausado = true;
        relogio.stop();
        pararSomLaser();

        List<Animation> todas = new ArrayList<>(animacoes);
        animacoesDaEntidade.values().forEach(todas::addAll);
        visuaisZumbis.values().forEach(v -> {
            if (v.animacao != null) {
                todas.add(v.animacao.getTimeline());
            }
        });

        pausadasPeloEsc.clear();
        for (Animation a : todas) {
            if (a.getStatus() == Animation.Status.RUNNING) { // so quem estava rodando
                a.pause();
                pausadasPeloEsc.add(a);
            }
        }
        cartoesDaBarra.forEach(CartaoPersonagem::pausar);
        atualizarIconePausa();
    }

    private void retomarPartida() {
        pausadasPeloEsc.forEach(Animation::play);
        pausadasPeloEsc.clear();
        cartoesDaBarra.forEach(CartaoPersonagem::retomar);
        ultimoTick = 0; // evita um salto de tempo no primeiro quadro
        pausado = false;
        relogio.start();
        atualizarIconePausa();
    }

    // =================================================================
    // INTRODUCAO DA FASE
    // =================================================================
    private static String estiloIntro(int px, String cor) {
        return "-fx-text-fill: " + cor + "; -fx-font-family: 'Poppins'; "
                + "-fx-font-weight: bold; -fx-font-size: " + px + "px;";
    }

    private Label textoIntro(int px, String cor) {
        Label l = new Label();
        l.setStyle(estiloIntro(px, cor));
        l.setOpacity(0);
        l.setMouseTransparent(true);
        return l;
    }

    /** Um passo da contagem: aparece com zoom, segura e some (dura 'duracao' s). */
    private Timeline passoContagem(Label l, String texto, int px, String cor, double duracao) {
        return new Timeline(
                new KeyFrame(Duration.ZERO, e -> {
                    l.setText(texto);
                    l.setStyle(estiloIntro(px, cor));
                },
                        new KeyValue(l.opacityProperty(), 0.0),
                        new KeyValue(l.scaleXProperty(), 1.8),
                        new KeyValue(l.scaleYProperty(), 1.8)),
                new KeyFrame(Duration.millis(250),
                        new KeyValue(l.opacityProperty(), 1.0, Interpolator.EASE_OUT),
                        new KeyValue(l.scaleXProperty(), 1.0, Interpolator.EASE_OUT),
                        new KeyValue(l.scaleYProperty(), 1.0, Interpolator.EASE_OUT)),
                new KeyFrame(Duration.seconds(duracao - 0.2),
                        new KeyValue(l.opacityProperty(), 1.0)),
                new KeyFrame(Duration.seconds(duracao),
                        new KeyValue(l.opacityProperty(), 0.0)));
    }

    private void iniciarIntroducao() {
        // Com o cheat ligado a introducao e sempre a versao pequena (so 3, 2, 1),
        // inclusive depois de zerar uma fase.
        boolean soContagem = repetirSoContagem || ConfiguracoesJogo.isCheatAtivo();
        repetirSoContagem = false;
        introAtiva = true;

        Label frase = textoIntro(64, "white");
        frase.setText("Impeça os alunos de irem embora...");
        Label contagem = textoIntro(200, "#f4a23a");

        camadaIntro = new StackPane(frase, contagem);
        camadaIntro.setStyle("-fx-background-color: rgba(0, 0, 0, 0.93);");
        camadaIntro.setPrefSize(LARGURA_PALCO, ALTURA_PALCO);
        camadaIntro.setMinSize(LARGURA_PALCO, ALTURA_PALCO);
        camadaIntro.setMaxSize(LARGURA_PALCO, ALTURA_PALCO);
        palco.getChildren().add(camadaIntro); // por ultimo: cobre tudo

        FadeTransition entra = new FadeTransition(Duration.seconds(1.0), frase);
        entra.setFromValue(0);
        entra.setToValue(1);

        PauseTransition le = new PauseTransition(Duration.seconds(2.5));

        FadeTransition sai = new FadeTransition(Duration.seconds(0.6), frase);
        sai.setToValue(0);

        PauseTransition dramatica = new PauseTransition(Duration.seconds(1.0));
        Timeline tres = passoContagem(contagem, "3", 200, "#f4a23a", 1.0);
        Timeline dois = passoContagem(contagem, "2", 200, "#f4a23a", 1.0);
        Timeline um = passoContagem(contagem, "1", 200, "#f4a23a", 1.0);
        Timeline programar = passoContagem(contagem, "PROGRAMAR!", 130, "#5CDB6E", 1.2);
        // toca o 321go junto com o "3", tanto na intro completa quanto na só-contagem
        tres.getKeyFrames().add(new KeyFrame(Duration.ZERO,
                e -> AudioManager.tocarEfeito(SOM_INTRO)));
        programar.setOnFinished(e -> {
            introAtiva = false;
            camadaIntro.setMouseTransparent(true); // o jogo ja pode receber clique
            iniciarPartida();
        });

        FadeTransition someFundo = new FadeTransition(Duration.seconds(0.7), camadaIntro);
        someFundo.setToValue(0);
        someFundo.setOnFinished(e -> palco.getChildren().remove(camadaIntro));

        if (soContagem) {
            animacaoIntro = new SequentialTransition(
                    tres, dois, um, programar, someFundo);
        } else {
            animacaoIntro = new SequentialTransition(
                    entra, le, sai, dramatica, tres, dois, um, programar, someFundo);
        }
        animacaoIntro.play();
    }

    private void esconder(javafx.scene.Node... nos) {
        for (javafx.scene.Node n : nos) {
            n.setVisible(false);
            n.setManaged(false);
        }
    }

    private void mostrar(javafx.scene.Node... nos) {
        for (javafx.scene.Node n : nos) {
            n.setVisible(true);
            n.setManaged(true);
        }
    }

    private void mostrarVitoria() {
        AudioManager.tocarEfeito("/audio/vitoria.mp3");
        camadaResultado.getStyleClass().add("vitoria");

        esconder(lblRostoResultado, lblTituloResultado, lblMensagemResultado, QRCode,
                btnJogarNovamente, btnProximaFase, btnVoltarResultado);
        mostrar(mensagemLabel);
        mensagemLabel.setText("");

        String mensagem = "Inicializando próxima fase...\nCarregando personagens...\n"
                + "Verificando habilidades...\nPreparando tabuleiro...\n\n"
                + "Todas as verificações concluídas!\nFase seguinte liberada!\n\nBUILD SUCCESS :)";

        AtomicInteger indice = new AtomicInteger(0);
        digitacao = new Timeline(new KeyFrame(Duration.millis(10),
                e -> mensagemLabel.setText(mensagem.substring(0, indice.incrementAndGet()))));
        digitacao.setCycleCount(mensagem.length());
        digitacao.setOnFinished(e -> {
            mostrar(btnJogarNovamente, btnVoltarResultado);
            if (SelecaoNivelController.faseSelecionada < TOTAL_FASES) {
                mostrar(btnProximaFase);
            }
        });

        camadaResultado.setVisible(true);
        digitacao.play();
    }

    private void mostrarDerrota() {
        AudioManager.tocarEfeito("/audio/faah.mp3");

        lblRostoResultado.setText(":(");
        lblTituloResultado.setText("DERROTA!");
        lblMensagemResultado.setText("MAS VOCÊ AINDA PODE TENTAR NOVAMENTE!");

        esconder(mensagemLabel, btnProximaFase);
        mostrar(QRCode, btnJogarNovamente, btnVoltarResultado);

        camadaResultado.setVisible(true);
    }

    @FXML
    private void jogarNovamente() {
        if (digitacao != null)
            digitacao.stop();
        repetirSoContagem = true; // pula a intro, só 3-2-1
        encerrarTela();
        gerenciadorDeTelas.mudarTela("Jogo.fxml");
    }

    @FXML
    private void proximaFase() {
        if (digitacao != null)
            digitacao.stop();
        encerrarTela();
        SelecaoNivelController.faseSelecionada++;
        gerenciadorDeTelas.mudarTela("Jogo.fxml");
    }

    @FXML
    private void voltarDoResultado() {
        if (digitacao != null)
            digitacao.stop();
        encerrarTela();
        gerenciadorDeTelas.voltarMenu();
    }

    private void esconderHudDeJogo() {
        lblCafe.setVisible(false);
        if (botaoDemissao != null) {
            botaoDemissao.setVisible(false);
        }
    }

}