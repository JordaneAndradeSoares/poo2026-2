package com.poo2026_2.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Uma onda de zumbis, como configurada no niveis.json: quando comeca, em que
 * ritmo os zumbis saem e quais zumbis vem nela.
 *
 * A onda so guarda dados; quem avanca o tempo e solta os zumbis e o Cronograma.
 */
public final class Onda {

    private final String nome;
    private final double inicio;           // segundos desde o comeco da fase
    private final double passo;            // segundos entre um zumbi e o seguinte
    private final boolean grande;          // aparece destacada na linha do tempo
    private final List<TipoZumbi> sequencia;

    public Onda(String nome, double inicio, double passo, boolean grande, List<TipoZumbi> sequencia) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("A onda precisa de um nome.");
        }
        if (inicio < 0 || !Double.isFinite(inicio)) {
            throw new IllegalArgumentException("O inicio da onda nao pode ser negativo.");
        }
        if (passo <= 0 || !Double.isFinite(passo)) {
            throw new IllegalArgumentException("O intervalo entre zumbis tem que ser positivo.");
        }
        if (sequencia == null || sequencia.isEmpty()) {
            throw new IllegalArgumentException("A onda precisa de pelo menos um zumbi.");
        }
        this.nome = nome;
        this.inicio = inicio;
        this.passo = passo;
        this.grande = grande;
        this.sequencia = List.copyOf(sequencia);
    }

    public String getNome() {
        return nome;
    }

    public double getInicio() {
        return inicio;
    }

    public double getPasso() {
        return passo;
    }

    public boolean isGrande() {
        return grande;
    }

    public int getQuantidadeZumbis() {
        return sequencia.size();
    }

    /** Os zumbis na ordem em que saem. */
    public List<TipoZumbi> getSequencia() {
        return sequencia;
    }

    /** Segundo (desde o comeco da fase) em que sai o zumbi de indice i. */
    public double getTempoDoZumbi(int i) {
        return inicio + i * passo;
    }

    /** Segundo em que a onda termina; a proxima onda, se nao disser o contrario, comeca aqui. */
    public double getFim() {
        return inicio + sequencia.size() * passo;
    }

    /** Quantos zumbis de cada tipo (para mostrar na linha do tempo). */
    public Map<TipoZumbi, Integer> getResumo() {
        Map<TipoZumbi, Integer> resumo = new EnumMap<>(TipoZumbi.class);
        for (TipoZumbi tipo : sequencia) {
            resumo.merge(tipo, 1, Integer::sum);
        }
        return Collections.unmodifiableMap(resumo);
    }

    /** Ex.: "5x Calouro, 2x Trote". */
    public String getResumoEmTexto() {
        List<String> partes = new ArrayList<>();
        for (Map.Entry<TipoZumbi, Integer> e : getResumo().entrySet()) {
            partes.add(e.getValue() + "x " + e.getKey().getNomeExibicao());
        }
        return String.join(", ", partes);
    }
}
