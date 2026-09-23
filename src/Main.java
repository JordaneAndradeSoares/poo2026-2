import jogo.Jogo;

public class Main {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            Jogo jogo = new Jogo();
            jogo.iniciar();
        });
    }
}
