/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.poo2026_2.controller;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import com.poo2026_2.audio.AudioManager;

import com.poo2026_2.model.tiros.tiro;

import com.poo2026_2.model.zumbis.Zumbi;
import com.poo2026_2.view.zumbis.ZumbiView;

import com.poo2026_2.model.Cafe;

// importando os professores (incluindo a cafeteira)
import com.poo2026_2.model.professores.Professor;
import com.poo2026_2.model.professores.Cafeteira;
import com.poo2026_2.model.professores.Daniel;
import com.poo2026_2.model.professores.Fabricio;
import com.poo2026_2.model.professores.Glaucia;
import com.poo2026_2.model.professores.Lucas;
import com.poo2026_2.model.professores.Marcus;
import com.poo2026_2.model.professores.Nacif;
import com.poo2026_2.model.professores.Philipe;
import com.poo2026_2.model.professores.Thais;

// importando os zumbis
import com.poo2026_2.model.zumbis.ZumbiCalouro;
import com.poo2026_2.model.zumbis.ZumbiCalouroBlindado;
import com.poo2026_2.model.zumbis.ZumbiCalouroProtegido;
import com.poo2026_2.model.zumbis.ZumbiDoutorando;
import com.poo2026_2.model.zumbis.ZumbiFormando;
import com.poo2026_2.model.zumbis.ZumbiTrote;
import com.poo2026_2.model.zumbis.ZumbiVibeCoder;
import com.poo2026_2.model.zumbis.ZumbiViciado;

import javafx.scene.image.ImageView;

import java.util.ArrayList;
import java.util.Random;

import com.poo2026_2.model.tiros.python; // tiro de python
import com.poo2026_2.view.professores.ProfessorView; // Visual dos professores
import com.poo2026_2.view.professores.CafeteiraView;// Visual da cafeteira

import javafx.animation.AnimationTimer;
import javafx.scene.Node;

/**
 * FXML Controller class
 *
 * @author thoma
 */
public class JogoController implements ControladorTela, Initializable {

    // deve alinhar com a imagem
    // Coordenadas em pixels da imagem original (1672 x 941).
    private static final double LARGURA_PALCO = 1672;
    private static final double ALTURA_PALCO  = 941;

    private int quantidadeDeZumbisPorOrda = 1; // Começa com 1 zumbi na primeira orda, depois aumenta a cada orda
    private int valorDeCafe = 200; // quantidade atual de cafe (começa com 200)
    private long agora; // momento atual
    private String professorSelecionado; // escolhendo o professor

    public void setProfessorSelecionado(String professorSelecionado) {
        this.professorSelecionado = professorSelecionado;
    }

    private static final int LINHAS   = 5;
    private static final int COLUNAS  = 12;
    private static final double GRADE_X  = 240;  // canto superior esquerdo da grade
    private static final double GRADE_Y  = 255;
    private static final double CEL_LARG = 110;  // tamanho de cada casa
    private static final double CEL_ALT  = 130;

    public double getCEL_LARG() { return this.CEL_LARG; }
    public double getCEL_ALT()  { return this.CEL_ALT; }

    public double getGRADE_X() { return this.GRADE_X; }
    public double getGRADE_Y() { return this.GRADE_Y; }

    private ArrayList<Professor> professores = new ArrayList<>(); // Lista para armazenar todos os professores

    private ArrayList<Zumbi> zumbis = new ArrayList<>(); // Lista para armazenar todos os zumbis
    private ArrayList<ZumbiView> visuaisDosZumbis = new ArrayList<>(); // Lista para armazenar os visuais dos zumbis

    private ArrayList<Cafeteira> cafeteiras = new ArrayList<>(); // Lista para armazenar todas as cafeteiras
    private ArrayList<CafeteiraView> cafeteirasVisual = new ArrayList<>(); // Lista do visual das cafeteiras
    private ArrayList<Cafe> cafes = new ArrayList<>(); // Lista para armazenar todas as cafes

    private ArrayList<tiro> tiros = new ArrayList<>(); // Lista para armazenar TODOS os tiros disparados pelos professores
    private ArrayList<ImageView> visuaisDosTiros = new ArrayList<>();

    private AnimationTimer timer = new AnimationTimer() {

        @Override
        public void handle(long agora) {

            // atualizando o agora;
            JogoController.this.agora = agora;

            // produção de café
            for (int c = 0; c < cafeteiras.size(); c++) {

                if (cafeteiras.get(c).podeProduzir(agora)) {

                    // cafe
                    Cafe cafe = new Cafe(
                        cafeteiras.get(c).getLinhaNoTabuleiroDaCafeteira(),
                        cafeteiras.get(c).getColunaNoTabuleiroDaCafeteira(),
                        cafeteiras.get(c),
                        agora
                    );

                    cafes.add(cafe);

                    // visual do cafe
                    cafeteirasVisual.get(c).mostrarCheia();
                }
            }

            // removendo café frio
            for (int c = 0; c < cafes.size(); c++) {

                // removendo a cafe frio
                if (!cafes.get(c).cafeEstaQuente(agora)) {

                    Cafeteira cafeteiraDoCafeFrio = cafes.get(c).getCafeteira();
                    int indiceCafeteira = cafeteiras.indexOf(cafeteiraDoCafeFrio);

                    if (indiceCafeteira >= 0 && indiceCafeteira < cafeteirasVisual.size()) {
                        cafeteirasVisual.get(indiceCafeteira).mostrarVazia();
                    }

                    System.out.println("Removendo café frio: índice " + c);

                    cafes.remove(c);
                    c--;
                }
            }

            // chama outra orda quando fica sem zumbis
            if (zumbis.size() == 0) {
                criarOrdaDeZumbis();
            }

            // Atualiza a posição de cada zumbi
            for (int i = 0; i < zumbis.size(); i++) {
                Zumbi zumbi = zumbis.get(i);

                // removendo zumbi que saí da tela
                if (zumbi.getPosicaoXDoZumbi() + 100 < 0) {

                    ZumbiView zumbiVisual = visuaisDosZumbis.get(i);

                    zumbiVisual.pararAnimacao();

                    camadaJogo.getChildren().remove(zumbiVisual);
                    visuaisDosZumbis.remove(i);
                    zumbis.remove(i);

                    i--;
                    continue;
                }

                ZumbiView zumbiVisual = visuaisDosZumbis.get(i);
                zumbiVisual.setLayoutX(zumbi.getPosicaoXDoZumbi());
                zumbiVisual.setLayoutY(zumbi.getPosicaoYDoZumbi());

                boolean professorBloqueando = false;
                boolean cafeteiraBloqueando = false;

                // Calcula a linha do zumbi pelo seu Y
                int linhaDoZumbi = (int) ((zumbi.getPosicaoYDoZumbi() - GRADE_Y) / CEL_ALT);

                Professor professorMaisProximo = null;

                for (int p = 0; p < professores.size(); p++) {

                    Professor professor = professores.get(p);

                    if (professor.getLinhaNoTabuleiroDoProfessor() == linhaDoZumbi
                            && professor.professorEstaVivo()) {

                        // Calcula o X do professor pela sua coluna
                        double xDoProfessor = GRADE_X
                                + professor.getColunaNoTabuleiroDoProfessor() * CEL_LARG;

                        if (xDoProfessor < zumbi.getPosicaoXDoZumbi()) {

                            // colidiu (professor com zumbi)
                            // a parte da divisão "(CEL_LARG / 5)" pode variar a depender do tamanho da imagem
                            if ((xDoProfessor <= zumbi.getPosicaoXDoZumbi()) && ((xDoProfessor + (CEL_LARG / 5)) >= zumbi.getPosicaoXDoZumbi())) {

                                if (professorMaisProximo == null) {
                                    professorMaisProximo = professor;
                                } else {
                                    double xDoProfessorMaisProximo = GRADE_X
                                            + professorMaisProximo.getColunaNoTabuleiroDoProfessor() * CEL_LARG;

                                    if (xDoProfessor > xDoProfessorMaisProximo) {
                                        professorMaisProximo = professor;
                                    }
                                }
                            }
                        }
                    }
                }

                // Removendo professores mortos
                for (int p = 0; p < professores.size(); p++) {
                    Professor professor = professores.get(p);

                    if (!professor.professorEstaVivo()) {
                        int linha = professor.getLinhaNoTabuleiroDoProfessor();
                        int coluna = professor.getColunaNoTabuleiroDoProfessor();

                        StackPane celula = celulas[linha][coluna];

                        for (int k = celula.getChildren().size() - 1; k >= 0; k--) {
                            Node filho = celula.getChildren().get(k);

                            if (filho instanceof ProfessorView) {
                                celula.getChildren().remove(k);
                                break;
                            }
                        }

                        professores.remove(p);
                        p--;
                    }
                }

                Cafeteira cafeteiraMaisProxima = null;

                for (int c = 0; c < cafeteiras.size(); c++) {

                    Cafeteira cafeteira = cafeteiras.get(c);

                    if (cafeteira.getLinhaNoTabuleiroDaCafeteira() == linhaDoZumbi
                            && cafeteira.cafeteiraEstaViva()) {

                        // Calcula o X da cafeteira pela sua coluna
                        double xDaCafeteira = GRADE_X + cafeteira.getColunaNoTabuleiroDaCafeteira() * CEL_LARG;
 
                        if (xDaCafeteira < zumbi.getPosicaoXDoZumbi()) {

                            // colidiu (cafeteira com zumbi)
                            if ((xDaCafeteira <= zumbi.getPosicaoXDoZumbi()) && ((xDaCafeteira + (CEL_LARG / 5)) >= zumbi.getPosicaoXDoZumbi())) {

                                if (cafeteiraMaisProxima == null) {
                                    cafeteiraMaisProxima = cafeteira;
                                } else {
                                    double xDaCafeteiraMaisProxima = GRADE_X
                                            + cafeteiraMaisProxima.getColunaNoTabuleiroDaCafeteira() * CEL_LARG;

                                    if (xDaCafeteira > xDaCafeteiraMaisProxima) {
                                        cafeteiraMaisProxima = cafeteira;
                                    }
                                }
                            }
                        }
                    }

                    // removendo a cafeteira morta
                    if (!cafeteira.cafeteiraEstaViva()) {

                        int linhaCafeteiraMorta = cafeteira.getLinhaNoTabuleiroDaCafeteira();
                        int colunaCafeteiraMorta = cafeteira.getColunaNoTabuleiroDaCafeteira();

                        StackPane cafeteiraParaRemover = celulas[linhaCafeteiraMorta][colunaCafeteiraMorta];

                        for (int k = 0; k < cafeteiraParaRemover.getChildren().size(); k++) {

                            Node filho = cafeteiraParaRemover.getChildren().get(k);

                            if (filho instanceof CafeteiraView) {
                                cafeteiraParaRemover.getChildren().remove(k);
                                break;
                            }
                        }

                        cafeteirasVisual.remove(c);
                        cafeteiras.remove(c);
                        c--;
                    }
                }

                if (professorMaisProximo != null && cafeteiraMaisProxima != null) {

                    double xDaCafeteiraMaisProxima = GRADE_X + cafeteiraMaisProxima.getColunaNoTabuleiroDaCafeteira() * CEL_LARG;
                    double xDoProfessorMaisProximo = GRADE_X + professorMaisProximo.getColunaNoTabuleiroDoProfessor() * CEL_LARG;

                    if (xDaCafeteiraMaisProxima > xDoProfessorMaisProximo) {
                        professorBloqueando = true;
                        zumbi.ataqueZumbi();

                        // zumbi morde o professor mais proximo dele
                        if (zumbi.zumbiEstaAtacando() && zumbi.podeMorder(agora)) {
                            professorMaisProximo.professorRecebeDano(zumbi.getDanoDoZumbi());
                        }
                    } else {
                        cafeteiraBloqueando = true;
                        zumbi.ataqueZumbi();

                        // zumbi morde a cafeteira mai proxima dele
                        if (zumbi.zumbiEstaAtacando() && zumbi.podeMorder(agora)) {
                            cafeteiraMaisProxima.cafeteiraRecebeDano(zumbi.getDanoDoZumbi());
                        }
                    }

                } else if (professorMaisProximo != null) {

                    professorBloqueando = true;
                    zumbi.ataqueZumbi();

                    // zumbi morde o professor mais proximo dele
                    if (zumbi.zumbiEstaAtacando() && zumbi.podeMorder(agora)) {
                        professorMaisProximo.professorRecebeDano(zumbi.getDanoDoZumbi());
                    }

                } else if (cafeteiraMaisProxima != null) {

                    cafeteiraBloqueando = true;
                    zumbi.ataqueZumbi();

                    // zumbi morde a cafeteira mai proxima dele
                    if (zumbi.zumbiEstaAtacando() && zumbi.podeMorder(agora)) {
                        cafeteiraMaisProxima.cafeteiraRecebeDano(zumbi.getDanoDoZumbi());
                    }
                }

                if (!professorBloqueando && !cafeteiraBloqueando) {
                    zumbi.pararAtaqueZumbi();
                }

                zumbi.moverZumbi();

                // jogador perde se 1 zumbi passar
                if (zumbi.getPosicaoXDoZumbi() + 100 < GRADE_X) {
                    System.out.println("JOGADOR PERDEU!");

                    timer.stop();
                    gerenciadorDeTelas.voltarMenu();
                }
            }

            // O professor ataca o zumbi
            for (Professor professor : professores) {

                Zumbi zumbiAlvoDoTiroDoProfessor = null;

                for (Zumbi zumbiAtual : zumbis) {

                    if (professor.getEsseProfessorDeveAtacarOsZumbis()) {
                        
                        if (professor.getLinhaNoTabuleiroDoProfessor() == (int) ((zumbiAtual.getPosicaoYDoZumbi() - GRADE_Y) / CEL_ALT)) {
    
                            if ((GRADE_X + professor.getColunaNoTabuleiroDoProfessor() * CEL_LARG) <= zumbiAtual.getPosicaoXDoZumbi()) {
    
                                if (zumbiAlvoDoTiroDoProfessor == null) {
    
                                    zumbiAlvoDoTiroDoProfessor = zumbiAtual;
    
                                } else {
    
                                    if (zumbiAtual.getPosicaoXDoZumbi()
                                            < zumbiAlvoDoTiroDoProfessor.getPosicaoXDoZumbi()) {
    
                                        // troca o alvo
                                        zumbiAlvoDoTiroDoProfessor = zumbiAtual;
                                    }
                                }
                            }
                        }
                    }
                }

                if (zumbiAlvoDoTiroDoProfessor != null && professor.podeAtirar(agora)) {
                    double posicaoXDoTiro = professor.getColunaNoTabuleiroDoProfessor()
                            * CEL_LARG + GRADE_X + CEL_LARG / 2;

                    double posicaoYDoTiro = professor.getLinhaNoTabuleiroDoProfessor()
                            * CEL_ALT + GRADE_Y + CEL_ALT / 2;

                    pedirParaOProfessorAtacarOZumbi(
                            professor,
                            zumbiAlvoDoTiroDoProfessor,
                            posicaoXDoTiro,
                            posicaoYDoTiro
                    );
                }
            }

            // Atualiza a posição de cada tiro
            for (int i = 0; i < tiros.size(); i++) {

                tiro tiroAtual = tiros.get(i);
                boolean tiroFoiRemovido = false;
                
                tiroAtual.moverTiro(); // movendo o tiro

                ImageView tiroVisual = visuaisDosTiros.get(i);

                tiroVisual.setLayoutX(tiroAtual.getPosicaoXDoTiro() - 15);
                tiroVisual.setLayoutY(tiroAtual.getPosicaoYDoTiro() - 15);

                for (int j = 0; j < zumbis.size(); j++) {

                    Zumbi zumbi = zumbis.get(j);

                    // verificar se tiroAtual colidiu com este zumbi
                    if (tiroAtual.tiroEstaColidindo(zumbi) && zumbi.zumbiEstaVivo()) {

                        // colisão aconteceu
                        zumbi.zumbiRecebeDano(tiroAtual.getDanoDoTiro());

                        // verificação de morte do zumbi (remove o zumbi morto)
                        if (zumbi.zumbiEstaVivo() == false) {

                            //Rectangle zumbiVisual = visuaisDosZumbis.get(j);

                            ZumbiView zumbiVisual = visuaisDosZumbis.get(j);
                            zumbiVisual.pararAnimacao();

                            camadaJogo.getChildren().remove(zumbiVisual);
                            visuaisDosZumbis.remove(j);

                            zumbis.remove(j);
                        }

                        // removendo o visual dos tiros
                        camadaJogo.getChildren().remove(tiroVisual);
                        visuaisDosTiros.remove(i);
                        
                        // ativando o efeito do tiro de python (philipe)
                        if (zumbi.zumbiEstaVivo() && tiroAtual instanceof python) {
                            ((python) tiroAtual).aplicarLentidaoNoZumbi(zumbi);
                        }

                        // removendo o tiro que acertou o zumbi
                        tiros.remove(i);
                        tiroFoiRemovido = true;

                        System.out.println("COLISÃO!");
                        System.out.println("Vida ATUAL do zumbi: " + zumbi.getVidaDoZumbi());

                        break;
                    }
                }

                if (tiroFoiRemovido == false && (tiroAtual.getPosicaoXDoTiro() > LARGURA_PALCO || tiroAtual.getPosicaoXDoTiro() < 0)) {

                    camadaJogo.getChildren().remove(tiroVisual);
                    visuaisDosTiros.remove(i);

                    // removendo o tiro que acertou o zumbi
                    tiros.remove(i);
                    tiroFoiRemovido = true;
                }

                if (tiroFoiRemovido) {
                    i--;
                    continue;
                }
            }
        }
    };

    // criando um novo tiro
    public void adicionarTiro(tiro novoTiro, double posicaoXDoTiro, double posicaoYDoTiro) {

        URL caminho = getClass().getResource(novoTiro.getCaminhoImagem());
        
        if (caminho == null) {
            System.err.println("Imagem não encontrada: " + novoTiro.getCaminhoImagem());
            return;
        }
        
        Image imagemTiro = new Image(caminho.toExternalForm());

        if (imagemTiro.isError()) {
            System.err.println("Erro ao carregar: " + novoTiro.getCaminhoImagem());

            if (imagemTiro.getException() != null) {
                imagemTiro.getException().printStackTrace();
            }

            return;
        }

        ImageView tiroVisual = new ImageView(imagemTiro);
        
        System.out.println("Caminho da imagem: " + caminho);
        System.out.println("Imagem carregada: " + !imagemTiro.isError());
        System.out.println("Dimensões: " + imagemTiro.getWidth() + " x " + imagemTiro.getHeight());
        
        tiros.add(novoTiro);

        tiroVisual.setFitWidth(30);
        tiroVisual.setFitHeight(30);
        tiroVisual.setPreserveRatio(true);

        tiroVisual.setLayoutX(posicaoXDoTiro - 15);
        tiroVisual.setLayoutY(posicaoYDoTiro - 15);

        camadaJogo.getChildren().add(tiroVisual);
        visuaisDosTiros.add(tiroVisual);
        tiroVisual.toFront();
    }

    // método para "pedir" ao professor para atacar o zumbi
    public void pedirParaOProfessorAtacarOZumbi(
            Professor professor,
            Zumbi zumbi,
            double posicaoXDoTiro,
            double posicaoYDoTiro) {

        tiro novoTiro = professor.professorAtacaZumbi(
                zumbi,
                posicaoXDoTiro,
                posicaoYDoTiro
        );

        if (novoTiro != null) {
            adicionarTiro(novoTiro, posicaoXDoTiro, posicaoYDoTiro); // Adiciona o tiro à lista de tiros e à camada de jogo
        }
    }

    // criando UM novo zumbi
    public void criarZumbi(double posicaoXDoZumbi, double posicaoYDoZumbi) {

        // sortenado o tipo de zumbi
        Random random = new Random();
        int sorteio = random.nextInt(8);

        // nome do arquivos do zumbi
        String nomeArquivo;

        Zumbi novoZumbi = null;

        switch (sorteio) {
            case 0:
                novoZumbi = new ZumbiCalouro();
                nomeArquivo = "calouro";
                break;

            case 1:
                novoZumbi = new ZumbiCalouroBlindado();
                nomeArquivo = "calouroBlindado";
                break;

            case 2:
                novoZumbi = new ZumbiCalouroProtegido();
                nomeArquivo = "calouroProtegido";
                break;

            case 3:
                novoZumbi = new ZumbiDoutorando();
                nomeArquivo = "doutorando";
                break;

            case 4:
                novoZumbi = new ZumbiFormando();
                nomeArquivo = "formando";
                break;

            case 5:
                novoZumbi = new ZumbiTrote();
                nomeArquivo = "trote";
                break;

            case 6:
                novoZumbi = new ZumbiVibeCoder();
                nomeArquivo = "vibeCoder";
                break;

            case 7:
                novoZumbi = new ZumbiViciado();
                nomeArquivo = "viciado";
                break;

            default:
                throw new IllegalArgumentException("Tipo de zumbi inválido: " + sorteio);
        }

        novoZumbi.criarZumbi(posicaoXDoZumbi, posicaoYDoZumbi);
        zumbis.add(novoZumbi);

        ZumbiView zumbiVisual = new ZumbiView(nomeArquivo, novoZumbi);
        zumbiVisual.setLayoutX(posicaoXDoZumbi);
        zumbiVisual.setLayoutY(posicaoYDoZumbi);

        camadaJogo.getChildren().add(zumbiVisual);
        visuaisDosZumbis.add(zumbiVisual);
    }

    // criando uma nova orda de zumbis
    public void criarOrdaDeZumbis() {
   
        for (int i = 0; i < quantidadeDeZumbisPorOrda; i++) {

            // Posição Y aleatória para o zumbi; fonte(acesso em 01/10/2026): https://www.devmedia.com.br/numeros-aleatorios-em-java-a-classe-java-util-random/26355
            int linhaAleatoria = (int) (Math.random() * LINHAS);
            double yAleatorio = GRADE_Y + linhaAleatoria * CEL_ALT;

            double distanciaMaximaParaOZumbiNascer = 100;
            double xAleatorio = LARGURA_PALCO + Math.random() * distanciaMaximaParaOZumbiNascer;

            criarZumbi(xAleatorio, yAleatorio); // Criando UM zumbi
        }

        quantidadeDeZumbisPorOrda++; // Incrementa a quantidade de zumbis para a próxima orda (aumenta a dificuldade do jogo)
    }

    @FXML private StackPane raiz;
    @FXML private Pane palco;
    @FXML private GridPane grade;
    @FXML private Pane camadaJogo;

    @FXML private Label lblCafe;
    @FXML private Button btnVoltar;

    private GerenciadorDeTelas gerenciadorDeTelas;

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
            //botao.setOnMousePressed(event -> AudioManager.tocarEfeitobotao.setOnMousePressed(event -> AudioManager.tocarSomBotao());("/audio/botao-hover.m4a"));
            botao.setOnMousePressed(event -> AudioManager.tocarEfeito("/audio/botao-hover.m4a"));
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        montarGrade();
        ajustarEscala();
        configurarSomDeHover(btnVoltar);
        configurarClick(btnVoltar);

        criarOrdaDeZumbis(); // Cria ordas de zumbis
        timer.start(); // Inicia o timer para atualizar a posição de zumbis e tiros

        // exibe o valor do café na tela
        lblCafe.setText("CAFÉ: " + valorDeCafe);
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
        StackPane celula = celulas[linha][coluna];
        boolean celulaOcupada = false;

        for (Professor professor : professores) {
            if (professor.getLinhaNoTabuleiroDoProfessor() == linha
                    && professor.getColunaNoTabuleiroDoProfessor() == coluna) {
                celulaOcupada = true; // deixa a celula ocupada
                System.out.println("Já existe um professor nesta célula.");
            }
        }

        for (Cafeteira cafeteira : cafeteiras) {
            if (cafeteira.getLinhaNoTabuleiroDaCafeteira() == linha
                    && cafeteira.getColunaNoTabuleiroDaCafeteira() == coluna) {
                celulaOcupada = true; // deixa a celula ocupada
                System.out.println("Já existe uma cafeteira nesta célula.");
            }
        }

        if (!celulaOcupada) {

            if ("Cafeteira".equals(professorSelecionado)) {
                Cafeteira cafeteira = new Cafeteira(linha, coluna);
                cafeteira.setUltimoMomentoDeProducao(agora);

                if (cafeteira.getCustoCafe() <= valorDeCafe) {
                    cafeteiras.add(cafeteira);

                    // VISUAL da cafeteira
                    CafeteiraView spriteCafeteira = new CafeteiraView();

                    spriteCafeteira.setOnMouseClicked(event -> {
                        int indiceCafeteira = cafeteirasVisual.indexOf(spriteCafeteira);

                        if (indiceCafeteira < 0 || indiceCafeteira >= cafeteiras.size()) {
                            return;
                        }

                        Cafeteira cafeteiraClicada = cafeteiras.get(indiceCafeteira);
                        int indiceCafe = -1;

                        for (int i = 0; i < cafes.size(); i++) {
                            if (cafes.get(i).getCafeteira() == cafeteiraClicada) {
                                indiceCafe = i;
                                break;
                            }
                        }

                        // Não existe café para coletar
                        if (indiceCafe < 0) {
                            return;
                        }

                        Cafe cafeClicado = cafes.get(indiceCafe);
                        valorDeCafe += cafeClicado.getValorDeCafeDaXicara();

                        System.out.println("Café coletado!");
                        System.out.println("Valor coletado: " + cafeClicado.getValorDeCafeDaXicara());
                        System.out.println("Café disponível: " + valorDeCafe);

                        cafes.remove(indiceCafe);
                        spriteCafeteira.mostrarVazia();
                        atualizarCafe();
                        event.consume();
                    });

                    celulas[linha][coluna].getChildren().add(spriteCafeteira);
                    cafeteirasVisual.add(spriteCafeteira);

                    System.out.println("Café antes: " + valorDeCafe);
                    valorDeCafe -= cafeteira.getCustoCafe();
                    System.out.println("Custo da cafeteira: " + cafeteira.getCustoCafe());
                    System.out.println("Café depois: " + valorDeCafe);
                    atualizarCafe();
                } else {
                    System.out.println("Sem café");
                }
            }

            if ("Daniel".equals(professorSelecionado)) {
                Daniel professorDaniel = new Daniel(linha, coluna);

                if (professorDaniel.getCustoCafe() <= valorDeCafe) {
                    professores.add(professorDaniel);

                    ProfessorView spriteProfessor = new ProfessorView("daniel_sprites.png");
                    celula.getChildren().add(spriteProfessor);

                    valorDeCafe -= professorDaniel.getCustoCafe();
                    atualizarCafe();
                } else {
                    System.out.println("Sem café");
                }
            }

            if ("Fabricio".equals(professorSelecionado)) {
                Fabricio professorFabricio = new Fabricio(linha, coluna);

                if (professorFabricio.getCustoCafe() <= valorDeCafe) {
                    professores.add(professorFabricio);

                    ProfessorView spriteProfessor = new ProfessorView("fabricio_sprites.png");
                    celula.getChildren().add(spriteProfessor);

                    valorDeCafe -= professorFabricio.getCustoCafe();
                    atualizarCafe();
                } else {
                    System.out.println("Sem café");
                }
            }

            if ("Glaucia".equals(professorSelecionado)) {
                Glaucia professorGlaucia = new Glaucia(linha, coluna);

                if (professorGlaucia.getCustoCafe() <= valorDeCafe) {
                    professores.add(professorGlaucia);

                    ProfessorView spriteProfessor = new ProfessorView("glaucia_sprites.png");
                    celula.getChildren().add(spriteProfessor);

                    valorDeCafe -= professorGlaucia.getCustoCafe();
                    atualizarCafe();
                } else {
                    System.out.println("Sem café");
                }
            }

            if ("Lucas".equals(professorSelecionado)) {
                Lucas professorLucas = new Lucas(linha, coluna);

                if (professorLucas.getCustoCafe() <= valorDeCafe) {
                    professores.add(professorLucas);

                    ProfessorView spriteProfessor = new ProfessorView("lucas_sprites.png");
                    celula.getChildren().add(spriteProfessor);

                    valorDeCafe -= professorLucas.getCustoCafe();
                    atualizarCafe();
                } else {
                    System.out.println("Sem café");
                }
            }

            if ("Marcus".equals(professorSelecionado)) {
                Marcus professorMarcus = new Marcus(linha, coluna);

                if (professorMarcus.getCustoCafe() <= valorDeCafe) {
                    professores.add(professorMarcus);

                    ProfessorView spriteProfessor = new ProfessorView("marcus_sprites.png");
                    celula.getChildren().add(spriteProfessor);

                    valorDeCafe -= professorMarcus.getCustoCafe();
                    atualizarCafe();
                } else {
                    System.out.println("Sem café");
                }
            }

            if ("Nacif".equals(professorSelecionado)) {
                Nacif professorNacif = new Nacif(linha, coluna);

                if (professorNacif.getCustoCafe() <= valorDeCafe) {
                    professores.add(professorNacif);

                    ProfessorView spriteProfessor = new ProfessorView("nacif_sprites.png");
                    celula.getChildren().add(spriteProfessor);

                    valorDeCafe -= professorNacif.getCustoCafe();
                    atualizarCafe();
                } else {
                    System.out.println("Sem café");
                }
            }

            if ("Philipe".equals(professorSelecionado)) {
                Philipe professorPhilipe = new Philipe(linha, coluna);

                if (professorPhilipe.getCustoCafe() <= valorDeCafe) {
                    professores.add(professorPhilipe);

                    ProfessorView spriteProfessor = new ProfessorView("philipe_sprites.png");
                    celulas[linha][coluna].getChildren().add(spriteProfessor);

                    valorDeCafe -= professorPhilipe.getCustoCafe();
                    atualizarCafe();
                } else {
                    System.out.println("Sem café");
                }
            }

            if ("Thais".equals(professorSelecionado)) {
                Thais professorThais = new Thais(linha, coluna);

                if (professorThais.getCustoCafe() <= valorDeCafe) {
                    professores.add(professorThais);

                    ProfessorView spriteProfessor = new ProfessorView("thais_sprites.png");
                    celula.getChildren().add(spriteProfessor);

                    valorDeCafe -= professorThais.getCustoCafe();
                    atualizarCafe();
                } else {
                    System.out.println("Sem café");
                }
            }
        }

        System.out.println("Casa clicada: linha " + linha + ", coluna " + coluna);
    }

    // Escala o palco (1672x941) para caber na janela, mantendo a grade alinhada.
    private void ajustarEscala() {
        Runnable atualizar = () -> {
            double s = Math.min(
                    raiz.getWidth() / LARGURA_PALCO,
                    raiz.getHeight() / ALTURA_PALCO
            );

            if (s > 0) {
                palco.setScaleX(s);
                palco.setScaleY(s);
            }
        };

        raiz.widthProperty().addListener((o, a, b) -> atualizar.run());
        raiz.heightProperty().addListener((o, a, b) -> atualizar.run());
    }

    private void atualizarCafe() {
        lblCafe.setText("Café: " + valorDeCafe);
    }

    @FXML
    private void voltar(ActionEvent event) {
        gerenciadorDeTelas.voltarMenu();
    }

    @FXML
    private void selecionarCafeteira(ActionEvent event) {
        professorSelecionado = "Cafeteira";
        System.out.println("Cafeteira selecionada para plantar");
    }

    @FXML
    private void selecionarDaniel(ActionEvent event) {
        professorSelecionado = "Daniel";
        System.out.println("Daniel selecionado para plantar");
    }

    @FXML
    private void selecionarFabricio(ActionEvent event) {
        professorSelecionado = "Fabricio";
        System.out.println("Fabricio selecionado para plantar");
    }

    @FXML
    private void selecionarGlaucia(ActionEvent event) {
        professorSelecionado = "Glaucia";
        System.out.println("Glaucia selecionada para plantar");
    }

    @FXML
    private void selecionarLucas(ActionEvent event) {
        professorSelecionado = "Lucas";
        System.out.println("Lucas selecionado para plantar");
    }

    @FXML
    private void selecionarMarcus(ActionEvent event) {
        professorSelecionado = "Marcus";
        System.out.println("Marcus selecionado para plantar");
    }

    @FXML
    private void selecionarNacif(ActionEvent event) {
        professorSelecionado = "Nacif";
        System.out.println("Nacif selecionado para plantar");
    }

    @FXML
    private void selecionarPhilipe(ActionEvent event) {
        professorSelecionado = "Philipe";
        System.out.println("Philipe selecionado para plantar");
    }

    @FXML
    private void selecionarThais(ActionEvent event) {
        professorSelecionado = "Thais";
        System.out.println("Thais selecionada para plantar");
    }
}