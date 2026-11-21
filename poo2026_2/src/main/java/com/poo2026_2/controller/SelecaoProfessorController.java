package com.poo2026_2.controller;

import com.poo2026_2.model.DescricaoProfessores;
import com.poo2026_2.model.Fase;
import com.poo2026_2.model.Professor;
import com.poo2026_2.model.utilizaveis.*;

import javafx.fxml.FXML;
import javafx.scene.control.Tooltip;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class SelecaoProfessorController implements ControladorTela {

    @FXML private BorderPane raiz;
    @FXML private VBox painelEquipe;
    @FXML private Label tituloFase;
    @FXML private Label contadorSelecionados;
    @FXML private GridPane gradeProfessores;
    @FXML private VBox listaSelecionados;

    private GerenciadorDeTelas gerenciadorDeTelas;
    private Fase fase;

    private static final List<OpcaoProfessor> CATALOGO = List.of(
        new OpcaoProfessor("Cafeteira", Cafeteira.class, true, "/images/interface/cafeteira.png"),
        new OpcaoProfessor("Daniel", Daniel.class, false, "/images/interface/daniel.png"),
        new OpcaoProfessor("Fabricio", Fabricio.class, false, "/images/interface/fabricio.png"),
        new OpcaoProfessor("Glaucia", Glaucia.class, false, "/images/interface/glaucia.png"),
        new OpcaoProfessor("Lucas", Lucas.class, false, "/images/interface/lucas.png"),
        new OpcaoProfessor("Marcus", Marcus.class, false, "/images/interface/marcus.png"),
        new OpcaoProfessor("Nacif", Nacif.class, false, "/images/interface/nacif.png"),
        new OpcaoProfessor("Philipe", Philipe.class, false, "/images/interface/philipe.png"),
        new OpcaoProfessor("Thais", Thais.class, false, "/images/interface/thais.png")
    );

    private final Set<OpcaoProfessor> selecionados = new LinkedHashSet<>();

    private static final String ICONE_DOCUMENTO = "/images/interface/documento.png";
    private Image iconeDocumento;

    public Set<String> getNomesProfessoresSelecionados() {

        Set<String> nomes = new LinkedHashSet<>();

        for (OpcaoProfessor opcao : selecionados) {

            if (opcao.cafeteira) {
                nomes.add("CAFETEIRA");
            } else {
                nomes.add(opcao.classe.getSimpleName().toUpperCase());
            }
        }

        return nomes;
    }

    @FXML
    private void initialize() {
        var stream = getClass().getResourceAsStream(ICONE_DOCUMENTO);
        if (stream != null) {
            iconeDocumento = new Image(stream, 64, 80, false, false);
        }

        raiz.widthProperty().addListener((obs, antiga, nova) -> ajustarLayout(nova.doubleValue()));
        ajustarLayout(raiz.getWidth());
    }

    private void ajustarLayout(double largura) {
        if (painelEquipe == null) return;

        // Em janelas menores, reduz o painel lateral para preservar espaço para a grade.
        double larguraPainel = largura < 900 ? 185 : (largura < 1150 ? 215 : 250);
        painelEquipe.setPrefWidth(larguraPainel);
    }

    @Override
    public void setGerenciadorDeTelas(GerenciadorDeTelas gerenciador) {
        this.gerenciadorDeTelas = gerenciador;
    }

    /**
     * Chamar depois de carregar o FXML e antes de mostrar a tela.
     * A grade é filtrada pelos professores liberados na fase recebida.
     */
    public void configurarFase(Fase fase) {
        if (fase == null) {
            throw new IllegalArgumentException("A fase não pode ser nula.");
        }

        this.fase = fase;
        tituloFase.setText("Professores vs Zumbiversitários - FASE " + fase.getNumero());
        selecionados.clear();
        gradeProfessores.getChildren().clear();

        Set<Class<? extends Professor>> liberados = new LinkedHashSet<>(fase.getProfessorDisponiveis());

        for (int indice = 0; indice < CATALOGO.size(); indice++) {

            OpcaoProfessor opcao = CATALOGO.get(indice);
            boolean estaLiberado = opcao.cafeteira || liberados.contains(opcao.classe.asSubclass(Professor.class));
            Button cartao = new Button(estaLiberado ? opcao.nome : opcao.nome + "\n(bloqueado)");
            var streamImagem = getClass().getResourceAsStream(opcao.caminhoImagem);

            if (streamImagem != null) {
                Image imagem = new Image(streamImagem);

                ImageView icone = new ImageView(imagem);
                icone.setFitWidth(90);
                icone.setFitHeight(100);
                icone.setPreserveRatio(true);
                icone.setSmooth(false);

                cartao.setGraphic(icone);
            } else {
                System.err.println("Imagem não encontrada: " + opcao.caminhoImagem);
            }

            cartao.setContentDisplay(ContentDisplay.TOP);
            cartao.getStyleClass().add("cartao-professor");
            cartao.setMnemonicParsing(false);
            cartao.setMinWidth(110);

            // Descrição exibida ao passar o mouse sobre o cartão.
            String nomeProfessor = opcao.cafeteira ? "CAFETEIRA" : opcao.classe.getSimpleName().toUpperCase();
            JogoController.configurarDescricao(cartao, nomeProfessor);

            if (estaLiberado) {
                cartao.setOnAction(evento -> alternarSelecao(opcao, cartao));
            } else {
                cartao.setDisable(true);
                cartao.getStyleClass().add("bloqueado");
            }

            // Sempre adiciona os nove cartões, mantendo a grade fixa 3x3.
            int coluna = indice % 3;
            int linha = indice / 3;

            gradeProfessores.add(cartao, coluna, linha);
        }

        atualizarResumo();
    }

    private void alternarSelecao(OpcaoProfessor opcao, Button cartao) {
        if (selecionados.remove(opcao)) {
            cartao.getStyleClass().remove("selecionado");
        } else {
            selecionados.add(opcao);
            if (!cartao.getStyleClass().contains("selecionado")) {
                cartao.getStyleClass().add("selecionado");
            }
        }

        atualizarResumo();
    }

    private void atualizarResumo() {
        contadorSelecionados.setText(selecionados.size() + " selecionado(s)");
        listaSelecionados.getChildren().clear();

        for (OpcaoProfessor opcao : selecionados) {
            Label item = new Label("✓  " + opcao.nome);
            item.getStyleClass().add("item-selecionado");
            listaSelecionados.getChildren().add(item);
        }
    }

    @FXML
    private void confirmarSelecao() {
        if (fase == null) {
            mostrarAviso("Nenhuma fase foi configurada para esta tela.");
            return;
        }

        if (selecionados.isEmpty()) {
            mostrarAviso("Selecione pelo menos um personagem.");
            return;
        }

        Set<String> nomesSelecionados = getNomesProfessoresSelecionados();

        JogoController.setProfessoresSelecionados(nomesSelecionados);

        gerenciadorDeTelas.mudarTela("Jogo.fxml");
    }

    @FXML
    private void voltar() {
        if (gerenciadorDeTelas != null) {
            gerenciadorDeTelas.voltarMenu();
        } else {
            mostrarAviso("O gerenciador de telas ainda não foi configurado.");
        }
    }

    private void mostrarAviso(String mensagem) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Seleção de Professores");
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }

    private static final class OpcaoProfessor {
        private final String nome;
        private final Class<?> classe;
        private final boolean cafeteira;
        private final String caminhoImagem;

        private OpcaoProfessor(String nome, Class<?> classe, boolean cafeteira, String caminhoImagem) {

            this.nome = nome;
            this.classe = classe;
            this.cafeteira = cafeteira;
            this.caminhoImagem = caminhoImagem;
        }
    }
}