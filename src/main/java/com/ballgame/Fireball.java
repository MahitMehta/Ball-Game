package com.ballgame;

import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.Shape;
import java.awt.image.*;
import java.util.List;

public class Fireball extends Circle {
    private Shape shape;
    private double velX;
    private double velY;

    public Fireball(List<BufferedImage> sprites) {
        this(0, 0, 0, 80, 80, sprites);
    }

    public Fireball(int x, int y, double velX, double velY, int r, List<BufferedImage> sprites) {
        super(x, y);

        this.initSprites(sprites, 15, 1.6);

        this.velX = velX;
        this.velY = velY;
        this.r = r;

        this.shape = new Ellipse2D.Double((int) this.x, (int) this.y, this.getDiameter(), this.getDiameter());
    }

    public Ellipse2D.Double getShape() {
        return (Ellipse2D.Double) this.shape;
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

    public void rotateSprites() {
        double angle = Math.atan(this.velX / -this.velY);
        for (int i = 0; i < this.sprites.size(); i++) {
            BufferedImage rotatedImage = this.rotateImage(this.sprites.get(i), angle * 180);
            this.transformedSprites.set(i, rotatedImage);
        }
    }

    public void update(double deltaTime, double previousTime) {
        boolean incrementedFrame = this.incrementImageFrame(deltaTime, previousTime);

        if (incrementedFrame) {
            // double angle = Math.atan(this.velX / -this.velY);
            //BufferedImage rotatedSprite = this.rotateImage((BufferedImage) this.image, angle * 180);
            //this.image = rotatedSprite;
        }

        double incrementX = this.velX * (deltaTime - previousTime);
        double incrementY = this.velY * (deltaTime - previousTime);

        super.setTransform(this.x + incrementX, this.y + incrementY);
    }

    public void render(Graphics2D g2d) {
        int diameter = (int) (this.getDiameter() * this.imagePaddingFactor);
        int translationAdjustment = (diameter - this.getDiameter()) / 2;

        g2d.drawImage(this.image, (int) this.x - translationAdjustment, (int) this.y - translationAdjustment, diameter,
                diameter, null);
        Ellipse2D.Double obj = (Ellipse2D.Double) this.shape;
        obj.setFrame(this.x, this.y, this.getDiameter(), this.getDiameter());
    }
}
