package com.ballgame;

import java.awt.Color;
import java.awt.Graphics2D;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;

public class Player extends Circle {
    private double velX;
    private double velY;
    private double a; 
    private double maxVel; 
    private Shape shape; 

    private boolean left; 
    private boolean down; 
    private boolean up; 
    private boolean right; 

    public Player() {
        this(0, 0, 120.0);
    }

    public Player(int x, int y, double maxVel) {
        super(x, y);

        this.left = false;
        this.down = false; 
        this.up = false; 
        this.right = false;  

        this.maxVel = maxVel;
        this.velX = 0; 
        this.velY = 0; 
        this.a = 800; 

        this.r = 16; 

        this.shape = new Ellipse2D.Double((int) this.x, (int) this.y, this.getDiameter(), this.getDiameter());
    }

    public Ellipse2D.Double getShape() { return (Ellipse2D.Double) this.shape; }

    public void setRight(boolean state) { this.right = state; }

    public void setLeft(boolean state) { this.left = state; }

    public void setDown(boolean state) { this.down = state; }

    public void setUp(boolean state) { this.up = state; }

    public double getSpeed() {
        return this.maxVel;
    }

    public void update(double deltaTime, double previousTime) {
        double incrementX = this.velX * (deltaTime - previousTime);
        double incrementY = this.velY * (deltaTime - previousTime);

        double velIncrement = this.a * (deltaTime - previousTime);

        if (this.right) {
            if (this.velX + velIncrement < this.maxVel) {
                this.velX += velIncrement;
            }
        } else if (!this.left && this.velX >= 0) {
            if (this.velX - velIncrement >= 0) {
                this.velX -= velIncrement;
            } else {
                this.velX = 0;
            }
        }

        if (this.down) {
            if (this.velY + velIncrement < this.maxVel) {
                this.velY += velIncrement;
            }
        } else if (!this.up && this.velY >= 0) {
            if (this.velY - velIncrement >= 0) {
                this.velY -= velIncrement;
            } else {
                this.velY = 0;
            }
        }

        if (this.up) {
            if (Math.abs(this.velY - velIncrement) < this.maxVel){
                this.velY -= velIncrement;
            } 
         } else if (!this.down && this.velY <= 0) {
             if (this.velY + velIncrement < 0) {
                 this.velY += velIncrement;
             } else {
                 this.velY = 0;
             }
         }

        if (this.left) {
           if (Math.abs(this.velX - velIncrement) < this.maxVel){
               this.velX -= velIncrement;
           } 
        } else if (!this.right && this.velX <= 0) {
            if (this.velX + velIncrement < 0) {
                this.velX += velIncrement;
            } else {
                this.velX = 0;
            }
        }

        if (this.up && this.velY > 0) {
            this.velY *= -1;
        }
        if (this.down && this.velY < 0) {
            this.velY *= -1;
        }
        if (this.right && this.velX < 0) {
            this.velX *= -1;
        }
        if (this.left && this.velX > 0) {
            this.velX *= -1;
        }

        this.setTransform(this.x + incrementX, this.y + incrementY);
    }

    public void render(Graphics2D g2d) {
        g2d.setColor(Color.BLUE);

        Ellipse2D.Double obj = (Ellipse2D.Double) this.shape;
        obj.setFrame(this.x, this.y, this.getDiameter(), this.getDiameter());

        g2d.fill(this.shape);
    }
}
