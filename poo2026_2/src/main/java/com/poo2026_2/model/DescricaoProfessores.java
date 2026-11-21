package com.poo2026_2.model;

import java.util.Map;

import com.poo2026_2.model.utilizaveis.Cafeteira;
import com.poo2026_2.model.utilizaveis.Daniel;
import com.poo2026_2.model.utilizaveis.Fabricio;
import com.poo2026_2.model.utilizaveis.Glaucia;
import com.poo2026_2.model.utilizaveis.Lucas;
import com.poo2026_2.model.utilizaveis.Marcus;
import com.poo2026_2.model.utilizaveis.Nacif;
import com.poo2026_2.model.utilizaveis.Philipe;
import com.poo2026_2.model.utilizaveis.Thais;

public class DescricaoProfessores {

    private static final Map<String, String> DESCRICOES = Map.ofEntries(
        Map.entry("CAFETEIRA", "Produz cafés periodicamente. Custo de café: " + Cafeteira.getCustoCafe()),
        Map.entry("DANIEL", "Professor de C. Dispara projéteis que causam dano padrão aos zumbis. Custo de café: " + Daniel.getCustoCafeDaniel()),
        Map.entry("FABRICIO", "Professor de Scala. Dispara projéteis em três linhas simultaneamente (atinge sua linha e as duas linhas vizinhas). Custo de café: " + Fabricio.getCustoCafeFabricio()),
        Map.entry("GLAUCIA", "Professora de JavaScript. Seus disparos causam dano e queimadura, causando dano adicional ao longo do tempo. Custo de café: " + Glaucia.getCustoCafeGlaucia()),
        Map.entry("LUCAS", "Ataca corpo a corpo eliminando instantaneamente um zumbi que entra em seu alcance e depois entra em tempo de recarga. Custo de café: " + Lucas.getCustoCafeLucas()),
        Map.entry("MARCUS", "Professor de MATLAB. Possui muita vida e atua como tanque, segurando os zumbis sem realizar disparos. Custo de café: " + Marcus.getCustoCafeMarcus()),
        Map.entry("NACIF", "Professor de Verilog. Funciona como uma bomba: explode após um tempo, eliminando os zumbis em uma área 3x3 e sacrificando no processo. Custo de café: " + Nacif.getCustoCafeNacif()),
        Map.entry("PHILIPE", "Professor de Python. Seus disparos causam dano e reduzem a velocidade dos zumbis atingidos (a lentidão não acumula). Custo de café: " + Philipe.getCustoCafePhilipe()),
        Map.entry("THAIS", "Professora de Go. Dispara projéteis em três linhas simultaneamente (atinge sua linha e as duas linhas vizinhas). Custo de café: " + Thais.getCustoCafeThais())
    );

    public static String obter(String nome) {
        if (nome == null) {
            return "Descrição não disponível.";
        }

        return DESCRICOES.getOrDefault(
            nome.toUpperCase(),
            "Descrição não disponível."
        );
    }
}