package com.example.uvfpoebatallanaval.vista;

import javafx.scene.paint.Color;
import javafx.scene.shape.*;

import java.util.ArrayList;
import java.util.List;

public class ElementosDisparo {
    public static List<Shape> agua(double x, double y) {
        List<Shape> formas = new ArrayList<>();

        Line cruz1 = new Line(x + 10, y + 10, x + 35, y + 35);
        Line cruz2 = new Line(x + 35, y + 10, x + 10, y + 35);

        cruz1.setStroke(Color.ORANGERED);
        cruz2.setStroke(Color.ORANGERED);
        cruz1.setStrokeWidth(4);
        cruz2.setStrokeWidth(4);

        formas.add(cruz1);
        formas.add(cruz2);
        return formas;
    }

    public static List<Shape> tocado(double x, double y) {
        List<Shape> formas = new ArrayList<>();

        // Bomba
        Circle cuerpo = new Circle(70, 470, 22);
        cuerpo.setFill(Color.BLACK);

        // Mecha
        Rectangle mecha = new Rectangle(68, 440, 5, 20);
        mecha.setFill(Color.RED);

        // Chispa
        Rectangle chispa = new Rectangle(65, 430, 10, 10);
        chispa.setFill(Color.YELLOW);

        formas.add(cuerpo);
        formas.add(mecha);
        formas.add(chispa);
        return formas;
    }

    public static List<Shape> hundido(double x, double y) {
        List<Shape> formas = new ArrayList<>();

        Polygon rombo = new Polygon();
        rombo.getPoints().addAll(
                x + 22.5, y + 5.0,
                x + 40.0, y + 22.5,
                x + 22.5, y + 40.0,
                x + 5.0, y + 22.5
        );
        rombo.setFill(Color.DARKRED);
        rombo.setStroke(Color.BLACK);
        rombo.setStrokeWidth(2);

        formas.add(rombo);
        return formas;
    }
}