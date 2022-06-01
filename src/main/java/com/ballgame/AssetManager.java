package com.ballgame;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
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

    private List<BufferedImage> fireballSprites;

    public List<BufferedImage> getFireballSprites() {
        return this.fireballSprites;
    }

    public AssetManager() {
        fireballSprites = new ArrayList<BufferedImage>();
        
        try {
            loadBallAssets();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadBallAssets() throws IOException {
        for (String path : fireballSpritesPaths) {
            File img = new File(Paths.get(ASSETS_PATH, path).toString());
            BufferedImage image = ImageIO.read(img); 
            this.fireballSprites.add(image); 
        }
    }
}
