package com.poo2026_2.audio;

import com.poo2026_2.model.ConfiguracoesJogo;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class AudioManager {

    private static final String MUSICA_MENU = "/audio/menu-loop.mp3";
    private static final String SOM_HOVER_BOTAO = "/audio/botao-hover.m4a";
    private static final String SOM_TROVAO1 = "/audio/trovao1.m4a";
    private static final String SOM_TROVAO2 = "/audio/trovao2.m4a";

    private static MediaPlayer musicaAtual;

    private static final Set<MediaPlayer> efeitosTocando = new HashSet<>();

    /** Cache de Media por caminho, para nao recarregar o arquivo toda vez. */
    private static final Map<String, Media> cacheMedia = new HashMap<>();

    private AudioManager() {
    }

    /** Toca a musica do menu em loop infinito (para a anterior, se houver). */
    public static void tocarMusicaMenu() {
        tocarMusica(MUSICA_MENU);
    }

    /**
     * Toca uma musica (caminho dentro de resources) em loop infinito,
     * parando qualquer musica que já estivesse tocando.
     */
    public static void tocarMusica(String caminhoRecurso) {
        pararMusica();

        try {
            Media media = new Media(
                    AudioManager.class.getResource(caminhoRecurso).toExternalForm()
            );

            musicaAtual = new MediaPlayer(media);
            musicaAtual.setCycleCount(MediaPlayer.INDEFINITE);
            aplicarVolumeMusica();
            musicaAtual.play();

        } catch (Exception e) {
            System.err.println(
                    "Nao foi possivel tocar a musica '" + caminhoRecurso + "': " + e.getMessage()
            );
        }
    }

    public static void pararMusica() {
        if (musicaAtual != null) {
            musicaAtual.stop();
            musicaAtual.dispose();
            musicaAtual = null;
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

    /** Toca o som de "mouse em cima do botão" uma vez. */
    public static void tocarSomHoverBotao() {
        tocarEfeito(SOM_HOVER_BOTAO);
    }
    
    /** Toca o som do trovao. */
    public static void tocarSomTrovao1() {
        tocarEfeito(SOM_TROVAO1);
    }
    
    public static void tocarSomTrovao2() {
        tocarEfeito(SOM_TROVAO2);
    }

    /** Toca um efeito sonoro curto uma vez (nao interrompe a musica). */
    public static void tocarEfeito(String caminhoRecurso) {
        if (ConfiguracoesJogo.isMudo()) {
            return;
        }

        try {
            Media media = cacheMedia.computeIfAbsent(caminhoRecurso, caminho ->
                    new Media(AudioManager.class.getResource(caminho).toExternalForm())
            );
            tocarEfeitoDescartavel(media, caminhoRecurso);

        } catch (Exception e) {
            System.err.println(
                    "Nao foi possivel tocar o efeito '" + caminhoRecurso + "': " + e.getMessage()
            );
        }
    }

    private static void tocarEfeitoDescartavel(Media media, String caminho) {
        MediaPlayer player = new MediaPlayer(media);
        efeitosTocando.add(player);   // referencia forte ate o fim

        Runnable liberar = () -> {
            efeitosTocando.remove(player);
            player.dispose();
        };

        player.setVolume(ConfiguracoesJogo.getVolumeEfeitos());
        player.setOnEndOfMedia(liberar);
        player.setOnError(() -> {
            System.err.println("Erro ao tocar '" + caminho + "': " + player.getError());
            liberar.run();
        });
        player.play();
    }
}