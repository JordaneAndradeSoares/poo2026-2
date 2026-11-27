package com.poo2026_2.model;

import java.util.ArrayList;
import java.util.List;

import com.poo2026_2.model.utilizaveis.*;

/**
 * Regras de desbloqueio. Comeca so com o Daniel contra o Calouro; vencer a fase
 * N libera o professor N da lista abaixo e o proximo zumbi (ordem do enum
 * TipoZumbi). Depois da fase 7 tudo esta liberado.
 *
 * O progresso e salvo no computador do jogador (ConfiguracoesJogo) e vale para
 * qualquer fase: quem zerou o jogo pode jogar a fase 1 com o Fabricio.
 */
public final class Progressao {

    /**
     * Professor + mensagem generica sobre o poder dele (mostrada na tela de
     * selecao).
     */
    public record InfoProfessor(String nome, Class<? extends Professor> classe, String poder) {

    }

    public static final InfoProfessor INICIAL = new InfoProfessor("Daniel", Daniel.class,
            "Atira código C. Tiro padrão, sem efeito especial: o professor ideal para começar.");

    public static final List<InfoProfessor> DESBLOQUEAVEIS = List.of(
            new InfoProfessor("Thais", Thais.class,
                    "Atira código Go em linha reta. Simples e confiável para paralisar a linha."),
            new InfoProfessor("Philipe", Philipe.class,
                    "Atira Python: a linguagem é lenta, os zumbis se tornam lentos."),
            new InfoProfessor("Nacif", Nacif.class,
                    "Programa uma surpresa em um FPGA utilizando Verilog, é melhor estar pronto."),
            new InfoProfessor("Lucas", Lucas.class,
                    "Ataca de skate, corpo a corpo: uma skatada derruba um zumbi na hora, mas demora para recarregar."),
            new InfoProfessor("Marcus", Marcus.class,
                    "Usa Matlab para criar uma matriz de defesa, o tanque perfeito."),
            new InfoProfessor("Glaucia", Glaucia.class,
                    "Atira JavaScript em chamas: além do dano, os zumbis atingidos continuam queimando."),
            new InfoProfessor("Fabricio", Fabricio.class,
                    "Atira Scala em três linhas ao mesmo tempo, com dano alto. O mais poderoso da turma."));

    private Progressao() {
    }

    public static boolean alternarDesbloqueioTotal() {
        boolean ativo = !ConfiguracoesJogo.isCheatAtivo();
        ConfiguracoesJogo.setCheatAtivo(ativo);
        if (!ativo) {
            // Desligar o cheat reinicia tudo: o jogador volta a começar do zero.
            ConfiguracoesJogo.reiniciarProgresso();
        }
        return ativo;
    }

    /**
     * Quantas fases ja foram vencidas (0 = nenhuma). Com o cheat ligado, todas.
     */
    public static int fasesConcluidas() {
        if (ConfiguracoesJogo.isCheatAtivo()) {
            return DESBLOQUEAVEIS.size();
        }
        return ConfiguracoesJogo.getUltimoNivelConcluido();
    }

    /**
     * Uma fase esta liberada se for a primeira ou se a anterior ja foi vencida.
     */
    public static boolean faseLiberada(int numero) {
        return numero <= fasesConcluidas() + 1;
    }

    public static boolean professorDesbloqueado(int indiceNaLista) {
        return indiceNaLista < fasesConcluidas();
    }

    public static List<Class<? extends Professor>> professoresDesbloqueados() {
        List<Class<? extends Professor>> lista = new ArrayList<>();
        lista.add(INICIAL.classe());

        for (int i = 0; i < DESBLOQUEAVEIS.size(); i++) {
            if (professorDesbloqueado(i)) {
                lista.add(DESBLOQUEAVEIS.get(i).classe());
            }
        }
        return lista;
    }

    public static List<TipoZumbi> zumbisDesbloqueados() {
        TipoZumbi[] todos = TipoZumbi.values();
        int quantos = Math.min(todos.length, 1 + fasesConcluidas());
        return List.of(todos).subList(0, quantos);
    }
}