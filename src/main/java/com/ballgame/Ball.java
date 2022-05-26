package com.ballgame;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Ellipse2D;
import java.awt.Shape;
import java.awt.image.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Ball extends GameObject {
    private int r;
    private Shape shape; 
    private double velX; 
    private double velY; 
    private Map<BufferedImage, RescaleOp> sprites; 
    private Image image; 

    public Ball(List<BufferedImage> sprites) {
        this(0, 0, 0, 80, 80, sprites);
    }

    public Ball(int x, int y, double velX, double velY, int r, List<BufferedImage> sprites) {
        super(x, y);
        
        this.velX = velX; 
        this.velY = velY;
        this.r = r; 

        this.sprites = new HashMap<BufferedImage, RescaleOp>();

        for (BufferedImage sprite : sprites) {
            int w = sprite.getWidth(null);
            int h = sprite.getHeight(null);
            BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            float[] scales = { 1f, 1f, 1f, 0.5f };
            float[] offsets = new float[4];
            RescaleOp rop = new RescaleOp(scales, offsets, null); 
            this.sprites.put(bi, rop);
        }

        this.shape = new Ellipse2D.Double((int) this.x, (int) this.y, this.r * 2, this.r * 2);
    }
    
    public Ellipse2D.Double getShape() { return (Ellipse2D.Double) this.shape; }

    public int getRadius() { return this.r; }

    public double getVelX() { return this.velX; }

    public double getVelY() { return this.velY; }

    public void setVelX(double velX) {
        this.velX = velX; 
    }

    public void setVelY(double velY) {
        this.velY = velY; 
    }

    public void update(double deltaTime, double previousTime) {
        double incrementX = this.velX * (deltaTime - previousTime); 
        double incrementY = this.velY * (deltaTime - previousTime); 

        super.setTransform(this.x + incrementX, this.y + incrementY);
    }

    public void render(Graphics2D g2d) {
        g2d.setColor(Color.WHITE);
        
        Ellipse2D.Double obj = (Ellipse2D.Double) this.shape;
        obj.setFrame(this.x, this.y, this.r * 2, this.r * 2);

        g2d.fill(this.shape);
    }
}
