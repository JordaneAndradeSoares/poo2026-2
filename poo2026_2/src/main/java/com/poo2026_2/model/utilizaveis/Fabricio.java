package com.poo2026_2.model.utilizaveis;

import java.util.ArrayList;
import java.util.List;

import com.poo2026_2.model.Professor;
import com.poo2026_2.model.Tiro;

/**
 * Atira Scala. Tiro padrao, disparado em 3 linhas ao mesmo tempo
 * (a dele, a de cima e a de baixo).
 *
 * @author thoma
 */
public class Fabricio extends Professor {

    private static final int CUSTO_CAFE = 300;
    private static final double TEMPO_DE_RECARGA = 8;  
    private static final double CADENCIA = 1.5;     

    private static final int DANO = 100;
    private static final double VELOCIDADE_TIRO = 5.0;

    private static final int ALCANCE_EM_LINHAS = 1;  

    public Fabricio(int linha, int coluna) {
        super(linha, coluna, CUSTO_CAFE, CADENCIA);
    }

    @Override
    public Tiro criarTiro() {
        return novoTiro(
            VELOCIDADE_TIRO,
            DANO,
            "/images/tiros/scala.png",
            "nenhum",
            "/audio/tiros/scala.mp3");
    }

    /** Tres tiros, um em cada linha. O GerenciadorDeJogo descarta os que cairiam fora do tabuleiro. */
    @Override
    public List<Tiro> criarTiros() {
        List<Tiro> tiros = new ArrayList<>();

        for (int deslocamento = -ALCANCE_EM_LINHAS; deslocamento <= ALCANCE_EM_LINHAS; deslocamento++) {
            Tiro tiro = criarTiro();
            tiro.setY(getLinha() + deslocamento);
            tiros.add(tiro);
        }

        return tiros;
    }

    @Override
    public int getAlcanceEmLinhas() {
        return ALCANCE_EM_LINHAS;
    }

    public double getTempoDeRecarga() {
        return this.TEMPO_DE_RECARGA;
    }
}
