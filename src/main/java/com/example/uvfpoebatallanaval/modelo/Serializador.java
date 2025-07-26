package com.example.uvfpoebatallanaval.modelo;

import java.io.*;

/**
 * Clase Serializador, utilitaria para guardar y cargar el estado del juego Batalla Naval.
 * Utiliza serialización para almacenar los tableros del jugador y de la máquina en un archivo.
 *
 * @author Steven Candela
 */
public class Serializador {
    /**
     * Guarda el estado actual de los tableros del jugador y de la máquina en un archivo.
     *
     * @param tableroJugador el tablero del jugador.
     * @param tableroMaquina el tablero de la máquina.
     * @param archivo        el archivo donde se guardará el estado.
     * @throws IOException si ocurre un error durante la escritura del archivo.
     */
    public static void guardarEstado(Tablero tableroJugador, Tablero tableroMaquina, File archivo) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(archivo))) {
            out.writeObject(tableroJugador);
            out.writeObject(tableroMaquina);
        }
    }

    /**
     * Carga el estado de los tableros del jugador y de la máquina desde un archivo.
     *
     * @param archivo el archivo desde donde se cargará el estado.
     * @return un arreglo de Tablero donde el primero es el del jugador y el segundo es el de la máquina.
     * @throws IOException si ocurre un error durante la lectura del archivo.
     * @throws ClassNotFoundException si no se puede encontrar la clase de los objetos serializados.
     */
    public static Tablero[] cargarEstado(File archivo) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(archivo))) {
            Tablero jugador = (Tablero) in.readObject();
            Tablero maquina = (Tablero) in.readObject();
            return new Tablero[]{jugador, maquina};
        }
    }
}