package main.java;

import java.io.Serializable;

/**
 * classe que representa as funcionalidades gerais comuns a todas as peças, e
 * serve de base para todas as outras.
 * 
 * @author: Jade Manzur de Almeida
 * 
 */

public abstract class Peca implements Serializable {

    /**
     * UID for serialization
     */
    private static final long serialVersionUID = 1226989636884465311L;
    protected String desenhoPeca = "";
    protected boolean capturada;
    protected CorEnum cor;

    /**
     * Função para checar se um determinado movimento é válido para aquela peça
     * 
     * @param linhaOrigem   a linha de origem daquela peça
     * @param colunaOrigem  a coluna de origem daquela peça
     * @param linhaDestino  a linha do destino da peça
     * @param colunaDestino a coluna do destino da peça
     * @return true se o movimento é válido, false se é inválido
     */
    public abstract boolean checaMovimento(int linhaOrigem, char colunaOrigem, int linhaDestino, char colunaDestino);

    // seta o desenho da peça baseado em sua cor
    protected abstract void setDesenhoPeca(CorEnum cor);

    // retorna o desenho da peça
    public String desenho() {
        return this.desenhoPeca;
    }

    // retorna a cor da peça
    public CorEnum getCor() {
        return cor;
    }

    // seta a cor da peça
    protected void setCor(CorEnum cor) {
        this.cor = cor;
    }

    // retorna se a peça foi ou não capturada
    public boolean isCapturada() {
        return this.capturada;
    }

    // seta a peça como capturada
    public void setCapturada(boolean capturada) {
        this.capturada = capturada;
    }

}
