package main.java;

import java.io.Serializable;

/**
 * @author: Jade Manzur de Almeida
 * 
 */

public class Jogador implements Serializable {

    /**
     * UID gerado para serializar esse objeto.
     */
    private static final long serialVersionUID = -301210643039964600L;

    private Peca[] pecas = new Peca[16];
    private String nome;
    private CorEnum cor;

    public Jogador(String nome, CorEnum cor, Peca[] p) {
        this.nome = nome;
        this.cor = cor;
        this.pecas = p;
    }

    /**
     * retorna o vetor de peças do jogador
     * 
     * @return vetor de objetos do tipo Peca
     */
    public Peca[] getPecas() {
        return pecas;
    }

    /**
     * retorna a cor escolhida pelo Jogador
     * 
     * @return representa a cor escolhida, em forma de um CorEnum
     */
    public CorEnum getCor() {
        return cor;
    }

    /**
     * retorna a string do nome do jogador
     * 
     * @return string correspondente ao nome do jogador
     */
    public String getNome() {
        return nome;
    }

    public boolean isReiCapturado() {
        for (int i = 0; i < 16; i++) {
            if (this.pecas[i] instanceof Rei)
                return pecas[i].isCapturada();
        }
        return false;
    }
}
