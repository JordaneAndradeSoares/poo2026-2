package com.poo2026_2.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Leitor de JSON pequeno, sem dependencias (o projeto nao usa Gson/Jackson).
 *
 * Devolve: Map (objeto, na ordem do arquivo), List (array), String, Double
 * (todo numero), Boolean ou null.
 *
 * Alem do JSON padrao, tolera comentarios (// e /* *&#47;) e virgula sobrando
 * no fim de listas e objetos, para facilitar a edicao a mao.
 */
public final class Json {

    /** Erro de sintaxe, ja com linha e coluna. */
    public static final class ErroJson extends RuntimeException {
        public ErroJson(String mensagem) {
            super(mensagem);
        }
    }

    private Json() { }

    public static Object ler(String texto) {
        return new Leitor(texto).lerDocumento();
    }

    // =================================================================

    private static final class Leitor {
        private final String s;
        private int i;

        private Leitor(String texto) {
            this.s = texto;
        }

        Object lerDocumento() {
            if (s.startsWith("\uFEFF")) {
                i = 1;
            }
            pularBrancos();
            Object valor = lerValor();
            pularBrancos();
            if (i < s.length()) {
                throw erro("texto inesperado depois do fim do documento");
            }
            return valor;
        }

        private Object lerValor() {
            if (i >= s.length()) {
                throw erro("o arquivo terminou antes da hora");
            }
            char c = s.charAt(i);
            switch (c) {
                case '{':
                    return lerObjeto();
                case '[':
                    return lerArray();
                case '"':
                    return lerTexto();
                case 't':
                    return lerPalavra("true", Boolean.TRUE);
                case 'f':
                    return lerPalavra("false", Boolean.FALSE);
                case 'n':
                    return lerPalavra("null", null);
                default:
                    if (c == '-' || (c >= '0' && c <= '9')) {
                        return lerNumero();
                    }
                    throw erro("caractere inesperado '" + c + "'");
            }
        }

        private Map<String, Object> lerObjeto() {
            Map<String, Object> mapa = new LinkedHashMap<>();
            i++; // {
            while (true) {
                pularBrancos();
                if (i >= s.length()) {
                    throw erro("objeto sem '}' no final");
                }
                if (s.charAt(i) == '}') {
                    i++;
                    return mapa;
                }
                if (s.charAt(i) != '"') {
                    throw erro("esperava o nome de um campo entre aspas");
                }
                int inicio = i;
                String chave = lerTexto();
                pularBrancos();
                if (i >= s.length() || s.charAt(i) != ':') {
                    throw erro("esperava ':' depois de \"" + chave + "\"");
                }
                i++;
                pularBrancos();
                Object valor = lerValor();
                if (mapa.containsKey(chave)) {
                    i = inicio;
                    throw erro("o campo \"" + chave + "\" aparece duas vezes");
                }
                mapa.put(chave, valor);

                pularBrancos();
                if (i < s.length() && s.charAt(i) == ',') {
                    i++;
                } else if (i < s.length() && s.charAt(i) == '}') {
                    // fecha na proxima volta
                } else {
                    throw erro("esperava ',' ou '}' depois do valor de \"" + chave + "\"");
                }
            }
        }

        private List<Object> lerArray() {
            List<Object> lista = new ArrayList<>();
            i++; // [
            while (true) {
                pularBrancos();
                if (i >= s.length()) {
                    throw erro("lista sem ']' no final");
                }
                if (s.charAt(i) == ']') {
                    i++;
                    return lista;
                }
                lista.add(lerValor());

                pularBrancos();
                if (i < s.length() && s.charAt(i) == ',') {
                    i++;
                } else if (i < s.length() && s.charAt(i) == ']') {
                    // fecha na proxima volta
                } else {
                    throw erro("esperava ',' ou ']' na lista");
                }
            }
        }

        private String lerTexto() {
            StringBuilder sb = new StringBuilder();
            i++; // aspas de abertura
            while (i < s.length()) {
                char c = s.charAt(i++);
                if (c == '"') {
                    return sb.toString();
                }
                if (c == '\n') {
                    i--;
                    throw erro("texto sem aspas de fechamento");
                }
                if (c != '\\') {
                    sb.append(c);
                    continue;
                }
                if (i >= s.length()) {
                    break;
                }
                char e = s.charAt(i++);
                switch (e) {
                    case '"':  sb.append('"');  break;
                    case '\\': sb.append('\\'); break;
                    case '/':  sb.append('/');  break;
                    case 'b':  sb.append('\b'); break;
                    case 'f':  sb.append('\f'); break;
                    case 'n':  sb.append('\n'); break;
                    case 'r':  sb.append('\r'); break;
                    case 't':  sb.append('\t'); break;
                    case 'u':
                        if (i + 4 > s.length()) {
                            throw erro("sequencia \\u incompleta");
                        }
                        try {
                            sb.append((char) Integer.parseInt(s.substring(i, i + 4), 16));
                        } catch (NumberFormatException ex) {
                            throw erro("sequencia \\u invalida");
                        }
                        i += 4;
                        break;
                    default:
                        i--;
                        throw erro("sequencia de escape invalida: \\" + e);
                }
            }
            throw erro("texto sem aspas de fechamento");
        }

        private Double lerNumero() {
            int inicio = i;
            while (i < s.length() && "+-0123456789.eE".indexOf(s.charAt(i)) >= 0) {
                i++;
            }
            try {
                return Double.valueOf(s.substring(inicio, i));
            } catch (NumberFormatException ex) {
                i = inicio;
                throw erro("numero invalido '" + s.substring(inicio, Math.max(inicio, Math.min(s.length(), i + 8))).strip() + "'");
            }
        }

        private Object lerPalavra(String palavra, Object valor) {
            if (!s.startsWith(palavra, i)) {
                throw erro("caractere inesperado '" + s.charAt(i) + "'");
            }
            i += palavra.length();
            return valor;
        }

        /** Espacos, quebras de linha e comentarios. */
        private void pularBrancos() {
            while (i < s.length()) {
                char c = s.charAt(i);
                if (c == ' ' || c == '\t' || c == '\n' || c == '\r') {
                    i++;
                } else if (c == '/' && s.startsWith("//", i)) {
                    while (i < s.length() && s.charAt(i) != '\n') {
                        i++;
                    }
                } else if (c == '/' && s.startsWith("/*", i)) {
                    int fim = s.indexOf("*/", i + 2);
                    if (fim < 0) {
                        throw erro("comentario /* sem */");
                    }
                    i = fim + 2;
                } else {
                    return;
                }
            }
        }

        private ErroJson erro(String mensagem) {
            int linha = 1;
            int coluna = 1;
            for (int k = 0; k < Math.min(i, s.length()); k++) {
                if (s.charAt(k) == '\n') {
                    linha++;
                    coluna = 1;
                } else {
                    coluna++;
                }
            }
            return new ErroJson("JSON invalido (linha " + linha + ", coluna " + coluna + "): " + mensagem);
        }
    }
}
