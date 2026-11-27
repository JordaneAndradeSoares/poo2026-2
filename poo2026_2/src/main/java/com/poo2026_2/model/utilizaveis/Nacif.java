package com.poo2026_2.model.utilizaveis;

import java.util.ArrayList;
import java.util.List;

import com.poo2026_2.audio.AudioManager;
import com.poo2026_2.model.Professor;
import com.poo2026_2.model.zumbis.Zumbi;

/**
 * Verilog: bomba. Quando e colocado, toca a animacao (12 frames) uma unica
 * vez; no fim dela explode e mata todos os zumbis da casa dele e das 8 casas
 * ao redor (area de 3x3, diagonais incluidas). Depois de explodir, some.
 *
 * Enquanto a animacao nao termina ele e um professor comum: os zumbis o mordem
 * e, se ele morrer antes, nao explode.
 *
 * Quem desenha a explosao e o JogoController; aqui fica so a regra.
 *
 * @author thoma
 */
public class Nacif extends Professor {

    private static final int CUSTO_CAFE = 150;
    private static final double TEMPO_DE_RECARGA = 30;

    /** Segundos entre colocar o Nacif e a explosao = duracao da animacao dele. */
    public static final double TEMPO_ATE_EXPLODIR = 2.0;

    /** Quantas casas a explosao alcanca em cada direcao (1 = area de 3x3). */
    private static final int RAIO_DA_EXPLOSAO = 1;

    public Nacif(int linha, int coluna) {
        super(linha, coluna, CUSTO_CAFE, TEMPO_ATE_EXPLODIR);
    }

    @Override
    public boolean atira() {
        return false;
    }

    /** true quando a animacao acabou e ele esta pronto para explodir. */
    public boolean estaArmada() {
        return estaCarregado();
    }

    /** Quanto da animacao ja passou: 0 (acabou de ser colocado) ate 1 (hora de explodir). */
    public double getProgressoDaAnimacao() {
        double progresso = 1.0 - getTempoRestanteDeCarga() / TEMPO_ATE_EXPLODIR;
        return Math.max(0.0, Math.min(1.0, progresso));
    }

    /**
     * Explode: mata todo zumbi vivo na area de 3x3 em volta dele (a casa do
     * zumbi e a mesma usada para as mordidas: floor da posicaoX) e se gasta.
     * Nao faz nada se ainda nao estiver armado.
     *
     * @param zumbis             todos os zumbis da partida
     * @param colunasDoTabuleiro zumbis alem da ultima coluna ainda nao entraram em campo
     * @return os zumbis que morreram na explosao (para a tela queima-los)
     */
    public List<Zumbi> explodir(List<Zumbi> zumbis, int colunasDoTabuleiro) {
        List<Zumbi> mortos = new ArrayList<>();
        if (!estaArmada()) {
            return mortos;
        }

        for (Zumbi zumbi : zumbis) {
            if (zumbi.estaVivo() && estaNaArea(zumbi, colunasDoTabuleiro)) {
                zumbi.receberDano(Zumbi.DANO_MEELE);
                mortos.add(zumbi);
            }
        }

        AudioManager.tocarEfeito("/audio/kaboom.mp3");
        setVida(0);   // a bomba se gasta; o jogo tira professores mortos do piso
        return mortos;
    }

    private boolean estaNaArea(Zumbi zumbi, int colunasDoTabuleiro) {
        int colunaDoZumbi = (int) Math.floor(zumbi.getPosicaoX());
        return zumbi.getPosicaoX() < colunasDoTabuleiro
                && Math.abs(zumbi.getLinha() - getLinha()) <= RAIO_DA_EXPLOSAO
                && Math.abs(colunaDoZumbi - getColuna()) <= RAIO_DA_EXPLOSAO;
    }

    public double getTempoDeRecarga() {
        return TEMPO_DE_RECARGA;
    }
}
