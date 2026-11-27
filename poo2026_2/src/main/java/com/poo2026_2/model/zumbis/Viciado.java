package com.poo2026_2.model.zumbis;

/**
 * Comum, mas tem um telefone (150 de vida) alem do corpo (200). Quando o
 * telefone quebra ele fica rapido, perigoso e estressado: morde a cada 0,5 s.
 *
 * @author thoma
 */
public class Viciado extends Zumbi {

    private static final int VIDA_DO_CORPO = 200;
    private static final int VIDA_DO_TELEFONE = 150;
    private static final double INTERVALO_ESTRESSADO = 0.5;

    private boolean estressado;

    public Viciado(int linha) {
        super(VIDA_DO_CORPO + VIDA_DO_TELEFONE, linha, VELOCIDADE_PADRAO, DANO_PADRAO);
        this.estressado = false;
    }

    public boolean estaEstressado() {
        return estressado;
    }

    @Override
    public void receberDano(int dano) {
        super.receberDano(dano);

        // telefone quebrou (a vida passou dos 200 do corpo): ele fica furioso, so uma vez
        if (!estressado && estaVivo() && getVida() <= VIDA_DO_CORPO) {
            estressado = true;
            setVelocidade(getVelocidade() * 2.5);
            setIntervaloAtaque(INTERVALO_ESTRESSADO);
        }
    }
}