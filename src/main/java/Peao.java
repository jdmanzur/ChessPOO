package main.java;

/**
 * @author: Jade Manzur de Almeida
 * 
 */

public class Peao extends Peca {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public Peao(CorEnum cor) {
        setCor(cor);
        setCapturada(false);
        setDesenhoPeca(cor);
    }

    protected void setDesenhoPeca(CorEnum cor) {
        if (cor == CorEnum.PRETO) {
            // se a cor é preto, colocar o unicode da peça preta
            this.desenhoPeca = "\u265F";
        } else {
            this.desenhoPeca = "\u2659";
        }
    }

    /**
     * Função para checar se um determinado movimento é válido para aquela peça
     * 
     * @param linhaOrigem   a linha de origem daquela peça
     * @param colunaOrigem  a coluna de origem daquela peça
     * @param linhaDestino  a linha do destino da peça
     * @param colunaDestino a coluna do destino da peça
     * @return true se o movimento é válido, false se é inválido
     */
    public boolean checaMovimento(int linhaOrigem, char colunaOrigem, int linhaDestino, char colunaDestino) {

        if (cor == CorEnum.PRETO) {
            // peão só irá andar de cima do tabuleiro para baixo
            // checa o primeiro movimento: linha de origem 2 e pode dar até dois passos
            return (colunaOrigem == colunaDestino
                    && ((linhaDestino - linhaOrigem == 1) || (linhaOrigem == 2 && linhaDestino - linhaOrigem == 2)));

        } else {
            // peão só irá andar de baixo do tabuleiro para cima
            // checa o primeiro movimento: linha de origem 7 e pode dar até dois passos
            return (colunaOrigem == colunaDestino
                    && ((linhaOrigem - linhaDestino == 1) || (linhaOrigem == 7 && linhaOrigem - linhaDestino == 2)));
        }
    }
}
