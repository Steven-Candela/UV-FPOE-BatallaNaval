package com.example.uvfpoebatallanaval.excepciones;

/**
 * Excepcion que se lanza cuando se intenta colocar un barco o realizar una acción
 * sobre una celda del tablero que ya está ocupada.
 *
 * @author Steven Candela
 */
public class ExcepcionCeldaOcupada extends Exception {
  /**
   * Crea una nueva excepción indicando que la celda ya está ocupada.
   *
   * @param mensaje Mensaje descriptivo del error.
   */
  public ExcepcionCeldaOcupada(String mensaje) {
    super(mensaje);
  }
}