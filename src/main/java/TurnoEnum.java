package main.java;

/**
 * @author: Jade Manzur de Almeida
 * 
 */

public enum TurnoEnum {
    J1(true), J2(false);

    private final boolean turno;

    TurnoEnum(boolean turno) {
        this.turno = turno;
    }

    public boolean getTurno() {
        return this.turno;
    }

    public String printTurno() {
        if (turno) {
            return "JOGADOR 1";
        } else {
            return "JOGADOR 2";
        }
    }
}
