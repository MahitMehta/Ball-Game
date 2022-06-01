package com.ballgame;

import java.awt.Color;
import java.awt.Graphics2D;

import java.awt.geom.Ellipse2D;
import java.awt.Shape;

public class Goal extends Circle {
    private int r; 
    private Shape shape; 

    public Goal() {
        this(0, 0, 30);
    }
    
    public Goal(int x, int y, int r) {
        super(x, y);

        this.r = r; 

        this.shape = new Ellipse2D.Double((int) this.x, (int) this.y, this.r * 2, this.r * 2);
    }

    public Ellipse2D.Double getShape() { return (Ellipse2D.Double) this.shape; }

    public void update(double deltaTime, double previousTime) {}

    public void render(Graphics2D g2d) {
        g2d.setColor(Color.GREEN);
        
        Ellipse2D.Double obj = (Ellipse2D.Double) this.shape;
        obj.setFrame(this.x, this.y, this.r * 2, this.r * 2);

        g2d.fill(this.shape);
    }
}
