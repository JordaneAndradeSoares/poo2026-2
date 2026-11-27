package com.poo2026_2.model.zumbis;

import com.poo2026_2.model.Entidade;

/**
 * Zumbi generico. A velocidade é dada em casas por "tick" de atualizacao.
 *
 * @author thoma
 */
public abstract class Zumbi extends Entidade {
    /** Intervalo padrao entre mordidas (cadencia de 1 s). */
    protected static final double INTERVALO_ATAQUE = 1.0;
    /** Velocidade padrao de todos os zumbis, em casas por segundo. */
    public static final double VELOCIDADE_PADRAO = 0.2;
    /** Dano padrao de uma mordida. */
    public static final int DANO_PADRAO = 100;
    /**
     * Dano de "hitkill": maior que a vida de qualquer professor (o Marcus tem
     * 4000).
     */
    public static final int DANO_HITKILL = 100_000;
    /* Dano do nacif */
    public static final int DANO_MEELE = 1370;
    // imunidades a efeitos de status
    private boolean imuneLentidao = false;
    private boolean imuneQueimadura = false;
    private boolean imuneParalisia = false;

    protected void setImuneLentidao(boolean imune) {
        this.imuneLentidao = imune;
        if (imune) { // limpa algum efeito que já estivesse ativo
            tempoLentidao = 0;
            fatorLentidao = 1.0;
        }
    }

    protected void setImuneQueimadura(boolean imune) {
        this.imuneQueimadura = imune;
        if (imune) {
            tempoQueimadura = 0;
            danoQueimaduraPorSegundo = 0;
            danoQueimaduraAcumulado = 0;
        }
    }

    protected void setImuneParalisia(boolean imune) {
        this.imuneParalisia = imune;
        if (imune) {
            tempoParalisia = 0;
        }
    }

    public boolean isImuneLentidao() {
        return imuneLentidao;
    }

    public boolean isImuneQueimadura() {
        return imuneQueimadura;
    }

    public boolean isImuneParalisia() {
        return imuneParalisia;
    }

    private double intervaloAtaque = INTERVALO_ATAQUE;
    private double tempoDesdeMordida;
    private double velocidade;
    private int dano;
    private double posicaoX;
    private boolean atacando;

    // efeitos de status
    private double fatorLentidao = 1.0;
    private double tempoLentidao = 0.0;
    private double danoQueimaduraPorSegundo = 0.0;
    private double tempoQueimadura = 0.0;
    private double danoQueimaduraAcumulado = 0.0;
    private double tempoParalisia = 0.0;

    public Zumbi(int vida, int linha, double velocidade, int dano) {
        super(vida, linha, 0);

        if (dano <= 0) {
            throw new IllegalArgumentException("O dano tem que ser maior que zero.");
        }
        if (velocidade <= 0 || !Double.isFinite(velocidade)) {
            throw new IllegalArgumentException("A velociadade tem que ser maior que zero.");
        }

        this.velocidade = velocidade;
        this.dano = dano;
        this.posicaoX = 0;
        this.atacando = false;
        this.tempoDesdeMordida = INTERVALO_ATAQUE; // a primeira mordida é imediata
    }

    public double getVelocidade() {
        return velocidade;
    }

    protected void setVelocidade(double velocidade) {
        this.velocidade = velocidade;
    }

    public int getDano() {
        return dano;
    }

    protected void setDano(int dano) {
        this.dano = dano;
    }

    /** Muda a cadencia da mordida (segundos entre uma mordida e a proxima). */
    protected void setIntervaloAtaque(double intervaloAtaque) {
        if (intervaloAtaque <= 0 || !Double.isFinite(intervaloAtaque)) {
            throw new IllegalArgumentException("O intervalo de ataque tem que ser positivo.");
        }
        this.intervaloAtaque = intervaloAtaque;
    }

    public double getIntervaloAtaque() {
        return intervaloAtaque;
    }

    public double getPosicaoX() {
        return posicaoX;
    }

    public void setPosicaoX(double posicaoX) {
        this.posicaoX = posicaoX;
    }

    public boolean estaAtacando() {
        return atacando;
    }

    /** Velocidade de verdade agora: a base, ja descontada a lentidao se houver. */
    public double getVelocidadeEfetiva() {
        return velocidade * (estaLento() ? fatorLentidao : 1.0);
    }

    /** O zumbi anda para a esquerda enquanto nao estiver atacando. */
    public void mover(double deltaTime) {
        atualizarEfeitos(deltaTime);
        if (!atacando && estaVivo() && !estaParalisado()) {
            posicaoX -= getVelocidadeEfetiva() * deltaTime;
        }
    }

    /** Reduz a velocidade em 30%. Nao acumula: um novo acerto so renova o tempo. */
    public void aplicarLentidao(double reducao, double duracao) {
        if (reducao <= 0 || reducao >= 1 || duracao <= 0 || !Double.isFinite(duracao)) {
            throw new IllegalArgumentException("Lentidao invalida: reducao entre 0 e 1 e duracao positiva.");
        }
        if (imuneLentidao) return ;
        double novoFator = 1.0 - reducao;
        fatorLentidao = estaLento() ? Math.min(fatorLentidao, novoFator) : novoFator;
        tempoLentidao = Math.max(tempoLentidao, duracao);
    }

    /**
     * Queima o zumbi: danoPorSegundo. Nao acumula: um novo acerto so renova o
     * tempo.
     */
    public void aplicarQueimadura(double danoPorSegundo, double duracao) {
        if (danoPorSegundo <= 0 || !Double.isFinite(danoPorSegundo) || duracao <= 0 || !Double.isFinite(duracao)) {
            throw new IllegalArgumentException("Queimadura invalida: dano e duracao tem que ser positivos.");
        }
        if (imuneQueimadura) return;
        danoQueimaduraPorSegundo = estaQueimando() ? Math.max(danoQueimaduraPorSegundo, danoPorSegundo)
                : danoPorSegundo;
        tempoQueimadura = Math.max(tempoQueimadura, duracao);
    }

    /**
     * Paralisa o zumbi: ele nao anda nem morde. Nao acumula: um novo acerto so
     * renova o tempo.
     */
    public void aplicarParalisia(double duracao) {
        if (duracao <= 0 || !Double.isFinite(duracao)) {
            throw new IllegalArgumentException("Paralisia invalida: a duracao tem que ser positiva.");
        }
        if (imuneParalisia) return;
        tempoParalisia = Math.max(tempoParalisia, duracao);
    }

    public boolean estaParalisado() {
        return tempoParalisia > 0;
    }

    public boolean estaLento() {
        return tempoLentidao > 0;
    }

    public boolean estaQueimando() {
        return tempoQueimadura > 0;
    }

    private void atualizarEfeitos(double deltaTime) {
        if (tempoParalisia > 0) {
            tempoParalisia = Math.max(0, tempoParalisia - deltaTime);
        }

        if (tempoLentidao > 0) {
            tempoLentidao = Math.max(0, tempoLentidao - deltaTime);
        }

        if (tempoQueimadura > 0) {
            double dt = Math.min(deltaTime, tempoQueimadura);
            tempoQueimadura -= dt;
            danoQueimaduraAcumulado += danoQueimaduraPorSegundo * dt;

            // a vida e inteira: aplica so a parte inteira e guarda o resto
            int inteiro = (int) (danoQueimaduraAcumulado + 1e-9);
            if (inteiro > 0) {
                danoQueimaduraAcumulado -= inteiro;
                receberDano(inteiro);
            }
            if (tempoQueimadura <= 0) {
                danoQueimaduraAcumulado = 0;
            }
        }
    }

    public void iniciarAtaque() {
        atacando = true;
    }

    public void pararAtaque() {
        atacando = false;
    }

    public boolean atacar(Entidade alvo, double deltaTime) {
        if (estaParalisado()) {
            return false; // paralisado nao morde
        }
        tempoDesdeMordida += deltaTime;
        if (tempoDesdeMordida >= intervaloAtaque) {
            tempoDesdeMordida = 0;
            alvo.receberDano(dano);
            return true;
        }
        return false;
    }
}