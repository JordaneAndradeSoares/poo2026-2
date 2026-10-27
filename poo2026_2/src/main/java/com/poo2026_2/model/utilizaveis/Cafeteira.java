package com.poo2026_2.model.utilizaveis;

import com.poo2026_2.model.Entidade;

/**
 * Representa uma cafeteira responsável por produzir cafés.
 *
 * A cafeteira produz cafés após um determinado intervalo de tempo e possui uma
 * área ao seu redor onde os cafés poderão ser posicionados.
 *
 * @author Rafael
 */
public class Cafeteira extends Entidade {

    private static final int CUSTO_CAFE_PADRAO = 50;
    private static final double TEMPO_DE_RECARGA = 8;

    private int custoCafe;
    private int cafePorCiclo;

    private double intervaloProducao;
    private double tempoUltimaProducao;

    public Cafeteira(int vida, int linha, int coluna, int cafePorCiclo, double intervaloProducao) {
        
        this(vida, linha, coluna, cafePorCiclo, intervaloProducao, TEMPO_DE_RECARGA, CUSTO_CAFE_PADRAO);
    }

    public Cafeteira(int vida, int linha, int coluna, int cafePorCiclo, double intervaloProducao, double tempoDeRecarga, int custoCafe) {

        super(vida, linha, coluna);

        if (cafePorCiclo <= 0) {
            throw new IllegalArgumentException("A quantidade de cafe por ciclo tem que ser positiva.");
        }

        if (intervaloProducao <= 0 || !Double.isFinite(intervaloProducao)) {
            throw new IllegalArgumentException("O intervalo de producao tem que ser positivo e finito.");
        }

        if (intervaloProducao <= 0 || !Double.isFinite(tempoDeRecarga)) {
            throw new IllegalArgumentException("O tempo de recarga tem que ser positivo e finito.");
        }

        if (custoCafe < 0) {
            throw new IllegalArgumentException("O custo de cafe nao pode ser negativo.");
        }

        this.cafePorCiclo = cafePorCiclo;
        this.intervaloProducao = intervaloProducao;
        this.tempoUltimaProducao = 0.0;
        this.custoCafe = custoCafe;
    }

    public int getCustoCafe() {
        return this.custoCafe;
    }

    public int getCafePorCiclo() {
        return this.cafePorCiclo;
    }

    public double getTempoDeRecarga() {
        return this.TEMPO_DE_RECARGA;
    }

    public double getIntervaloProducao() {
        return this.intervaloProducao;
    }

    public double getTempoUltimaProducao() {
        return this.tempoUltimaProducao;
    }

    /**
     * Atualiza o tempo desde a última produção.
     *
     * @param deltaTime tempo passado desde a última atualização
     * @return true quando a cafeteira estiver pronta para produzir
     */
    public boolean podeProduzir(double deltaTime) {
        tempoUltimaProducao += deltaTime;

        if (tempoUltimaProducao >= intervaloProducao) {
            tempoUltimaProducao = 0.0;
            return true;
        }

        return false;
    }

    /**
     * Cria um novo café.
     *
     * A posição do café será definida posteriormente pelo controlador do jogo.
     *
     * @return novo café produzido pela cafeteira
     */

    public Cafe produzirCafe() {

        return new Cafe(cafePorCiclo, 0.0, 0.0, 8.0);
    }
}