package main.java;

/**
 * @author: Jade Manzur de Almeida
 * 
 */

public class Torre extends Peca {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public Torre(CorEnum cor) {
        setCor(cor);
        setCapturada(false);
        setDesenhoPeca(cor);
    }

    protected void setDesenhoPeca(CorEnum cor) {
        if (cor == CorEnum.PRETO) {
            // se a cor é preto, colocar o unicode da peça preta
            this.desenhoPeca = "\u265C";
        } else {
            this.desenhoPeca = "\u2656";
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

        // a torre anda em linha reta para cima, baixo,
        // esquerda e direita, então apenas uma das coordenadas muda de cada vez
        return ((linhaOrigem == linhaDestino && cOrigem != cDestino) || // movendo-se na horizontal: linhas iguais
                (cOrigem == cDestino && linhaOrigem != linhaDestino)); // movendo-se na vertical: colunas iguais
    }
}
