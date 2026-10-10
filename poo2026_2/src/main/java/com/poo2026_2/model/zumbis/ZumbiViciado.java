package com.poo2026_2.model.zumbis;

public class ZumbiViciado extends Zumbi {
    
    // comum porém fica rápido, perigoso e extressado ao quebrar seu telefone
    public ZumbiViciado() {

        super(
            0.6,
            40,
            100,
            1_000_000_000L
        );
    }

    /*
    private void ativarHabilidade() {

        if ((this.getVidaDoZumbi() < vidaOriginalDoZumbiViciado) && (zumbiEstaVivo())) {

            // Aumenta a velocidade do zumbi
            aumentarVelocidadeDoZumbi(0.4);
        }
    }
    */
    
    @Override
    public void zumbiRecebeDano(double dano) {
        super.zumbiRecebeDano(dano);
        
        // Aumenta a velocidade do zumbi
        aumentarVelocidadeDoZumbi(0.4);
    }
}