/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.poo2026_2.model;

import java.util.List;
import java.util.function.Consumer;

import com.poo2026_2.model.zumbis.Zumbi;

/**
 *
 * @author thoma Professor generico. Cada professor concreto define o seu tiro
 * (ou o seu comportamento proprio, no caso de tanque, bomba e corpo a corpo).
 */
public abstract class Professor extends Entidade {

    /** Vida que todo professor tem, a menos que o construtor diga outra. */
    public static final int VIDA_PADRAO = 300;

    private double tempoDesdeUltimoTiro = 0.0;

    private int custoCafe;
    private double cadenciaTiro; 

    /** Professor com a vida padrao. */
    public Professor(int linha, int coluna, int custoCafe, double cadenciaTiro) {
        this(VIDA_PADRAO, linha, coluna, custoCafe, cadenciaTiro);
    }

    public Professor(int vida, int linha, int coluna, int custoCafe, double cadenciaTiro) {
        super(vida, linha, coluna);

        if (custoCafe < 0) {
            throw new IllegalArgumentException("O custo nao pode ser negativo .");
        }
        if (cadenciaTiro < 0 || !Double.isFinite(cadenciaTiro)) {
            throw new IllegalArgumentException("A cadencia de tiro nao pode ser negativa e tem que ser finita.");
        }

        this.custoCafe = custoCafe;
        this.cadenciaTiro = cadenciaTiro;
    }

    public int getCustoCafe() {
        return this.custoCafe;
    }

    public double getCadenciaTiro() {
        return cadenciaTiro;
    }

    /** Indica se este professor utiliza disparos convencionais. */
    public boolean atira() {
        return cadenciaTiro > 0;
    }

    /** Cada professor que atira define o seu próprio projétil. */
    public Tiro criarTiro() {
        throw new UnsupportedOperationException(getClass().getSimpleName() + " nao dispara tiros.");
    }

    /** Atualiza a recarga e informa se o professor já pode atacar. */
    public boolean podeAtacar(double deltaTime) {
        if (deltaTime > 0 && Double.isFinite(deltaTime)) {
            tempoDesdeUltimoTiro += deltaTime;
        }
        return estaCarregado();
    }

    /** Reinicia a recarga após uma habilidade ou disparo. */
    public void reiniciarCadencia() {
        reiniciarCarga();
    }


    /** Todos os tiros de um disparo. */
    public List<Tiro> criarTiros() {
        return List.of(criarTiro());
    }

    public int getAlcanceEmLinhas() {
        return 0;
    }

    public boolean podeAtacar() {
        return atira() && estaCarregado();
    }

    public List<Tiro> atacar() {
        reiniciarCarga();
        return criarTiros();
    }

    /** Cria um tiro na posicao do professor com os valores informados.*/
    protected Tiro novoTiro(double velocidade, int dano, String sprite, String efeitoVisual, String som) {
        return novoTiro(velocidade, dano, sprite, efeitoVisual, som, null);
    }

    /**
     * Igual ao anterior, mas com um efeito aplicado ao zumbi atingido
     * (lentidao, queimadura...).
     */
    protected Tiro novoTiro(double velocidade, int dano, String sprite, String efeitoVisual, String som, Consumer<Zumbi> efeito) {
        return new Tiro(getColuna(), getLinha(), velocidade, dano, sprite, efeitoVisual, som, efeito);
    }

    public void atualizarTempo(double deltaTime) {
        tempoDesdeUltimoTiro += deltaTime;
    }

    protected boolean estaCarregado() {
        return estaVivo() && tempoDesdeUltimoTiro >= cadenciaTiro;
    }

    protected void reiniciarCarga() {
        tempoDesdeUltimoTiro = 0.0;
    }

    protected void carregarTotalmente() {
        tempoDesdeUltimoTiro = cadenciaTiro;
    }

    /**
     * Acao propria do professor a cada tick, alem de atirar. Por padrao nao faz nada.
     *
     * @param zumbis todos os zumbis da partida
     */
    public void agir(List<Zumbi> zumbis) {
    }
}
