package com.poo2026_2.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.image.ImageView;
import java.util.HashMap;
import java.util.Map;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.List;

import com.poo2026_2.audio.AudioManager;
import com.poo2026_2.model.Entidade;
import com.poo2026_2.model.Fase;
import com.poo2026_2.model.GerenciadorDeJogo;
import com.poo2026_2.model.Tabuleiro;
import com.poo2026_2.model.Onda;
import com.poo2026_2.model.Tiro;

import com.poo2026_2.model.zumbis.*;
import com.poo2026_2.model.utilizaveis.*;

/**
 * FXML Controller class
 *
 * @author thoma
 */
public class JogoController implements ControladorTela, Initializable {

    private GerenciadorDeJogo gerenciadorDeJogo;
    private AnimationTimer timer;

    // deve alinhar com a imagem
    // Coordenadas em pixels da imagem original (1672 x 941).
    private static final double LARGURA_PALCO = 1672;
    private static final double ALTURA_PALCO = 941;

    private static final int LINHAS = 5;
    private static final int COLUNAS = 12;
    private static final double GRADE_X = 240; // canto superior esquerdo da grade
    private static final double GRADE_Y = 255;
    private static final double CEL_LARG = 110; // tamanho de cada casa
    private static final double CEL_ALT = 130;

    private String personagemSelecionado = ""; // nome do personagem atual

    private GerenciadorDeTelas gerenciadorDeTelas;
    
    // =================================================================

    @FXML
    private StackPane raiz;

    @FXML
    private Pane palco;

    @FXML
    private GridPane grade;

    @FXML
    private Pane camadaJogo;

    @FXML
    private Button btnVoltar;

    @FXML
    private StackPane camadaResultado;

    @FXML
    private ImageView imagemVitoria;

    @FXML
    private ImageView imagemDerrota;

    @FXML
    private Label lblCafe;

    // listas dos visuais
    private final Map<ImageView, Timeline> animacoes = new HashMap<>();
    private final Map<Tiro, ImageView> tirosVisuais = new HashMap<>();

    private final Map<Zumbi, ImageView> zumbisVisuais = new HashMap<>();
    private final Map<Zumbi, Timeline> animacoesCaminhada = new HashMap<>();
    private final Map<Zumbi, Timeline> animacoesAtaque = new HashMap<>();

    // =================================================================

    private String obterCaminhoCaminhada(Zumbi zumbi) {
        String tipo = zumbi.getClass().getSimpleName();

        switch (tipo) {
            case "CalouroBlindado":
                return "/images/zumbis/caminhada/calouroBlindadoCaminhada.png";

            case "CalouroProtegido":
                return "/images/zumbis/caminhada/calouroProtegidoCaminhada.png";

            case "Doutorando":
                return "/images/zumbis/caminhada/doutorandoCaminhada.png";

            case "Formando":
                return "/images/zumbis/caminhada/formandoCaminhada.png";

            case "Trote":
                return "/images/zumbis/caminhada/troteCaminhada.png";

            case "VibeCoder":
                return "/images/zumbis/caminhada/vibeCoderCaminhada.png";

            case "Viciado":
                return "/images/zumbis/caminhada/viciadoCaminhada.png";

            default:
                return "/images/zumbis/caminhada/calouroCaminhada.png";
        }
    }
   
    private String obterCaminhoAtaque(Zumbi zumbi) {
        String tipo = zumbi.getClass().getSimpleName();

        switch (tipo) {
            case "CalouroBlindado":
                return "/images/zumbis/ataque/calouroBlindadoAtaque.png";

            case "CalouroProtegido":
                return "/images/zumbis/ataque/calouroProtegidoAtaque.png";

            case "Doutorando":
                return "/images/zumbis/ataque/doutorandoAtaque.png";

            case "Formando":
                return "/images/zumbis/ataque/formandoAtaque.png";

            case "Trote":
                return "/images/zumbis/ataque/troteAtaque.png";

            case "VibeCoder":
                return "/images/zumbis/ataque/vibeCoderAtaque.png";

            case "Viciado":
                return "/images/zumbis/ataque/viciadoAtaque.png";

            default:
                return "/images/zumbis/ataque/calouroAtaque.png";
        }
    }

    // =================================================================

    // remove do tabuleiro os professores/cafeteiras mortos e seus visuais
    private void removerProfessoresOuCafeteirasMortas() {

        for (int linha = 0; linha < LINHAS; linha++) {
            for (int coluna = 0; coluna < COLUNAS; coluna++) {

                StackPane celula = celulas[linha][coluna];
                Entidade ocupante = gerenciadorDeJogo.getOcupante(linha, coluna);

                if (ocupante != null && !ocupante.estaVivo()) {
                    gerenciadorDeJogo.removerDoPiso(linha, coluna);
                    ocupante = null;
                }

                if (ocupante == null && !celula.getChildren().isEmpty()) {
                    for (javafx.scene.Node filho : new ArrayList<>(celula.getChildren())) {

                        if (filho instanceof ImageView) {

                            Timeline animacao = animacoes.remove((ImageView) filho);

                            if (animacao != null) {
                                animacao.stop();
                            }
                        }

                        celula.getChildren().remove(filho);
                    }
                }
            }
        }
    }

    // =================================================================

    private void atualizarVisuaisDosTiros() {

        List<Tiro> tirosAtivos = gerenciadorDeJogo.getTiros();

        // remove tiros que já desapareceram do modelo.
        tirosVisuais.entrySet().removeIf(entrada -> {

            if (!tirosAtivos.contains(entrada.getKey())) {
                camadaJogo.getChildren().remove(entrada.getValue());
                return true;
            }

            return false;
        });

        for (Tiro tiro : tirosAtivos) {

            ImageView imagem = tirosVisuais.get(tiro);

            if (imagem == null) {

                String caminho = tiro.getSprite();
                URL recurso = getClass().getResource(caminho);

                if (recurso == null) {
                    System.err.println("Imagem de tiro não encontrada: " + caminho);
                    continue;
                }

                imagem = new ImageView(new Image(recurso.toExternalForm()));
                imagem.setFitWidth(40);
                imagem.setFitHeight(40);
                imagem.setPreserveRatio(true);
                imagem.setMouseTransparent(true);

                tirosVisuais.put(tiro, imagem);
                camadaJogo.getChildren().add(imagem);
            }

            imagem.setLayoutX(GRADE_X + tiro.getX() * CEL_LARG);
            imagem.setLayoutY(GRADE_Y + tiro.getY() * CEL_ALT + CEL_ALT * 0.4);
        }
    }

    private void atualizarVisuaisDosZumbis() {

        List<Zumbi> zumbisAtivos = gerenciadorDeJogo.getZumbis();

        // Remove zumbis que saíram do jogo e interrompe as duas animações.
        zumbisVisuais.entrySet().removeIf(entrada -> {
            Zumbi zumbi = entrada.getKey();

            if (!zumbisAtivos.contains(zumbi)) {
                camadaJogo.getChildren().remove(entrada.getValue());

                Timeline caminhada = animacoesCaminhada.remove(zumbi);
                if (caminhada != null) {
                    caminhada.stop();
                }

                Timeline ataque = animacoesAtaque.remove(zumbi);
                if (ataque != null) {
                    ataque.stop();
                }

                return true;
            }

            return false;
        });

        for (Zumbi zumbi : zumbisAtivos) {
            ImageView imagem = zumbisVisuais.get(zumbi);

            // cria o visual do zumbi
            if (imagem == null) {

                String caminho = obterCaminhoCaminhada(zumbi);
                URL recurso = getClass().getResource(caminho);

                if (recurso == null) {
                    System.err.println("Spritesheet de caminhada não encontrado: " + caminho);
                    continue;
                }

                Image spritesheet = new Image(recurso.toExternalForm());
                Image[] quadros = recortarSprites(spritesheet);

                if (quadros.length == 0) {
                    System.err.println("Nenhum quadro encontrado: " + caminho);
                    continue;
                }

                imagem = new ImageView(quadros[0]);
                imagem.setFitWidth(140);
                imagem.setFitHeight(200);
                imagem.setTranslateY(-100);
                imagem.setPreserveRatio(true);
                imagem.setSmooth(false);
                imagem.setMouseTransparent(true);

                // guarda os quadros para restaurar a caminhada após o ataque
                imagem.getProperties().put("quadrosCaminhada", quadros);
                imagem.getProperties().put("quadroCaminhada", 0);
                imagem.getProperties().put("mostrandoAtaque", false);

                zumbisVisuais.put(zumbi, imagem);
                camadaJogo.getChildren().add(imagem);

                final ImageView imagemAnimada = imagem;

                    Timeline animacao = new Timeline(
                        new KeyFrame(Duration.millis(120), evento -> {

                            int atual = (Integer) imagemAnimada.getProperties().getOrDefault("quadroCaminhada", 0);
                            int proximo = (atual + 1) % quadros.length;

                            imagemAnimada.setImage(quadros[proximo]);
                            imagemAnimada.getProperties().put("quadroCaminhada", proximo);
                        })
                    );

                    animacao.setCycleCount(Timeline.INDEFINITE);
                    animacoesCaminhada.put(zumbi, animacao);
                    animacao.playFromStart();
                }

                Timeline animacao = animacoesCaminhada.get(zumbi);

                boolean atacando = zumbi.estaAtacando();
                boolean mostrandoAtaque = Boolean.TRUE.equals(imagem.getProperties().get("mostrandoAtaque"));

                // Zumbi começou a atacar (carrega o spritesheet e troca a animação)
                if (atacando && !mostrandoAtaque) {

                    String caminhoAtaque = obterCaminhoAtaque(zumbi);
                    URL recursoAtaque = getClass().getResource(caminhoAtaque);

                    if (recursoAtaque == null) {
                        System.err.println("Spritesheet de ataque não encontrado: " + caminhoAtaque);
                    } else {
                        Image spritesheetAtaque = new Image(recursoAtaque.toExternalForm());
                        Image[] quadrosAtaque = recortarSprites(spritesheetAtaque);

                        if (quadrosAtaque.length > 0) {
                            if (animacao != null) {
                                animacao.stop();
                            }

                            imagem.setImage(quadrosAtaque[0]);
                            imagem.getProperties().put("mostrandoAtaque", true);
                            imagem.getProperties().put("quadroAtaque", 0);

                            // Referência final para ser usada dentro da expressão lambda
                            final ImageView imagemAnimadaAtaque = imagem;

                            Timeline animacaoAtaque = new Timeline(
                                new KeyFrame(Duration.millis(120), evento -> {
                                    int atual = (Integer) imagemAnimadaAtaque.getProperties()
                                        .getOrDefault("quadroAtaque", 0);
                                    int proximo = (atual + 1) % quadrosAtaque.length;

                                    imagemAnimadaAtaque.setImage(quadrosAtaque[proximo]);
                                    imagemAnimadaAtaque.getProperties().put("quadroAtaque", proximo);
                                })
                            );

                            animacaoAtaque.setCycleCount(Timeline.INDEFINITE);

                            Timeline anterior = animacoesAtaque.put(zumbi, animacaoAtaque);
                            if (anterior != null) {
                                anterior.stop();
                            }

                            animacaoAtaque.playFromStart();
                        } else {
                            System.err.println("Nenhum quadro de ataque encontrado: " + caminhoAtaque);
                        }
                    }
                }

                // Parou de atacar (restaura a caminhada) 
                else if (!atacando && mostrandoAtaque) {

                    Timeline ataque = animacoesAtaque.remove(zumbi);

                    if (ataque != null) {
                        ataque.stop();
                    }

                    Image[] quadros = (Image[]) imagem.getProperties()
                        .get("quadrosCaminhada");

                    int quadroAtual = (Integer) imagem.getProperties()
                        .getOrDefault("quadroCaminhada", 0);

                    if (quadros != null && quadros.length > 0) {
                        quadroAtual = quadroAtual % quadros.length;
                        imagem.setImage(quadros[quadroAtual]);
                    }

                    imagem.getProperties().put("mostrandoAtaque", false);

                    if (animacao != null) {
                        animacao.playFromStart();
                    }
                }

            // Mantém o zumbi na posição correspondente ao modelo
            imagem.setLayoutX(GRADE_X + zumbi.getPosicaoX() * CEL_LARG - 40);
            imagem.setLayoutY(GRADE_Y + zumbi.getLinha() * CEL_ALT + 15);
        }
    }
    
    // =================================================================

    private Image[] recortarSprites(Image imagem) {

        final int colunas = 3;
        final int linhas = 3;
        final int totalQuadros = 8;

        PixelReader leitor = imagem.getPixelReader();

        if (leitor == null) {
            throw new IllegalArgumentException("Não foi possível ler o spritesheet");
        }

        int larguraQuadro = (int) imagem.getWidth() / colunas;
        int alturaQuadro = (int) imagem.getHeight() / linhas;

        List<Image> quadros = new ArrayList<>();

        for (int i = 0; i < totalQuadros; i++) {
            int xInicial = (i % colunas) * larguraQuadro;
            int yInicial = (i / colunas) * alturaQuadro;

            int minX = larguraQuadro;
            int minY = alturaQuadro;
            int maxX = -1;
            int maxY = -1;

            for (int y = 0; y < alturaQuadro; y++) {
                for (int x = 0; x < larguraQuadro; x++) {
                    if (leitor.getColor(
                            xInicial + x, yInicial + y
                        ).getOpacity() > 0.05) {

                        minX = Math.min(minX, x);
                        minY = Math.min(minY, y);
                        maxX = Math.max(maxX, x);
                        maxY = Math.max(maxY, y);
                    }
                }
            }

            // Ignora quadros totalmente transparentes
            if (maxX < minX || maxY < minY) {
                continue;
            }

            int margem = 3;

            minX = Math.max(0, minX - margem);
            minY = Math.max(0, minY - margem);
            maxX = Math.min(larguraQuadro - 1, maxX + margem);
            maxY = Math.min(alturaQuadro - 1, maxY + margem);

            quadros.add(new WritableImage(
                leitor,
                xInicial + minX,
                yInicial + minY,
                maxX - minX + 1,
                maxY - minY + 1
            ));
        }

        return quadros.toArray(new Image[0]);
    }

    private void adicionarSpriteAnimado(StackPane celula, String caminhoSpritesheet) {

        URL recurso = getClass().getResource(caminhoSpritesheet);

        if (recurso == null) {
            System.err.println("Spritesheet de professor não encontrado: " + caminhoSpritesheet);
            return;
        }

        Image spritesheet = new Image(recurso.toExternalForm());
        Image[] quadros = recortarSprites(spritesheet);

        if (quadros.length == 0) {
            System.err.println("Nenhum quadro encontrado no spritesheet: " + caminhoSpritesheet);
            return;
        }

        ImageView imagem = new ImageView(quadros[0]);
        imagem.setPreserveRatio(true);
        imagem.setFitWidth(140);
        imagem.setFitHeight(200);
        imagem.setSmooth(false);

        celula.getChildren().add(imagem);

        Timeline animacao = new Timeline(new KeyFrame(Duration.millis(100), event -> {

                int proximo = (Integer) imagem.getProperties().getOrDefault("quadroAtual", 0);

                proximo = (proximo + 1) % quadros.length;

                imagem.setImage(quadros[proximo]);
                imagem.getProperties().put("quadroAtual", proximo);
            })
        );

        animacao.setCycleCount(Timeline.INDEFINITE);
        animacao.play();

        animacoes.put(imagem, animacao);
    }

    // =================================================================

    private void adicionarCafeteiraVisual(StackPane celula) {

        URL recursoVazia = getClass().getResource("/images/professores/cafeteiraVazia.png");
        URL recursoCheia = getClass().getResource("/images/professores/cafeteiraCheia.png");

        if (recursoVazia == null || recursoCheia == null) {

            System.err.println("Não foi possível encontrar as imagens da cafeteira.");
            return;
        }

        Image cafeteiraVazia = new Image(recursoVazia.toExternalForm());
        Image cafeteiraCheia = new Image(recursoCheia.toExternalForm());

        ImageView visual = new ImageView(cafeteiraVazia);
        visual.setFitWidth(90);
        visual.setFitHeight(110);
        visual.setPreserveRatio(true);
        visual.setSmooth(false);

        celula.getChildren().add(visual);

        visual.getProperties().put("cheia", false);

        Timeline ciclo = new Timeline(

            new KeyFrame(Duration.seconds(5), evento -> {
                visual.setImage(cafeteiraCheia);
                visual.getProperties().put("cheia", true);
            }),

            new KeyFrame(Duration.seconds(15), evento -> {
                visual.setImage(cafeteiraVazia);
                visual.getProperties().put("cheia", false);
            })
        );

        ciclo.setCycleCount(Timeline.INDEFINITE);
        ciclo.playFromStart();

        animacoes.put(visual, ciclo);

        visual.setOnMouseClicked(evento -> {

            boolean cheia = (boolean) visual.getProperties().get("cheia");

            if (!cheia) {
                evento.consume();
                return;
            }

            // Coleta do café.
            gerenciadorDeJogo.adicionarCafe(50);

            // Reinicia a produção.
            visual.setImage(cafeteiraVazia);
            visual.getProperties().put("cheia", false);
            ciclo.playFromStart();

            atualizarQuantidadeDeCafe();

            // Impede que o clique chegue ao evento da célula.
            evento.consume();
        });
    }

    private void atualizarQuantidadeDeCafe() {

        if (lblCafe != null && gerenciadorDeJogo != null) {
            lblCafe.setText("Café: " + gerenciadorDeJogo.getCafe());
        }
    }

    private void iniciarPartida() {

        Fase faseSelecionada = Fase.criarFaseSelecionada();

        Tabuleiro tabuleiro = new Tabuleiro(LINHAS, COLUNAS);

        gerenciadorDeJogo = new GerenciadorDeJogo(faseSelecionada, tabuleiro);

        gerenciadorDeJogo.iniciarJogo();
        atualizarQuantidadeDeCafe();

        timer = new AnimationTimer() {
            private long ultimo = 0;

            @Override
            public void handle(long agora) {

                if (ultimo == 0) {
                    ultimo = agora;
                    return;
                }

                double delta = (agora - ultimo) / 1_000_000_000.0;
                ultimo = agora;
                delta = Math.min(delta, 0.1);

                gerenciadorDeJogo.atualizar(delta);
                atualizarQuantidadeDeCafe();
                removerProfessoresOuCafeteirasMortas();
                atualizarVisuaisDosTiros();
                atualizarVisuaisDosZumbis();
                mostrarResultado();
            }
        };

        timer.start();
    }

    // =================================================================

    @Override
    public void setGerenciadorDeTelas(GerenciadorDeTelas gerenciadorDeTelas) {
        this.gerenciadorDeTelas = gerenciadorDeTelas;
    }

    // Placeholder da view: quais casas estao ocupadas (o modelo real fica no Tabuleiro)
    private final StackPane[][] celulas = new StackPane[LINHAS][COLUNAS];

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

    public void initialize(URL url, ResourceBundle rb) {

        montarGrade();
        ajustarEscala();
        configurarSomDeHover(btnVoltar);
        configurarClick(btnVoltar);
        iniciarPartida(); 
    }

    private void montarGrade() {

        grade.setLayoutX(GRADE_X);
        grade.setLayoutY(GRADE_Y);

        for (int l = 0; l < LINHAS; l++) {
            for (int c = 0; c < COLUNAS; c++) {

                StackPane celula = new StackPane();

                celula.setPrefSize(CEL_LARG, CEL_ALT);
                celula.setMinSize(CEL_LARG, CEL_ALT);
                celula.setMaxSize(CEL_LARG, CEL_ALT);
                celula.getStyleClass().add("celula");

                final int linha = l, coluna = c;
                celula.setOnMouseClicked(e -> aoClicarNaCelula(linha, coluna));

                celulas[l][c] = celula;
                grade.add(celula, c, l);
            }
        }
    }

    private void aoClicarNaCelula(int linha, int coluna) {

        if (gerenciadorDeJogo.terminou()) {
            return;
        }

        if (personagemSelecionado.isEmpty()) {
            System.out.println("Selecione um personagem primeiro.");
            return;
        }

        StackPane celula = celulas[linha][coluna];

        if (!celula.getChildren().isEmpty()) {
            System.out.println("Essa casa já está ocupada.");
            return;
        }

        boolean instalou = false;
        String nome = personagemSelecionado;

        switch (personagemSelecionado) {
            case "DANIEL":
                instalou = gerenciadorDeJogo.adicionarProfessor(new Daniel(linha, coluna), linha, coluna);
                break;

            case "FABRICIO":
                instalou = gerenciadorDeJogo.adicionarProfessor(new Fabricio(linha, coluna), linha, coluna);
                break;

            case "GLAUCIA":
                instalou = gerenciadorDeJogo.adicionarProfessor(new Glaucia(linha, coluna), linha, coluna);
                break;

            case "LUCAS":
                instalou = gerenciadorDeJogo.adicionarProfessor(new Lucas(linha, coluna), linha, coluna);
                break;

            case "MARCUS":
                instalou = gerenciadorDeJogo.adicionarProfessor(new Marcus(linha, coluna), linha, coluna);
                break;

            case "NACIF":
                instalou = gerenciadorDeJogo.adicionarProfessor(new Nacif(linha, coluna), linha, coluna);
                break;

            case "PHILIPE":
                instalou = gerenciadorDeJogo.adicionarProfessor(new Philipe(linha, coluna), linha, coluna);
                break;

            case "THAIS":
                instalou = gerenciadorDeJogo.adicionarProfessor(new Thais(linha, coluna), linha, coluna);
                break;

            case "CAFETEIRA":
                instalou = gerenciadorDeJogo.adicionarCafeteira(
                    new Cafeteira(100, 
                        linha, 
                        coluna, 
                        60, 
                        5.0, 
                        1.0
                    ),

                    linha, coluna
                );

                break;

            default:
                System.out.println("Desconhecido.");
                return;
        }

        if (!instalou) {

            System.out.println("Não foi possível instalar. Verifique o café disponível.");
            return;
        }

        if (nome.equals("CAFETEIRA")) {
            adicionarCafeteiraVisual(celula);
        } else {
            String caminhoSpritesheet;

            switch (nome) {
                case "DANIEL":
                    caminhoSpritesheet = "/images/professores/daniel.png";
                    break;

                case "FABRICIO":
                    caminhoSpritesheet = "/images/professores/fabricio.png";
                    break;

                case "GLAUCIA":
                    caminhoSpritesheet = "/images/professores/glaucia.png";
                    break;

                case "LUCAS":
                    caminhoSpritesheet = "/images/professores/lucas.png";
                    break;

                case "MARCUS":
                    caminhoSpritesheet = "/images/professores/marcus.png";
                    break;

                case "NACIF":
                    caminhoSpritesheet = "/images/professores/nacif.png";
                    break;

                case "PHILIPE":
                    caminhoSpritesheet = "/images/professores/philipe.png";
                    break;

                case "THAIS":
                    caminhoSpritesheet = "/images/professores/thais.png";
                    break;

                default:
                    System.out.println("Spritesheet não configurado: " + nome);
                    return;
            }

            adicionarSpriteAnimado(celula, caminhoSpritesheet);
        }

        System.out.println("Casa clicada: linha " + linha + ", coluna " + coluna);

        atualizarQuantidadeDeCafe();
        personagemSelecionado = "";
    }

    // Escala o palco (1672x941) para caber na janela, mantendo a grade alinhada.
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
    }

    @FXML
    private void voltar(ActionEvent event) {

        timer.stop(); 
        gerenciadorDeJogo.encerrar();
        gerenciadorDeTelas.voltarMenu();
    }

    private void mostrarResultado() {

        if (!gerenciadorDeJogo.terminou()) {
            return;
        }

        boolean venceu = gerenciadorDeJogo.isVitoria();

        imagemVitoria.setVisible(venceu);
        imagemDerrota.setVisible(!venceu);
        camadaResultado.setVisible(true);
    }

    @FXML
    private void jogarNovamente() {

        animacoes.values().forEach(Timeline::stop);
        animacoes.clear();

        camadaResultado.setVisible(false);
        imagemVitoria.setVisible(false);
        imagemDerrota.setVisible(false);

        tirosVisuais.values().forEach(imagem -> camadaJogo.getChildren().remove(imagem));
        tirosVisuais.clear();

        // Limpa todas as animações de caminhada antigas dos zumbis
        animacoesCaminhada.values().forEach(Timeline::stop);
        animacoesCaminhada.clear();

        // Limpa todas as animações de ataque antigas dos zumbis
        animacoesAtaque.values().forEach(Timeline::stop);
        animacoesAtaque.clear();

        // Remove os visuais antigos dos zumbis
        zumbisVisuais.values().forEach(imagem -> camadaJogo.getChildren().remove(imagem));
        zumbisVisuais.clear();

        gerenciadorDeJogo.reiniciarJogo();
        personagemSelecionado = "";

        for (StackPane[] linha : celulas) {

            for (StackPane celula : linha) {
                celula.getChildren().clear();
            }
        }

        atualizarQuantidadeDeCafe();
    }

    @FXML
    private void selecionarCafeteira() {
        personagemSelecionado = "CAFETEIRA";
    }

    @FXML
    private void selecionarDaniel() {
        personagemSelecionado = "DANIEL";
    }

    @FXML
    private void selecionarFabricio() {
        personagemSelecionado = "FABRICIO";
    }

    @FXML
    private void selecionarGlaucia() {
        personagemSelecionado = "GLAUCIA";
    }

    @FXML
    private void selecionarLucas() {
        personagemSelecionado = "LUCAS";
    }

    @FXML
    private void selecionarMarcus() {
        personagemSelecionado = "MARCUS";
    }

    @FXML
    private void selecionarNacif() {
        personagemSelecionado = "NACIF";
    }

    @FXML
    private void selecionarPhilipe() {
        personagemSelecionado = "PHILIPE";
    }

    @FXML
    private void selecionarThais() {
        personagemSelecionado = "THAIS";
    }
}