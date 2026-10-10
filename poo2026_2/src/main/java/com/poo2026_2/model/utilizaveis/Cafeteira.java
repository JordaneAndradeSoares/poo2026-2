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
    private int custoCafe;
    private int cafePorCiclo;
    private double intervaloProducao;
    private double tempoUltimaProducao;
    private double raioProducao;

    public Cafeteira(int vida, int linha, int coluna, int cafePorCiclo,
            double intervaloProducao, double raioProducao) {
        this(vida, linha, coluna, cafePorCiclo, intervaloProducao, raioProducao, CUSTO_CAFE_PADRAO);
    }

    public Cafeteira(int vida, int linha, int coluna, int cafePorCiclo,
            double intervaloProducao, double raioProducao, int custoCafe) {
        super(vida, linha, coluna);
        this.cafePorCiclo = cafePorCiclo;
        this.intervaloProducao = intervaloProducao;
        this.raioProducao = raioProducao;
        this.tempoUltimaProducao = 0.0;
        this.custoCafe = custoCafe;
    }

    public int getCustoCafe() {
        return custoCafe;
    }

    public int getCafePorCiclo() {
        return cafePorCiclo;
    }

    public void setCafePorCiclo(int cafePorCiclo) {
        this.cafePorCiclo = cafePorCiclo;
    }

    public double getIntervaloProducao() {
        return intervaloProducao;
    }

    public void setIntervaloProducao(double intervaloProducao) {
        this.intervaloProducao = intervaloProducao;
    }

    public double getTempoUltimaProducao() {
        return tempoUltimaProducao;
    }

    public void setTempoUltimaProducao(double tempoUltimaProducao) {
        this.tempoUltimaProducao = tempoUltimaProducao;
    }

    public double getRaioProducao() {
        return raioProducao;
    }

    public void setRaioProducao(double raioProducao) {
        this.raioProducao = raioProducao;
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
        return new Cafe(cafePorCiclo, 0.0, 0.0, 10.0);
    }
}
