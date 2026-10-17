package com.poo2026_2.model.professores;

public class Marcus extends Professor {

    // Construtor
    public Marcus(int linhaNoTabuleiroDoProfessor, int colunaNoTabuleiroDoProfessor ) {

        super(
            150, 
            1_500_000_000, // uma cadencia 0 faz o professor atacar sem parar (parece mais ou raio do que um tiro)
            4000, 
            linhaNoTabuleiroDoProfessor, 
            colunaNoTabuleiroDoProfessor
        );

        setEsseProfessorDeveAtacarOsZumbis(false); // apenas tanka (nunca deve atacar)
    }
}