package com.ballgame;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;
import java.awt.image.*;

public class AssetManager {
    private String ASSETS_PATH = "src/main/java/com/ballgame/assets/";

    private String[] fireballSpritesPaths = {
            "fireball/fb-1.png",
            "fireball/fb-2.png",
            "fireball/fb-3.png",
            "fireball/fb-4.png",
            "fireball/fb-5.png"
    };

    private String[] heartSpritesPaths = {
        "static/heart.png"
};

    private List<BufferedImage> fireballSprites;
    private List<BufferedImage> heartSprites; 

    public List<BufferedImage> getFireballSprites() {
        return this.fireballSprites;
    }
    
    public List<BufferedImage> getHeartSprites() {
        return this.heartSprites;
    }

    public static <T> LinkedList<T> clone(Class<T> generic, List<T> e) {
        LinkedList<T> clone = new LinkedList<T>();

        for (T i : e)
            clone.add((T) i);

        return clone;
    }

    public AssetManager() {
        fireballSprites = new ArrayList<BufferedImage>();
        heartSprites = new ArrayList<BufferedImage>();

        try {
            loadAssets(this.fireballSprites, this.fireballSpritesPaths);
            loadAssets(this.heartSprites, this.heartSpritesPaths);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadAssets(List<BufferedImage> sprites, String[] spritePaths) throws IOException  {
        for (String path : spritePaths) {
            File img = new File(Paths.get(ASSETS_PATH, path).toString());
            BufferedImage image = ImageIO.read(img);
            sprites.add(image);
        }
    }
}
