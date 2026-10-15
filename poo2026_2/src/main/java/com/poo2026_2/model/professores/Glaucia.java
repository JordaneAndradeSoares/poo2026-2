package com.poo2026_2.model.professores;

import com.poo2026_2.model.tiros.javaScript;
import com.poo2026_2.model.tiros.tiro;
import com.poo2026_2.model.zumbis.Zumbi;

/*

Professora Glaucia

atira JavaScript - custo 100 cafe
dano: 40hp
efeito: disparo de fogo, queima os inimigos
cadência: 1.5s

*/

public class Glaucia extends Professor {

    // Construtor
    public Glaucia(int linhaNoTabuleiroDoProfessor, int colunaNoTabuleiroDoProfessor) {
        super(
            100, 
            1_500_000_000L, 
            100, 
            linhaNoTabuleiroDoProfessor, 
            colunaNoTabuleiroDoProfessor
        );

        //setDanoDoProfessor(40); // Definindo o dano do professor Glaucia

        setEsseProfessorDeveAtacarOsZumbis(true);
    }

    @Override
    public tiro professorAtacaZumbi(Zumbi zumbi, double posicaoXDoTiro, double posicaoYDoTiro) {

        if (professorEstaVivo() == true) {

            return new javaScript(
                posicaoXDoTiro, 
                posicaoYDoTiro,
                40, // Dano do tiro
                1 // Velocidade do tiro
            ); 
        } 
        
        return null; // retorna null se o professor não estiver vivo, indicando que ele não pode atacar
    }
}