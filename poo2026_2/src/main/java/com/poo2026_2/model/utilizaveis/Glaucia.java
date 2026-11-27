package com.poo2026_2.model.utilizaveis;

import com.poo2026_2.model.Professor;
import com.poo2026_2.model.Tiro;
import com.poo2026_2.model.zumbis.Zumbi;
import java.util.List;

/**
 * Atira JavaScript. Disparo de fogo: alem do dano, queima o zumbi.
 *
 * @author thoma
 */
public class Glaucia extends Professor {

    private static final int CUSTO_CAFE = 225;
    private static final double TEMPO_DE_RECARGA = 15;
    private static final double CADENCIA = 1.5;

    private static final double VELOCIDADE_TIRO = 5.0;

    private static final int DANO = 45;
    private static final double DANO_QUEIMADURA_POR_SEGUNDO = 15;
    private static final double DURACAO_QUEIMADURA = 2.0;

    private static final double ALCANCE_EM_COLUNAS = 2.0; 

    /**
     * Cone: atinge a linha dela e as duas vizinhas, ate o alcance, com ou sem
     * zumbi la.
     */
    public void incendiar(List<Zumbi> zumbis, int colunas) {
        double inicio = getColuna();
        double fim = Math.min(colunas, getColuna() + 1 + ALCANCE_EM_COLUNAS);

        for (Zumbi zumbi : zumbis) {
            if (zumbi.estaVivo()
                    && Math.abs(zumbi.getLinha() - getLinha()) <= 1
                    && zumbi.getPosicaoX() >= inicio
                    && zumbi.getPosicaoX() < fim) {
                zumbi.receberDano(DANO);
                zumbi.aplicarQueimadura(DANO_QUEIMADURA_POR_SEGUNDO, DURACAO_QUEIMADURA);
            }
        }
    }

    public Glaucia(int linha, int coluna) {
        super(linha, coluna, CUSTO_CAFE, CADENCIA);
    }

    @Override
    public Tiro criarTiro() {
        return novoTiro(
                VELOCIDADE_TIRO,
                DANO,
                "",
                "fogo",
                "/audio/tiros/fogo.mp3",
                zumbi -> zumbi.aplicarQueimadura(DANO_QUEIMADURA_POR_SEGUNDO, DURACAO_QUEIMADURA));
    }

    public double getAlcanceEmColunas() {
        return ALCANCE_EM_COLUNAS;
    }

    public double getTempoDeRecarga() {
        return TEMPO_DE_RECARGA;
    }
}
