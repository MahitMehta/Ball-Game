package com.ballgame;

import java.awt.Graphics2D;
import java.awt.Shape;

public class WallBarrier extends Rectangle {
    static enum Side {
        LEFT,
        RIGHT
    }

    private Shape shape;
    private double velX;
    private double velY;
    private Side side;

    public WallBarrier(int width, int height, Side side) {
        super(0, 0);

        if (side == Side.LEFT)
            this.x = 150;
        else if (side == Side.RIGHT)
            this.x = Eskiv.WIDTH - width - 150;
        this.side = side;
        this.width = width;
        this.height = height;
        this.velY = 0;
        this.velX = 0;

        this.shape = new java.awt.Rectangle.Double((int) this.x, (int) this.y, this.width, this.height);
    }

    public Side getSide() {
        return this.side;
    }

    public Shape getShape() {
        return this.shape;
    }

    public double getVelX() {
        return this.velX;
    }

    public double getVelY() {
        return this.velY;
    }

    public void setVelX(double velX) {
        this.velX = velX;
    }

    public void setVelY(double velY) {
        this.velY = velY;
    }

    public void update(double deltaTime, double previousTime) {
        double incrementX = this.velX * (deltaTime - previousTime);
        double incrementY = this.velY * (deltaTime - previousTime);

        if ((this.side == Side.LEFT || this.side == Side.RIGHT) && this.y + this.height >= Eskiv.HEIGHT
                && this.velY > 0) {
            this.velY *= -1;
        } else if ((this.side == Side.LEFT || this.side == Side.RIGHT) && this.y <= 0 && this.velY < 0) {
            this.velY *= -1;
        }

        super.setTransform(this.x + incrementX, this.y + incrementY);
    }

    public void render(Graphics2D g2d) {
        java.awt.Rectangle.Double obj = (java.awt.Rectangle.Double) this.shape;
        obj.setFrame(this.x, this.y, this.width, this.height);
        g2d.fill(this.shape);
    }
}
