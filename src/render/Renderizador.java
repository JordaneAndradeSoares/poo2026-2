package render;

import jogo.Jogo;
import entidades.*;
import util.Constantes;

import java.awt.*;
import java.util.List;

public class Renderizador {
    private final Jogo jogo;

    public Renderizador(Jogo jogo) {
        this.jogo = jogo;
    }

    public void desenhar(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        desenharFundo(g);
        desenharInterface(g);

        for (Planta p : jogo.getPlantas()) {
            if (p.estaViva()) p.desenhar(g);
        }

        for (Projetil p : jogo.getProjeteis()) {
            if (p.estaViva()) p.desenhar(g);
        }

        for (Zumbi z : jogo.getZumbis()) {
            if (z.estaViva()) z.desenhar(g);
        }

        for (Sol s : jogo.getSois()) {
            if (s.estaViva()) s.desenhar(g);
        }

        if (jogo.isPausado()) {
            g.setColor(new Color(0, 0, 0, 130));
            g.fillRect(0, 0, Constantes.LARGURA, Constantes.ALTURA);
            textoCentral(g, "PAUSADO", 44);
        }

        if (jogo.isDerrota()) {
            g.setColor(new Color(0, 0, 0, 160));
            g.fillRect(0, 0, Constantes.LARGURA, Constantes.ALTURA);
            textoCentral(g, "VOCÊ PERDEU!", 48);
            g.setFont(new Font("Arial", Font.BOLD, 22));
            g.setColor(Color.WHITE);
            g.drawString("Pressione R para reiniciar", 405, 410);
        }

        if (jogo.isVitoria()) {
            g.setColor(new Color(0, 0, 0, 160));
            g.fillRect(0, 0, Constantes.LARGURA, Constantes.ALTURA);
            textoCentral(g, "VOCÊ VENCEU!", 48);
            g.setFont(new Font("Arial", Font.BOLD, 22));
            g.setColor(Color.WHITE);
            g.drawString("Pressione R para jogar novamente", 365, 410);
        }
    }

    private void desenharFundo(Graphics2D g) {
        g.setColor(new Color(120, 205, 80));
        g.fillRect(0, 0, Constantes.LARGURA, Constantes.ALTURA);

        g.setColor(new Color(225, 238, 205));
        g.fillRect(0, 0, Constantes.LARGURA, Constantes.TOPO);

        g.setColor(new Color(105, 180, 65));
        g.fillRect(0, Constantes.TOPO, 70, Constantes.ALTURA - Constantes.TOPO);

        for (int l = 0; l < Constantes.LINHAS; l++) {
            for (int c = 0; c < Constantes.COLUNAS; c++) {
                int x = Constantes.CAMPO_X + c * Constantes.CELULA_LARGURA;
                int y = Constantes.CAMPO_Y + l * Constantes.CELULA_ALTURA;

                g.setColor((l + c) % 2 == 0
                        ? new Color(150, 220, 105)
                        : new Color(137, 210, 92));
                g.fillRect(x, y,
                        Constantes.CELULA_LARGURA,
                        Constantes.CELULA_ALTURA);

                g.setColor(new Color(105, 180, 70));
                g.drawRect(x, y,
                        Constantes.CELULA_LARGURA,
                        Constantes.CELULA_ALTURA);
            }
        }
    }

    private void desenharInterface(Graphics2D g) {
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 18));
        g.drawString("Sóis: " + jogo.getSoisJogador(), 15, 30);
        g.drawString("Onda: " + jogo.getOnda(), 15, 58);

        int cardX = 180;
        for (int i = 0; i < jogo.getCartas().size(); i++) {
            Jogo.Carta carta = jogo.getCartas().get(i);

            g.setColor(jogo.getCartaSelecionada() == i
                    ? new Color(255, 235, 100)
                    : Color.WHITE);
            g.fillRoundRect(cardX, 12, 125, 70, 12, 12);

            g.setColor(Color.DARK_GRAY);
            g.drawRoundRect(cardX, 12, 125, 70, 12, 12);

            g.setFont(new Font("Arial", Font.BOLD, 14));
            g.drawString(carta.nome, cardX + 8, 37);

            g.setFont(new Font("Arial", Font.PLAIN, 13));
            g.drawString("Custo: " + carta.custo, cardX + 8, 59);

            cardX += 140;
        }

        g.setColor(Color.DARK_GRAY);
        g.setFont(new Font("Arial", Font.PLAIN, 12));
        g.drawString("Clique em uma carta e depois no gramado.  P = pausa | R = reiniciar",
                260, 705);
    }

    private void textoCentral(Graphics2D g, String texto, int tamanho) {
        g.setFont(new Font("Arial", Font.BOLD, tamanho));
        g.setColor(Color.WHITE);
        int largura = g.getFontMetrics().stringWidth(texto);
        g.drawString(texto, (Constantes.LARGURA - largura) / 2, 360);
    }
}
