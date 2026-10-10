package com.poo2026_2.model;

import com.poo2026_2.model.utilizaveis.Cafeteira;
import com.poo2026_2.model.utilizaveis.Cafe;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Motor do jogo: guarda o estado de uma partida e a atualiza a cada "tick".
 * Quem chama atualizar(deltaTime) e o JogoController (via AnimationTimer).
 *
 * Unidade de posicao: CASAS do tabuleiro. A coluna c ocupa [c, c+1), entao o
 * centro da coluna c e c + 0.5. Velocidades sao em casas por segundo.
 * 
 * @author thoma
 */
public class GerenciadorDeJogo {
 
    /** Tolerancia (em casas) para o tiro acertar o zumbi. */
    private static final double RAIO_ACERTO = 0.4;
 
    /**
     * true: o cafe produzido ja entra direto no saldo do jogador.
     * false: o Cafe fica em cafesNoChao ate o jogador clicar (coletarCafe).
     */
    private static final boolean COLETA_AUTOMATICA = true;
 
    private Fase faseAtual;
    private Tabuleiro tabuleiro;
    private int cafe;
    private List<Zumbi> zumbis;
    private List<Tiro> tiros;
    private List<Cafe> cafesNoChao;
    private boolean jogoAtivo;
    private boolean vitoria;
    private boolean terminou;
 
    // ===================== CONSTRUTOR =====================
    public GerenciadorDeJogo(Fase faseAtual, Tabuleiro tabuleiro) {
        this.faseAtual = faseAtual;
        this.tabuleiro = tabuleiro;
        this.cafe = faseAtual.getCafeInicial();
        this.zumbis = new ArrayList<>();
        this.tiros = new ArrayList<>();
        this.cafesNoChao = new ArrayList<>();
        this.jogoAtivo = false;
        this.vitoria = false;
        this.terminou = false;
    }
 
    /** Liga o jogo */
    public void iniciarJogo() {
        jogoAtivo = true;
    }
 
    /** Um passo da simulacao. deltaTime em segundos. */
    public void atualizar(double deltaTime) {
        if (!jogoAtivo) {
            return;
        }
        gerarZumbisDaOnda(deltaTime);
        atualizarZumbis(deltaTime);
        professoresAtacam(deltaTime);
        atualizarTiros(deltaTime);
        verificarColisoes(deltaTime);
        gerarCafe(deltaTime);
        atualizarCafesNoChao(deltaTime);
        verificarVitoria();
        verificarDerrota();
    }
 
    public void pausar() {
        jogoAtivo = false;
    }
 
    public void continuar() {
        // so retoma se a partida ainda nao acabou
        if (!terminou) {
            jogoAtivo = true;
        }
    }
 
    public void encerrar() {
        jogoAtivo = false;
        zumbis.clear();
        tiros.clear();
        cafesNoChao.clear();
        tabuleiro.limpar();   // tira professores e cafeteiras dos pisos
    }
 
    // ===================== PISO (professores e cafeteiras) =====================
 
    /**
     * Instala o professor no piso se ele for valido, estiver livre e houver
     * cafe suficiente. So gasta o cafe se a instalacao der certo.
     */
    public boolean adicionarProfessor(Professor professor, int linha, int coluna) {
        if (professor == null) {
            return false;
        }
        return instalarNoPiso(professor, professor.getCustoCafe(), linha, coluna);
    }
 
    public boolean adicionarCafeteira(Cafeteira cafeteira, int linha, int coluna) {
        if (cafeteira == null) {
            return false;
        }
        return instalarNoPiso(cafeteira, cafeteira.getCustoCafe(), linha, coluna);
    }
 
    /** Tira quem estiver no piso (professor ou cafeteira). */
    public void removerDoPiso(int linha, int coluna) {
        removerOcupanteDoPiso(linha, coluna);
    }
 
    private boolean instalarNoPiso(Entidade entidade, int custo, int linha, int coluna) {
        if (cafe < custo) {
            return false;
        }
        if (!tentarInstalarNoPiso(entidade, linha, coluna)) {
            return false;   // piso invalido ou ocupado: nao gasta nada
        }
        gastarCafe(custo);
        return true;
    }
 
    // ===================== CAFE =====================
 
    public void adicionarCafe(int quantidade) {
        if (quantidade > 0) {
            cafe += quantidade;
        }
    }
 
    /** @return true se havia cafe suficiente e ele foi descontado */
    public boolean gastarCafe(int quantidade) {
        if (quantidade < 0 || cafe < quantidade) {
            return false;
        }
        cafe -= quantidade;
        return true;
    }
 
    public int getCafe() {
        return cafe;
    }
 
    /**
     * Percorre as cafeteiras do tabuleiro: se podeProduzir(deltaTime), chama
     * produzirCafe() e entrega o Cafe.
     */
    public void gerarCafe(double deltaTime) {
        for (Cafeteira cafeteira : listarCafeteiras()) {
            if (!cafeteira.estaVivo() || !cafeteira.podeProduzir(deltaTime)) {
                continue;
            }
            Cafe produzido = cafeteira.produzirCafe();
            if (COLETA_AUTOMATICA) {
                adicionarCafe(produzido.getQuantidade());
            } else {
                produzido.setPosicaoX(cafeteira.getColuna() + 0.5);
                produzido.setPosicaoY(cafeteira.getLinha() + 0.5);
                cafesNoChao.add(produzido);
            }
        }
    }
 
    /** Diminui o tempo de vida dos cafes no chao e remove os que "esfriaram". */
    private void atualizarCafesNoChao(double deltaTime) {
        Iterator<Cafe> it = cafesNoChao.iterator();
        while (it.hasNext()) {
            if (!it.next().atualizar(deltaTime)) {
                it.remove();
            }
        }
    }
 
    /** Jogador clicou no cafe do chao: soma ao saldo. */
    public void coletarCafe(Cafe cafeColetado) {
        if (cafesNoChao.remove(cafeColetado)) {
            adicionarCafe(cafeColetado.getQuantidade());
        }
    }
 
    public List<Cafe> getCafesNoChao() {
        return cafesNoChao;
    }
 
    // ===================== ZUMBIS =====================
 
    /** Coloca o zumbi na borda direita do tabuleiro e o adiciona a partida. */
    public void adicionarZumbi(Zumbi zumbi) {
        if (zumbi == null) {
            return;
        }
        zumbi.setPosicaoX(getColunas() + 0.5);   // logo fora da tela, a direita
        zumbis.add(zumbi);
    }
 
    public void removerZumbi(Zumbi zumbi) {
        zumbis.remove(zumbi);
    }
 
    /**
     * Move cada zumbi (mover()) e remove os que morreram (!estaVivo()).
     * Quem chegou ao fim do tabuleiro conta para a derrota.
     */
    public void atualizarZumbis(double deltaTime) {
        Iterator<Zumbi> it = zumbis.iterator();
        while (it.hasNext()) {
            Zumbi z = it.next();
            if (!z.estaVivo()) {
                it.remove();
                continue;
            }
            z.mover(deltaTime);
        }
    }
 
    // ===================== PROFESSORES ATIRAM =====================
 
    /**
     * Cada professor dispara quando a cadencia permite E ha algum zumbi vivo
     * na sua linha, a frente dele (a direita).
     */
    private void professoresAtacam(double deltaTime) {
        for (Professor professor : listarProfessores()) {
            // podeAtacar(deltaTime) mexe no relogio do professor
            if (haZumbiNaFrente(professor) && professor.podeAtacar(deltaTime)) {
                Tiro tiro = professor.atacar();
                tiro.setX(professor.getColuna() + 0.5);   // sai do centro da casa
                tiro.setY(professor.getLinha());
                adicionarTiro(tiro);
            }
        }
    }
 
    private boolean haZumbiNaFrente(Professor professor) {
        double xProfessor = professor.getColuna() + 0.5;
        for (Zumbi z : zumbis) {
            if (z.estaVivo() && z.getLinha() == professor.getLinha()
                    && z.getPosicaoX() >= xProfessor) {
                return true;
            }
        }
        return false;
    }
 
    // ===================== TIROS =====================
 
    public void adicionarTiro(Tiro tiro) {
        if (tiro != null) {
            tiros.add(tiro);
        }
    }
 
    public void removerTiro(Tiro tiro) {
        tiros.remove(tiro);
    }
 
    /** Move cada tiro (mover()) e remove os inativos ou fora do tabuleiro. */
    public void atualizarTiros(double deltaTime) {
        Iterator<Tiro> it = tiros.iterator();
        while (it.hasNext()) {
            Tiro t = it.next();
            t.mover(deltaTime);
            if (!t.isAtivo() || t.getX() > getColunas() + 1) {
                it.remove();
            }
        }
    }
 
    // ===================== COLISOES =====================
 
    /**
     * 1) tiro x zumbi: mesma linha e posicoes proximas -> tiro.atingir(zumbi).
     * 2) zumbi x piso: zumbi chegou num piso ocupado (professor ou
     *    cafeteira, ambos sao Entidade) -> iniciarAtaque() e o ocupante
     *    recebe dano (a cada mordida). Se morreu, remove do piso e
     *    pararAtaque().
     */
    public void verificarColisoes(double deltaTime) {
        colisaoTiroZumbi();
        colisaoZumbiPiso(deltaTime);
    }
 
    private void colisaoTiroZumbi() {
        for (Tiro tiro : tiros) {
            if (!tiro.isAtivo()) {
                continue;
            }
            Zumbi alvo = null;
            for (Zumbi z : zumbis) {
                if (!z.estaVivo() || z.getLinha() != (int) tiro.getY()) {
                    continue;
                }
                if (Math.abs(z.getPosicaoX() - tiro.getX()) <= RAIO_ACERTO) {
                    // se houver mais de um ao alcance, acerta o mais proximo da saida
                    if (alvo == null || z.getPosicaoX() < alvo.getPosicaoX()) {
                        alvo = z;
                    }
                }
            }
            if (alvo != null) {
                tiro.atingir(alvo);
            }
        }
    }
 
    private void colisaoZumbiPiso(double deltaTime) {
        for (Zumbi z : zumbis) {
            if (!z.estaVivo()) {
                continue;
            }
            int coluna = z.getColuna();
            Entidade ocupante = (coluna >= 0 && coluna < getColunas())
                    ? ocupanteDoPiso(z.getLinha(), coluna) : null;
 
            if (ocupante == null || !ocupante.estaVivo()) {
                z.pararAtaque();
                continue;
            }
            z.iniciarAtaque();
            z.atacar(ocupante, deltaTime);
            if (!ocupante.estaVivo()) {
                removerDoPiso(z.getLinha(), coluna);
                z.pararAtaque();
            }
        }
    }
 
    // ===================== VITORIA / DERROTA =====================
    public void verificarVitoria() {
        if (terminou) {
            return;
        }
        if (faseConcluida() && zumbis.isEmpty()) {
            vitoria = true;
            terminou = true;
            jogoAtivo = false;
        }
    }
 
    /**
     * Derrota = algum zumbi cruzou o tabuleiro inteiro (posicaoX <= 0).
     * Se for o caso: vitoria = false e jogoAtivo = false.
     */
    public void verificarDerrota() {
        if (terminou) {
            return;
        }
        for (Zumbi z : zumbis) {
            if (z.estaVivo() && z.getPosicaoX() <= 0) {
                vitoria = false;
                terminou = true;
                jogoAtivo = false;
                return;
            }
        }
    }
    public boolean terminou() {
        return terminou;
    }
 
    // ===================== GETTERS PARA A VIEW =====================
 
    public boolean isJogoAtivo() {
        return jogoAtivo;
    }
 
    public boolean isVitoria() {
        return vitoria;
    }
 
    public List<Zumbi> getZumbis() {
        return zumbis;
    }
 
    public List<Tiro> getTiros() {
        return tiros;
    }
 
    // ====================================================================
    // Tudo que depende das classes ainda nao prontas está abaixo
    // =====================================================================
 
    /** Quantidade de colunas do tabuleiro. */
    private int getColunas() {
        // return tabuleiro.getColunas();
        return 12;   // provisorio: (mesmo valor qque defini em JogoController.COLUNAS)
    }
 
    /** Todos os professores instalados nos pisos. */
    private List<Professor> listarProfessores() {
        // return tabuleiro.getProfessores();
        return Collections.emptyList();
    }
 
    /** Todas as cafeteiras instaladas nos pisos. */
    private List<Cafeteira> listarCafeteiras() {
        // return tabuleiro.getCafeteiras();
        return Collections.emptyList();
    }
 
    /** Quem esta no piso (linha, coluna), ou null se vazio/invalido. */
    private Entidade ocupanteDoPiso(int linha, int coluna) {
        // return tabuleiro.getOcupante(linha, coluna);
        return null;
    }
 
    /** Instala a entidade no piso. @return false se invalido ou ocupado. */
    private boolean tentarInstalarNoPiso(Entidade entidade, int linha, int coluna) {
        //  return tabuleiro.instalar(entidade, linha, coluna);
        //   (o Tabuleiro deve tambem atualizar entidade.setLinha/setColuna)
        return false;
    }
 
    private void removerOcupanteDoPiso(int linha, int coluna) {
        // tabuleiro.remover(linha, coluna);
    }
 
    /** Pede a Fase/Onda os zumbis que devem nascer neste instante e os adiciona. */
    private void gerarZumbisDaOnda(double deltaTime) {
        // for (Zumbi z : faseAtual.gerarZumbis(deltaTime)) { adicionarZumbi(z); }
        //  (a Onda controla o tempo entre spawns e a linha sorteada de cada zumbi)
    }
 
    /** true quando a Fase ja gerou todas as ondas. */
    private boolean faseConcluida() {
        // return faseAtual.estaConcluida();
        return false;
    }
}