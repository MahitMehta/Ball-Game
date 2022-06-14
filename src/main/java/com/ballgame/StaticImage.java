package com.ballgame;

import java.awt.Graphics2D;
import java.awt.image.*;
import java.util.*;

public class StaticImage extends GameObject {
    private int width;
    private int height;
    
    public void update(double deltaTime, double previousTime){
        this.incrementImageFrame(deltaTime, previousTime);
    }

    public void render(Graphics2D g2d){
     g2d.drawImage(this.image, (int) this.x, (int) this.y , this.width, this.height, null);
    }

    public int getWidth(){ return this.width; };

    public int getHeight(){ return this.height; };

    public StaticImage(int x, int y, int width, int height, ArrayList<BufferedImage> sprites){
        super(x,y);
        this.width = width;
        this.height = height;
        this.initSprites(sprites, 1, 0d);

    }
}
