package com.poo2026_2.model;

import java.util.function.IntFunction;

import com.poo2026_2.model.zumbis.Calouro;
import com.poo2026_2.model.zumbis.CalouroBlindado;
import com.poo2026_2.model.zumbis.CalouroProtegido;
import com.poo2026_2.model.zumbis.Doutorando;
import com.poo2026_2.model.zumbis.Formando;
import com.poo2026_2.model.zumbis.Trote;
import com.poo2026_2.model.zumbis.VibeCoder;
import com.poo2026_2.model.zumbis.Viciado;
import com.poo2026_2.model.zumbis.Zumbi;

/**
 * Tipos de zumbi que podem aparecer no niveis.json.
 * O nome usado no JSON é o do enum sem diferenciar maiusculas/minusculas
 * e ignorando "_" (ex.: CalouroBlindado, CALOURO_BLINDADO, calouroblindado).
 */
public enum TipoZumbi {

    // A ORDEM AQUI E A ORDEM DE DESBLOQUEIO.
    CALOURO("Calouro", Calouro::new),
    VIBE_CODER("Vibe Coder", VibeCoder::new),
    CALOURO_PROTEGIDO("Calouro Protegido", CalouroProtegido::new),
    CALOURO_BLINDADO("Calouro Blindado", CalouroBlindado::new),
    TROTE("Trote", Trote::new),
    VICIADO("Viciado", Viciado::new),
    FORMANDO("Formando", Formando::new),
    DOUTORANDO("Doutorando", Doutorando::new);

    private final String nomeExibicao;
    private final IntFunction<? extends Zumbi> criador;

    TipoZumbi(String nomeExibicao, IntFunction<? extends Zumbi> criador) {
        this.nomeExibicao = nomeExibicao;
        this.criador = criador;
    }

    public String getNomeExibicao() {
        return nomeExibicao;
    }

    /** Cria um zumbi deste tipo na linha informada. */
    public IntFunction<? extends Zumbi> getCriador() {
        return criador;
    }

    /** Os nomes que podem ser usados no niveis.json, separados por virgula. */
    public static String nomesValidos() {
        StringBuilder sb = new StringBuilder();
        for (TipoZumbi tipo : values()) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(tipo.name());
        }
        return sb.toString();
    }

    /** Converte o texto do JSON no tipo correspondente. */
    public static TipoZumbi deTexto(String texto) {
        String chave = normalizar(texto);

        for (TipoZumbi tipo : values()) {
            if (normalizar(tipo.name()).equals(chave)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Tipo de zumbi desconhecido: '" + texto + "'");
    }

    private static String normalizar(String s) {
        return s.replace("_", "").replace(" ", "").toLowerCase();
    }
}
