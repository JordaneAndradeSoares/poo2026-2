/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.poo2026_2.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import com.poo2026_2.model.tiros.tiro;

import com.poo2026_2.model.zumbis.Zumbi;

import com.poo2026_2.model.Cafe;

import com.poo2026_2.model.professores.Cafeteira;
import com.poo2026_2.model.professores.Professor;
import com.poo2026_2.model.professores.Daniel;
import com.poo2026_2.model.professores.Fabricio;

import java.util.ArrayList;

import javafx.scene.shape.Circle; // Importação da classe Circle para criar visuais dos tiros
import javafx.scene.shape.Rectangle; // Importação da classe Rectangle para criar visuais dos zumbis
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

    private ArrayList<tiro> tiros = new ArrayList<>(); // Lista para armazenar TODOS os tiros disparados pelos professores
    private ArrayList<Professor> professores = new ArrayList<>(); // Lista para armazenar todos os professores
    private ArrayList<Zumbi> zumbis = new ArrayList<>(); // Lista para armazenar todos os zumbis
    
    private ArrayList<Cafeteira> cafeteiras = new ArrayList<>(); // Lista para armazenar todas as cafeteiras
    private ArrayList<Cafe> cafes = new ArrayList<>(); // Lista para armazenar todas as cafes
    private ArrayList<Circle> cafesVisual = new ArrayList<>();

    private ArrayList<Circle> visuaisDosTiros = new ArrayList<>(); // Lista para armazenar os visuais dos tiros (apenas para teste)
    private ArrayList<Rectangle> visuaisDosZumbis = new ArrayList<>(); // Lista para armazenar os visuais dos zumbis (apenas para teste)

    private AnimationTimer timer = new AnimationTimer() {

        @Override
        public void handle(long agora) {

            // atualizando o agora;
            JogoController.this.agora = agora;

            for (int c = 0; c < cafeteiras.size(); c++) {

                if(cafeteiras.get(c).podeProduzir(agora)){

                    // cafe
                    Cafe cafe = new Cafe(cafeteiras.get(c).getLinhaNoTabuleiroDaCafeteira(), cafeteiras.get(c).getColunaNoTabuleiroDaCafeteira(), cafeteiras.get(c), agora);
                    cafes.add(cafe);

                    // visual do cafe
                    Circle cafeVisual = new Circle(20, Color.BROWN);

                    cafeVisual.setOnMouseClicked(event -> {
                        int indice = cafesVisual.indexOf(cafeVisual);

                        Cafe cafeClicado = cafes.get(indice);

                        valorDeCafe += cafeClicado.getValorDeCafeDaXicara();

                        System.out.println("Café coletado!");
                        System.out.println("Valor coletado: " + cafeClicado.getValorDeCafeDaXicara());
                        System.out.println("Café disponível: " + valorDeCafe);

                        int linhaCafe = cafeClicado.getPosicaoLinhaCafe();
                        int colunaCafe = cafeClicado.getPosicaoColunaCafe();

                        celulas[linhaCafe][colunaCafe].getChildren().remove(cafeVisual);

                        cafesVisual.remove(indice);
                        cafes.remove(indice);

                        atualizarCafe();

                        event.consume();
                    });

                    cafeVisual.setFill(Color.BROWN);

                    int linhaCafe = cafe.getPosicaoLinhaCafe();
                    int colunaCafe = cafe.getPosicaoColunaCafe();

                    StackPane celulaCafe = celulas[linhaCafe][colunaCafe];

                    celulaCafe.getChildren().add(cafeVisual);
                    cafeVisual.toFront();
                    cafesVisual.add(cafeVisual);
                }
            }

            for (int c = 0; c < cafes.size(); c++) {

                // removendo a cafe frio
                if (!cafes.get(c).cafeEstaQuente(agora)) {

                    int linhaCafeFrio = cafes.get(c).getPosicaoLinhaCafe();
                    int colunaCafeFrio = cafes.get(c).getPosicaoColunaCafe();

                    Circle cafeVisualParaRemover = cafesVisual.get(c);
                    celulas[linhaCafeFrio][colunaCafeFrio].getChildren().remove(cafeVisualParaRemover);
                    cafesVisual.remove(c);
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
                    Rectangle zumbiVisual = visuaisDosZumbis.get(i);

                    camadaJogo.getChildren().remove(zumbiVisual);
                    visuaisDosZumbis.remove(i);
                    zumbis.remove(i);

                    i--;
                    continue;
                }

                // Atualiza a posição visual do zumbi (apenas para teste)
                Rectangle zumbiVisual = visuaisDosZumbis.get(i);
                zumbiVisual.setX(zumbi.getPosicaoXDoZumbi());
                zumbiVisual.setY(zumbi.getPosicaoYDoZumbi());

                boolean professorBloqueando = false;
                boolean cafeteiraBloqueando = false;

                // Calcula a linha do zumbi pelo seu Y
                int linhaDoZumbi = (int) ((zumbi.getPosicaoYDoZumbi() - GRADE_Y) / CEL_ALT); 

                Professor professorMaisProximo = null;
                
                for (int p = 0; p < professores.size(); p++) {
                    
                    Professor professor = professores.get(p);
                    
                    if (professor.getLinhaNoTabuleiroDoProfessor() == linhaDoZumbi && professor.professorEstaVivo()) {
                        
                        // Calcula o X do professor pela sua coluna
                        double xDoProfessor = GRADE_X + professor.getColunaNoTabuleiroDoProfessor() * CEL_LARG;
    
                        /*
                        if (zumbi.getPosicaoXDoZumbi() <= xDoProfessor + CEL_LARG) {
    
                            // zumbi topou com um professor
                            professorBloqueando = true;
                                
                            // O zumbi está atacando o professor, então ele para de se mover
                            zumbi.ataqueZumbi();
                        }
                        */

                        if (xDoProfessor < zumbi.getPosicaoXDoZumbi()) {

                            /*
                            Professor:
                            esquerda = xDoProfessor
                            direita  = xDoProfessor + CEL_LARG

                            Zumbi:
                            esquerda = xDoZumbi
                            direita  = xDoZumbi + 100
                            */

                            // colidiu (professor com zumbi)
                            if ((xDoProfessor <= (zumbi.getPosicaoXDoZumbi() + 100)) && ((xDoProfessor + CEL_LARG) >= zumbi.getPosicaoXDoZumbi())) {

                                if (professorMaisProximo == null) {
                                    professorMaisProximo = professor;
                                } else {
                                    double xDoProfessorMaisProximo = GRADE_X + professorMaisProximo.getColunaNoTabuleiroDoProfessor() * CEL_LARG;
    
                                    if (xDoProfessor > xDoProfessorMaisProximo) {
                                        professorMaisProximo = professor;
                                    }
                                }
                            }
                        }
    
                    }

                    // removendo o professor morto
                    if (!professor.professorEstaVivo()) {

                        int linhaProfessorMorto = professor.getLinhaNoTabuleiroDoProfessor();
                        int colunaProfessorMorto = professor.getColunaNoTabuleiroDoProfessor();

                        StackPane professorParaRemover = celulas[linhaProfessorMorto][colunaProfessorMorto];

                        for (int k = 0; k < professorParaRemover.getChildren().size(); k++) {

                            Node filho = professorParaRemover.getChildren().get(k);

                            if (filho instanceof Label) {

                                Label spriteProfessor = (Label) filho;

                                if (
                                    "D".equals(spriteProfessor.getText()) ||
                                    "F".equals(spriteProfessor.getText())
                                ) {
                                    professorParaRemover.getChildren().remove(k);
                                    break;
                                }
                            }
                        }

                        professores.remove(p);
                        p--;
                    }
                } 

                /*
                if (professorMaisProximo != null) {
                    double xDoProfessorMaisProximo = GRADE_X + professorMaisProximo.getColunaNoTabuleiroDoProfessor() * CEL_LARG;

                    if(zumbi.getPosicaoXDoZumbi() <= xDoProfessorMaisProximo + CEL_LARG){
                        professorBloqueando = true;
                        zumbi.ataqueZumbi();
                    }
                }
                
                if (zumbi.zumbiEstaAtacando() && zumbi.podeMorder(agora)){
                    professorMaisProximo.professorRecebeDano(zumbi.getDanoDoZumbi());
                }
                */

                Cafeteira cafeteiraMaisProxima = null;

                for (int c = 0; c < cafeteiras.size(); c++) {
                    
                    Cafeteira cafeteira = cafeteiras.get(c);
                    
                    if (cafeteira.getLinhaNoTabuleiroDaCafeteira() == linhaDoZumbi && cafeteira.cafeteiraEstaViva()) {
                        
                        // Calcula o X da cafeteira pela sua coluna
                        double xDaCafeteira = GRADE_X + cafeteira.getColunaNoTabuleiroDaCafeteira() * CEL_LARG;

                        if (xDaCafeteira < zumbi.getPosicaoXDoZumbi()) {

                            // colidiu (cafeteira com zumbi) 
                            // if ((xDoProfessor <= (zumbi.getPosicaoXDoZumbi() + 100)) && ((xDoProfessor + CEL_LARG) >= zumbi.getPosicaoXDoZumbi())) {
                            if ((xDaCafeteira <= (zumbi.getPosicaoXDoZumbi() + 100)) && ((xDaCafeteira + CEL_LARG) >= zumbi.getPosicaoXDoZumbi())) {
    
                                if (cafeteiraMaisProxima == null) {
                                    cafeteiraMaisProxima = cafeteira;
                                } else {
                                    double xDaCafeteiraMaisProxima = GRADE_X + cafeteiraMaisProxima.getColunaNoTabuleiroDaCafeteira() * CEL_LARG;

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

                            if (filho instanceof Label) {

                                Label spriteCafeteira = (Label) filho;

                                if ("C".equals(spriteCafeteira.getText())) {
                                    cafeteiraParaRemover.getChildren().remove(k);
                                    break;
                                }
                            }
                        }

                        cafeteiras.remove(c);
                        c--;
                    }
                } 
                
                //if ((cafeteiraMaisProxima != null) || (professorMaisProximo != null)) {

                /*
                if (cafeteiraMaisProxima != null) {

                    double xDaCafeteiraMaisProxima = GRADE_X + cafeteiraMaisProxima.getColunaNoTabuleiroDaCafeteira() * CEL_LARG;

                    if (professorMaisProximo != null) {
                        double xDoProfessorMaisProximo = GRADE_X + professorMaisProximo.getColunaNoTabuleiroDoProfessor() * CEL_LARG;

                        if((zumbi.getPosicaoXDoZumbi() <= xDaCafeteiraMaisProxima + CEL_LARG) && (xDaCafeteiraMaisProxima > xDoProfessorMaisProximo)){
        
                            cafeteiraBloqueando = true;
                            zumbi.ataqueZumbi();
                        }
                    }
                } 

                if (professorMaisProximo != null) {
                    double xDoProfessorMaisProximo = GRADE_X + professorMaisProximo.getColunaNoTabuleiroDoProfessor() * CEL_LARG;

                    if (professorMaisProximo != null) {
                        double xDaCafeteiraMaisProxima = GRADE_X + cafeteiraMaisProxima.getColunaNoTabuleiroDaCafeteira() * CEL_LARG;

                        if((zumbi.getPosicaoXDoZumbi() <= xDoProfessorMaisProximo + CEL_LARG) && (xDaCafeteiraMaisProxima > xDoProfessorMaisProximo)){

                            professorBloqueando = true;
                            zumbi.ataqueZumbi();
                        }
                    }
                }
                */

                /*
                if (professorMaisProximo != null) {
                   
                   if (cafeteiraMaisProxima != null) {

                        double xDaCafeteiraMaisProxima = GRADE_X + cafeteiraMaisProxima.getColunaNoTabuleiroDaCafeteira() * CEL_LARG;
                        double xDoProfessorMaisProximo = GRADE_X + professorMaisProximo.getColunaNoTabuleiroDoProfessor() * CEL_LARG;
                        
                        if (xDaCafeteiraMaisProxima > xDoProfessorMaisProximo) {
                            professorBloqueando = true;
                            zumbi.ataqueZumbi();
                        }
                    } else {
                        double xDaCafeteiraMaisProxima = GRADE_X + cafeteiraMaisProxima.getColunaNoTabuleiroDaCafeteira() * CEL_LARG;
                        double xDoProfessorMaisProximo = GRADE_X + professorMaisProximo.getColunaNoTabuleiroDoProfessor() * CEL_LARG;
                        
                        if (xDaCafeteiraMaisProxima < xDoProfessorMaisProximo) {
                            cafeteiraBloqueando = true;
                            zumbi.ataqueZumbi();
                        }
                    }
                }
                */

                if (professorMaisProximo != null && cafeteiraMaisProxima != null) {

                    double xDaCafeteiraMaisProxima = GRADE_X + cafeteiraMaisProxima.getColunaNoTabuleiroDaCafeteira() * CEL_LARG;
                    double xDoProfessorMaisProximo = GRADE_X + professorMaisProximo.getColunaNoTabuleiroDoProfessor() * CEL_LARG;
                        
                    if (xDaCafeteiraMaisProxima > xDoProfessorMaisProximo) {
                        professorBloqueando = true;
                        zumbi.ataqueZumbi();
                        //professorMaisProximo.professorRecebeDano(zumbi.getDanoDoZumbi());

                        // zumbi morde o professor mais proximo dele
                        if (zumbi.zumbiEstaAtacando() && zumbi.podeMorder(agora)){
                            professorMaisProximo.professorRecebeDano(zumbi.getDanoDoZumbi());
                        }
                    } else {
                        cafeteiraBloqueando = true;
                        zumbi.ataqueZumbi();
                        //cafeteiraMaisProxima.cafeteiraRecebeDano(zumbi.getDanoDoZumbi());

                        // zumbi morde a cafeteira mai proxima dele
                        if (zumbi.zumbiEstaAtacando() && zumbi.podeMorder(agora)){
                            cafeteiraMaisProxima.cafeteiraRecebeDano(zumbi.getDanoDoZumbi());
                        }
                    }

                } else if (professorMaisProximo != null) {

                    professorBloqueando = true;
                    zumbi.ataqueZumbi();
                    //professorMaisProximo.professorRecebeDano(zumbi.getDanoDoZumbi());

                    // zumbi morde o professor mais proximo dele
                    if (zumbi.zumbiEstaAtacando() && zumbi.podeMorder(agora)){
                        professorMaisProximo.professorRecebeDano(zumbi.getDanoDoZumbi());
                    }

                } else if (cafeteiraMaisProxima != null) {

                    cafeteiraBloqueando = true;
                    zumbi.ataqueZumbi();
                    //cafeteiraMaisProxima.cafeteiraRecebeDano(zumbi.getDanoDoZumbi());

                    // zumbi morde a cafeteira mai proxima dele
                    if (zumbi.zumbiEstaAtacando() && zumbi.podeMorder(agora)){
                        cafeteiraMaisProxima.cafeteiraRecebeDano(zumbi.getDanoDoZumbi());
                    }
                }

                /*
                if (zumbi.zumbiEstaAtacando() && zumbi.podeMorder(agora)){
                    professorMaisProximo.p

                for (int c = 0; c < cafeteiras.size(); c++) {
                    Cafeteira cafeteira = cafeteiras.get(c);

                    if (cafeteira.getLinhaNoTabuleiroDaCafeteira() == linhaDoZumbi) {

                        double xDaCafeteira = GRADE_X + cafeteira.getColunaNoTabuleiroDaCafeteira() * CEL_LARG;

                        if (zumbi.getPosicaoXDoZumbi() <= xDaCafeteira + CEL_LARG) {

                            // zumbi topou com uma cafeteira
                            System.out.println("ZUMBI ENCONTROU CAFETEIRA");
                            cafeteiraBloqueando = true;
                            
                            // O zumbi está atacando a cafeteira, então ele para de se mover
                            zumbi.ataqueZumbi();
                        }

                        if (zumbi.zumbiEstaAtacando() && zumbi.podeMorder(agora)){
                            cafeteira.cafeteiraRecebeDano(zumbi.getDanoDoZumbi());
                        }

                        // removendo a cafeteira morta
                        if (!cafeteira.cafeteiraEstaViva()) {

                            int linhaCafeteiraMorta = cafeteira.getLinhaNoTabuleiroDaCafeteira();
                            int colunaCafeteiraMorta = cafeteira.getColunaNoTabuleiroDaCafeteira();

                            StackPane cafeteiraParaRemover = celulas[linhaCafeteiraMorta][colunaCafeteiraMorta];
                            

                            for (int k = 0; k < cafeteiraParaRemover.getChildren().size(); k++) {

                                Node filho = cafeteiraParaRemover.getChildren().get(k);

                                if (filho instanceof Label) {

                                    Label spriteCafeteira = (Label) filho;

                                    if ("C".equals(spriteCafeteira.getText())) {
                                        cafeteiraParaRemover.getChildren().remove(k);
                                        break;
                                    }
                                }
                            }

                            cafeteiras.remove(c);
                            c--;
                        }
                    }
                } 
                */

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
                    
                    if (professor.getLinhaNoTabuleiroDoProfessor() == (int) ((zumbiAtual.getPosicaoYDoZumbi() - GRADE_Y) / CEL_ALT)){

                        if ((GRADE_X + professor.getColunaNoTabuleiroDoProfessor() * CEL_LARG) <= zumbiAtual.getPosicaoXDoZumbi()){

                            if(zumbiAlvoDoTiroDoProfessor == null){

                                zumbiAlvoDoTiroDoProfessor = zumbiAtual;

                            } else {

                                if (zumbiAtual.getPosicaoXDoZumbi() < zumbiAlvoDoTiroDoProfessor.getPosicaoXDoZumbi()) {

                                    // troca o alvo
                                    zumbiAlvoDoTiroDoProfessor = zumbiAtual;
                                }
                            }
                        }
                    }
                }

                /* 
                if (zumbiAlvoDoTiroDoProfessor != null && professor.podeAtirar(agora)) {
                
                    if (professor.getLinhaNoTabuleiroDoProfessor() == (int) ((zumbiAtual.getPosicaoYDoZumbi() - GRADE_Y) / CEL_ALT)) {
                        if ((GRADE_X + professor.getColunaNoTabuleiroDoProfessor() * CEL_LARG) <= zumbiAtual.getPosicaoXDoZumbi()){
                            zumbiAlvoDoTiroDoProfessor = zumbiAtual;
                        }
                    }
                }   
                */
                
                if (zumbiAlvoDoTiroDoProfessor != null && professor.podeAtirar(agora)) {
                    double posicaoXDoTiro = professor.getColunaNoTabuleiroDoProfessor() * CEL_LARG + GRADE_X + CEL_LARG / 2;
                    double posicaoYDoTiro = professor.getLinhaNoTabuleiroDoProfessor() * CEL_ALT + GRADE_Y + CEL_ALT / 2;
                    pedirParaOProfessorAtacarOZumbi(professor, zumbiAlvoDoTiroDoProfessor, posicaoXDoTiro, posicaoYDoTiro);
                }
            }

            // Atualiza a posição de cada tiro
            for (int i = 0; i < tiros.size(); i++) {
                tiro tiroAtual = tiros.get(i);
                boolean tiroFoiRemovido = false;

                tiroAtual.moverTiro(); // movendo o tiro

                for (int j = 0; j < zumbis.size(); j++) {

                    Zumbi zumbi = zumbis.get(j);
  
                    // verificar se tiroAtual colidiu com este zumbi
                    if (tiroAtual.tiroEstaColidindo(zumbi) && zumbi.zumbiEstaVivo()) {

                        // colisão aconteceu
                        zumbi.zumbiRecebeDano(tiroAtual.getDanoDoTiro());
 
                        // verificação de morte do zumbi (remove o zumbi morto)
                        if (zumbi.zumbiEstaVivo() == false) {
                            Rectangle zumbiVisual = visuaisDosZumbis.get(j);
                            camadaJogo.getChildren().remove(zumbiVisual);
                            visuaisDosZumbis.remove(j);
                            
                            zumbis.remove(j);
                        }

                        // removendo o visual dos tiros (circulo de teste)
                        Circle tiroVisual = visuaisDosTiros.get(i);
                        camadaJogo.getChildren().remove(tiroVisual);
                        visuaisDosTiros.remove(i);
                        
                        // removendo o tiro que acertou o zumbi
                        tiros.remove(i);
                        tiroFoiRemovido = true;
                        
                        System.out.println("COLISÃO!");
                        System.out.println("Vida ATUAL do zumbi: " + zumbi.getVidaDoZumbi());

                        break;
                    }  
                } 

                if (tiroFoiRemovido == false && (tiroAtual.getPosicaoXDoTiro() > LARGURA_PALCO || tiroAtual.getPosicaoXDoTiro() < 0)) {
                    
                    // removendo o visual dos tiros (circulo de teste)
                    Circle tiroVisual = visuaisDosTiros.get(i);
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

                Circle tiroVisual = visuaisDosTiros.get(i); // visual do tiro (apenas para teste)
                tiroVisual.setCenterX(tiroAtual.getPosicaoXDoTiro());
                tiroVisual.setCenterY(tiroAtual.getPosicaoYDoTiro());
            }
        }
    };

    // criando um novo tiro
    public void adicionarTiro(tiro novoTiro, double posicaoXDoTiro, double posicaoYDoTiro) {
        tiros.add(novoTiro);

        // visual provizorio do tiro (apenas para teste)
        Circle tiroVisual = new Circle(5, Color.YELLOW);
        tiroVisual.setCenterX(posicaoXDoTiro);
        tiroVisual.setCenterY(posicaoYDoTiro);
        camadaJogo.getChildren().add(tiroVisual);
        visuaisDosTiros.add(tiroVisual);
    }

    // método para "pedir" ao professor para atacar o zumbi
    public void pedirParaOProfessorAtacarOZumbi(Professor professor, Zumbi zumbi, double posicaoXDoTiro, double posicaoYDoTiro) {
        tiro novoTiro = professor.professorAtacaZumbi(zumbi, posicaoXDoTiro, posicaoYDoTiro);

        if (novoTiro != null) {
            adicionarTiro(novoTiro, posicaoXDoTiro, posicaoYDoTiro); // Adiciona o tiro à lista de tiros e à camada de jogo
        }
    }

    // criando UM novo zumbi
    public void criarZumbi(double posicaoXDoZumbi, double posicaoYDoZumbi) {
        Zumbi novoZumbi = new Zumbi(0.6, 40); // Velocidade e dano do zumbi
        novoZumbi.criarZumbi(posicaoXDoZumbi, posicaoYDoZumbi);
        zumbis.add(novoZumbi);

        // visual provizorio do zumbi (apenas para teste)
        Rectangle zumbiVisual = new Rectangle(100, 100);
        zumbiVisual.setFill(Color.GREEN);
        zumbiVisual.setX(posicaoXDoZumbi);
        zumbiVisual.setY(posicaoYDoZumbi);
        camadaJogo.getChildren().add(zumbiVisual);
        visuaisDosZumbis.add(zumbiVisual);
    }

    // criando uma nova orda de zumbis
    public void criarOrdaDeZumbis() {
        //System.out.println("Criando horda com " + quantidadeDeZumbisPorOrda + " zumbis");

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
            botao.setOnMousePressed(event -> AudioManager.tocarSomBotao());
        }
    }
    public void initialize(URL url, ResourceBundle rb) {
        montarGrade();
        ajustarEscala();
        configurarSomDeHover(btnVoltar);
        configurarClick(btnVoltar);

        criarOrdaDeZumbis(); // Cria ordas de zumbis
        timer.start(); // Inicia o timer para atualizar a posição de zumbis e tiros

        //atualizarCafe(); // Descontando o café

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

            if (professor.getLinhaNoTabuleiroDoProfessor() == linha && professor.getColunaNoTabuleiroDoProfessor() == coluna) {
                    
                celulaOcupada = true; // deixa a celula ocupada
                System.out.println("Já existe um professor nesta célula.");
            } 
        }

        for (Cafeteira cafeteira : cafeteiras) {
    
            if (cafeteira.getLinhaNoTabuleiroDaCafeteira( )== linha && cafeteira.getColunaNoTabuleiroDaCafeteira() == coluna) {
                    
                celulaOcupada = true; // deixa a celula ocupada
                System.out.println("Já existe uma cafeteira nesta célula.");
            } 
        }

        //if (celula.getChildren().isEmpty()) {
        if (!celulaOcupada) {
            /* 
            Label sprite = new Label("P");
            sprite.getStyleClass().add("sprite-teste");
            celula.getChildren().add(sprite);
            */

            if ("Cafeteira".equals(professorSelecionado)) {

                Cafeteira cafeteira = new Cafeteira(linha, coluna);
                cafeteira.setUltimoMomentoDeProducao(agora);

                if (cafeteira.getCustoCafe() <= valorDeCafe) {
                    
                    cafeteiras.add(cafeteira);
    
                    Label sprite = new Label("C");
                    sprite.getStyleClass().add("sprite-teste");
                    celula.getChildren().add(sprite);
                
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
    
                    Label sprite = new Label("D");
                    sprite.getStyleClass().add("sprite-teste");
                    celula.getChildren().add(sprite);
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

                    Label sprite = new Label("F");
                    sprite.getStyleClass().add("sprite-teste");
                    celula.getChildren().add(sprite);
                    valorDeCafe -= professorFabricio.getCustoCafe();
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
            double s = Math.min(raiz.getWidth() / LARGURA_PALCO,
                                raiz.getHeight() / ALTURA_PALCO);
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
}
