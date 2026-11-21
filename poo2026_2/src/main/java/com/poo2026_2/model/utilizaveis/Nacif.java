package com.poo2026_2.model.utilizaveis;

import java.util.List;

import com.poo2026_2.audio.AudioManager;
import com.poo2026_2.model.Professor;
import com.poo2026_2.model.zumbis.Zumbi;

/**
 * Verilog: bomba. Leva 15 segundos para armar; depois de armada, explode
 * quando um zumbi entra na mesma casa, causando dano em todos os zumbis
 * dessa casa. Depois de explodir, some.
 *
 * Enquanto nao esta armado e um professor comum: os zumbis o mordem.
 *
 * @author thoma
 */
public class Nacif extends Professor {

    private static final int CUSTO_CAFE = 50;
    private static final double TEMPO_DE_RECARGA = 1.2;

    public Nacif(int linha, int coluna) {
        super(100, linha, coluna, CUSTO_CAFE, TEMPO_DE_RECARGA);
    }

    @Override
    public boolean atira() {
        return false;
    }

    public double getTempoDeRecarga() {
        return this.TEMPO_DE_RECARGA;
    }

    public static int getCustoCafeNacif() {
        return CUSTO_CAFE;
    }

    // ================================================================================
    // A habilidade especial do Nacif elimina todos os zumbis na área 3x3 ao redor dele e depois ele se sacrifica, recebendo dano igual à sua vida

    // Elimina todos os zumbis na área e o próprio Nacif.
    public void explodir(List<Zumbi> zumbis) {

        // explodindo os zumbis na área 3x3
        for (Zumbi zumbi : zumbis) {

            if (zumbi.estaVivo() && estaNaArea(zumbi)) {
                zumbi.receberDano(zumbi.getVida());
            }
        }

        // tocando som da explosão
        AudioManager.tocarEfeito("/audio/explosao.mp3"); // fonte (acesso em 10/11/2026): https://pixabay.com/pt/sound-effects/epic-cinematic-explosion-454857/

        // sacrificando o Nacif
        receberDano(getVida());
    }

    // Verifica se existe algum zumbi na área 3x3
    public boolean existeZumbiNaArea(List<Zumbi> zumbis) {

        for (Zumbi zumbi : zumbis) {
            if (zumbi.estaVivo() && estaNaArea(zumbi)) {
                return true;
            }
        }

        return false;
    }

    // verifica se o zumbi está na área 3x3 ao redor do Nacif
    private boolean estaNaArea(Zumbi zumbi) {

        int colunaZumbi = (int) Math.floor(zumbi.getPosicaoX());
        return Math.abs(zumbi.getLinha() - getLinha()) <= 1 && Math.abs(colunaZumbi - getColuna()) <= 1;
    }
}