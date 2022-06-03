package com.ballgame;

import java.awt.Graphics2D;
import java.awt.Image;
import java.util.List;
import java.awt.image.*;
import java.awt.geom.AffineTransform; 

public abstract class GameObject {
    protected double x; 
    protected double y; 

    protected double imagePaddingFactor = 1.0d; 
    protected BufferedImage image; 
    protected double imageFrame;
    protected int imageFPS; 

    protected List<BufferedImage> sprites; 
    protected List<BufferedImage> transformedSprites; 

    public GameObject(int x, int y) {
        this.x = x; 
        this.y = y; 
    }

    public GameObject() {
        this(0, 0);
    }

    public double getImageFrame() { return this.imageFrame; }

    public List<BufferedImage> getSprites() { return this.sprites; }

    public double getY() { return this.y; }
 
    public double getX() { return this.x; }

    protected void initSprites(List<BufferedImage> sprites, int imageFPS, double imagePaddingFactor) {
        if (sprites.size() > 0) {
            this.transformedSprites = AssetManager.clone(BufferedImage.class, sprites); 
            this.imagePaddingFactor = imagePaddingFactor; 
            this.sprites = sprites;
            this.imageFPS = imageFPS; 
            this.imageFrame = 0;
            this.image = this.sprites.get((int) this.imageFrame);
        }
    }

    protected void initSprites(List<BufferedImage> sprites) {
        this.initSprites(sprites, 24, 1.0d);
    }

    protected boolean incrementImageFrame(double deltaTime, double previousTime) {
        int prevFrame = (int) this.imageFrame;
        if ((int) this.imageFrame < this.sprites.size() - 1) {
            this.imageFrame = this.imageFrame + (this.imageFPS * (deltaTime - previousTime));
        } 
        else {
            this.imageFrame = 0;
        }

        if (prevFrame == (int) this.imageFrame) {
            return false; 
        }
        this.image = this.sprites.get((int) this.imageFrame);
        return true;
    }

    public double getDistance(double x, double y, double otherX, double otherY) {
        double deltaX = otherX - x; 
        double deltaY = otherY - y; 
        return Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));
    }

    public BufferedImage rotateImage(Image image, double angle) {
        double rads = Math.toRadians(angle);
        double sin = Math.abs(Math.sin(rads)), cos = Math.abs(Math.cos(rads));
        int w = image.getWidth(null);
        int h = image.getHeight(null);
        int newWidth = (int) Math.floor(w * cos + h * sin * (1 / this.imagePaddingFactor));
        int newHeight = (int) Math.floor(h * cos + w * sin * (1 / this.imagePaddingFactor)) ;
    
        BufferedImage rotated = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = rotated.createGraphics();
        AffineTransform at = new AffineTransform();
        at.translate((newWidth - w) / 2, (newHeight - h) / 2);
    
        int x = w / 2;
        int y = h / 2;
    
        at.rotate(rads, x, y);
        g2d.setTransform(at);
        g2d.drawImage((BufferedImage) image, 0, 0, null);
        g2d.dispose();

        return rotated;
    }

    public void setImageFrame(double frame) {
        this.imageFrame = frame; 
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
