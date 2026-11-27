package com.poo2026_2.model;

import com.poo2026_2.model.utilizaveis.Cafeteira;
import com.poo2026_2.model.utilizaveis.Daniel;
import com.poo2026_2.model.utilizaveis.Fabricio;
import com.poo2026_2.model.utilizaveis.Glaucia;
import com.poo2026_2.model.utilizaveis.Lucas;
import com.poo2026_2.model.utilizaveis.Marcus;
import com.poo2026_2.model.utilizaveis.Nacif;
import com.poo2026_2.model.utilizaveis.Philipe;
import com.poo2026_2.model.utilizaveis.Thais;

/**
 * Catálogo de tudo que pode ser colocado no tabuleiro. É o ÚNICO lugar que
 * sabe: qual classe criar, como o personagem se chama na tela, em qual pasta
 * ficam os frames e qual é a imagem do botão/card.
 *
 * Convenção de arquivos (tudo em src/main/resources): frames no jogo :
 * /images/game/personagens/professores/<pasta>/<pasta>1.png ... (12 frames;
 * Lucas tem 24) card / botão :
 * /images/game/personagens/profiles/<pasta>-profile.png (ou .jpg)
 *
 * Tamanho na tela: cada personagem tem a sua própria caixa (largura x altura do
 * CANVAS inteiro do frame, já escalado) e um ajuste vertical, para que o corpo
 * de todos fique com a mesma altura e os pés na mesma linha da casa. Os
 * canvases dos PNGs têm tamanhos diferentes, por isso o valor é por personagem.
 * Para mudar o tamanho de um deles, basta mexer só na linha dele aqui.
 */
public enum TipoPersonagem {

    //            nome       pasta       frames  largura  altura  ajusteY
    CAFETEIRA("Cafeteira", "cafeteira", 12, 130.1, 130.1, 7.5, (l, c) -> new Cafeteira(100, l, c, 60, Cafeteira.SEGUNDOS_ATE_O_ESTAGIO_FINAL)),
    DANIEL("Daniel", "daniel", 12, 154.4, 154.4, -14.5, Daniel::new),
    FABRICIO("Fabrício", "fabricio", 12, 148.1, 148.1, -17.5, Fabricio::new),
    GLAUCIA("Gláucia", "glaucia", 12, 137.9, 137.9, -13.7, Glaucia::new),
    LUCAS("Lucas", "lucas", 24, 144.0, 144.0, -10.0, Lucas::new),
    MARCUS("Marcus", "marcus", 12, 164.0, 164.0, -10.6, Marcus::new),
    NACIF("Nacif", "nacif", 12, 152.9, 152.0, -8.0, Nacif::new),
    PHILIPE("Philipe", "philipe", 12, 157.8, 140.1, -9.6, Philipe::new),
    THAIS("Thaís", "thais", 12, 161.6, 143.5, -8.7, Thais::new);

    /**
     * Quantidade padrão de frames de uma animação de professor.
     */
    public static final int FRAMES = 12;

    /**
     * Cria o personagem numa linha/coluna do tabuleiro.
     */
    @FunctionalInterface
    private interface Fabrica {

        Entidade criar(int linha, int coluna);
    }

    private final String nomeExibicao;
    private final String pasta;
    private final int quantidadeDeFrames;
    private final double larguraCaixa;
    private final double alturaCaixa;
    private final double ajusteY;
    private final Fabrica fabrica;

    TipoPersonagem(String nomeExibicao, String pasta, int quantidadeDeFrames,
            double larguraCaixa, double alturaCaixa, double ajusteY, Fabrica fabrica) {
        this.nomeExibicao = nomeExibicao;
        this.pasta = pasta;
        this.quantidadeDeFrames = quantidadeDeFrames;
        this.larguraCaixa = larguraCaixa;
        this.alturaCaixa = alturaCaixa;
        this.ajusteY = ajusteY;
        this.fabrica = fabrica;
    }

    /**
     * Quantos PNGs a animação tem (12 para todos, 24 para o Lucas).
     */
    public int getQuantidadeDeFrames() {
        return quantidadeDeFrames;
    }

    /**
     * Largura em que o canvas inteiro do frame é desenhado na casa.
     */
    public double getLarguraCaixa() {
        return larguraCaixa;
    }

    /**
     * Altura em que o canvas inteiro do frame é desenhado na casa.
     */
    public double getAlturaCaixa() {
        return alturaCaixa;
    }

    /**
     * Deslocamento vertical (px) que põe os pés na linha de chão da casa.
     */
    public double getAjusteY() {
        return ajusteY;
    }

    public String getNomeExibicao() {
        return nomeExibicao;
    }

    public Entidade criar(int linha, int coluna) {
        return fabrica.criar(linha, coluna);
    }

    public int getCusto() {
        return ((Instalavel) criar(0, 0)).getCustoCafe();
    }

    public double getTempoDeRecarga() {
        return ((Instalavel) criar(0, 0)).getTempoDeRecarga();
    }

    /**
     * true para professores (a Cafeteira é a única que não é).
     */
    public boolean ehProfessor() {
        return criar(0, 0) instanceof Professor;
    }

    /**
     * Padrão do caminho dos frames, para String.format(padrao, numero).
     */
    public String getPadraoFrames() {
        return "/images/game/personagens/professores/" + pasta + "/" + pasta + "%d.png";
    }

    /**
     * Imagem do botão/card. Aceita png, jpg ou jpeg (os seus profiles são
     * misturados). Se nenhum existir, devolve o caminho png esperado — assim a
     * mensagem de "imagem não encontrada" já diz onde colocar o arquivo.
     */
    public String getCaminhoIcone() {
        String base = "/images/game/personagens/profiles/" + pasta + "-profile";
        for (String extensao : new String[]{".png", ".jpg", ".jpeg"}) {
            if (TipoPersonagem.class.getResource(base + extensao) != null) {
                return base + extensao;
            }
        }
        // cafeteira não tem profile: usa o primeiro frame
        String primeiroFrame = String.format(getPadraoFrames(), 1);
        if (TipoPersonagem.class.getResource(primeiroFrame) != null) {
            return primeiroFrame;
        }
        return base + ".png";
    }

    /**
     * Onde fica o corpo dentro do canvas do frame, como fracao da altura (0 =
     * topo, 1 = base): {topo da cabeca, pes}. Medido nos PNGs.
     */
    private double[] limitesDoCorpo() {
        return switch (this) {
            case DANIEL ->
                new double[]{0.075, 0.980};
            case THAIS ->
                new double[]{0.035, 0.977};
            case PHILIPE ->
                new double[]{0.035, 0.941};
            case GLAUCIA ->
                new double[]{0.035, 0.990};
            case FABRICIO ->
                new double[]{0.087, 0.990};
            case MARCUS ->
                new double[]{0.153, 0.955};
            case NACIF ->
                new double[]{0.044, 0.979};
            case LUCAS ->
                new double[]{0.076, 0.976};
            default ->
                new double[]{0.050, 0.980};
        };
    }

    /**
     * Posicao vertical, em px dentro da casa (0 = topo da casa), do ponto que
     * esta a "fracao" da altura do corpo contando da cabeca para os pes. Usa a
     * mesma conta do sprite: caixa centrada na casa, deslocada pelo ajusteY.
     */
    public double yNaCasa(double fracao, double alturaDaCasa, double escala) {
        double[] corpo = limitesDoCorpo();
        double altura = alturaCaixa * escala;
        double topoDaCaixa = alturaDaCasa / 2 + ajusteY * escala - altura / 2;
        return topoDaCaixa + (corpo[0] + fracao * (corpo[1] - corpo[0])) * altura;
    }
}
