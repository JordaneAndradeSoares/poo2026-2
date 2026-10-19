package com.poo2026_2.controller;

import com.poo2026_2.model.Fase;
import com.poo2026_2.model.Professor;
import com.poo2026_2.model.utilizaveis.*;
import com.poo2026_2.controller.JogoController;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class SelecaoProfessorController implements ControladorTela {

    @FXML private BorderPane raiz;
    @FXML private VBox painelEquipe;
    @FXML private Label tituloFase;
    @FXML private Label contadorSelecionados;
    @FXML private GridPane gradeProfessores;
    @FXML private VBox listaSelecionados;

    private GerenciadorDeTelas gerenciadorDeTelas;
    private Fase fase;

    /*
     * Cafeteira atualmente não estende Professor no modelo.
     * Por isso, ela é representada aqui como opção visual separada.
     * A confirmação devolve apenas professores; a integração da Cafeteira
     * deve ser feita separadamente no modelo da equipe/fase.
     */
    private static final List<OpcaoProfessor> CATALOGO = List.of(
        new OpcaoProfessor("Cafeteira", Cafeteira.class, true),
        new OpcaoProfessor("Daniel", Daniel.class, false),
        new OpcaoProfessor("Fabricio", Fabricio.class, false),
        new OpcaoProfessor("Glaucia", Glaucia.class, false),
        new OpcaoProfessor("Luca", Lucas.class, false),
        new OpcaoProfessor("Marcus", Marcus.class, false),
        new OpcaoProfessor("Nacif", Nacif.class, false),
        new OpcaoProfessor("Philipe", Philipe.class, false),
        new OpcaoProfessor("Thais", Thais.class, false)
    );

    private final Set<OpcaoProfessor> selecionados = new LinkedHashSet<>();
    private final List<Button> botoes = new ArrayList<>();

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
        // Redimensiona os cartões conforme o espaço disponível na janela.
        gradeProfessores.widthProperty().addListener((obs, antiga, nova) -> ajustarCartoes());
        gradeProfessores.heightProperty().addListener((obs, antiga, nova) -> ajustarCartoes());
        raiz.widthProperty().addListener((obs, antiga, nova) -> ajustarLayout(nova.doubleValue(), raiz.getHeight()));
        raiz.heightProperty().addListener((obs, antiga, nova) -> ajustarLayout(raiz.getWidth(), nova.doubleValue()));
        ajustarLayout(raiz.getWidth(), raiz.getHeight());
    }

    private void ajustarLayout(double largura, double altura) {
        if (painelEquipe == null || gradeProfessores == null) return;

        // Em janelas menores, reduz o painel lateral para preservar espaço para a grade.
        double larguraPainel = largura < 900 ? 185 : (largura < 1150 ? 215 : 250);
        painelEquipe.setPrefWidth(larguraPainel);
        ajustarCartoes();
    }

    private void ajustarCartoes() {
        if (gradeProfessores == null || botoes.isEmpty()) return;

        double larguraDisponivel = Math.max(300, gradeProfessores.getWidth() - 70);
        double alturaDisponivel = Math.max(300, gradeProfessores.getHeight() - 65);
        double larguraCartao = Math.max(92, Math.min(220, (larguraDisponivel - 28) / 3.0));
        double alturaCartao = Math.max(82, Math.min(175, (alturaDisponivel - 28) / 3.0));

        for (Button cartao : botoes) {
            cartao.setPrefSize(larguraCartao, alturaCartao);
            cartao.setMinSize(larguraCartao, alturaCartao);
            cartao.setMaxSize(larguraCartao, alturaCartao);
        }
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
        tituloFase.setText("FASE " + fase.getNumero() + " • " + fase.getNome());
        selecionados.clear();
        gradeProfessores.getChildren().clear();
        botoes.clear();

        Set<Class<? extends Professor>> liberados = new LinkedHashSet<>(fase.getProfessorDisponiveis());

        for (int indice = 0; indice < CATALOGO.size(); indice++) {
            OpcaoProfessor opcao = CATALOGO.get(indice);

            //boolean estaLiberado = opcao.cafeteira || liberados.contains(opcao.classe.asSubclass(Professor.class));
            boolean estaLiberado = true;

            Button cartao = new Button();
            cartao.setText(estaLiberado ? opcao.nome + "\n" + (opcao.cafeteira ? "Produção de café" : "Professor selecionável") : opcao.nome + "\n🔒 BLOQUEADO");
            cartao.getStyleClass().add("cartao-professor");
            cartao.setWrapText(true);
            cartao.setPrefSize(172, 150);
            cartao.setMinSize(92, 82);
            cartao.setMaxSize(220, 175);

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
            botoes.add(cartao);
        }

        ajustarCartoes();
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

        // A seleção de professores é devolvida/registrada na fase.
        /*
        for (OpcaoProfessor opcao : selecionados) {
            if (!opcao.cafeteira && Professor.class.isAssignableFrom(opcao.classe)) {
                @SuppressWarnings("unchecked")
                Class<? extends Professor> tipo = (Class<? extends Professor>) opcao.classe;
                if (!fase.getProfessorDisponiveis().contains(tipo)) {
                    mostrarAviso(opcao.nome + " não está liberado nesta fase.");
                    return;
                }
            }
        }
        */

        /*
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Seleção confirmada");
        alerta.setHeaderText("Equipe selecionada para " + fase.getNome());
        alerta.setContentText("Personagens selecionados: " + String.join(", ", selecionados.stream().map(o -> o.nome).toList()) + "\n\nA selecao visual foi confirmada.");
        alerta.showAndWait();
        */

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

        private OpcaoProfessor(String nome, Class<?> classe, boolean cafeteira) {
            this.nome = nome;
            this.classe = classe;
            this.cafeteira = cafeteira;
        }
    }
}