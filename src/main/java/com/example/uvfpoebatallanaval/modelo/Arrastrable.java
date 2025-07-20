package com.example.uvfpoebatallanaval.modelo;

public interface Arrastrable {
    void iniciarArrastre(double offsetX, double offsetY);
    void mover(double x, double y);
    void soltar(double x, double y);
}