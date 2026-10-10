package com.poo2026_2.model.professores;

public class Cafeteira {

    private int cafePorCiclo;
    private long intervaloProducao;
    private long temperatura;

    private int custoCafeCafeteira;
    
    private int linhaNoTabuleiroDaCafeteira;
    private int colunaNoTabuleiroDaCafeteira;

    private double vidaDaCafeteira;

    private long ultimoMomentoDeProducao;

    // Construtor
    public Cafeteira(int linhaNoTabuleiroDaCafeteira, int colunaNoTabuleiroDaCafeteira) {

        this.cafePorCiclo = 50;
        this.intervaloProducao = 10_000_000_000L;
        this.temperatura = 6_000_000_000L;

        this.custoCafeCafeteira = 50;
        this.vidaDaCafeteira = 100;

        this.linhaNoTabuleiroDaCafeteira = linhaNoTabuleiroDaCafeteira;
        this.colunaNoTabuleiroDaCafeteira = colunaNoTabuleiroDaCafeteira;
    }

    public int getCustoCafe() {return this.custoCafeCafeteira;}
    public int getCafePorCiclo() {return this.cafePorCiclo;}
    public int getLinhaNoTabuleiroDaCafeteira() {return this.linhaNoTabuleiroDaCafeteira;}
    public int getColunaNoTabuleiroDaCafeteira() {return this.colunaNoTabuleiroDaCafeteira;}
    public long getTemperatura() {return this.temperatura;}

    public void cafeteiraRecebeDano(double danoDoZumbi) {this.vidaDaCafeteira -= danoDoZumbi;}
    public void setUltimoMomentoDeProducao(long agora) {this.ultimoMomentoDeProducao = agora;}

    public boolean cafeteiraEstaViva() {
        if (this.vidaDaCafeteira <= 0) {
            return false;
        } else {
            return true;
        }
    }

    public boolean podeProduzir(long agora) {

        if((agora - ultimoMomentoDeProducao) >= intervaloProducao){
            ultimoMomentoDeProducao = agora;
            return true;
        } else {
            return false;
        }
    }
} 