package com.poo2026_2.audio;

import com.poo2026_2.model.ConfiguracoesJogo;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javafx.util.Duration;

public final class AudioManager {

    private static final String MUSICA_MENU = "/audio/menu-loop.mp3";
    private static final String SOM_HOVER_BOTAO = "/audio/botao-hover.mp3";
    private static final String CLICK_BOTAO = "/audio/clickButton.mp3";
    private static final String SOM_TROVAO1 = "/audio/trovao1.mp3";
    private static final String SOM_TROVAO2 = "/audio/trovao2.mp3";

    // =================================================================
    // VOZES
    // =================================================================
    private static final String PASTA_VOZES = "/audio/voicelines/";

    /**
     * Tabela de GANHO por voz (so o programador mexe aqui).
     *
     * volume final = slider "Voices" das Configuracoes x ganho da voz,
     * limitado a 1.0 (teto do MediaPlayer).
     *
     * 1.0 = igual ao slider | >1.0 = voz mais alta | <1.0 = voz mais baixa
     *
     * Chave = nome do arquivo (sem a pasta). Voz fora da tabela usa
     * GANHO_PADRAO_VOZ. Como o slider padrao e 0.50, ganhos ate 2.0 tem folga
     * no padrao; se o jogador subir o slider para perto de 100%, as vozes com
     * ganho alto passam a bater no teto e a diferenca entre elas diminui.
     *
     * Valores iniciais calculados a partir do volume medio de cada arquivo.
     */
    private static final double GANHO_PADRAO_VOZ = 1.0;
    private static final Map<String, Double> GANHO_VOZES = new HashMap<>();

    static {
        GANHO_VOZES.put("daniel.mp3", 1.4);
        GANHO_VOZES.put("philipe.mp3", 1.4);
        GANHO_VOZES.put("glaucia.mp3", 1.4);
        GANHO_VOZES.put("lucas.mp3", 1.7);
        GANHO_VOZES.put("fabricio.mp3", 1.8);
        GANHO_VOZES.put("marcus.mp3", 1.8);
        GANHO_VOZES.put("cafeteira.mp3", 1.5);
        GANHO_VOZES.put("demitido.mp3", 2.0);
        GANHO_VOZES.put("thais.mp3", 1.5);
        GANHO_VOZES.put("nacif.mp3", 1.5);
    }

    /** Vozes tocando agora (para o slider atualizar ao vivo). */
    private static final Map<MediaPlayer, String> vozesTocando = new HashMap<>();

    private static MediaPlayer musicaAtual;
    private static String caminhoMusicaAtual;

    private static final Set<MediaPlayer> efeitosTocando = new HashSet<>();

    /**
     * Cache de Media por caminho, para nao recarregar o arquivo toda vez.
     */
    private static final Map<String, Media> cacheMedia = new HashMap<>();

    private AudioManager() {
    }

    /**
     * Toca a musica do menu em loop infinito (para a anterior, se houver).
     */
    public static void tocarMusicaMenu() {
        tocarMusica(MUSICA_MENU);
    }

    /**
     * Toca uma musica (caminho dentro de resources) em loop infinito, parando
     * qualquer musica que já estivesse tocando.
     */
    public static void tocarMusica(String caminhoRecurso) {
        if (musicaAtual != null && caminhoRecurso.equals(caminhoMusicaAtual)) {
            aplicarVolumeMusica();
            return;
        }

        pararMusica();

        try {
            Media media = new Media(
                    AudioManager.class.getResource(caminhoRecurso).toExternalForm());

            musicaAtual = new MediaPlayer(media);
            caminhoMusicaAtual = caminhoRecurso;
            musicaAtual.setCycleCount(MediaPlayer.INDEFINITE);
            aplicarVolumeMusica();
            musicaAtual.play();

        } catch (Exception e) {
            System.err.println(
                    "Nao foi possivel tocar a musica '" + caminhoRecurso + "': " + e.getMessage());
        }
    }

    public static void pararMusica() {
        if (musicaAtual != null) {
            musicaAtual.stop();
            musicaAtual.dispose();
            musicaAtual = null;
            caminhoMusicaAtual = null;
        }
    }

    public static void aplicarVolumeMusica() {
        if (musicaAtual != null) {
            double volume = ConfiguracoesJogo.isMudo()
                    ? 0.0
                    : ConfiguracoesJogo.getVolumeMusica();

            musicaAtual.setVolume(volume);
        }
    }

    /**
     * Toca o som de "mouse em cima do botão" uma vez.
     */
    public static void tocarSomHoverBotao() {
        tocarEfeito(SOM_HOVER_BOTAO);
    }

    /**
     * Toca o som do trovao.
     */
    public static void tocarSomTrovao1() {
        tocarEfeito(SOM_TROVAO1);
    }

    public static void tocarSomTrovao2() {
        tocarEfeito(SOM_TROVAO2);
    }

    public static void tocarSomBotao() {
        tocarEfeito(CLICK_BOTAO);
    }

    /**
     * Toca um efeito sonoro curto uma vez (nao interrompe a musica).
     */
    public static void tocarEfeito(String caminhoRecurso) {
        tocarEfeito(caminhoRecurso, 0);
    }

    public static void tocarEfeito(String caminhoRecurso, double maximoSegundos) {
        // qualquer arquivo da pasta voicelines responde ao slider de vozes
        if (ehVoz(caminhoRecurso)) {
            tocarVoz(caminhoRecurso);
            return;
        }

        if (ConfiguracoesJogo.isMudo()) {
            return;
        }

        try {
            Media media = cacheMedia.computeIfAbsent(caminhoRecurso,
                    caminho -> new Media(AudioManager.class.getResource(caminho).toExternalForm()));
            tocarEfeitoDescartavel(media, caminhoRecurso, maximoSegundos);

        } catch (Exception e) {
            System.err.println("Nao foi possivel tocar o efeito '" + caminhoRecurso + "': " + e.getMessage());
        }
    }

    private static void tocarEfeitoDescartavel(Media media, String caminho, double maximoSegundos) {
        MediaPlayer player = new MediaPlayer(media);
        efeitosTocando.add(player);

        Runnable liberar = () -> {
            efeitosTocando.remove(player);
            player.dispose();
        };

        player.setVolume(volumeDoEfeito(caminho));
        if (maximoSegundos > 0) {
            player.setStopTime(Duration.seconds(maximoSegundos));
        }
        player.setOnEndOfMedia(liberar);
        player.setOnError(() -> {
            System.err.println("Erro ao tocar '" + caminho + "': " + player.getError());
            liberar.run();
        });
        player.play();
    }

    // =================================================================
    // VOZES: tocar, ganho por voz e volume
    // =================================================================
    private static boolean ehVoz(String caminhoRecurso) {
        return caminhoRecurso != null && caminhoRecurso.startsWith(PASTA_VOZES);
    }

    private static double ganhoDaVoz(String caminhoRecurso) {
        String arquivo = caminhoRecurso.substring(caminhoRecurso.lastIndexOf('/') + 1);
        return GANHO_VOZES.getOrDefault(arquivo, GANHO_PADRAO_VOZ);
    }

    /** slider de vozes x ganho da voz, limitado a [0, 1]. */
    private static double volumeDaVoz(String caminhoRecurso) {
        if (ConfiguracoesJogo.isMudo()) {
            return 0.0;
        }
        double volume = ConfiguracoesJogo.getVolumeVozes() * ganhoDaVoz(caminhoRecurso);
        return Math.max(0.0, Math.min(1.0, volume));
    }

    /** Toca uma voz (arquivo de /audio/voicelines/) uma vez. */
    public static void tocarVoz(String caminhoRecurso) {
        if (ConfiguracoesJogo.isMudo()) {
            return;
        }

        try {
            Media media = cacheMedia.computeIfAbsent(caminhoRecurso,
                    caminho -> new Media(AudioManager.class.getResource(caminho).toExternalForm()));

            MediaPlayer player = new MediaPlayer(media);
            vozesTocando.put(player, caminhoRecurso);

            Runnable liberar = () -> {
                vozesTocando.remove(player);
                player.dispose();
            };

            player.setVolume(volumeDaVoz(caminhoRecurso));
            player.setOnEndOfMedia(liberar);
            player.setOnError(() -> {
                System.err.println("Erro ao tocar voz '" + caminhoRecurso + "': " + player.getError());
                liberar.run();
            });
            player.play();

        } catch (Exception e) {
            System.err.println("Nao foi possivel tocar a voz '" + caminhoRecurso + "': " + e.getMessage());
        }
    }

    /**
     * Reaplica o volume nas vozes que estao tocando (chamado ao aplicar as
     * Configuracoes).
     */
    public static void aplicarVolumeVozes() {
        vozesTocando.forEach((player, caminho) -> player.setVolume(volumeDaVoz(caminho)));
    }

    /**
     * Toca um efeito em loop ate pararEfeitoEmLoop(). Devolve o player (ou null
     * se falhar).
     */
    public static MediaPlayer iniciarEfeitoEmLoop(String caminhoRecurso) {
        try {
            Media media = cacheMedia.computeIfAbsent(caminhoRecurso,
                    caminho -> new Media(AudioManager.class.getResource(caminho).toExternalForm()));
            MediaPlayer player = new MediaPlayer(media);
            player.setCycleCount(MediaPlayer.INDEFINITE);
            player.setVolume(volumeDoEfeito(caminhoRecurso));
            player.play();
            return player;
        } catch (Exception e) {
            System.err.println("Nao foi possivel tocar o efeito '" + caminhoRecurso + "': " + e.getMessage());
            return null;
        }
    }

    public static void pararEfeitoEmLoop(MediaPlayer player) {
        if (player != null) {
            player.stop();
            player.dispose();
        }
    }

    private static final double GANHO_PADRAO_EFEITO = 1.0;
    private static final Map<String, Double> GANHO_EFEITOS = new HashMap<>();

    static {
        // mais baixos
        GANHO_EFEITOS.put("clickButton.mp3", 0.5);
        GANHO_EFEITOS.put("botao-hover.mp3", 0.4);

        // mais altos
        GANHO_EFEITOS.put("faah.mp3", 1.6);
        GANHO_EFEITOS.put("carrinho.mp3", 1.5);
        GANHO_EFEITOS.put("porta-aberta.mp3", 1.5);
        GANHO_EFEITOS.put("digitando.mp3", 1.6);
        GANHO_EFEITOS.put("pancada.mp3", 1.6);
        GANHO_EFEITOS.put("321go.mp3", 2.0);
    }

    private static double volumeDoEfeito(String caminhoRecurso) {
        if (ConfiguracoesJogo.isMudo()) {
            return 0.0;
        }
        String arquivo = caminhoRecurso.substring(caminhoRecurso.lastIndexOf('/') + 1);
        double ganho = GANHO_EFEITOS.getOrDefault(arquivo, GANHO_PADRAO_EFEITO);
        double volume = ConfiguracoesJogo.getVolumeEfeitos() * ganho;
        return Math.max(0.0, Math.min(1.0, volume));
    }
}