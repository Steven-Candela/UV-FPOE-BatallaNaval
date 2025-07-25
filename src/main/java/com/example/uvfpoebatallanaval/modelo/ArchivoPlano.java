package com.example.uvfpoebatallanaval.modelo;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class ArchivoPlano {

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

