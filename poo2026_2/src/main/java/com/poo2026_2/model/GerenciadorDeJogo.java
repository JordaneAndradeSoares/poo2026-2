package com.poo2026_2.model;

import java.util.ArrayList;
import java.util.List;

import com.poo2026_2.model.utilizaveis.Cafeteira;
import com.poo2026_2.model.zumbis.Zumbi;

/**
 * Estado de uma partida: a fase carregada, o tabuleiro e o café do jogador.
 *
 * Guarda também os zumbis e os tiros que estão em campo, para a camada
 * visual (visuaisProva, visuaisZumbis, visuaisTiros) poder desenhá-los.
 */
public class GerenciadorDeJogo {

    private final Fase fase;
    private final Tabuleiro tabuleiro;
    private int cafe;
    private final List<Zumbi> zumbis = new ArrayList<>();
    private final List<Tiro> tiros = new ArrayList<>();

    public GerenciadorDeJogo(Fase fase, Tabuleiro tabuleiro) {
        if (fase == null) {
            throw new IllegalArgumentException("A fase nao pode ser nula.");
        }
        if (tabuleiro == null) {
            throw new IllegalArgumentException("O tabuleiro nao pode ser nulo.");
        }
        this.fase = fase;
        this.tabuleiro = tabuleiro;
        this.cafe = fase.getCafeInicial();
    }

    public Fase getFase() {
        return fase;
    }

    public Tabuleiro getTabuleiro() {
        return tabuleiro;
    }

    // ===================== ZUMBIS E TIROS =====================

    /** Zumbis em campo (lista viva: quem remove um zumbi morto mexe nela direto). */
    public List<Zumbi> getZumbis() {
        return zumbis;
    }

    /** Tiros em campo (lista viva: quem remove um tiro inativo mexe nela direto). */
    public List<Tiro> getTiros() {
        return tiros;
    }

    public void adicionarZumbi(Zumbi zumbi) {
        if (zumbi != null) {
            zumbis.add(zumbi);
        }
    }

    public void adicionarTiro(Tiro tiro) {
        if (tiro != null) {
            tiros.add(tiro);
        }
    }

    /** Tira da partida os zumbis mortos e os tiros que já acertaram ou saíram. */
    public void limparInativos() {
        zumbis.removeIf(z -> !z.estaVivo());
        tiros.removeIf(t -> !t.isAtivo());
    }

    // ===================== CAFÉ =====================

    public int getCafe() {
        return cafe;
    }

    public void adicionarCafe(int quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade de cafe tem que ser positiva.");
        }
        cafe += quantidade;
    }

    public boolean podePagar(int custo) {
        return cafe >= custo;
    }

    // ===================== TABULEIRO =====================

    /** Instala o professor se a casa estiver livre e houver café. Só cobra se instalar. */
    public boolean adicionarProfessor(Professor professor, int linha, int coluna) {
        return professor != null
                && instalar(professor, professor.getCustoCafe(), linha, coluna);
    }

    public boolean adicionarCafeteira(Cafeteira cafeteira, int linha, int coluna) {
        return cafeteira != null
                && instalar(cafeteira, cafeteira.getCustoCafe(), linha, coluna);
    }

    /** Quem ocupa a casa, ou null se estiver vazia. */
    public Entidade getOcupante(int linha, int coluna) {
        return tabuleiro.getOcupante(linha, coluna);
    }

    public void removerDoPiso(int linha, int coluna) {
        tabuleiro.remover(linha, coluna);
    }

    private boolean instalar(Entidade entidade, int custo, int linha, int coluna) {
        if (!podePagar(custo)) {
            return false;
        }
        if (!tabuleiro.instalar(entidade, linha, coluna)) {
            return false; // casa inválida ou ocupada: não gasta nada
        }
        cafe -= custo;
        return true;
    }
}