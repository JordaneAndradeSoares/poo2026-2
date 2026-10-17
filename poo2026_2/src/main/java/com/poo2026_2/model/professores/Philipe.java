package com.poo2026_2.model.professores;

import com.poo2026_2.model.tiros.python;
import com.poo2026_2.model.tiros.tiro;
import com.poo2026_2.model.zumbis.Zumbi;

public class Philipe extends Professor {

    // Construtor
    public Philipe(int linhaNoTabuleiroDoProfessor, int colunaNoTabuleiroDoProfessor) {

        super(
            150, 
            1_500_000_000L, 
            100, 
            linhaNoTabuleiroDoProfessor, 
            colunaNoTabuleiroDoProfessor
        );
            
        setEsseProfessorDeveAtacarOsZumbis(true);
    }

    @Override
    public tiro professorAtacaZumbi(Zumbi zumbi, double posicaoXDoTiro, double posicaoYDoTiro) {

        if (professorEstaVivo() == true) {
            return new python(
                posicaoXDoTiro, 
                posicaoYDoTiro,
                20, // Dano do tiro
                1 // Velocidade do tiro
            ); 
        } 
        
        return null; // retorna null se o professor não estiver vivo, indicando que ele não pode atacar
    }
}