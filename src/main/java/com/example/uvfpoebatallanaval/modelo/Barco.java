package com.example.uvfpoebatallanaval.modelo;

import javafx.scene.paint.Color;
import javafx.scene.shape.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Barco implements Serializable {
    private String tipo;
    private int tamaño;
    private boolean horizontal;
    private int impactos = 0;

    public Barco(String tipo, boolean horizontal) {
        this.tipo = tipo;
        this.horizontal = horizontal;
        this.tamaño = obtenerTamañoPorTipo(tipo);
    }

    private int obtenerTamañoPorTipo(String tipo) {
        return switch (tipo.toLowerCase()) {
            case "portaaviones" -> 4;
            case "destructor" -> 3;
            case "submarino" -> 2;
            case "fragata" -> 1;
            default -> 0;
        };
    }

    public List<List<Shape>> crearFormas(double posicionX, double posicionY) {
        List<List<Shape>> formas = new ArrayList<>();

        for (int i = 0; i < tamaño; i++) {
            double x = horizontal ? posicionX + i * 45 : posicionX;
            double y = horizontal ? posicionY : posicionY + i * 45;

            List<Shape> celda = new ArrayList<>();

            // Parte base del barco
            Rectangle parte = new Rectangle(x, y, 45, 45);
            parte.setFill(colorPorTipo());
            parte.setStroke(Color.BLACK);
            celda.add(parte);

            // Decoraciones para cada tipo
            switch (tipo.toLowerCase()) {
                case "portaaviones" -> {
                    if (i == 1) {
                        Rectangle torre = new Rectangle(x + 10, y + 10, 25, 25);
                        torre.setFill(Color.LIGHTGRAY);
                        torre.setStroke(Color.BLACK);
                        celda.add(torre);
                    }
                    if (i == tamaño - 1) {
                        Ellipse helipuerto = new Ellipse(x + 22.5, y + 22.5, 12, 8);
                        helipuerto.setFill(Color.DARKRED);
                        helipuerto.setStroke(Color.BLACK);
                        celda.add(helipuerto);
                    }
                }

                case "submarino" -> {
                    if (i == 0) {
                        Rectangle torre = new Rectangle(x + 15, y - 10, 15, 20);
                        torre.setFill(Color.DARKGRAY);
                        celda.add(torre);

                        Rectangle periscopio = new Rectangle(x + 20, y - 20, 5, 10);
                        periscopio.setFill(Color.GRAY);
                        celda.add(periscopio);
                    }
                }

                case "destructor" -> {
                    if (i == 0) {
                        Polygon proa = new Polygon(
                                x, y + 45.0,
                                x + 22.5, y,
                                x + 45.0, y + 45.0
                        );
                        proa.setFill(Color.OLIVE);
                        proa.setStroke(Color.BLACK);
                        celda.add(proa);
                    }
                }

                case "fragata" -> {
                    if (i == 0) {
                        Circle cabina = new Circle(x + 22.5, y + 22.5, 10);
                        cabina.setFill(Color.BURLYWOOD);
                        cabina.setStroke(Color.BLACK);
                        celda.add(cabina);
                    }
                }
            }

            formas.add(celda);
        }

        return formas;
    }

    private Color colorPorTipo() {
        return switch (tipo.toLowerCase()) {
            case "portaaviones" -> Color.DARKGRAY;
            case "destructor" -> Color.OLIVE;
            case "submarino" -> Color.NAVY;
            case "fragata" -> Color.SIENNA;
            default -> Color.GRAY;
        };
    }

    public void rotar() {
        this.horizontal = !this.horizontal;
    }

    public int getTamaño() {
        return tamaño;
    }

    public boolean esHorizontal() {
        return horizontal;
    }

    public String getTipo() {
        return tipo;
    }

    public void setOrientacion(boolean horizontal) {this.horizontal = horizontal;}

    public void registrarImpacto() {impactos++;}

    public boolean estaHundido() {return impactos >= tamaño;}
}