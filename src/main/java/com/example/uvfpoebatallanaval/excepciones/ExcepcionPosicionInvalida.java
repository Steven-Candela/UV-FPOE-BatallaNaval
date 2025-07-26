package com.example.uvfpoebatallanaval.excepciones;

/**
 * Excepción que se lanza cuando se intenta colocar un barco en una posición
 * inválida del tablero (por ejemplo, fuera de los límites o en orientación incorrecta).
 *
 * @author Steven Candela
 */
public class ExcepcionPosicionInvalida extends Exception {
    /**
     * Crea una nueva excepción indicando que la posición no es válida.
     *
     * @param mensaje Mensaje descriptivo del error.
     */
    public ExcepcionPosicionInvalida(String mensaje) {
        super(mensaje);
    }
}
