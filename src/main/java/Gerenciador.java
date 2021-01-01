package main.java;

/**
 * 
 * classe que vai disparar o jogo Representa a classe principal e deve ser a
 * mais simples possível, simplesmente criando um objeto para o jogo atual
 * 
 * @author Jade Manzur de Almeida
 */

public class Gerenciador {

    public static void main(String[] args) {

        Jogo xadrez = new Jogo();
        xadrez.iniciarJogo();
    }
}
