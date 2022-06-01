package com.ballgame;

public abstract class Circle extends GameObject {
    protected int r; 

    public Circle(int x, int y) {
        super(x, y);
    }

    public double getCenterX() {
        return this.x + this.r; 
    }

    public double getCenterY() {
        return this.y + this.r; 
    }

    public boolean intersects(Circle other) {
        double distance = this.getDistance(other.getCenterX(), other.getCenterY());; 
        return distance <= this.r + other.r; 
    }
}