package com.poo2026_2.model;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.poo2026_2.util.Json;

/**
 * Le o /niveis/niveis.json e monta as Fases do jogo.
 *
 * Cada fase diz se tem ondas ou nao, quais zumbis vem, quando e em que ritmo.
 * O formato esta explicado no campo "_ajuda" do proprio niveis.json.
 *
 * Campos que comecam com "_" sao ignorados (servem de comentario). Qualquer
 * outro campo que o jogo nao conheca da erro, para pegar erro de digitacao.
 */
public final class CatalogoNiveis {

    private static final String ARQUIVO = "/niveis/niveis.json";

    /** Quando a primeira onda comeca, se o JSON nao disser. */
    public static final double INICIO_PADRAO_DA_PRIMEIRA_ONDA = 10;

    // =================================================================
    //  DADOS LIDOS DO JSON
    // =================================================================

    /** Uma onda como escrita no JSON (ainda sem os tempos resolvidos). */
    public record ConfigOnda(String nome,
                             Double inicio,
                             Double intervalo,
                             Double duracao,
                             Map<TipoZumbi, Integer> zumbis,
                             boolean aleatoria,
                             boolean grande) { }

    /** Fase sem ondas: fluxo continuo de zumbis. */
    public record ConfigContinuo(double inicio,
                                 double duracao,
                                 double intervalo,
                                 Map<TipoZumbi, Integer> pesos) { }

    /** Uma fase do JSON. Ou tem ondas, ou tem fluxo continuo. */
    public record Nivel(int numero,
                        String nome,
                        int cafeInicial,
                        List<ConfigOnda> ondas,          // null se a fase nao tem ondas
                        ConfigContinuo continuo,         // null se a fase tem ondas
                        Long semente) {

        public boolean temOndas() {
            return ondas != null;
        }

        /**
         * Monta uma Fase nova (estado zerado). Os professores liberados sao
         * os que o jogador ja desbloqueou, em qualquer fase. Sorteios (ordem
         * aleatoria, fluxo continuo) sao refeitos a cada chamada, a menos que
         * o nivel tenha "semente".
         */
        public Fase criarFase() {
            Fase fase = new Fase(numero, nome, cafeInicial);

            for (Class<? extends Professor> professor : Progressao.professoresDesbloqueados()) {
                fase.adicionarProfessorDisponivel(professor);
            }

            Random sorteio = semente != null ? new Random(semente) : new Random();
            fase.setCronograma(temOndas()
                    ? montarOndas(sorteio)
                    : Cronograma.continuo(continuo.inicio(), continuo.duracao(),
                            continuo.intervalo(), continuo.pesos(), sorteio));
            return fase;
        }

        private Cronograma montarOndas(Random sorteio) {
            List<Onda> prontas = new ArrayList<>();
            double fimDaAnterior = -1;   // -1: ainda nao teve onda anterior

            for (int i = 0; i < ondas.size(); i++) {
                ConfigOnda c = ondas.get(i);

                List<TipoZumbi> sequencia = new ArrayList<>();
                for (Map.Entry<TipoZumbi, Integer> e : c.zumbis().entrySet()) {
                    for (int k = 0; k < e.getValue(); k++) {
                        sequencia.add(e.getKey());
                    }
                }
                if (c.aleatoria()) {
                    Collections.shuffle(sequencia, sorteio);
                }

                // "intervalo" = segundos entre zumbis; "duracao" = espalha os zumbis nesse tempo
                double passo = c.intervalo() != null
                        ? c.intervalo()
                        : c.duracao() / sequencia.size();

                double inicio;
                if (c.inicio() != null) {
                    inicio = c.inicio();
                } else if (fimDaAnterior < 0) {
                    inicio = INICIO_PADRAO_DA_PRIMEIRA_ONDA;
                } else {
                    inicio = fimDaAnterior;   // segue no embalo: comeca quando a anterior termina
                }

                Onda onda = new Onda(c.nome() != null ? c.nome() : "Onda " + (i + 1),
                        inicio, passo, c.grande(), sequencia);
                prontas.add(onda);
                fimDaAnterior = onda.getFim();
            }
            return Cronograma.deOndas(prontas);
        }

        /** Tipos de zumbi que aparecem na fase, na ordem de desbloqueio. */
        public List<TipoZumbi> tiposDeZumbi() {
            Map<TipoZumbi, Integer> todos = new EnumMap<>(TipoZumbi.class);
            if (temOndas()) {
                ondas.forEach(o -> todos.putAll(o.zumbis()));
            } else {
                todos.putAll(continuo.pesos());
            }
            return new ArrayList<>(todos.keySet());
        }
    }

    private static Map<Integer, Nivel> niveis;

    private CatalogoNiveis() { }

    // =================================================================
    //  CONSULTA
    // =================================================================

    /** Todos os niveis, ordenados pelo numero. */
    public static synchronized List<Nivel> todos() {
        carregar();
        return Collections.unmodifiableList(new ArrayList<>(niveis.values()));
    }

    public static synchronized int quantidade() {
        carregar();
        return niveis.size();
    }

    /** Nivel pelo numero; se nao existir, devolve o primeiro. */
    public static synchronized Nivel obter(int numero) {
        carregar();
        Nivel nivel = niveis.get(numero);
        return nivel != null ? nivel : niveis.values().iterator().next();
    }

    // =================================================================
    //  LEITURA DO ARQUIVO
    // =================================================================

    private static void carregar() {
        if (niveis != null) {
            return;
        }

        String texto;
        try (InputStream stream = CatalogoNiveis.class.getResourceAsStream(ARQUIVO)) {
            if (stream == null) {
                throw new IllegalStateException("Arquivo de niveis nao encontrado: " + ARQUIVO);
            }
            texto = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Erro ao ler " + ARQUIVO, e);
        }

        niveis = interpretar(texto);
    }

    /** Transforma o texto de um niveis.json nos niveis, ordenados pelo numero. Publico para testes. */
    public static Map<Integer, Nivel> interpretar(String texto) {
        Object raiz;
        try {
            raiz = Json.ler(texto);
        } catch (Json.ErroJson e) {
            throw new IllegalStateException("niveis.json: " + e.getMessage(), e);
        }

        try {
            Objeto documento = new Objeto(raiz, "o arquivo");
            documento.somenteCampos("fases");
            List<Object> listaDeFases = documento.lista("fases");

            List<Nivel> lidos = new ArrayList<>();
            for (int i = 0; i < listaDeFases.size(); i++) {
                lidos.add(lerFase(new Objeto(listaDeFases.get(i), "a fase #" + (i + 1) + " da lista")));
            }
            if (lidos.isEmpty()) {
                throw new IllegalArgumentException("nenhuma fase em \"fases\"");
            }

            lidos.sort((a, b) -> Integer.compare(a.numero(), b.numero()));
            Map<Integer, Nivel> resultado = new LinkedHashMap<>();
            for (Nivel nivel : lidos) {
                if (resultado.put(nivel.numero(), nivel) != null) {
                    throw new IllegalArgumentException("a fase " + nivel.numero() + " aparece duas vezes");
                }
            }
            return resultado;
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("niveis.json: " + e.getMessage(), e);
        }
    }

    private static Nivel lerFase(Objeto f) {
        f.somenteCampos("numero", "nome", "cafeInicial", "temOndas", "ondas", "continuo", "semente");

        int numero = f.inteiro("numero");
        if (numero <= 0) {
            throw f.erro("\"numero\" tem que ser maior que zero");
        }
        String caminho = "a fase " + numero;
        f.renomear(caminho);

        String nome = f.texto("nome", "Fase " + numero);
        int cafe = f.inteiro("cafeInicial", 150);
        if (cafe < 0) {
            throw f.erro("\"cafeInicial\" nao pode ser negativo");
        }
        Long semente = f.tem("semente") ? Long.valueOf((long) f.numero("semente")) : null;

        // temOndas: se nao vier, deduz de qual bloco existe
        boolean temOndas = f.tem("temOndas")
                ? f.booleano("temOndas")
                : f.tem("ondas");

        if (temOndas) {
            if (f.tem("continuo")) {
                throw f.erro("tem \"temOndas\": true, entao \"continuo\" nao vale; apague um dos dois");
            }
            if (!f.tem("ondas")) {
                throw f.erro("tem ondas, mas falta a lista \"ondas\"");
            }
            List<Object> lista = f.lista("ondas");
            if (lista.isEmpty()) {
                throw f.erro("a lista \"ondas\" esta vazia");
            }
            List<ConfigOnda> ondas = new ArrayList<>();
            for (int i = 0; i < lista.size(); i++) {
                ondas.add(lerOnda(new Objeto(lista.get(i), caminho + ", onda " + (i + 1))));
            }
            return new Nivel(numero, nome, cafe, ondas, null, semente);
        }

        if (f.tem("ondas")) {
            throw f.erro("tem \"temOndas\": false, entao a lista \"ondas\" nao vale; apague um dos dois");
        }
        if (!f.tem("continuo")) {
            throw f.erro("sem ondas, mas falta o bloco \"continuo\" (duracao, intervalo e zumbis)");
        }
        return new Nivel(numero, nome, cafe, null,
                lerContinuo(new Objeto(f.valor("continuo"), caminho + ", bloco \"continuo\"")), semente);
    }

    private static ConfigOnda lerOnda(Objeto o) {
        o.somenteCampos("nome", "inicio", "intervalo", "duracao", "zumbis", "ordem", "grande");

        String nome = o.tem("nome") ? o.texto("nome", null) : null;
        Double inicio = o.tem("inicio") ? o.numero("inicio") : null;
        Double intervalo = o.tem("intervalo") ? o.numero("intervalo") : null;
        Double duracao = o.tem("duracao") ? o.numero("duracao") : null;

        if (inicio != null && inicio < 0) {
            throw o.erro("\"inicio\" nao pode ser negativo");
        }
        if ((intervalo == null) == (duracao == null)) {
            throw o.erro("use \"intervalo\" (segundos entre zumbis) OU \"duracao\" (tempo total da onda), um dos dois");
        }
        if (intervalo != null && intervalo <= 0) {
            throw o.erro("\"intervalo\" tem que ser maior que zero");
        }
        if (duracao != null && duracao <= 0) {
            throw o.erro("\"duracao\" tem que ser maior que zero");
        }

        Map<TipoZumbi, Integer> zumbis = o.zumbis("zumbis");
        if (zumbis.isEmpty()) {
            throw o.erro("\"zumbis\" precisa ter pelo menos um tipo com quantidade maior que zero");
        }

        boolean aleatoria = false;
        String ordem = o.texto("ordem", "sequencial");
        switch (ordem.strip().toLowerCase()) {
            case "sequencial" -> aleatoria = false;
            case "aleatoria", "aleatória" -> aleatoria = true;
            default -> throw o.erro("\"ordem\" tem que ser \"sequencial\" ou \"aleatoria\" (veio \"" + ordem + "\")");
        }

        return new ConfigOnda(nome, inicio, intervalo, duracao, zumbis, aleatoria, o.booleano("grande", false));
    }

    private static ConfigContinuo lerContinuo(Objeto c) {
        c.somenteCampos("inicio", "duracao", "intervalo", "zumbis");

        double inicio = c.tem("inicio") ? c.numero("inicio") : INICIO_PADRAO_DA_PRIMEIRA_ONDA;
        double duracao = c.numeroObrigatorio("duracao");
        double intervalo = c.numeroObrigatorio("intervalo");
        if (inicio < 0) {
            throw c.erro("\"inicio\" nao pode ser negativo");
        }
        if (duracao <= 0 || intervalo <= 0) {
            throw c.erro("\"duracao\" e \"intervalo\" tem que ser maiores que zero");
        }

        Map<TipoZumbi, Integer> pesos = c.zumbis("zumbis");
        if (pesos.isEmpty()) {
            throw c.erro("\"zumbis\" precisa ter pelo menos um tipo com peso maior que zero");
        }
        return new ConfigContinuo(inicio, duracao, intervalo, pesos);
    }

    // =================================================================
    //  AJUDANTE: um objeto JSON com mensagens de erro que dizem onde esta o problema
    // =================================================================

    private static final class Objeto {

        private final Map<String, Object> campos;
        private String caminho;

        @SuppressWarnings("unchecked")
        Objeto(Object valor, String caminho) {
            this.caminho = caminho;
            if (!(valor instanceof Map)) {
                throw new IllegalArgumentException(caminho + " deveria ser um objeto { ... }");
            }
            this.campos = (Map<String, Object>) valor;
        }

        void renomear(String novoCaminho) {
            this.caminho = novoCaminho;
        }

        IllegalArgumentException erro(String mensagem) {
            return new IllegalArgumentException(caminho + ": " + mensagem);
        }

        void somenteCampos(String... aceitos) {
            List<String> lista = List.of(aceitos);
            for (String chave : campos.keySet()) {
                if (!chave.startsWith("_") && !lista.contains(chave)) {
                    throw erro("campo desconhecido \"" + chave + "\". Campos aceitos aqui: "
                            + String.join(", ", aceitos));
                }
            }
        }

        boolean tem(String chave) {
            return campos.containsKey(chave) && campos.get(chave) != null;
        }

        Object valor(String chave) {
            return campos.get(chave);
        }

        String texto(String chave, String padrao) {
            if (!tem(chave)) {
                return padrao;
            }
            Object v = campos.get(chave);
            if (!(v instanceof String s) || s.isBlank()) {
                throw erro("\"" + chave + "\" deveria ser um texto entre aspas");
            }
            return s.strip();
        }

        double numero(String chave) {
            Object v = campos.get(chave);
            if (!(v instanceof Double d) || !Double.isFinite(d)) {
                throw erro("\"" + chave + "\" deveria ser um numero (sem aspas)");
            }
            return d;
        }

        double numeroObrigatorio(String chave) {
            if (!tem(chave)) {
                throw erro("falta o campo \"" + chave + "\"");
            }
            return numero(chave);
        }

        int inteiro(String chave) {
            if (!tem(chave)) {
                throw erro("falta o campo \"" + chave + "\"");
            }
            return inteiro(chave, 0);
        }

        int inteiro(String chave, int padrao) {
            if (!tem(chave)) {
                return padrao;
            }
            double d = numero(chave);
            if (d != Math.rint(d)) {
                throw erro("\"" + chave + "\" deveria ser um numero inteiro (veio " + d + ")");
            }
            return (int) d;
        }

        boolean booleano(String chave) {
            if (!tem(chave)) {
                throw erro("falta o campo \"" + chave + "\"");
            }
            return booleano(chave, false);
        }

        boolean booleano(String chave, boolean padrao) {
            if (!tem(chave)) {
                return padrao;
            }
            Object v = campos.get(chave);
            if (!(v instanceof Boolean b)) {
                throw erro("\"" + chave + "\" deveria ser true ou false (sem aspas)");
            }
            return b;
        }

        @SuppressWarnings("unchecked")
        List<Object> lista(String chave) {
            if (!tem(chave)) {
                throw erro("falta a lista \"" + chave + "\"");
            }
            Object v = campos.get(chave);
            if (!(v instanceof List)) {
                throw erro("\"" + chave + "\" deveria ser uma lista [ ... ]");
            }
            return (List<Object>) v;
        }

        /**
         * { "CALOURO": 5, "TROTE": 2 } -> tipo e quantidade (ou peso), na ordem
         * em que foram escritos. Quantidade 0 e ignorada.
         */
        Map<TipoZumbi, Integer> zumbis(String chave) {
            if (!tem(chave)) {
                throw erro("falta o campo \"" + chave + "\"");
            }
            Objeto interno = new Objeto(campos.get(chave), caminho + ", \"" + chave + "\"");

            Map<TipoZumbi, Integer> mapa = new LinkedHashMap<>();
            for (String nomeDoTipo : interno.campos.keySet()) {
                if (nomeDoTipo.startsWith("_")) {
                    continue;
                }
                TipoZumbi tipo;
                try {
                    tipo = TipoZumbi.deTexto(nomeDoTipo);
                } catch (IllegalArgumentException e) {
                    throw interno.erro("tipo de zumbi desconhecido \"" + nomeDoTipo + "\". Tipos validos: "
                            + TipoZumbi.nomesValidos());
                }
                int quantidade = interno.inteiro(nomeDoTipo, 0);
                if (quantidade < 0) {
                    throw interno.erro("\"" + nomeDoTipo + "\" nao pode ser negativo");
                }
                if (quantidade > 0) {
                    if (mapa.put(tipo, quantidade) != null) {
                        throw interno.erro("o tipo " + tipo.name() + " aparece duas vezes (com nomes diferentes)");
                    }
                }
            }
            return mapa;
        }
    }
}
