package com.ballgame;

public abstract class Circle extends GameObject {
    protected int r;

    public Circle(int x, int y) {
        super(x, y);
    }

    public double getCenterX() {
        return this.x + this.r;
    }

    public int getRadius() {
        return this.r;
    }

    public int getDiameter() {
        return this.r * 2;
    }

    public double getCenterY() {
        return this.y + this.r;
    }

    public boolean intersects(Circle other) {
        double distance = this.getDistance(this.getCenterX(), this.getCenterY(), other.getCenterX(),
                other.getCenterY());
        ;
        return distance <= this.r + other.r;
    }
}