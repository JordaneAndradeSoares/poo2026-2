package com.poo2026_2.model.utilizaveis;

import java.util.List;

import com.poo2026_2.audio.AudioManager;
import com.poo2026_2.model.Professor;
import com.poo2026_2.model.zumbis.Zumbi;

/**
 * Skate: corpo a corpo e habilidade especial de hitkill a distância.
 * @author thoma
 */
public class Lucas extends Professor {

    private static final int CUSTO_CAFE = 150;
    private static final double TEMPO_DE_RECARGA = 8;
    private static final double RECARGA_DO_GOLPE = 17;
    private static final double ALCANCE_EXTRA_EM_CASAS = 1.0;

    private double tempoDesdeUltimaHabilidade = TEMPO_DE_RECARGA;

    public Lucas(int linha, int coluna) {
        super(linha, coluna, CUSTO_CAFE, RECARGA_DO_GOLPE);
        carregarTotalmente();
    }

    @Override
    public boolean atira() {
        return false;
    }

    @Override
    public void agir(List<Zumbi> zumbis) {
        if (!estaCarregado()) {
            return;
        }

        Zumbi alvo = zumbiMaisProximoAoAlcance(zumbis);

        if (alvo == null) {
            return;
        }

        alvo.receberDano(alvo.getVida());   // hitkill
        reiniciarCarga();
        AudioManager.tocarEfeito("/audio/impacto.mp3");
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

    public static int getCustoCafeLucas() {
        return CUSTO_CAFE;
    }

    // ================================================================================
    // A habilidade especial do Lucas causa morte instantânea do zumbi atingido e depois ele entra em tempo de recarga

    public void habilidadeEspecial(Zumbi zumbiAlvo) {

        if (!habilidadePronta()) {
            return;
        }

        if (zumbiAlvo == null || !zumbiAlvo.estaVivo()) {
            return;
        }

        zumbiAlvo.receberDano(zumbiAlvo.getVida());
        this.tempoDesdeUltimaHabilidade = 0;
    }

    // ================================================================================
    // Métodos para gerenciar o tempo de recarga da habilidade especial

    public void atualizarRecarga(double deltaTime) {
        if (tempoDesdeUltimaHabilidade < TEMPO_DE_RECARGA) {
            tempoDesdeUltimaHabilidade += deltaTime;

            if (tempoDesdeUltimaHabilidade > TEMPO_DE_RECARGA) {
                tempoDesdeUltimaHabilidade = TEMPO_DE_RECARGA;
            }
        }
    }

    public boolean habilidadePronta() {
        return tempoDesdeUltimaHabilidade >= TEMPO_DE_RECARGA;
    }
}
