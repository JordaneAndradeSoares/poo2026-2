package com.poo2026_2.model;

import java.util.prefs.Preferences;

/** Configurações simples persistidas entre execuções do jogo. */
public final class ConfiguracoesJogo {

    private static final Preferences PREFS =
            Preferences.userNodeForPackage(ConfiguracoesJogo.class);

    private static final String RESOLUCAO = "resolucao";
    private static final String VOLUME_MUSICA = "volumeMusica";
    private static final String VOLUME_EFEITOS = "volumeEfeitos";
    private static final String MUDO = "mudo";
    private static final String TELA_CHEIA = "telaCheia";

    private ConfiguracoesJogo() { }

    public static String getResolucao() {
        return PREFS.get(RESOLUCAO, "1280x720");
    }

    public static void setResolucao(String resolucao) {
        PREFS.put(RESOLUCAO, resolucao);
    }

    public static double getVolumeMusica() {
        return PREFS.getDouble(VOLUME_MUSICA, 0.70);
    }

    public static void setVolumeMusica(double volume) {
        PREFS.putDouble(VOLUME_MUSICA, volume);
    }

    public static double getVolumeEfeitos() {
        return PREFS.getDouble(VOLUME_EFEITOS, 0.80);
    }

    public static void setVolumeEfeitos(double volume) {
        PREFS.putDouble(VOLUME_EFEITOS, volume);
    }

    public static boolean isMudo() {
        return PREFS.getBoolean(MUDO, false);
    }

    public static void setMudo(boolean mudo) {
        PREFS.putBoolean(MUDO, mudo);
    }

    public static boolean isTelaCheia() {
        return PREFS.getBoolean(TELA_CHEIA, false);
    }

    public static void setTelaCheia(boolean telaCheia) {
        PREFS.putBoolean(TELA_CHEIA, telaCheia);
    }
}
