package main.java;

/**
 * @author: Jade Manzur de Almeida
 * 
 */

public class Cavalo extends Peca {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public Cavalo(CorEnum cor) {
        setCor(cor);
        setCapturada(false);
        setDesenhoPeca(cor);
    }

    protected void setDesenhoPeca(CorEnum cor) {
        if (cor == CorEnum.PRETO) {
            // se a cor é preto, colocar o unicode da peça preta
            this.desenhoPeca = "\u265E";
        } else {
            this.desenhoPeca = "\u2658";
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
        int cOrigem = (int) colunaOrigem - 64;
        int cDestino = (int) colunaDestino - 64;

        // o cavalo anda em L, tanto para a vertical quanto para a horizontal
        return (Math.abs(linhaOrigem - linhaDestino) == 2 && Math.abs(cOrigem - cDestino) == 1)
                || (Math.abs(linhaOrigem - linhaDestino) == 1 && Math.abs(cOrigem - cDestino) == 2);
    }

}
