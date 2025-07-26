package com.example.uvfpoebatallanaval.modelo;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

/**
 * Clase GestorPartida encargada de gestionar el guardado, carga y eliminación del estado de una partida de Batalla Naval.
 * Utiliza archivos binarios y planos para almacenar la información de los tableros y del jugador.
 *
 * @author Steven Candela
 */
public class GestorPartida {
    private static final File archivoTableros = new File("estadoJuego.dat");
    private static final File archivoPlano = new File("infoJugador.txt");

    /**
     * Guarda el estado actual de la partida, incluyendo los tableros y la información del jugador.
     *
     * @param jugador        Tablero del jugador.
     * @param maquina        Tablero de la máquina.
     * @param nickname       Nombre del jugador.
     * @param barcosHundidos Cantidad de barcos hundidos por el jugador.
     * @param textoTurno     Texto que indica el turno actual.
     * @param colorTurno     Color del texto del turno.
     * @param textoDisparo   Texto del resultado del último disparo.
     * @param colorDisparo   Color del texto del resultado del disparo.
     */
    public static void guardarPartida(Tablero jugador, Tablero maquina, String nickname, int barcosHundidos,
                                      String textoTurno, String colorTurno,
                                      String textoDisparo, String colorDisparo) {
        try {
            Serializador.guardarEstado(jugador, maquina, archivoTableros);
            ArchivoPlano.guardarInfoJugador(nickname, barcosHundidos, textoTurno, colorTurno, textoDisparo, colorDisparo, archivoPlano);
        } catch (IOException e) {
            System.err.println("Error al guardar partida: " + e.getMessage());
        }
    }

    /**
     * Carga el estado previamente guardado de la partida.
     *
     * @return Un objeto {@code Optional<EstadoJuego>} con los datos de la partida si existen; vacío en caso de error.
     */
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

    /**
     * Elimina los archivos que contienen la partida guardada.
     */
    public static void eliminarPartidaGuardada() {
        archivoPlano.delete();
        archivoTableros.delete();
    }

    /**
     * Verifica si existe una partida guardada.
     *
     * @return {@code true} si existe un archivo de estado del juego, {@code false} de lo contrario.
     */
    public static boolean existePartidaGuardada() {
        File archivo = new File("estadoJuego.dat");
        return archivo.exists();
    }

    /**
     * Clase auxiliar que representa el estado completo de una partida cargada.
     */
    public static class EstadoJuego {
        public Tablero tableroJugador;
        public Tablero tableroMaquina;
        public String nickname;
        public int barcosHundidos;

        public String textoTurno;
        public String colorTurno;

        public String textoDisparo;
        public String colorDisparo;

        /**
         * Constructor del estado del juego cargado.
         *
         * @param jugador       Tablero del jugador.
         * @param maquina       Tablero de la máquina.
         * @param nick          Nickname del jugador.
         * @param hundidos      Barcos hundidos por el jugador.
         * @param textoTurno    Texto del turno.
         * @param colorTurno    Color del texto del turno.
         * @param textoDisparo  Texto del resultado del disparo.
         * @param colorDisparo  Color del resultado del disparo.
         */
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