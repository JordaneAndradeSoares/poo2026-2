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
    private static final double TEMPO_DE_RECARGA = 8;   
    private static final double TEMPO_PARA_ARMAR = 15;  

    private static final int DANO_EXPLOSAO = 1800;

    public Nacif(int linha, int coluna) {
        super(linha, coluna, CUSTO_CAFE, TEMPO_PARA_ARMAR);
    }

    @Override
    public boolean atira() {
        return false;
    }

    public boolean estaArmada() {
        return estaCarregado();
    }

    @Override
    public void agir(List<Zumbi> zumbis) {
        if (!estaArmada()) {
            return;
        }

        boolean explodiu = false;

        for (Zumbi zumbi : zumbis) {
            if (zumbi.estaVivo() && zumbi.getLinha() == getLinha()
                    && (int) Math.floor(zumbi.getPosicaoX()) == getColuna()) {
                zumbi.receberDano(DANO_EXPLOSAO);
                explodiu = true;
            }
        }

        if (explodiu) {
            AudioManager.tocarEfeito("/audio/impacto.mp3");
            // a bomba se gasta; o jogo tira professores mortos do piso
            setVida(0);
        }
    }

    public double getTempoDeRecarga() {
        return this.TEMPO_DE_RECARGA;
    }
}
