package main.java;

import java.io.Serializable;

/**
 * 
 * a classe Posicao tem uma cor, uma linha e uma coluna, de (1 a 8/A a H) e cada
 * posição pode estar vazia ou ocupada por uma peça, sabendo qual peça a ocupa
 * 
 * @author: Jade Manzur de Almeida
 */

public class Posicao implements Serializable {
    /**
     * UID para serialização
     */
    private static final long serialVersionUID = -1221723759141231417L;
    private CorEnum cor;
    private boolean ocupada;
    private boolean selecionada;
    private int linha;
    private char coluna;
    private Peca peca;

    public Posicao(int linha, char coluna, CorEnum cor) {
        this.setOcupada(false);
        this.setLinha(linha);
        this.setColuna(coluna);
        this.setCor(cor);
    }

    /**
     * Seta a peça para a posição, também colocando ela como ocupada
     * 
     * @param p
     */
    public void setPeca(Peca p) {
        this.peca = p;
        this.setOcupada(true);
    }

    /**
     * retorna a peça corresponde a essa posição
     */
    public Peca getPeca() {
        if (isOcupada()) {
            return this.peca;
        } else {
            return null;
        }
    }

    /**
     * retorna se a posição está ocupada
     * 
     * @return verdadeiro se está ocupada, falso se não
     */
    public boolean isOcupada() {
        return this.ocupada;
    }

    /**
     * Seta a posição como ocupada
     */
    public void setOcupada(boolean ocupada) {
        this.ocupada = ocupada;
    }

    /**
     * retorna se a posição está selecionada
     * 
     * @return verdadeiro se está selecionada, falso se não
     */
    public boolean isSelecionada() {
        return this.selecionada;
    }

    /**
     * Seta a posição como selecionada ou não
     * 
     */
    public void setSelecionada(boolean sel) {
        this.selecionada = sel;
    }

    // retorna o caractere da coluna
    public char getColuna() {
        return this.coluna;
    }

    /**
     * @param coluna caractere entre A e H correspondente à coluna a função converte
     *               o inteiro para o respectivo caracter que representa a coluna,
     *               de A à H;
     */
    private boolean setColuna(char coluna) {
        if (coluna >= 'A' && coluna <= 'H') {
            this.coluna = coluna;
            return true;
        } else {
            return false;
        }
    }

    // retorna o inteiro correspondente à linha
    public int getLinha() {
        return this.linha;
    }

    /**
     * @param linha inteiro entre 1 e 8 correspondente à coluna a função converte o
     *              inteiro para o respectivo caracter que representa a linha, de 1
     *              à 8;
     */
    private boolean setLinha(int linha) {
        if (linha >= 1 && linha <= 8) {
            this.linha = linha;
            return true;
        } else {
            return false;
        }
    }

    public CorEnum getCor() {
        return cor;
    }

    // seta a cor da posição
    private void setCor(CorEnum cor) {
        this.cor = cor;
    }

}
