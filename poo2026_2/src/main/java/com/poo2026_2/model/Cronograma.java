package com.poo2026_2.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * A linha do tempo de uma fase: em que segundo sai cada zumbi.
 *
 * Existem dois jeitos de montar:
 *  - com ondas: uma lista de Onda, cada uma com seu inicio e seu ritmo;
 *  - sem ondas: um fluxo continuo, durante X segundos, sorteando o tipo de
 *    cada zumbi pelos pesos configurados.
 *
 * O jogo chama avancar(dt) a cada tick e recebe os zumbis que devem nascer
 * agora. A velocidade do jogo (1.25x, 1.5x, 2x) entra no dt, entao nada aqui
 * precisa saber dela.
 */
public final class Cronograma {

    /** Um zumbi marcado para nascer em determinado segundo. */
    public record Evento(double tempo, TipoZumbi tipo) { }

    private final boolean comOndas;
    private final List<Onda> ondas;
    private final List<Evento> eventos;
    private final double duracao;

    private double decorrido;
    private int proximo;

    private Cronograma(boolean comOndas, List<Onda> ondas, List<Evento> eventos) {
        if (eventos.isEmpty()) {
            throw new IllegalArgumentException("A fase precisa ter pelo menos um zumbi.");
        }
        this.comOndas = comOndas;
        this.ondas = Collections.unmodifiableList(ondas);
        this.eventos = Collections.unmodifiableList(eventos);
        this.duracao = eventos.get(eventos.size() - 1).tempo();
    }

    // ===================== construcao =====================

    /** Fase com ondas. A ordem da lista nao importa: elas sao ordenadas pelo inicio. */
    public static Cronograma deOndas(List<Onda> ondas) {
        if (ondas == null || ondas.isEmpty()) {
            throw new IllegalArgumentException("A fase precisa de pelo menos uma onda.");
        }
        List<Onda> ordenadas = new ArrayList<>(ondas);
        ordenadas.sort((a, b) -> Double.compare(a.getInicio(), b.getInicio()));

        List<Evento> eventos = new ArrayList<>();
        for (Onda onda : ordenadas) {
            for (int i = 0; i < onda.getQuantidadeZumbis(); i++) {
                eventos.add(new Evento(onda.getTempoDoZumbi(i), onda.getSequencia().get(i)));
            }
        }
        eventos.sort((a, b) -> Double.compare(a.tempo(), b.tempo()));
        return new Cronograma(true, ordenadas, eventos);
    }

    /**
     * Fase sem ondas: um zumbi a cada "intervalo" segundos, comecando em
     * "inicio", ate completar "duracao" segundos. O tipo de cada um e sorteado
     * pelos pesos.
     */
    public static Cronograma continuo(double inicio, double duracao, double intervalo,
                                      Map<TipoZumbi, Integer> pesos, Random sorteio) {
        if (inicio < 0 || duracao <= 0 || intervalo <= 0
                || !Double.isFinite(inicio) || !Double.isFinite(duracao) || !Double.isFinite(intervalo)) {
            throw new IllegalArgumentException("Inicio, duracao e intervalo tem que ser positivos.");
        }
        int total = 0;
        for (int peso : pesos.values()) {
            total += peso;
        }
        if (total <= 0) {
            throw new IllegalArgumentException("Os pesos dos zumbis precisam somar mais que zero.");
        }

        int quantidade = Math.max(1, (int) Math.floor(duracao / intervalo + 1e-9));
        List<Evento> eventos = new ArrayList<>();
        for (int i = 0; i < quantidade; i++) {
            int sorteado = sorteio.nextInt(total);
            TipoZumbi escolhido = null;
            for (Map.Entry<TipoZumbi, Integer> e : pesos.entrySet()) {
                sorteado -= e.getValue();
                if (sorteado < 0) {
                    escolhido = e.getKey();
                    break;
                }
            }
            eventos.add(new Evento(inicio + i * intervalo, escolhido));
        }
        return new Cronograma(false, List.of(), eventos);
    }

    // ===================== andamento =====================

    /** Avanca o relogio e devolve os zumbis que devem nascer neste intervalo. */
    public List<TipoZumbi> avancar(double deltaTime) {
        List<TipoZumbi> devidos = new ArrayList<>();
        if (deltaTime > 0) {
            decorrido += deltaTime;
        }
        while (proximo < eventos.size() && eventos.get(proximo).tempo() <= decorrido) {
            devidos.add(eventos.get(proximo).tipo());
            proximo++;
        }
        return devidos;
    }

    /** true quando o ultimo zumbi ja nasceu. */
    public boolean estaConcluido() {
        return proximo >= eventos.size();
    }

    public void reiniciar() {
        decorrido = 0;
        proximo = 0;
    }

    // ===================== consulta (linha do tempo) =====================

    public boolean temOndas() {
        return comOndas;
    }

    public List<Onda> getOndas() {
        return ondas;
    }

    public List<Evento> getEventos() {
        return eventos;
    }

    /** Segundo em que nasce o ultimo zumbi (= tamanho da linha do tempo). */
    public double getDuracao() {
        return duracao;
    }

    public double getDecorrido() {
        return decorrido;
    }

    public int getQuantidadeDeZumbis() {
        return eventos.size();
    }

    /** Indice da onda em andamento (a ultima que ja comecou), ou -1 se ainda nao comecou nenhuma. */
    public int getIndiceDaOndaAtual() {
        int atual = -1;
        for (int i = 0; i < ondas.size(); i++) {
            if (ondas.get(i).getInicio() <= decorrido) {
                atual = i;
            }
        }
        return atual;
    }

    /** Segundos ate o primeiro zumbi (0 se ele ja nasceu). */
    public double getSegundosAteOPrimeiro() {
        return Math.max(0, eventos.get(0).tempo() - decorrido);
    }
}
