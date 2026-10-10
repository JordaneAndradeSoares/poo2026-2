package com.poo2026_2.model.zumbis;

import com.poo2026_2.model.Zumbi;

/**
 * Comum, mas quando o telefone quebra (vida chega na metade)
 * fica rapido, perigoso e estressado.
 *
 * @author thoma
 */
public class Viciado extends Zumbi {

    private static final int VIDA_INICIAL = 100;

    private boolean estressado;

    public Viciado(int linha) {
        super(VIDA_INICIAL, linha, 0.006, 10);
        this.estressado = false;
    }

    public boolean estaEstressado() {
        return estressado;
    }

    @Override
    public void receberDano(int dano) {
        super.receberDano(dano);

        // telefone quebrou: ele fica furioso (so acontece uma vez)
        if (!estressado && estaVivo() && getVida() <= VIDA_INICIAL / 2) {
            estressado = true;
            setVelocidade(getVelocidade() * 2.5);
            setDano(getDano() * 2);
        }
    }
}
