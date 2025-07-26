package com.example.uvfpoebatallanaval.modelo;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Clase ArchivoPlano, utilitaria para guardar y leer la información de un jugador
 * en un archivo de texto plano.
 *
 * @author Steven Candela
 */
public class ArchivoPlano {
    /**
     * Guarda la información del jugador en un archivo de texto plano.
     * @param nickname      Apodo del jugador.
     * @param barcosHundidos Número de barcos hundidos por el jugador.
     * @param textoTurno     Texto descriptivo del turno actual.
     * @param colorTurno     Color asociado al turno.
     * @param textoDisparo   Texto relacionado con el disparo.
     * @param colorDisparo   Color asociado al resultado del disparo.
     * @param archivo        Archivo en el cual se guardará la información.
     * @throws IOException Si ocurre un error al escribir en el archivo.
     */
    public static void guardarInfoJugador(String nickname, int barcosHundidos,
                                                  String textoTurno, String colorTurno,
                                                  String textoDisparo, String colorDisparo,
                                                  File archivo) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivo))) {
            writer.write("nickname=" + nickname);
            writer.newLine();
            writer.write("barcosHundidos=" + barcosHundidos);
            writer.newLine();
            writer.write("textoTurno=" + textoTurno);
            writer.newLine();
            writer.write("colorTurno=" + colorTurno);
            writer.newLine();
            writer.write("textoDisparo=" + textoDisparo);
            writer.newLine();
            writer.write("colorDisparo=" + colorDisparo);
            writer.newLine();
        }
    }

    /**
     * Lee la información de un jugador desde un archivo de texto plano.
     *
     * @param archivo Archivo desde el cual se leerá la información.
     * @return Un mapa con los datos leídos, donde las claves son los nombres de los atributos
     *         y los valores son sus respectivos contenidos.
     * @throws IOException Si ocurre un error al leer el archivo.
     */
    public static Map<String, String> leerInfoJugador(File archivo) throws IOException {
        Map<String, String> datos = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] partes = linea.split("=");
                if (partes.length == 2) {
                    datos.put(partes[0].trim(), partes[1].trim());
                }
            }
        }
        return datos;
    }
}

