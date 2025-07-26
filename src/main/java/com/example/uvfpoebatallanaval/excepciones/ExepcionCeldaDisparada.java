package com.example.uvfpoebatallanaval.excepciones;

/**
 * Excepción que se lanza cuando se intenta disparar a una celda
 * que ya ha sido atacada previamente en el tablero.
 *
 * @author Camilo Portilla
 */
public class ExepcionCeldaDisparada extends RuntimeException {
    /**
     * Crea una nueva excepción con un mensaje descriptivo del error.
     *
     * @param message Mensaje que describe el motivo del error.
     */
    public ExepcionCeldaDisparada(String message) {
        super(message);
    }
}
