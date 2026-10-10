package com.poo2026_2.model.zumbis;

public class Zumbi {

    private double velocidadeDoZumbi;
    private double danoDoZumbi;

    private double vidaDoZumbi;
    private long cadenciaDoZumbi;
    private long ultimaMordida; // Atributo para armazenar o tempo do último ataque

    private double posicaoXDoZumbi;
    private double posicaoYDoZumbi;

    private boolean zumbiAtacando;
    private boolean ZumbiFoiAtingido;

    public Zumbi(double velocidadeDoZumbi, double danoDoZumbi, double vidaDoZumbi, long cadenciaDoZumbi) {
        this.velocidadeDoZumbi = velocidadeDoZumbi;
        this.danoDoZumbi = danoDoZumbi;

        this.vidaDoZumbi = vidaDoZumbi; // Valor inicial de vida do zumbi
        this.cadenciaDoZumbi = cadenciaDoZumbi; // Espaço entre os ataques em nanossegundos (lembre de usar o L no final)

        this.posicaoXDoZumbi = 0;
        this.posicaoYDoZumbi = 0;

        this.zumbiAtacando = false;
        this.ZumbiFoiAtingido = false;
    } 
    
    public boolean zumbiEstaVivo() {return this.vidaDoZumbi > 0;}
    public boolean foiAtingido() {return this.ZumbiFoiAtingido;}

    // gets
    public double getPosicaoXDoZumbi() {return posicaoXDoZumbi;}
    public double getPosicaoYDoZumbi() {return posicaoYDoZumbi;}
    public double getVelocidadeDoZumbi() {return velocidadeDoZumbi;}
    public double getCadenciaDoZumbi() {return cadenciaDoZumbi;}
    public long getUltimaMordidaDoZumbi() {return ultimaMordida;}
    public double getDanoDoZumbi() {return danoDoZumbi;}
    public double getVidaDoZumbi() {return vidaDoZumbi;}
    
    // sets
    public void setPosicaoXDoZumbi(double posicaoXDoZumbi) {this.posicaoXDoZumbi = posicaoXDoZumbi;}
    public void setPosicaoYDoZumbi(double posicaoYDoZumbi) {this.posicaoYDoZumbi = posicaoYDoZumbi;}
    public void setVidaDoZumbi(double vidaDoZumbi) {this.vidaDoZumbi = vidaDoZumbi;}
    public void setCadenciaDoZumbi(long cadenciaDoZumbi) {this.cadenciaDoZumbi = cadenciaDoZumbi;}
    public void setUltimaMordidaDoZumbi(long ultimaMordida) {this.ultimaMordida = ultimaMordida;}

    public void criarZumbi(double posicaoXDoZumbi, double posicaoYDoZumbi) {
        this.posicaoXDoZumbi = posicaoXDoZumbi;
        this.posicaoYDoZumbi = posicaoYDoZumbi;
        this.zumbiAtacando = false; // Inicialmente, o zumbi não está atacando
        this.ZumbiFoiAtingido = false; // Inicialmente, o zumbi não foi atingido
    }

    public void moverZumbi() {

        // zumbi anda enquanto não estiver atacando
        if (!zumbiAtacando) {
            this.posicaoXDoZumbi -= velocidadeDoZumbi;
        }
    }

    public void ataqueZumbi() {

        // zumbi ataca
        zumbiAtacando = true;
    }

    public void pararAtaqueZumbi() {

        // zumbi para de atacar
        zumbiAtacando = false;
    }

    public void zumbiRecebeDano(double dano) {
        this.ZumbiFoiAtingido = true; // Marca que o zumbi foi atingido
        this.vidaDoZumbi -= dano;

        if (this.vidaDoZumbi < 0) {
            this.vidaDoZumbi = 0; // Evita que a vida fique negativa
        }
    }

    public boolean zumbiEstaAtacando() {
        return zumbiAtacando;
    }

    public boolean podeMorder(long agora) {

        // Verifica se o zumbi pode atacar 
        if (agora - getUltimaMordidaDoZumbi() >=  getCadenciaDoZumbi()) {
            setUltimaMordidaDoZumbi(agora); // Atualiza o tempo do último ataque
            return true; // Permite o ataque
        }
            
        return false; // Retorna false se o zumbi não pode atacar
    }

    public void aumentarVelocidadeDoZumbi(double velocidadeExtra) {

        velocidadeDoZumbi += velocidadeExtra;
    }
}