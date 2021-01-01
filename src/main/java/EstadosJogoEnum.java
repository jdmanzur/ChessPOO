package main.java;

/**
 * @author: Jade Manzur de Almeida
 * 
 */

public enum EstadosJogoEnum {

    INICIO(0), EM_ANDAMENTO(1), CHEQUE(2), CHEQUEMATE(3);

    private final int estado;

    EstadosJogoEnum(int estado) {
        this.estado = estado;
    }

    public int getEstado() {
        return this.estado;
    }

    public String printEstado() {

        switch (this.estado) {
            case 0:
                return "INICIO";
            case 1:
                return "EM ANDAMENTO";
            case 2:
                return "EM CHEQUE";
            case 3:
                return "CHEQUE-MATE!";
            default:
                return "NÃO INICIADO";
        }
    }

}
