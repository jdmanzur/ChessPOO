package main.java;

/**
 * @author: Jade Manzur de Almeida
 * 
 */

public enum CorEnum {

    BRANCO(false), PRETO(true);

    private final boolean cor;

    CorEnum(boolean cor) {
        this.cor = cor;
    }

    public boolean getCor() {
        return this.cor;
    }

}
