package com.ballgame;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.awt.Graphics; 
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Font; 
import java.awt.FontFormatException; 
import java.awt.FontMetrics; 
import java.awt.Rectangle; 

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;

public final class Eskiv extends JPanel implements Runnable, KeyListener {   
    private static final int WIDTH = 750; 
    private static final int HEIGHT = 500; 
    private static final int FPS = 144;

    private final Long startTime; 
    private double deltaTime;     
    private double previousTime;

    private List<GameObject> obstacles;
    private Player player; 
    private Goal goal;

    private boolean gameStarted; 
    private boolean gameLost;
    private int gameScore = 0; 

    // State
    private final StateManager state; 

    // Assets
    private final AssetManager am;
    private final Font ROBOTO_12;
    private final Font ROBOTO_14;
    private final Font ROBOTO_24;
    private final Font ROBOTO_36;

    private Eskiv() throws InterruptedException, IOException, FontFormatException {
        this.am = new AssetManager();
        this.state = new StateManager();

        ROBOTO_12 = Font.createFont(Font.TRUETYPE_FONT, new File("src/main/java/com/ballgame/assets/fonts/Roboto/Roboto-Regular.ttf")).deriveFont(12f);
        ROBOTO_14 = Font.createFont(Font.TRUETYPE_FONT, new File("src/main/java/com/ballgame/assets/fonts/Roboto/Roboto-Bold.ttf")).deriveFont(14f);;  
        ROBOTO_24 = Font.createFont(Font.TRUETYPE_FONT, new File("src/main/java/com/ballgame/assets/fonts/Roboto/Roboto-Bold.ttf")).deriveFont(24f);;  
        ROBOTO_36 = Font.createFont(Font.TRUETYPE_FONT, new File("src/main/java/com/ballgame/assets/fonts/Roboto/Roboto-Bold.ttf")).deriveFont(36f);
       
        this.startTime = System.nanoTime();
        
        JFrame frame = new JFrame("Eskiv Game");
        frame.add(this);  
        frame.getContentPane().setBackground(Color.BLACK);
        frame.setSize(Eskiv.WIDTH, Eskiv.HEIGHT);  
        frame.setLocationRelativeTo(null);  
        frame.setResizable(false);
        frame.addKeyListener(this);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
        frame.setVisible(true);  

        Thread t1 = new Thread(this);
        t1.start();

        this.gameStarted = false; 

        this.initGame();
    }

    public void initGame() {
        this.obstacles = new ArrayList<GameObject>();

        this.player = new Player(200, 200, 400);

        this.gameScore = 0; 

        this.state.refreshJsonBody();

        this.goal = new Goal(100, 100, 15);

        generateRandomBall();

        this.gameLost = false; 
    }

    public void generateRandomBall() {
        Random random = new Random();

        int size = random.nextInt(11) + 20;

        int x = random.nextInt(Eskiv.WIDTH - 100 - (size * 2)) + 50;
        int y = random.nextInt(Eskiv.HEIGHT - 100 - (size * 2)) + 50;

        int velX = random.nextInt(51) + 100;
        int velY = random.nextInt(51) + 100;

        GameObject b = new Ball(x, y, velX, velY, size, am.getFireballSprites());
        obstacles.add(b); 
    }

    public void displayStartScreen(Graphics2D g2d) {
        int BOX_WIDTH = 300;
        int BOX_HEIGHT = 200; 

        double x = (WIDTH * 0.5) - (BOX_WIDTH * 0.5);
        double y = (HEIGHT * 0.5) - (BOX_HEIGHT * 0.5);

        g2d.setColor(new Color(0.075f,0.075f,0.075f,0.75f));
        g2d.fillRoundRect((int) x, (int) y, BOX_WIDTH, BOX_HEIGHT, 5, 5);

        g2d.setColor(Color.GREEN);
        this.drawCenteredString(g2d, "Welcome to Eskiv!", new Rectangle(WIDTH, HEIGHT - 100), ROBOTO_24);

        g2d.setColor(Color.WHITE);
        this.drawCenteredString(g2d, "Use Arrow Keys or WASD Keys", new Rectangle(WIDTH, HEIGHT - 25), ROBOTO_12);
        this.drawCenteredString(g2d, "to Move the Blue Ball to the Green Goal", new Rectangle(WIDTH, HEIGHT + 10), ROBOTO_12);
        this.drawCenteredString(g2d, "and try to avoid any Moving Obstacles.", new Rectangle(WIDTH, HEIGHT + 45), ROBOTO_12);

        g2d.setColor(Color.GREEN);
        this.drawCenteredString(g2d, "Press \"S\" to Begin...", new Rectangle(WIDTH, HEIGHT + 125), ROBOTO_12);
    }

    public void displayPlayAgain(Graphics2D g2d) {
        int BOX_WIDTH = 300;
        int BOX_HEIGHT = 200; 

        double x = (WIDTH * 0.5) - (BOX_WIDTH * 0.5);
        double y = (HEIGHT * 0.5) - (BOX_HEIGHT * 0.5);

        g2d.setColor(new Color(0.075f,0.075f,0.075f,0.75f));
        g2d.fillRoundRect((int) x, (int) y, BOX_WIDTH, BOX_HEIGHT, 5, 5);

        g2d.setColor(Color.WHITE);
        this.drawCenteredString(g2d, "Game Over.", new Rectangle(WIDTH, HEIGHT - 100), ROBOTO_24);

        int previousScore = this.state.getHighScore();
        if (this.gameScore > previousScore) {
            g2d.setColor(Color.GREEN);
            this.drawCenteredString(g2d, "New High Score! Your Scored " + this.gameScore + " Points.", new Rectangle(WIDTH, HEIGHT - 25), ROBOTO_14);
        } else {
            g2d.setColor(Color.WHITE);
            this.drawCenteredString(g2d, "Your Scored " + this.gameScore + " Points.", new Rectangle(WIDTH, HEIGHT - 25), ROBOTO_14);
        }
        
        g2d.setColor(Color.GREEN);
        this.drawCenteredString(g2d, "Press \"R\" to Play Again!", new Rectangle(WIDTH, HEIGHT + 100), ROBOTO_14);
    }

    @Override
    public void paintComponent(Graphics g)  // Need to have this method to complete the painting
	{   
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D)g;

        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, Eskiv.WIDTH, Eskiv.HEIGHT);

        this.previousTime = this.deltaTime;
        this.deltaTime = (System.nanoTime() - this.startTime) / (double) 1000000000;

        try {
            this.gameLoop(g2d, this.previousTime);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                repaint();
                Thread.sleep(1000 / Eskiv.FPS);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    } 

    public void drawCenteredString(Graphics g, String text, Rectangle rect, Font font) {
        FontMetrics metrics = g.getFontMetrics(font);
        int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
        int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
        g.setFont(font);
        g.drawString(text, x, y);
    }

    public void gameLoop(Graphics2D g2d, double previousTime) throws InterruptedException, IOException {
        // Display FPS
        g2d.setFont(ROBOTO_12);
        g2d.setColor(Color.WHITE);
        double currentFPS = 1 / (deltaTime - previousTime); 
        g2d.drawString((int) currentFPS + "", 20, 30);

        // Score Board
        this.drawCenteredString(g2d, "SCORE: " + this.gameScore, new Rectangle(WIDTH, 100), ROBOTO_36);
        // High Score
        this.drawCenteredString(g2d, "High Score: " + this.state.getHighScore(), new Rectangle(WIDTH, 200), ROBOTO_12);

        // Update Obstacles 
        for (GameObject obj : this.obstacles) {
            if (obj instanceof Ball) {
                Ball b = (Ball) obj;


                if (b.intersects(this.player)) {
                    int highScore = this.state.getHighScore();

                    if (this.gameScore > highScore) {
                        this.state.updateKeyWithPrimitive(StateManager.STATE_KEYS.HIGH_SCORE, this.gameScore);
                    }

                    this.gameLost = true; 
                }

                b.render(g2d);

                if (b.getX() + b.getDiameter() >= Eskiv.WIDTH) {
                    b.setVelX(-b.getVelX());
                }

                if (b.getX() <= 0) {
                    b.setVelX(-b.getVelX());
                }

                // Constant Accommodates for MacOS Header Height
                final int HEADER_TOP = 27; 
                if (b.getY() + HEADER_TOP+ b.getDiameter() >= Eskiv.HEIGHT) {
                    b.setVelY(-b.getVelY());
                }

                if (b.getY() <= 0) {
                    b.setVelY(-b.getVelY());
                }

                if (!gameLost && gameStarted) {
                    b.update(this.deltaTime, previousTime);
                }
            }
        }
    
        goal.render(g2d);

        player.render(g2d);
        if (!gameLost && gameStarted) player.update(deltaTime, previousTime);

        if (player.getX() > Eskiv.WIDTH) {
            player.setTransformX(-player.getDiameter());
        }

        if (player.getX() < -player.getDiameter()) {
            player.setTransformX(Eskiv.WIDTH);
        }

        if (player.getY() > Eskiv.HEIGHT) {
            player.setTransformY(-player.getDiameter());
        }

        if (player.getY() < -player.getDiameter()) {
            player.setTransformY(Eskiv.HEIGHT);
        }

        if (goal.getShape().intersects(player.getShape().getBounds2D())) {
            this.gameScore++;

            Random random = new Random();
            int x = random.nextInt(Eskiv.WIDTH - 50 + 1) + 50 - player.getDiameter();
            int y = random.nextInt(Eskiv.HEIGHT - 50 + 1) + 50 - player.getDiameter();
            goal.setTransform(x, y);

            generateRandomBall();
        }

        if (gameLost) {
            this.displayPlayAgain(g2d);
        }

        if (!gameStarted) {
            this.displayStartScreen(g2d);
        }
    }

    @Override
    public void keyPressed(KeyEvent ke)
    {
        // Reset Game
        if (ke.getKeyCode() == 82 && this.gameLost) {
            this.initGame();
        }

        // Start Game
        if (ke.getKeyCode() == 83 && !this.gameStarted) {
            this.gameStarted = true;
        }

        if (ke.getKeyCode() == 37 || ke.getKeyCode() == 65) {
            this.player.setLeft(true);
        }

        if (ke.getKeyCode() == 38 || ke.getKeyCode() == 87) {
            this.player.setUp(true);
        }

        if (ke.getKeyCode() == 39 || ke.getKeyCode() == 68) {
            this.player.setRight(true);
        }

        if (ke.getKeyCode() == 40 || ke.getKeyCode() == 83) {
            this.player.setDown(true);
        }
    }

    @Override
    public void keyReleased(KeyEvent ke){
        if (ke.getKeyCode() == 37 || ke.getKeyCode() == 65) {
            this.player.setLeft(false);
        }

        if (ke.getKeyCode() == 38 || ke.getKeyCode() == 87) {
            this.player.setUp(false);
        }

        if (ke.getKeyCode() == 39 || ke.getKeyCode() == 83) {
            this.player.setRight(false);
        }

        if (ke.getKeyCode() == 40 || ke.getKeyCode() == 83) {
            this.player.setDown(false);
        }
    }   

    @Override 
    public void keyTyped(KeyEvent ke) {};

    public static void main(String[] args) {
        try {
            new Eskiv();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }  catch (FontFormatException e) {
            e.printStackTrace();
        }
    }
}
