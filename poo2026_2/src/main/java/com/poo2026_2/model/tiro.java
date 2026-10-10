//crie so a class , começar de verdade apartir do dia 12.

public class Tiro {
    private double x;
    private double y;
    private double velocidade;
    private int dano;
    private boolean ativo;
    private String sprite;
    private String efeitoVisual;
    private String som;

    public Tiro() {
        this.x = 0;
        this.y = 0;
        this.velocidade = 1;
        this.dano = 1;
        this.ativo = true;
        this.sprite = "";
        this.efeitoVisual = "";
        this.som = "";
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getVelocidade() {
        return velocidade;
    }

    public void setVelocidade(double velocidade) {
        if (velocidade >= 0) {
            this.velocidade = velocidade;
        }
    }

    public int getDano() {
        return dano;
    }

    public void setDano(int dano) {
        if (dano >= 0) {
            this.dano = dano;
        }
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public String getSprite() {
        return sprite;
    }

    public void setSprite(String sprite) {
        this.sprite = sprite;
    }

    public String getEfeitoVisual() {
        return efeitoVisual;
    }

    public void setEfeitoVisual(String efeitoVisual) {
        this.efeitoVisual = efeitoVisual;
    }

    public String getSom() {
        return som;
    }

    public void setSom(String som) {
        this.som = som;
    }

    public void mover() {
        if (ativo) {
            y -= velocidade;
        }
    }

    public void atingir() {
        ativo = false;
    }

    public void desativar() {
        ativo = false;
    }
}