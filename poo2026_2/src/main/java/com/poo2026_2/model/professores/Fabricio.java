package com.poo2026_2.model.professores;

import com.poo2026_2.model.tiros.scala;
import com.poo2026_2.model.tiros.tiro;
import com.poo2026_2.model.zumbis.Zumbi;

/* 

ProfessorFabricio

Scalla - custo 300 cafe
dano: 100hp
cadência: 1.5s
efeito: tiro padrão, mas em 3 linhas

*/

public class Fabricio extends Professor {

    // Construtor
    public Fabricio(int linhaNoTabuleiroDoProfessor, int colunaNoTabuleiroDoProfessor) {
        super(
            300,
            1_500_000_000L, 
            100, 
            linhaNoTabuleiroDoProfessor, 
            colunaNoTabuleiroDoProfessor
        );
                
        //setDanoDoProfessor(40); // Definindo o dano do professor Fabricio

        setEsseProfessorDeveAtacarOsZumbis(true);
    }

    @Override
    public tiro professorAtacaZumbi(Zumbi zumbi, double posicaoXDoTiro, double posicaoYDoTiro) {

        if (professorEstaVivo() == true) {
            return new scala(
                posicaoXDoTiro, 
                posicaoYDoTiro,
                100, // Dano do tiro
                1 // Velocidade do tiro
            ); 
        } 
        
        return null; // retorna null se o professor não estiver vivo, indicando que ele não pode atacar
    }
}