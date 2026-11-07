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

import com.poo2026_2.model.visuais.visuaisTiros;
import com.poo2026_2.model.visuais.visuaisProfessores;
import com.poo2026_2.model.visuais.visuaisZumbis;
import com.poo2026_2.model.visuais.visuaisBotoes;
import com.poo2026_2.model.visuais.visuaisGeral;
import com.poo2026_2.model.visuais.visuaisGrades;
import com.poo2026_2.model.visuais.visuaisProva;

import javafx.scene.layout.VBox;
import javafx.geometry.Pos;

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

    // =================================================================
    
    @FXML
    private Pane palco;
    
    @FXML
    private GridPane grade;
    
    @FXML
    private Pane camadaJogo;

    @FXML
    private Button btnCafeteira;

    @FXML
    private Button btnDaniel;

    @FXML
    private Button btnFabricio;

    @FXML
    private Button btnGlaucia;

    @FXML
    private Button btnLucas;

    @FXML
    private Button btnMarcus;

    @FXML
    private Button btnNacif;

    @FXML
    private Button btnPhilipe;

    @FXML
    private Button btnThais;

    @FXML
    private StackPane raiz;

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

    // =================================================================

    private final visuaisTiros visualTiros = new visuaisTiros();
    private final visuaisProfessores visualProfessores = new visuaisProfessores();
    private final visuaisZumbis visualZumbis = new visuaisZumbis();
    private final visuaisBotoes visualBotoes = new visuaisBotoes();
    private final visuaisGeral visualGeral = new visuaisGeral();
    private final visuaisGrades visualGrades = new visuaisGrades(LINHAS, COLUNAS, CEL_LARG, CEL_ALT, GRADE_X, GRADE_Y);
    private final visuaisProva visualProva = new visuaisProva();

    // =================================================================

    @Override
    public void setGerenciadorDeTelas(GerenciadorDeTelas gerenciadorDeTelas) {
        this.gerenciadorDeTelas = gerenciadorDeTelas;
    }

    private StackPane[][] celulas;

    // =================================================================

    private String personagemSelecionado = ""; // nome do personagem (professor) atual
    private static List<String> professoresSelecionados = new ArrayList<>(); // lista dos professores selecionados7

    private GerenciadorDeTelas gerenciadorDeTelas;
    
    // listas dos visuais
    private final Map<ImageView, Timeline> animacoes = new HashMap<>();
    private final Map<Tiro, ImageView> tirosVisuais = new HashMap<>();

    private final Map<Zumbi, ImageView> zumbisVisuais = new HashMap<>();
    private final Map<Zumbi, Timeline> animacoesCaminhada = new HashMap<>();
    private final Map<Zumbi, Timeline> animacoesAtaque = new HashMap<>();

    private final Map<String, Timeline> recargas = new HashMap<>();
    private final Map<String, Button> botoesPersonagens = new HashMap<>();

    public static void setProfessoresSelecionados(java.util.Set<String> selecionados) {
        professoresSelecionados = new ArrayList<>(selecionados);
    }

    // =================================================================

    private void aoClicarNaCelula(int linha, int coluna) {

        if (gerenciadorDeJogo.terminou()) {
            return;
        }

        if (personagemSelecionado.isEmpty()) {
            System.out.println("Selecione um personagem primeiro.");
            return;
        }

        if (!professoresSelecionados.contains(personagemSelecionado)) {
            System.out.println("Esse personagem não foi selecionado para a partida.");
            personagemSelecionado = "";
            return;
        }

        StackPane celula = celulas[linha][coluna];

        if (!celula.getChildren().isEmpty()) {
            System.out.println("Essa casa já está ocupada.");
            return;
        }

        boolean instalou = false;
        String nome = personagemSelecionado;
        double segundos = 0;

        switch (personagemSelecionado) {

            case "DANIEL": {
                Daniel daniel = new Daniel(linha, coluna);
                segundos = daniel.getTempoDeRecarga();
                instalou = gerenciadorDeJogo.adicionarProfessor(daniel, linha, coluna);
                break;
            }

            case "FABRICIO": {
                Fabricio fabricio = new Fabricio(linha, coluna);
                segundos = fabricio.getTempoDeRecarga();
                instalou = gerenciadorDeJogo.adicionarProfessor(fabricio, linha, coluna);
                break;
            }

            case "GLAUCIA": {
                Glaucia glaucia = new Glaucia(linha, coluna);
                segundos = glaucia.getTempoDeRecarga();
                instalou = gerenciadorDeJogo.adicionarProfessor(glaucia, linha, coluna);
                break;
            }

            case "LUCAS": {
                Lucas lucas = new Lucas(linha, coluna);
                segundos = lucas.getTempoDeRecarga();
                instalou = gerenciadorDeJogo.adicionarProfessor(lucas, linha, coluna);
                break;
            }

            case "MARCUS": {
                Marcus marcus = new Marcus(linha, coluna);
                segundos = marcus.getTempoDeRecarga();
                instalou = gerenciadorDeJogo.adicionarProfessor(marcus, linha, coluna);
                break;
            }

            case "NACIF": {
                Nacif nacif = new Nacif(linha, coluna);
                segundos = nacif.getTempoDeRecarga();
                instalou = gerenciadorDeJogo.adicionarProfessor(nacif, linha, coluna);
                break;
            }

            case "PHILIPE": {
                Philipe philipe = new Philipe(linha, coluna);
                segundos = philipe.getTempoDeRecarga();
                instalou = gerenciadorDeJogo.adicionarProfessor(philipe, linha, coluna);
                break;
            }

            case "THAIS": {
                Thais thais = new Thais(linha, coluna);
                segundos = thais.getTempoDeRecarga();
                instalou = gerenciadorDeJogo.adicionarProfessor(thais, linha, coluna);
                break;
            }

            case "CAFETEIRA": {
                Cafeteira cafeteira = new Cafeteira(100, linha, coluna, 60, 5.0);
                segundos = cafeteira.getTempoDeRecarga();
                instalou = gerenciadorDeJogo.adicionarCafeteira(cafeteira, linha, coluna);
                break;
            }

            default:
                System.out.println("Personagem desconhecido.");
                return;
        }

        if (!instalou) {
            System.out.println("Não foi possível instalar. Verifique o café disponível.");
            return;
        }

        visualBotoes.iniciarTempoDeRecarga(
            personagemSelecionado,
            segundos,
            botoesPersonagens,
            recargas
        );

        if (nome.equals("CAFETEIRA")) {

            visualProfessores.adicionarCafeteiraVisual(
                celula,
                gerenciadorDeJogo,
                animacoes,
                this::atualizarQuantidadeDeCafe
            );

        } else {

            String caminhoSpritesheet;

            switch (nome) {
                case "DANIEL": caminhoSpritesheet = "/images/professores/daniel.png"; break;
                case "FABRICIO": caminhoSpritesheet = "/images/professores/fabricio.png"; break;
                case "GLAUCIA": caminhoSpritesheet = "/images/professores/glaucia.png"; break;
                case "LUCAS": caminhoSpritesheet = "/images/professores/lucas.png"; break;
                case "MARCUS": caminhoSpritesheet = "/images/professores/marcus.png"; break;
                case "NACIF": caminhoSpritesheet = "/images/professores/nacif.png"; break;
                case "PHILIPE": caminhoSpritesheet = "/images/professores/philipe.png"; break;
                case "THAIS": caminhoSpritesheet = "/images/professores/thais.png"; break;

                default:
                    System.out.println("Spritesheet não configurado: " + nome);
                    return;
            }

            visualGeral.adicionarSpriteAnimado(
                celula,
                caminhoSpritesheet,
                3,
                3,
                8,
                animacoes
            );
        }

        System.out.println("Casa clicada: linha " + linha + ", coluna " + coluna);

        atualizarQuantidadeDeCafe();
        personagemSelecionado = "";
    }

    // =================================================================

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

        // Criando uma prova para cada linha
        visualProva.armarProva(
            camadaJogo,
            LINHAS,
            COLUNAS,
            GRADE_X,
            GRADE_Y,
            CEL_LARG,
            CEL_ALT
        );

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

                visualProva.moveProva(delta, gerenciadorDeJogo);
                gerenciadorDeJogo.atualizar(delta);
                atualizarQuantidadeDeCafe();
                visualProfessores.removerProfessoresOuCafeteirasMortas(COLUNAS, LINHAS, celulas, gerenciadorDeJogo, animacoes);
                visualTiros.atualizarVisuaisDosTiros(CEL_LARG, CEL_ALT, GRADE_X, GRADE_Y, camadaJogo, gerenciadorDeJogo, tirosVisuais);
                visualZumbis.atualizarVisuaisDosZumbis(camadaJogo, gerenciadorDeJogo, zumbisVisuais, animacoesCaminhada, animacoesAtaque, GRADE_X, GRADE_Y, CEL_LARG, CEL_ALT);
                mostrarResultado();
            }
        };

        timer.start();
    }
    
    // INICIALIZAR
    // =================================================================

    public void initialize(URL url, ResourceBundle rb) {
        
        visualBotoes.configurarBotaoPersonagem(
            btnCafeteira,
            "Cafeteira",
            "/images/interface/cafeteira.png",
            new Cafeteira(100, 0, 0, 50, 5.0).getCustoCafe()
        );

        visualBotoes.configurarBotaoPersonagem(
            btnDaniel, "Daniel",
            "/images/interface/daniel.png",
            new Daniel(0, 0).getCustoCafe()
        );
        
        visualBotoes.configurarBotaoPersonagem(
            btnFabricio, "Fabrício",
            "/images/interface/fabricio.png",
            new Fabricio(0, 0).getCustoCafe()
        );

        visualBotoes.configurarBotaoPersonagem(
            btnGlaucia, "Gláucia",
            "/images/interface/glaucia.png",
            new Glaucia(0, 0).getCustoCafe()
        );

        visualBotoes.configurarBotaoPersonagem(
            btnLucas, "Lucas",
            "/images/interface/lucas.png",
            new Lucas(0, 0).getCustoCafe()
        );

        visualBotoes.configurarBotaoPersonagem(
            btnMarcus, "Marcus",
            "/images/interface/marcus.png",
            new Marcus(0, 0).getCustoCafe()
        );

        visualBotoes.configurarBotaoPersonagem(
            btnNacif, "Nacif",
            "/images/interface/nacif.png",
            new Nacif(0, 0).getCustoCafe()
        );

        visualBotoes.configurarBotaoPersonagem(
            btnPhilipe, "Philipe",
            "/images/interface/philipe.png",
            new Philipe(0, 0).getCustoCafe()
        );
        
        visualBotoes.configurarBotaoPersonagem(
            btnThais, "Thaís",
            "/images/interface/thais.png",
            new Thais(0, 0).getCustoCafe()
        );
        
        botoesPersonagens.put("CAFETEIRA", btnCafeteira);
        botoesPersonagens.put("DANIEL", btnDaniel);
        botoesPersonagens.put("FABRICIO", btnFabricio);
        botoesPersonagens.put("GLAUCIA", btnGlaucia);
        botoesPersonagens.put("LUCAS", btnLucas);
        botoesPersonagens.put("MARCUS", btnMarcus);
        botoesPersonagens.put("NACIF", btnNacif);
        botoesPersonagens.put("PHILIPE", btnPhilipe);
        botoesPersonagens.put("THAIS", btnThais);

        celulas = visualGrades.getCelulas();

        visualBotoes.atualizarBotoesProfessores(professoresSelecionados, btnCafeteira, btnDaniel, btnFabricio, btnGlaucia, btnLucas, btnMarcus, btnNacif, btnPhilipe, btnThais);     
        visualGrades.montarGrade(grade, this::aoClicarNaCelula);
        visualGrades.ajustarEscala(raiz, palco, LARGURA_PALCO, ALTURA_PALCO);
        visualBotoes.configurarSomDeHover(btnVoltar);
        visualBotoes.configurarClick(btnVoltar);
        iniciarPartida(); 
    }

    // =================================================================

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

        imagemVitoria.setManaged(venceu);
        imagemVitoria.setVisible(venceu);

        imagemDerrota.setManaged(!venceu);
        imagemDerrota.setVisible(!venceu);

        camadaResultado.setManaged(true);
        camadaResultado.setVisible(true);
    }

    @FXML
    private void jogarNovamente() {

        // Interrompe as animações dos professores e cafeteiras
        animacoes.values().forEach(Timeline::stop);
        animacoes.clear();

        camadaResultado.setVisible(false);
        imagemVitoria.setVisible(false);
        imagemDerrota.setVisible(false);

        // Remove os tiros antigos
        tirosVisuais.values().forEach(imagem -> camadaJogo.getChildren().remove(imagem));
        tirosVisuais.clear();

        // Interrompe as animações de caminhada
        animacoesCaminhada.values().forEach(Timeline::stop);
        animacoesCaminhada.clear();

        // Interrompe as animações de ataque
        animacoesAtaque.values().forEach(Timeline::stop);
        animacoesAtaque.clear();

        // Remove os visuais antigos dos zumbis
        zumbisVisuais.values().forEach(imagem -> camadaJogo.getChildren().remove(imagem));
        zumbisVisuais.clear();

        // Interrompe as recargas dos botões
        recargas.values().forEach(Timeline::stop);
        recargas.clear();

        for (Button botao : botoesPersonagens.values()) {
            botao.setDisable(false);
        }

        gerenciadorDeJogo.reiniciarJogo();
        personagemSelecionado = "";

        // Reposiciona e rearma todas as provas
        visualProva.armarProva(
            camadaJogo,
            LINHAS,
            COLUNAS,
            GRADE_X,
            GRADE_Y,
            CEL_LARG,
            CEL_ALT
        );

        // Limpa as células
        for (StackPane[] linha : celulas) {
            for (StackPane celula : linha) {
                celula.getChildren().clear();
            }
        }

        atualizarQuantidadeDeCafe();
    }

    // =================================================================

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