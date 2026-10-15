package com.poo2026_2.model.professores;

/*
atira Matlab - custo 150 cafe

hp: 4000
dano: 0
cadência: 0
efeito: invoca matriz pra tankar muito dano
*/

public class Marcus extends Professor {

    // Construtor
    public Marcus(int linhaNoTabuleiroDoProfessor, int colunaNoTabuleiroDoProfessor ) {

        super(
            150, 
            1_500_000_000, // uma cadencia 0 faz o professor atacar infinitamente (parece mais ou raio do que um tiro)
            4000, 
            linhaNoTabuleiroDoProfessor, 
            colunaNoTabuleiroDoProfessor
        );
            
        //setDanoDoProfessor(40); // Definindo o dano do professor Marcus

        setEsseProfessorDeveAtacarOsZumbis(false); // apenas tanka (nunca deve atacar)
    }
}