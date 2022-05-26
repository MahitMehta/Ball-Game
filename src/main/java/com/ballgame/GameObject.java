package com.ballgame;

import java.awt.Graphics2D;

public abstract class GameObject {
    protected double x; 
    protected double y; 

    public GameObject(int x, int y) {
        this.x = x; 
        this.y = y;
    }

    public GameObject() {
        this(0, 0);
    }

    public double getY() {
        return this.y; 
    }

    public double getX() {
        return this.x; 
    }

    public void setTransform(double x, double y) {
        this.x = x; 
        this.y = y; 
    }

    public double setTransformX(double x) {
        this.x = x; 
        return this.x; 
    }

    public double setTransformY(double y) {
        this.y = y; 
        return this.y; 
    }
    
    public abstract void update(double deltaTime, double previousTime);

    public abstract void render(Graphics2D g2d);
}
