package org.example;

import javafx.scene.canvas.GraphicsContext;

public abstract class GameObject implements Drawable {

    protected double x;
    protected double y;

    public GameObject(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    @Override
    public abstract void draw(GraphicsContext gc);
}