package com.poo2026_2.model;

import java.util.prefs.Preferences;

/** Configurações simples persistidas entre execuções do jogo. */
public final class ConfiguracoesJogo {

    private static final Preferences PREFS =
            Preferences.userNodeForPackage(ConfiguracoesJogo.class);

    private static final String RESOLUCAO = "resolucao";
    private static final String VOLUME_MUSICA = "volumeMusica";
    private static final String VOLUME_EFEITOS = "volumeEfeitos";
    private static final String VOLUME_VOZES = "volumeVozes";
    private static final String MUDO = "mudo";
    private static final String TELA_CHEIA = "telaCheia";
    private static final String NIVEL_CONCLUIDO = "ultimoNivelConcluido";
    private static final String CHEAT_ATIVO = "cheatDesbloqueioTotal";

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

    /** Volume das vozes (0.0 a 1.0). Padrao 0.50 para dar folga ao ganho da tabela do AudioManager. */
    public static double getVolumeVozes() {
        return PREFS.getDouble(VOLUME_VOZES, 0.50);
    }

    public static void setVolumeVozes(double volume) {
        PREFS.putDouble(VOLUME_VOZES, volume);
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

    /** Maior nivel ja vencido (0 = nenhum). O proximo nivel fica liberado. */
    public static int getUltimoNivelConcluido() {
        return PREFS.getInt(NIVEL_CONCLUIDO, 0);
    }

    /** Registra uma vitoria; so avanca o progresso, nunca volta. */
    public static void registrarNivelConcluido(int nivel) {
        if (nivel > getUltimoNivelConcluido()) {
            PREFS.putInt(NIVEL_CONCLUIDO, nivel);
        }
    }

    public static void reiniciarProgresso() {
        PREFS.putInt(NIVEL_CONCLUIDO, 0);
    }
    
    public static boolean isCheatAtivo() {
        return PREFS.getBoolean(CHEAT_ATIVO, false);
    }

    public static void setCheatAtivo(boolean ativo) {
        PREFS.putBoolean(CHEAT_ATIVO, ativo);
    }
}