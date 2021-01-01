package main.java;

/**
 * @author: Jade Manzur de Almeida
 * 
 */

public class Dama extends Peca {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public Dama(CorEnum cor) {
        setCor(cor);
        setCapturada(false);
        setDesenhoPeca(cor);
    }

    protected void setDesenhoPeca(CorEnum cor) {
        if (cor == CorEnum.PRETO) {
            // se a cor é preto, colocar o unicode da peça preta
            this.desenhoPeca = "\u265B";
        } else {
            this.desenhoPeca = "\u2655";
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

        // a dama consegue anda para qualquer posição que combine o movimento da Torre
        // com o do Bispo
        return ((Math.abs(linhaOrigem - linhaDestino) == Math.abs(cOrigem - cDestino)
                && Math.abs(cOrigem - cDestino) != 0) || // movimento diagonal
                (linhaOrigem == linhaDestino && cOrigem != cDestino) || // movendo-se na horizontal: linhas iguais
                (cOrigem == cDestino && linhaOrigem != linhaDestino)); // movendo-se na vertical: colunas iguais
    }
}
