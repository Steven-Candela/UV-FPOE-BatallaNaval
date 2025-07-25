package com.example.uvfpoebatallanaval.modelo;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

public class GestorPartida {
    private static final File archivoTableros = new File("estadoJuego.dat");
    private static final File archivoPlano = new File("infoJugador.txt");

    public static void guardarPartida(Tablero jugador, Tablero maquina, String nickname, int barcosHundidos,
                                      String textoTurno, String colorTurno,
                                      String textoDisparo, String colorDisparo) {
        try {
            Serializador.guardarEstado(jugador, maquina, archivoTableros);
            ArchivoPlano.guardarInfoJugadorAvanzado(nickname, barcosHundidos, textoTurno, colorTurno, textoDisparo, colorDisparo, archivoPlano);
        } catch (IOException e) {
            System.err.println("Error al guardar partida: " + e.getMessage());
        }
    }


    public static Optional<EstadoJuego> cargarPartida() {
        try {
            Tablero[] tableros = Serializador.cargarEstado(archivoTableros);
            Map<String, String> datosJugador = ArchivoPlano.leerInfoJugador(archivoPlano);

            EstadoJuego estado = new EstadoJuego(
                    tableros[0],
                    tableros[1],
                    datosJugador.get("nickname"),
                    Integer.parseInt(datosJugador.get("barcosHundidos")),
                    datosJugador.getOrDefault("textoTurno", "Turno del Jugador"),
                    datosJugador.getOrDefault("colorTurno", "-fx-text-fill: black;"),
                    datosJugador.getOrDefault("textoDisparo", ""),
                    datosJugador.getOrDefault("colorDisparo", "-fx-text-fill: black;")
            );

            return Optional.of(estado);
        } catch (Exception e) {
            System.err.println("No se pudo cargar partida: " + e.getMessage());
            return Optional.empty();
        }
    }


    public static void eliminarPartidaGuardada() {
        archivoPlano.delete();
        archivoTableros.delete();
    }

    public static boolean existePartidaGuardada() {
        File archivo = new File("estadoJuego.dat");
        return archivo.exists();
    }

    // Clase auxiliar para retornar el estado completo
    public static class EstadoJuego {
        public Tablero tableroJugador;
        public Tablero tableroMaquina;
        public String nickname;
        public int barcosHundidos;

        public String textoTurno;
        public String colorTurno;

        public String textoDisparo;
        public String colorDisparo;

        public EstadoJuego(Tablero jugador, Tablero maquina, String nick, int hundidos,
                           String textoTurno, String colorTurno,
                           String textoDisparo, String colorDisparo) {
            this.tableroJugador = jugador;
            this.tableroMaquina = maquina;
            this.nickname = nick;
            this.barcosHundidos = hundidos;
            this.textoTurno = textoTurno;
            this.colorTurno = colorTurno;
            this.textoDisparo = textoDisparo;
            this.colorDisparo = colorDisparo;
        }
    }

}