package com.example.uvfpoebatallanaval.modelo;

import java.io.*;

public class Serializador {

    public static void guardarEstado(Tablero tableroJugador, Tablero tableroMaquina, File archivo) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(archivo))) {
            out.writeObject(tableroJugador);
            out.writeObject(tableroMaquina);
        }
    }

    public static Tablero[] cargarEstado(File archivo) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(archivo))) {
            Tablero jugador = (Tablero) in.readObject();
            Tablero maquina = (Tablero) in.readObject();
            return new Tablero[]{jugador, maquina};
        }
    }
}
