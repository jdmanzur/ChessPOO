package main.java;

import java.io.Serializable;

/**
 * Essa classe é responsável pela configuração inicial do tabuleiro, manutenção
 * da configuração do tabuleiro a cada jogada e pelas checagens de adequação dos
 * movimentos solicitados, bem como pelo desenho do tabuleiro (com as peças nas
 * posições ocupadas) na tela.
 * 
 * @author: Jade Manzur de Almeida
 * 
 */

public class Tabuleiro implements Serializable {

    /**
     * UID gerado para serialização desse objeto
     */
    private static final long serialVersionUID = -1575230228322781531L;

    private Posicao[][] tabuleiroPosicao = new Posicao[8][8];

    /**
     * Monta o tabuleiro, inicializando e colorindo as posições
     */
    public Tabuleiro(Peca[] pretas, Peca[] brancas) {
        CorEnum cor;
        // cria as posições corretamente com as cores certas
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                // atribui o valor true para a cor sempre que as
                // linhas e colunas tiverem paridades iguais
                if ((i % 2) == (j % 2))
                    cor = CorEnum.PRETO;
                else
                    cor = CorEnum.BRANCO;

                this.tabuleiroPosicao[i][j] = new Posicao(i + 1, ((char) (j + 'A')), cor);

            }
        }

        colocarPecas(pretas);
        colocarPecas(brancas);

    }

    /**
     * Coloca o vetor de peças nas posições adequadas do tabuleiro, considerando sua
     * cor
     * 
     * @param p vetor de peças a ser inserido
     */
    private void colocarPecas(Peca[] p) {
        int linhaInicial = 0, resto = 0;
        if (p[0].getCor() == CorEnum.BRANCO) {
            resto = 7;
            linhaInicial = 6;
        } else if (p[0].getCor() == CorEnum.PRETO) {
            linhaInicial = 1;
        }

        int peao = 0, torre = 0, cavalo = 1, bispo = 2, rainha = 3, rei = 4;

        // adiciona os peoes no tabuleiro
        for (int i = 0; i < 16; i++) {
            if (p[i] instanceof Peao) {
                tabuleiroPosicao[linhaInicial][peao].setPeca(p[i]);
                peao++;

            } else if (p[i] instanceof Torre) {
                tabuleiroPosicao[resto][torre].setPeca(p[i]);
                torre = 7;
            } else if (p[i] instanceof Cavalo) {
                tabuleiroPosicao[resto][cavalo].setPeca(p[i]);
                cavalo = 6;
            } else if (p[i] instanceof Bispo) {
                tabuleiroPosicao[resto][bispo].setPeca(p[i]);
                bispo = 5;
            } else if (p[i] instanceof Dama) {
                tabuleiroPosicao[resto][rainha].setPeca(p[i]);
            } else if (p[i] instanceof Rei) {
                tabuleiroPosicao[resto][rei].setPeca(p[i]);
            }
        }

    }

    /**
     * Retona um vetor de Strings, com cada string representando uma linha do
     * tabuleiro;
     */

    public String getDesenhoPosicao(int i, int j) {
        try {
            if (this.tabuleiroPosicao[i][j].isOcupada()) {
                return " " + this.tabuleiroPosicao[i][j].getPeca().desenho() + " ";
            } else if (this.tabuleiroPosicao[i][j].getCor() == CorEnum.PRETO) {
                return "▒▒▒";
            } else {
                return "███";
            }
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    /**
     * Retorna um vetor de strings em que cada entrada representa uma linha do
     * tabuleiro
     * 
     * @return vetor de strings com as strings do tabuleiro
     */
    public String[] mostrarTabuleiro() {

        String[] s = new String[12];
        s[0] = "╭┈┈A┈┈B┈┈C┈┈D┈┈E┈┈F┈┈G┈┈H┈┈╮\n";
        s[1] = "╔══════════════════════════╗\n";

        for (int i = 0; i < 8; i++) {
            s[i + 2] = "";
            s[i + 2] += this.tabuleiroPosicao[i][0].getLinha() + " ";

            for (int j = 0; j < 8; j++) {

                if (this.tabuleiroPosicao[i][j].getCor() == CorEnum.PRETO) {
                    if (this.tabuleiroPosicao[i][j].isOcupada()) {
                        s[i + 2] += " " + this.tabuleiroPosicao[i][j].getPeca().desenho() + " ";
                    } else {
                        s[i + 2] += "▒▒▒";
                    }
                } else {
                    if (this.tabuleiroPosicao[i][j].isOcupada()) {
                        s[i + 2] += " " + this.tabuleiroPosicao[i][j].getPeca().desenho() + " ";
                    } else {
                        s[i + 2] += "███";
                    }
                }

            }
            s[i + 2] += " ║";
        }
        s[10] = "╚══════════════════════════╝\n";

        return s;

    }

    /**
     * 
     * Verifica se um movimento é valido baseado em uma série de fatores:
     * 
     * - Verifica se há uma peça na posição inicial e se o movimento a ser feito é
     * adequado para ela - Verifica se a peça não salta sobre outras em seu
     * percurso, (com exceção do cavalo, que pode realizar esse movimento) -
     * Verifica se as coordenadas de origem e destino estão dentro do tabuleiro
     * 
     * Dispara uma exceção se alguma das coordenadas não estiver dentro do tabuleiro
     * 
     * 
     * @param linhaOrigem   linha de origem da peça a ser movida
     * @param colunaOrigem  coluna de origem da peça a ser movida
     * @param linhaDestino  linha de destino da peça a ser movida
     * @param colunaDestino coluna de destino da peça a ser movida
     * @return retorna verdadeiro se o movimento foi executado com sucesso
     */
    public boolean checaMovimento(int linhaOrigem, char colunaOrigem, int linhaDestino, char colunaDestino) {

        // caso esteja dentro do limite, analisa as seguintes situações:
        // 1-peça presente na posição final:
        // a- se for do inimigo o movimento é valido e a peça pode ser comida,
        // b- é invalido caso contrario
        // 2- existem peças bloqueando o caminho.

        Posicao origem, destino;
        int cOrigem = (int) colunaOrigem - 65;
        int cDestino = (int) colunaDestino - 65;
        int lOrigem = linhaOrigem - 1;
        int lDestino = linhaDestino - 1;

        try {

            origem = tabuleiroPosicao[lOrigem][cOrigem];
            destino = tabuleiroPosicao[lDestino][cDestino];

        } catch (IndexOutOfBoundsException i) {
            // System.out.println("Posição fora do limite do tabuleiro!");
            return false;
        }

        if (origem == null) {
            // System.out.println("A posição inicial está vazia!");
            return false;
        }

        // verifica se o caminho da peça está livre para ela se movimentar
        if (caminhoLivre(lOrigem, cOrigem, lDestino, cDestino)) {
            // se o caminho está livre e o movimento é valido, pode se movimentar
            if (origem.getPeca().checaMovimento(linhaOrigem, colunaOrigem, linhaDestino, colunaDestino))
                return true;
        }

        // checa o movimento diagonal do peao
        if (origem.getPeca() instanceof Peao) {
            // se o movimento for válido para o peão, retorna verdadeiro
            // se está na diagonal e há uma peça da cor oposta ocupando a posicao final,
            // o peão pode se movimentar e comer a peça
            if (((cDestino == cOrigem - 1) || (cDestino == cOrigem + 1)) && Math.abs(lDestino - lOrigem) == 1) {
                if (destino.isOcupada() && destino.getPeca().getCor() != origem.getPeca().getCor())
                    return true;
            }

        }

        return false;
    }

    /**
     * Verifica se o percurso de uma peça até o seu destino não tem nenhum obstáculo
     * e pode ser realizado com sucesso
     * 
     * 
     * Dispara uma exceção caso acesse uma linha ou coluna fora dos limites do
     * tabuleiro
     * 
     * @param lrigem   indice da linha de origem da peça a ser movida
     * @param cOrigem  indice da coluna de origem da peça a ser movida
     * @param lDestino indice da linha de destino da peça a ser movida
     * @param cDestino indice da coluna de destino da peça a ser movida
     * @return retorna verdadeiro se o caminho está livre para a peça se mexer
     */
    private boolean caminhoLivre(int lOrigem, int cOrigem, int lDestino, int cDestino) {

        // se há uma peça na posição final do movimento e ela possui a mesma cor da peça
        // que está se movimentando
        // o movimento é inválido
        Posicao origem, destino;

        try {

            origem = tabuleiroPosicao[lOrigem][cOrigem];
            destino = tabuleiroPosicao[lDestino][cDestino];

        } catch (IndexOutOfBoundsException i) {
            // System.out.println("Posição fora do limite do tabuleiro!");
            return false;
        }

        // checa se a posição de destino está ocupada
        if (destino.isOcupada()) {
            if (origem.getPeca() instanceof Peao)
                return false;

            if (origem.getPeca().getCor() == destino.getPeca().getCor()) {
                // System.out.println("A posição está ocupada!");
                return false;
            }
            // há uma peça da cor oposta bloqueando a posição final
        }

        // checa se há peças no caminho a ser percorrido
        // o cavalo passa por cima das peças então essa checagem não é necessária para
        // eles.
        // elimina o cavalo das checagens
        if (origem.getPeca() instanceof Cavalo)
            return true;

        // movimento vertical
        if (cOrigem == cDestino) {
            int inicio, fim;

            inicio = (lOrigem < lDestino) ? lOrigem : lDestino; // recebe o menor indice da linha
            fim = (lOrigem > lDestino) ? lOrigem : lDestino; // recebe o maior

            // itera do menor ao maior procurando por obstaculos
            for (int i = inicio + 1; i < fim; i++) {
                if (tabuleiroPosicao[i][cOrigem].isOcupada()) {
                    // System.out.println("O caminho está bloqueado!");
                    return false;
                }
            }

        } else if (lOrigem == lDestino) {
            // movimento horizontal
            int inicio, fim;

            inicio = (cOrigem < cDestino) ? cOrigem : cDestino; // recebe o menor indice da coluna
            fim = (cOrigem > cDestino) ? cOrigem : cDestino; // recebe o maior

            // itera do menor ao maior procurando por obstaculos
            for (int i = inicio + 1; i < fim; i++) {
                if (tabuleiroPosicao[lOrigem][i].isOcupada()) {
                    // System.out.println("o caminho está bloqueado!");
                    return false;
                }
            }

        } else {
            // movimento diagonal
            int cInicio = (cOrigem < cDestino) ? cOrigem : cDestino; // recebe o menor indice da coluna
            int lInicio = (lOrigem < lDestino) ? lOrigem : lDestino; // recebe o menor indice da linha
            int lFim = (lOrigem > lDestino) ? lOrigem : lDestino; // recebe o maior

            // diagonal superior direita e diagonal inferior esquerda
            if (lDestino - lOrigem < 0 && cDestino - cOrigem < 0 || lDestino - lOrigem > 0 && cDestino - cOrigem > 0) {

                for (int j = cInicio + 1, i = lInicio + 1; i < lFim; i++, j++) {
                    try {

                        if (tabuleiroPosicao[i][j].isOcupada()) {
                            // System.out.println("O caminho está bloqueado!");
                            return false;
                        }
                    } catch (IndexOutOfBoundsException io) {
                        continue;
                    }
                }

                // diagonal superior esquerda e diagonal inferior direita
            } else if (lDestino - lOrigem < 0 && cDestino - cOrigem > 0
                    || lDestino - lOrigem > 0 && cDestino - cOrigem < 0) {

                for (int j = cInicio + 1, i = lFim - 1; i > lInicio; i--, j++) {
                    try {

                        if (tabuleiroPosicao[i][j].isOcupada()) {
                            // System.out.println("O caminho está bloqueado!");
                            return false;
                        }
                    } catch (IndexOutOfBoundsException e) {
                        continue;
                    }
                }
            }
        }

        return true;

    }

    /**
     * 
     * Esse método move uma peça de sua posição de origem para a posição de destino
     * O método checaMovimento é utilizado para verificar se o movimento é valido
     * (ou seja, não há obstaculos e o movimento pertence à peça) A função move
     * então a peça da posição de origem até sua posição de destino capturando a
     * peça na posicao de destino se for adequado;
     * 
     * @param linhaOrigem   linha de origem da peça a ser movida
     * @param colunaOrigem  col de origem da peça
     * @param linhaDestino  linha de destino
     * @param colunaDestino col de destino
     * @return retorna verdadeiro se o movimento foi realizado com sucesso
     */
    public boolean moverPeca(int linhaOrigem, char colunaOrigem, int linhaDestino, char colunaDestino) {

        if (checaMovimento(linhaOrigem, colunaOrigem, linhaDestino, colunaDestino)) {

            int cOrigem = (int) colunaOrigem - 65;
            int cDestino = (int) colunaDestino - 65;
            int lOrigem = linhaOrigem - 1;
            int lDestino = linhaDestino - 1;

            if (tabuleiroPosicao[lDestino][cDestino].isOcupada()) {
                // se o movimento foi considerado válido e a posicao de destino está ocupada, é
                // porque
                // a peça que a ocupa possui cor oposta e pode ser capturada
                tabuleiroPosicao[lDestino][cDestino].getPeca().setCapturada(true);
            }
            // move a peça
            tabuleiroPosicao[lDestino][cDestino].setPeca(tabuleiroPosicao[lOrigem][cOrigem].getPeca());
            tabuleiroPosicao[lOrigem][cOrigem].setOcupada(false);

            return true;
        }
        return false;
    }

    /**
     * Método que retorna uma peça em uma certa posição Se a posição estiver fora
     * dos limites do tabuleiro, retorna uma peça nula caso não haja peças ou se
     * estiver fora do tabuleiro
     * 
     * @param linha  linha da peça
     * @param coluna coluna da peça
     * @return retorna a peça que está na posição linha, coluna do tabuleiro
     */
    public Peca getPecaPosicao(int linha, char coluna) {
        int c = (int) coluna - 65;
        int l = linha - 1;

        try {
            return tabuleiroPosicao[l][c].getPeca();
        } catch (IndexOutOfBoundsException i) {
            return null;
        }
    }

    /**
     * Método que retorna uma certa posição Se a posição estiver fora dos limites do
     * tabuleiro, retorna uma peça nula caso não haja peças ou se estiver fora do
     * tabuleiro
     * 
     * @param l linha da peça
     * @param c coluna da peça
     * @return retorna a posição do tabuleiro
     */
    public Posicao getPosicao(int l, int c) {
        try {

            return tabuleiroPosicao[l][c];
        } catch (IndexOutOfBoundsException e) {
            return null;
        }

    }

    /**
     * Checa se um dos reis está em cheque baseado em sua cor
     * 
     * @param cor do rei
     * @return verdadeiro se o rei está em cheque
     */
    public boolean isEmCheque(CorEnum cor) {
        // acha a posição do rei
        int reix = 1;
        char reiy = 'A';
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (tabuleiroPosicao[i][j].isOcupada()) {
                    Peca p = tabuleiroPosicao[i][j].getPeca();
                    if (p instanceof Rei && p.getCor() == cor) {
                        reix = i + 1;
                        reiy = ((char) (j + 'A'));
                        break;
                    }
                }
            }
        }

        // checa para todas as demais peças de cor oposta
        // se o caminho até o rei está desocupado e se o movimento é valido.
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (tabuleiroPosicao[i][j].isOcupada() && tabuleiroPosicao[i][j].getPeca().getCor() != cor) {
                    if (checaMovimento(i + 1, ((char) (j + 'A')), reix, reiy)) {
                        // se o movimento da peça até o rei for válido, então ele pode ser capturado
                        return true;
                    }
                }

            }
        }

        return false;

    }

}