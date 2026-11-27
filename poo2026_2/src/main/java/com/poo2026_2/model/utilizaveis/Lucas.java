package com.poo2026_2.model.utilizaveis;

import java.util.List;

import com.poo2026_2.audio.AudioManager;
import com.poo2026_2.model.Professor;
import com.poo2026_2.model.zumbis.Zumbi;

/**
 * Skate: corpo a corpo. Quando um zumbi chega ao alcance, da uma skatada que
 * mata na hora (hitkill) e depois fica 17 segundos carregando. Ja entra em
 * campo carregado.
 *
 * A skatada nao e instantanea: o golpe comeca (a animacao sai do repouso e
 * levanta o skate) e o dano so e aplicado no momento do impacto, que e o
 * frame ~11 da animacao. Assim o skate acerta o zumbi na tela e nao o ar.
 * Enquanto carrega, o Lucas fica abaixado no chao e so levanta o skate
 * quando faltam poucos instantes para a carga acabar (ver SpriteLucas).
 *
 * @author thoma
 */
public class Lucas extends Professor {

    private static final int CUSTO_CAFE = 100;
    private static final double TEMPO_DE_RECARGA = 8; 
    private static final double RECARGA_DO_GOLPE = 17; 

    private static final double ALCANCE_EXTRA_EM_CASAS = 0.0;

    /**
     * Segundos entre o inicio da skatada e o impacto. A animacao toca a
     * 12 frames por segundo e o impacto e no 5o frame depois do inicio do golpe
     * (frames 6 a 10 do sprite), entao 5/12 s.
     */
    public static final double ATRASO_DO_IMPACTO = 5.0 / 12.0;

    private Zumbi alvoDoGolpe;
    private double tempoDoGolpe;

    public Lucas(int linha, int coluna) {
        super(linha, coluna, CUSTO_CAFE, RECARGA_DO_GOLPE);
        carregarTotalmente();   // o primeiro golpe nao precisa esperar
    }

    @Override
    public boolean atira() {
        return false;
    }

    /** true entre o inicio da skatada e o impacto. */
    public boolean estaGolpeando() {
        return alvoDoGolpe != null;
    }

    @Override
    public void atualizarTempo(double deltaTime) {
        super.atualizarTempo(deltaTime);

        if (alvoDoGolpe == null) {
            return;
        }

        if (!estaVivo()) {   // morreu no meio do golpe: o golpe se perde
            alvoDoGolpe = null;
            return;
        }

        tempoDoGolpe += deltaTime;

        if (tempoDoGolpe >= ATRASO_DO_IMPACTO) {
            if (alvoDoGolpe.estaVivo()) {
                alvoDoGolpe.receberDano(Zumbi.DANO_MEELE);
                AudioManager.tocarEfeito("/audio/tiros/pancada.mp3");
            }
            alvoDoGolpe = null;
            tempoDoGolpe = 0;
        }
    }

    @Override
    public void agir(List<Zumbi> zumbis) {
        if (alvoDoGolpe != null || !estaCarregado()) {
            return;
        }

        Zumbi alvo = zumbiMaisProximoAoAlcance(zumbis);

        if (alvo == null) {
            return;
        }

        alvoDoGolpe = alvo;    // o dano sai no impacto (atualizarTempo)
        tempoDoGolpe = 0;
        reiniciarCarga();      // a recarga de 17 s comeca junto com o golpe
    }

    private Zumbi zumbiMaisProximoAoAlcance(List<Zumbi> zumbis) {
        double inicio = getColuna();
        double fim = getColuna() + 1 + ALCANCE_EXTRA_EM_CASAS;
        Zumbi maisProximo = null;

        for (Zumbi zumbi : zumbis) {
            if (!zumbi.estaVivo() || zumbi.getLinha() != getLinha()) {
                continue;
            }
            double x = zumbi.getPosicaoX();
            if (x >= inicio && x < fim
                    && (maisProximo == null || x < maisProximo.getPosicaoX())) {
                maisProximo = zumbi;
            }
        }

        return maisProximo;
    }

    public double getTempoDeRecarga() {
        return this.TEMPO_DE_RECARGA;
    }
}
