package com.poo2026_2.model.utilizaveis;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import com.poo2026_2.model.Professor;
import com.poo2026_2.model.zumbis.Zumbi;

/**
 * Laser constante. Enquanto houver zumbi na mira, o Fabricio mantem o laser
 * ligado: um feixe para o PRIMEIRO zumbi de cada linha que ele alcanca (a dele,
 * a de cima e a de baixo). Linha sem zumbi nao recebe feixe.
 *
 * O dano e continuo (por segundo), nao por tiro.
 *
 * @author thoma
 */
public class Fabricio extends Professor {

    private static final int CUSTO_CAFE = 300;
    private static final double TEMPO_DE_RECARGA = 8;

    /** Mesmo dano por segundo que o tiro antigo (100 a cada 1,5 s) em cada linha. */
    private static final double DANO_POR_SEGUNDO = 70 / 1.5;

    private static final int ALCANCE_EM_LINHAS = 1;

    /** Dano fracionario acumulado em cada zumbi (a vida e inteira). */
    private final Map<Zumbi, Double> danoAcumulado = new IdentityHashMap<>();

    public Fabricio(int linha, int coluna) {
        super(linha, coluna, CUSTO_CAFE, 0);
    }

    /** O laser nao tem cadencia: so depende de haver alvo. */
    @Override
    public boolean atira() {
        return true;
    }

    @Override
    public boolean podeAtacar() {
        return estaVivo();
    }

    /**
     * O primeiro zumbi (o mais proximo do professor) de cada linha ao alcance.
     * Linhas sem zumbi ficam de fora, entao a lista tem de 0 a 3 alvos.
     *
     * @param colunasVisiveis zumbis a partir desta coluna ainda nao estao na tela
     */
    public List<Zumbi> escolherAlvos(List<Zumbi> zumbis, int colunasVisiveis) {
        List<Zumbi> alvos = new ArrayList<>();

        for (int deslocamento = -ALCANCE_EM_LINHAS; deslocamento <= ALCANCE_EM_LINHAS; deslocamento++) {
            int linha = getLinha() + deslocamento;
            Zumbi primeiro = null;

            for (Zumbi zumbi : zumbis) {
                if (zumbi.estaVivo()
                        && zumbi.getLinha() == linha
                        && zumbi.getPosicaoX() >= getColuna()
                        && zumbi.getPosicaoX() < colunasVisiveis
                        && (primeiro == null || zumbi.getPosicaoX() < primeiro.getPosicaoX())) {
                    primeiro = zumbi;
                }
            }

            if (primeiro != null) {
                alvos.add(primeiro);
            }
        }
        return alvos;
    }

    /** Aplica o dano do laser durante deltaTime segundos em cada alvo. */
    public void aplicarLaser(List<Zumbi> alvos, double deltaTime) {
        danoAcumulado.keySet().removeIf(zumbi -> !alvos.contains(zumbi));

        for (Zumbi alvo : alvos) {
            double acumulado = danoAcumulado.getOrDefault(alvo, 0.0) + DANO_POR_SEGUNDO * deltaTime;
            int inteiro = (int) acumulado;
            if (inteiro > 0) {
                alvo.receberDano(inteiro);
                acumulado -= inteiro;
            }
            danoAcumulado.put(alvo, acumulado);
        }
    }

    /** Laser desligado: descarta o dano fracionario que sobrou. */
    public void pararLaser() {
        danoAcumulado.clear();
    }

    @Override
    public int getAlcanceEmLinhas() {
        return ALCANCE_EM_LINHAS;
    }

    public double getTempoDeRecarga() {
        return this.TEMPO_DE_RECARGA;
    }
}
