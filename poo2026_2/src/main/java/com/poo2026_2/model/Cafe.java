package com.poo2026_2.model;

import com.poo2026_2.model.professores.Cafeteira;

public class Cafe {

    private int posicaoLinhaCafe;
    private int posicaoColunaCafe;
    private int valorDeCafeDaXicara;
    private long temperaturaDoCafe;
    private long tempoDoCafeNoChao;

    public Cafe(int posicaoLinhaCafe, int posicaoColunaCafe, Cafeteira cafeteira, long agora){

        this.posicaoLinhaCafe = posicaoLinhaCafe;
        this.posicaoColunaCafe = posicaoColunaCafe;

        this.valorDeCafeDaXicara = cafeteira.getCafePorCiclo();
        this.temperaturaDoCafe = cafeteira.getTemperatura();
        
        this.tempoDoCafeNoChao = agora;
    }

    public int getPosicaoLinhaCafe(){return this.posicaoLinhaCafe;}
    public int getPosicaoColunaCafe(){return this.posicaoColunaCafe;}
    public int getValorDeCafeDaXicara(){return this.valorDeCafeDaXicara;}
    public long getTemperaturaDoCafe(){return this.temperaturaDoCafe;}
    public long getTempoDoCafeNoChao(){return this.tempoDoCafeNoChao;}

    public boolean cafeEstaQuente(long agora){

        long tempoDecorrido = agora - this.tempoDoCafeNoChao;

        if(tempoDecorrido <= this.temperaturaDoCafe){
            return true;
        } else {
            return false;
        }
    }

    public void diminuirTemperaturaDoCafe(){
        
    }
}