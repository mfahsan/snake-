

import java.awt.EventQueue;
import javax.swing.JFrame;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import java.io.IOException;
import java.util.*;
import java.io.*;



public class Main extends JFrame {

    class Board extends JPanel implements ActionListener {

        private final int B_WIDTH = 1280;
        private final int B_HEIGHT = 800;
        private final int DOT_SIZE = 10;
        private final int ALL_DOTS = 80;
        private int delay;
        private int score = 0;
        private int level = 1;
        private int counter = 0;
        private int countDown = 30;


        private final int x[] = new int[ALL_DOTS];
        private final int y[] = new int[ALL_DOTS];

        private int dots;


        private boolean goingRight = true;
        private boolean goingLeft = false;
        private boolean goingUp = false;
        private boolean goingDown = false;

        private boolean inGame = false;
        private boolean paused = false;
        private boolean splashscreen = true;


        private Timer timer;
        private Image ball;
        private Image apple;
        private Image star;

        private AudioInputStream audioInputStream;
        private Clip clip;


        private int[] applesX;
        private int[] applesY;

        private int[] l1applesX = {200,400,700,600,900};
        private int[] l1applesY = {600,200,100,700,500};

        private int[] l2applesX = {180,670,560,200,310,870,400,500,200,600};
        private int[] l2applesY = {500,100,260, 400,700,600,230, 320, 100,700};

        private int[] l3applesX = {200,400,700,600,900, 180,670,560,200,310,870,400,500,200,600};
        private int[] l3applesY = {500,100,260, 400,700,600,230, 320, 100,700, 600,200,100,700,500};

        private int starX = 800;
        private int starY = 700;

        private boolean powerUp = false;
        private int extraLife = 0;

        public Board() {

            initBoard();
        }

        // initializes the board
        private void initBoard() {

            addKeyListener(new keyPress());
            setBackground(Color.black);
            setFocusable(true);

            setPreferredSize(new Dimension(1280, 800));
            loadImages();

            initGame(1);
        }

        // used for the countdown clock
        private void updateCounter() {

                if ((1000 / delay) == counter) {
                    countDown--;
                    counter = 0;
                }
            if (countDown == 0) {
                levelUp();
                countDown = 30;
            }
        }

        // loads the images used for icons
        private void loadImages() {

            ImageIcon iid = new ImageIcon("resources/dot.png");
            ball = iid.getImage();

            ImageIcon iia = new ImageIcon("resources/apple2.png");
            apple = iia.getImage();

            ImageIcon iis = new ImageIcon("resources/star-3.png");
            star = iis.getImage();

        }

        // level up
        private void levelUp() {
            setLevel(level + 1);
        }

        // this sets the level to 1, 2 or 3
        private void setLevel(int level) {
            if (level == 1) {
                applesX = l1applesX;
                applesY = l1applesY;
                this.level = 1;
                timer.stop();
                timer = new Timer(40, this);
                timer.start();
            }
            else if (level == 2) {
                applesX = l2applesX;
                applesY = l2applesY;
                this.level = 2;
                timer.stop();
                timer = new Timer(30, this);
                timer.start();
            }
            else if (level == 3) {
                applesX = l3applesX;
                applesY = l3applesY;
                this.level = 3;
                timer.stop();
                timer = new Timer(20, this);
                timer.start();
            }
        }

        // displays the score
        private void displayScore(Graphics g) {
            String message = "Score: " + score;
            Font small = new Font("Helvetica", Font.BOLD, 10);
            FontMetrics metr = getFontMetrics(small);

            g.setColor(Color.white);
            g.setFont(small);
            g.drawString(message, 30, 30);
        }

        // displays the countdown timer
        private void displayCountDown(Graphics g) {
            String message = "Timer: " + countDown;
            Font small = new Font("Helvetica", Font.BOLD, 10);
            FontMetrics metr = getFontMetrics(small);

            g.setColor(Color.white);
            g.setFont(small);
            g.drawString(message, 160, 30);
        }

        // display  whether the user has an extra life
        private void displayLife(Graphics g) {
            String message = "Extra Life: " + extraLife;
            Font small = new Font("Helvetica", Font.BOLD, 10);
            FontMetrics metr = getFontMetrics(small);

            g.setColor(Color.white);
            g.setFont(small);
            g.drawString(message, 390, 30);
        }

        // initialize the game with initial level of 1
        private void initGame(int level) {
            dots = 3;
            goingDown = false;
            goingLeft = false;
            goingUp = false;
            goingRight = true;

            for (int i = 0; i < dots; i++) {
                x[i] = 50 - i * 10;
                y[i] = 50;
            }

            if (level == 1) {
                 applesX = l1applesX;
                 applesY = l1applesY;
                 delay = 40;
            }
            else if (level == 2) {
                applesX = l2applesX;
                applesY = l2applesY;
                delay = 25;
            }
            else {
                applesX = l3applesX;
                applesY = l3applesY;
                delay = 20;
            }
            timer = new Timer(delay, this);
            timer.start();
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            doDrawing(g);
        }

        // drawing part
        private void doDrawing(Graphics g) {

            if (inGame) {

                counter++;
                displayScore(g);
                updateCounter();
                displayCountDown(g);
                displayLevel(g);
                displayLife(g);


                for (int i =0; i < applesX.length; i++) {
                    g.drawImage(apple, applesX[i], applesY[i], this);
                }

                if (!powerUp) {
                    g.drawImage(star, starX, starY, this);
                }

                for (int i= 0; i < dots; i++) {
                        g.drawImage(ball, x[i], y[i], this);
                }

                Toolkit.getDefaultToolkit().sync();

            } else {
                if (splashscreen == true) {
                    splashScreen(g);
                }
                else {
                    gameOver(g);
                }
            }
        }

        // play the  gulp sound when the snake eats an apple
        private void playSound() {
                try {
                    File file = new File("resources/sound.WAV");
                    audioInputStream = AudioSystem.getAudioInputStream(file.getAbsoluteFile());
                    clip = AudioSystem.getClip();
                    clip.open(audioInputStream);
                    clip.start();

                } catch(Exception ex) {
                    System.out.println("Error with playing sound.");
                    ex.printStackTrace();
                }
        }

        // display game over messages
        private void gameOver(Graphics g) {

            String message1 = "Game Over!";
            String message2 = "High Score: " + score;
            Font small = new Font("Helvetica", Font.BOLD, 18);
            FontMetrics metr = getFontMetrics(small);

            g.setColor(Color.white);
            g.setFont(small);
            g.drawString(message1, (B_WIDTH - metr.stringWidth(message1)) / 2, 300);
            g.drawString(message2, (B_WIDTH - metr.stringWidth(message2)) / 2, 330);
        }

        // check if the snake is eating an apple
        private void checkApple() {
            if (Math.abs(x[0] - starX) <= 10 && Math.abs(y[0] - starY) <= 10 && powerUp == false) {
                powerUp = true;
                extraLife = 1;
            }

            for (int i =0; i<applesX.length; i++) {

                if ((x[0] == applesX[i]) && (y[0] == applesY[i])) {
                    playSound();
                    dots++;
                    randApple(i);
                    score+= level;
                }
            }
        }



        // splash screen messages
        public void splashScreen(Graphics g) {
            String message = "Welcome to Snake";
            String message2 = "Mubtasim Fuad Ahsan";
            String message3 = "Userid: mfahsan";
            String message4 = "Use up, down, right and left arrows for movement";
            String message5 = "'P' pauses and resumes the game, 'R' brings it back to this screen";
            String message6 = "'Q' quits the game and displays high score";
            String message7 = "There are three levels. Switch to another level by pressing 1,2 or 3";
            String message8 = "Press S to start playing";
            Font font = new Font("Helvetica", Font.BOLD, 16);
            FontMetrics metr = getFontMetrics(font);


            g.setColor(Color.white);
            g.setFont(font);
            g.drawString(message, (B_WIDTH - metr.stringWidth(message)) / 2, 200);
            g.drawString(message2, (B_WIDTH - metr.stringWidth(message2)) / 2, 230);
            g.drawString(message3, (B_WIDTH - metr.stringWidth(message3)) / 2, 260);
            g.drawString(message4, (B_WIDTH - metr.stringWidth(message4)) / 2, 370);
            g.drawString(message5, (B_WIDTH - metr.stringWidth(message5)) / 2, 400);
            g.drawString(message6, (B_WIDTH - metr.stringWidth(message6)) / 2, 430);
            g.drawString(message7, (B_WIDTH - metr.stringWidth(message7)) / 2, 460);
            g.drawString(message8, (B_WIDTH - metr.stringWidth(message8)) / 2, 550);

        }


        // show the level at the top of the panel
        public void displayLevel(Graphics g) {
            String message = "Level: " + level;
            Font small = new Font("Helvetica", Font.BOLD, 10);
            FontMetrics metr = getFontMetrics(small);

            g.setColor(Color.white);
            g.setFont(small);
            g.drawString(message, 300, 30);
        }


        // check if the snake collided with itself or the boundaries
        private void checkCollision() {

            for (int z = dots; z > 0; z--) {

                if ((z > 4) && (x[0] == x[z]) && (y[0] == y[z])) {
                    if (extraLife == 1) {
                        extraLife = 0;
                    }
                    else {
                        inGame = false;
                        timer.stop();
                    }
                }
            }

            if (y[0] >= B_HEIGHT) {
                if (extraLife == 1) {
                    extraLife = 0;
                    goingDown = false;
                    goingUp = true;
                    goingRight = false;
                    goingLeft = false;
                }
                else {
                    inGame = false;
                    timer.stop();
                }
            }

            if (y[0] < 0) {
                if (extraLife == 1) {
                    extraLife = 0;
                    goingDown = true;
                    goingUp = false;
                    goingRight = false;
                    goingLeft = false;
                }
                else {
                    inGame = false;
                    timer.stop();
                }
            }

            if (x[0] >= B_WIDTH) {
                if (extraLife == 1) {
                    extraLife = 0;
                    goingRight = false;
                    goingLeft = true;
                    goingDown = false;
                    goingUp = false;
                }
                else {
                    inGame = false;
                    timer.stop();
                }
            }

            if (x[0] < 0) {
                if (extraLife == 1) {
                    extraLife = 0;
                    goingLeft = false;
                    goingRight = true;
                    goingDown = false;
                    goingUp = false;
                }
                else {
                    inGame = false;
                    timer.stop();
                }
            }




        }


        // places a random apple once an apple gets eaten
        private void randApple(int i) {

            int ran = (int) (Math.random() *  125);
            applesX[i] = ran * DOT_SIZE;


            int ran2 = (int) (Math.random() * 75);
            applesY[i] = ran2 * DOT_SIZE;


        }

        @Override
        public void actionPerformed(ActionEvent e) {

            if (inGame && !paused) {

                checkApple();
                checkCollision();
                for (int z = dots; z > 0; z--) {
                    x[z] = x[(z - 1)];
                    y[z] = y[(z - 1)];
                }

                if (goingRight == true) {
                    x[0] += DOT_SIZE;
                }


                if (goingLeft == true) {
                    x[0] -= DOT_SIZE;
                }


                if (goingUp == true) {
                    y[0] -= DOT_SIZE;
                }

                if (goingDown == true) {
                    y[0] += DOT_SIZE;
                }

            }

            repaint();
        }

        private class keyPress extends KeyAdapter {


            @Override
            public void keyPressed(KeyEvent e) {

                int key = e.getKeyCode();

                if ((key == KeyEvent.VK_LEFT) && (!goingRight)) {
                    goingLeft = true;
                    goingDown = false;
                    goingUp = false;
                }

                if ((key == KeyEvent.VK_RIGHT) && (!goingLeft)) {
                    goingRight = true;
                    goingDown = false;
                    goingUp = false;
                }

                if ((key == KeyEvent.VK_UP) && (!goingDown)) {
                    goingUp = true;
                    goingRight = false;
                    goingLeft = false;
                }

                if ((key == KeyEvent.VK_DOWN) && (!goingUp)) {
                    goingDown = true;
                    goingRight = false;
                    goingLeft = false;
                }

                if ((key == KeyEvent.VK_P)) {
                    paused = !paused;
                }

                if ((key == KeyEvent.VK_Q)) {
                    inGame = false;
                }

                if ((key == KeyEvent.VK_R)) {
                    timer.stop();
                    inGame = false;
                    splashscreen = true;
                }

                if ((key == KeyEvent.VK_1)) {
                    setLevel(1);
                }

                if ((key == KeyEvent.VK_2)) {
                    setLevel(2);
                }

                if ((key == KeyEvent.VK_3)) {
                    setLevel(3);
                }

                if ((key == KeyEvent.VK_S) && (splashscreen == true)) {
                    inGame = true;
                    splashscreen = false;
                    initGame(1);
                }
            }
        }
    }

    public Main() {
        
        initUI();
    }
    
    private void initUI() {
        
        add(new Board());
        
        setResizable(false);
        pack();
        
        setTitle("Snake");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    public static void main(String[] args) {
        
        EventQueue.invokeLater(() -> {
            JFrame frame = new Main();
            frame.setVisible(true);
        });
    }
}
