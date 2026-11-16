package com.poo2026_2.model.zumbis;

import com.poo2026_2.model.Entidade;

/**
 * Zumbi generico. A velocidade é dada em casas por "tick" de atualizacao.
 *
 * @author thoma
 */
public abstract class Zumbi extends Entidade {
    protected static final double INTERVALO_ATAQUE = 1.0;
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
        this.tempoDesdeMordida = INTERVALO_ATAQUE;   // a primeira mordida é imediata
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
        if (!atacando && estaVivo()) {
            posicaoX -= getVelocidadeEfetiva() * deltaTime;
        }
    }

    /** Reduz a velocidade em 30%. Nao acumula: um novo acerto so renova o tempo. */
    public void aplicarLentidao(double reducao, double duracao) {
        if (reducao <= 0 || reducao >= 1 || duracao <= 0 || !Double.isFinite(duracao)) {
            throw new IllegalArgumentException("Lentidao invalida: reducao entre 0 e 1 e duracao positiva.");
        }
        double novoFator = 1.0 - reducao;
        fatorLentidao = estaLento() ? Math.min(fatorLentidao, novoFator) : novoFator;
        tempoLentidao = Math.max(tempoLentidao, duracao);
    }

    /** Queima o zumbi: danoPorSegundo. Nao acumula: um novo acerto so renova o tempo. */
    public void aplicarQueimadura(double danoPorSegundo, double duracao) {
        if (danoPorSegundo <= 0 || !Double.isFinite(danoPorSegundo) || duracao <= 0 || !Double.isFinite(duracao)) {
            throw new IllegalArgumentException("Queimadura invalida: dano e duracao tem que ser positivos.");
        }
        danoQueimaduraPorSegundo = estaQueimando() ? Math.max(danoQueimaduraPorSegundo, danoPorSegundo) : danoPorSegundo;
        tempoQueimadura = Math.max(tempoQueimadura, duracao);
    }

    public boolean estaLento() {
        return tempoLentidao > 0;
    }

    public boolean estaQueimando() {
        return tempoQueimadura > 0;
    }

    private void atualizarEfeitos(double deltaTime) {
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
    tempoDesdeMordida += deltaTime;
    if (tempoDesdeMordida >= INTERVALO_ATAQUE) {
        tempoDesdeMordida = 0;
        alvo.receberDano(dano);
        return true;
    }
    return false;
}
}
