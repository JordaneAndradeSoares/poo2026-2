package com.poo2026_2.model.utilizaveis;

import java.util.ArrayList;
import java.util.List;

import com.poo2026_2.model.Professor;
import com.poo2026_2.model.Tiro;
import com.poo2026_2.model.zumbis.Zumbi;

/**
 * Atira ... (linguagem ainda indefinida). (valores provisorios: ajustar no balanceamento)
 *
 * @author thoma
 */
public class Thais extends Professor {

    private static final int CUSTO_CAFE = 300;
    private static final double TEMPO_DE_RECARGA = 8;

    public Thais(int linha, int coluna) {
        super(
            100, 
            linha, 
            coluna, 
            CUSTO_CAFE, 
            1.5
        );
    }

    @Override
    public Tiro criarTiro() {
        return novoTiro(
            5.0, 
            20,
            "/images/tiros/go.png",
            "nenhum",
            "/audio/tiros/go.mp3"
        );
    }

    public static int getCustoCafeThais() {
        return CUSTO_CAFE;    
    }

    public double getTempoDeRecarga() {
        return this.TEMPO_DE_RECARGA;
    }

    // ==================================================================
    // habilidade da thais

    public List<Tiro> atacarTresLinhas(int primeiraLinha, int ultimaLinha) {

        List<Tiro> tiros = new ArrayList<>();

        for (int linha = primeiraLinha; linha <= ultimaLinha; linha++) {

            Tiro tiro = criarTiro();

            tiro.setX(getColuna() + 0.5);
            tiro.setY(linha);

            tiros.add(tiro);
        }

        return tiros;
    }

    // Verifica se existe pelo menos um zumbi vivo em qualquer uma das três linhas da Thais, desde que esteja à sua frente
    public boolean haZumbiNasTresLinhas(Thais thais, List<Zumbi> zumbis) {

        double xProfessor = thais.getColuna() + 0.5;

        for (Zumbi z : zumbis) {

            if (!z.estaVivo()) {
                continue;
            }

            boolean linhaValida = Math.abs(z.getLinha() - thais.getLinha()) <= 1;
            boolean estaNaFrente = z.getPosicaoX() >= xProfessor;

            if (linhaValida && estaNaFrente) {
                return true;
            }
        }

        return false;
    }
}
