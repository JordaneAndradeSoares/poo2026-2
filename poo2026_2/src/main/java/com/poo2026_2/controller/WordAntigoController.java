package com.poo2026_2.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.print.PrinterJob;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** Editor clássico integrado ao GerenciadorDeTelas do jogo. */
public class WordAntigoController implements ControladorTela {
    @FXML private StackPane root;
    @FXML private TextArea editor, descricaoRafael;
    @FXML private VBox paginaCapa, paginaIntegrantes1, paginaIntegrantes2;
    @FXML private ComboBox<String> estilo;
    @FXML private ComboBox<String> fonte;
    @FXML private ComboBox<String> tamanho;
    @FXML private ToggleButton btnNegrito, btnItalico, btnSublinhado;
    @FXML private ToggleButton alinharEsquerda, alinharCentro, alinharDireita;
    @FXML private HBox reguaHorizontal;
    @FXML private VBox reguaVertical;
    @FXML private Label statusPosicao, statusArquivo;

    private GerenciadorDeTelas gerenciadorDeTelas;
    private File arquivoAtual;
    private boolean modificandoFonte;

    @Override
    public void setGerenciadorDeTelas(GerenciadorDeTelas gerenciadorDeTelas) {
        this.gerenciadorDeTelas = gerenciadorDeTelas;
    }

    @FXML private void initialize() {
        estilo.getItems().setAll("Normal", "Título 1", "Título 2");
        fonte.getItems().setAll("Times New Roman", "Arial", "Courier New", "Verdana", "Tahoma");
        tamanho.getItems().setAll("8", "9", "10", "11", "12", "14", "16", "18", "20", "24", "28", "36", "48", "72");
        fonte.setValue("Times New Roman");
        tamanho.setValue("12");
        estilo.setValue("Normal");
        editor.caretPositionProperty().addListener((obs, a, b) -> atualizarPosicao());
        editor.textProperty().addListener((obs, a, b) -> atualizarPosicao());
        // O filtro fica no root da tela: não interfere com o ESC de outras telas.
        root.addEventFilter(KeyEvent.KEY_PRESSED, evento -> {
            if (evento.getCode() == KeyCode.ESCAPE) {
                evento.consume();
                voltar();
            } else if (evento.isControlDown() && evento.getCode() == KeyCode.S) {
                evento.consume();
                salvar();
            } else if (evento.isControlDown() && evento.getCode() == KeyCode.O) {
                evento.consume();
                abrir();
            } else if (evento.isControlDown() && evento.getCode() == KeyCode.N) {
                evento.consume();
                novo();
            }
        });
        Platform.runLater(editor::requestFocus);
    }

    @FXML private void voltar() {
        if (gerenciadorDeTelas != null) {
            gerenciadorDeTelas.voltarMenu();
        } else {
            alerta("Navegação indisponível", "O GerenciadorDeTelas não foi configurado.");
        }
    }

    @FXML private void minimizar() {
        if (gerenciadorDeTelas != null) gerenciadorDeTelas.getStage().setIconified(true);
    }

    @FXML private void maximizar() {
        if (gerenciadorDeTelas != null) {
            var stage = gerenciadorDeTelas.getStage();
            if (stage.isFullScreen()) return;
            stage.setMaximized(!stage.isMaximized());
        }
    }

    @FXML private void novo() {
        editor.clear();
        descricaoRafael.clear();
        arquivoAtual = null;
        statusArquivo.setText("Documento1");
    }

    private FileChooser seletor(String titulo) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle(titulo);
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Arquivos de texto (*.txt)", "*.txt"));
        return chooser;
    }

    @FXML private void abrir() {
        FileChooser chooser = seletor("Abrir documento");
        File arquivo = chooser.showOpenDialog(root.getScene().getWindow());
        if (arquivo == null) return;
        try {
            editor.setText(Files.readString(arquivo.toPath(), StandardCharsets.UTF_8));
            arquivoAtual = arquivo;
            statusArquivo.setText(arquivo.getName());
        } catch (IOException ex) {
            alerta("Erro ao abrir", ex.getMessage());
        }
    }

    @FXML private void salvar() {
        if (arquivoAtual == null) { salvarComo(); return; }
        try {
            Files.writeString(arquivoAtual.toPath(), textoDocumento(), StandardCharsets.UTF_8);
            statusArquivo.setText(arquivoAtual.getName() + " — salvo");
        } catch (IOException ex) {
            alerta("Erro ao salvar", ex.getMessage());
        }
    }

    @FXML private void salvarComo() {
        FileChooser chooser = seletor("Salvar documento como");
        chooser.setInitialFileName(arquivoAtual == null ? "Documento1.txt" : arquivoAtual.getName());
        File escolhido = chooser.showSaveDialog(root.getScene().getWindow());
        if (escolhido == null) return;
        arquivoAtual = escolhido;
        salvar();
    }

    @FXML private void imprimir() {
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job != null && job.showPrintDialog(root.getScene().getWindow())) {
            if (job.printPage(paginaCapa) && job.printPage(paginaIntegrantes1)
                    && job.printPage(paginaIntegrantes2)) job.endJob();
        }
    }

    private String textoDocumento() {
        return "UNIVERSIDADE FEDERAL DE VIÇOSA · CAMPUS FLORESTAL\n"
            + "CIÊNCIA DA COMPUTAÇÃO - BANCO DE DADOS\n"
            + "CCF 313 - PROGRAMAÇÃO ORIENTADA A OBJETOS\nTP1\n\n"
            + "Mateus Henrique Machado Braz - [6594]\n"
            + "Gean Augusto Lino de Oliveira - [6578]\n"
            + "Thomaz Augusto Araujo Silva - [6577]\n"
            + "Jordane Andrade Soares - [5106]\n"
            + "Luiz Henrique Laudares - [6596]\n"
            + "Rafael de Matos Silva - [6575]\npassu\nFlorestal - MG\n\n"
            + "17 de Novembro de 2026\nRafael de Matos Silva\n"
            + descricaoRafael.getText() + "\nGean Augusto Lino de Oliveira\n"
            + "Thomaz Augusto Araujo Silva\nPassu alves\n\n"
            + "Mateus Henrique Machado Braz\nJordane Andrade Soares\n"
            + "Luiz Henrique Laudares\n\nAnotações\n" + editor.getText();
    }

    @FXML private void desfazer() { editor.undo(); }
    @FXML private void refazer() { editor.redo(); }
    @FXML private void recortar() { editor.cut(); }
    @FXML private void copiar() { editor.copy(); }
    @FXML private void colar() { editor.paste(); }
    @FXML private void selecionarTudo() { editor.selectAll(); }

    @FXML private void alternarRegua() {
        boolean mostrar = !reguaHorizontal.isVisible();
        reguaHorizontal.setVisible(mostrar);
        reguaHorizontal.setManaged(mostrar);
        reguaVertical.setVisible(mostrar);
        reguaVertical.setManaged(mostrar);
    }

    @FXML private void inserirData() {
        editor.replaceSelection(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
    }

    /** TextArea não oferece formatação rica por seleção: aplica à área inteira. */
    @FXML private void aplicarFonte() {
        if (modificandoFonte || editor == null || fonte.getValue() == null || tamanho.getValue() == null) return;
        double pontos;
        try { pontos = Double.parseDouble(tamanho.getValue()); }
        catch (NumberFormatException ex) { return; }
        String family = fonte.getValue();
        String weight = btnNegrito.isSelected() ? "bold" : "normal";
        String posture = btnItalico.isSelected() ? "italic" : "normal";
        editor.setStyle("-fx-font-family: '" + family + "'; -fx-font-size: " + pontos
                + "pt; -fx-font-weight: " + weight + "; -fx-font-style: " + posture
                + "; -fx-underline: " + btnSublinhado.isSelected() + ";"
                + "-fx-text-alignment: " + alinhamentoAtual() + ";");
    }

    private String alinhamentoAtual() {
        if (alinharCentro.isSelected()) return "center";
        if (alinharDireita.isSelected()) return "right";
        return "left";
    }

    private void alinhar(ToggleButton selecionado) {
        alinharEsquerda.setSelected(selecionado == alinharEsquerda);
        alinharCentro.setSelected(selecionado == alinharCentro);
        alinharDireita.setSelected(selecionado == alinharDireita);
        aplicarFonte();
    }
    @FXML private void alinharEsquerda() { alinhar(alinharEsquerda); }
    @FXML private void alinharCentro() { alinhar(alinharCentro); }
    @FXML private void alinharDireita() { alinhar(alinharDireita); }
    @FXML private void inserirMarcador() { editor.replaceSelection("• "); }
    @FXML private void inserirNumeracao() { editor.replaceSelection("1. "); }

    @FXML private void contarPalavras() {
        String texto = editor.getText().trim();
        int total = texto.isEmpty() ? 0 : texto.split("\\s+").length;
        alerta("Contagem de palavras", "Palavras: " + total + "\nCaracteres: " + editor.getLength());
    }
    @FXML private void indisponivel() { alerta("Recurso não implementado", "Este recurso é apenas visual nesta versão."); }
    @FXML private void ajuda() { alerta("WordAntigo", "Editor clássico em JavaFX integrado ao menu do projeto."); }

    private void atualizarPosicao() {
        int pos = editor.getCaretPosition();
        String antes = editor.getText().substring(0, Math.min(pos, editor.getLength()));
        int linha = 1;
        int ultimaQuebra = -1;
        for (int i = 0; i < antes.length(); i++) if (antes.charAt(i) == '\n') { linha++; ultimaQuebra = i; }
        statusPosicao.setText("Lin " + linha + "   Col " + (pos - ultimaQuebra));
    }

    private void alerta(String titulo, String mensagem) {
        Alert dialog = new Alert(Alert.AlertType.INFORMATION);
        dialog.setTitle(titulo);
        dialog.setHeaderText(null);
        dialog.setContentText(mensagem);
        dialog.showAndWait();
    }
}
