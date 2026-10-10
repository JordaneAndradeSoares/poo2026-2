package com.poo2026_2.audio;

import com.poo2026_2.model.ConfiguracoesJogo;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public final class AudioManager {

    private static final String MUSICA_MENU = "/audio/menu-loop.mp3";
    private static final String SOM_HOVER_BOTAO = "/audio/botao-hover.m4a";

    private static MediaPlayer musicaAtual;
    private static Media somHoverCache;

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

    /** Toca um efeito sonoro curto uma vez (nao interrompe a musica). */
    public static void tocarEfeito(String caminhoRecurso) {
        if (ConfiguracoesJogo.isMudo()) {
            return;
        }

        try {
            if (SOM_HOVER_BOTAO.equals(caminhoRecurso)) {
                if (somHoverCache == null) {
                    somHoverCache = new Media(
                            AudioManager.class.getResource(caminhoRecurso).toExternalForm()
                    );
                }
                tocarEfeitoDescartavel(somHoverCache);
                return;
            }

            Media media = new Media(
                    AudioManager.class.getResource(caminhoRecurso).toExternalForm()
            );
            tocarEfeitoDescartavel(media);

        } catch (Exception e) {
            System.err.println(
                    "Nao foi possivel tocar o efeito '" + caminhoRecurso + "': " + e.getMessage()
            );
        }
    }

    private static void tocarEfeitoDescartavel(Media media) {
        MediaPlayer player = new MediaPlayer(media);
        player.setVolume(ConfiguracoesJogo.getVolumeEfeitos());
        player.setOnEndOfMedia(player::dispose);
        player.setOnError(player::dispose);
        player.play();
    }
}
