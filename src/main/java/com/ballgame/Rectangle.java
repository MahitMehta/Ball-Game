package com.ballgame;

public abstract class Rectangle extends GameObject {
    protected int width;
    protected int height;

    public Rectangle(int x, int y) {
        super(x, y);
    }

    public double getCenterX() {
        return this.x + (this.width / 2);
    }

    public double getCenterY() {
        return this.y + (this.height / 2);
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }
}
