package pongPackage;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JLabel;

public class Game implements Runnable{

	private Display display;
	private JLabel startText;

	private Paddle paddle;
	private EnemyPaddle enemyPaddle;
	public Ball ball;
	
	private BufferStrategy bs;
	private Graphics g;
	
	private final int width, height;
	private boolean running = false;
	private int newBallSequence = -1;
	public static long time;
	
	private int gameCount;
	private int[] score;
	private Font font; 
		
	public Game(int width, int height){
		this.width = width;
		this.height = height;
		time = 0;
	}
	
	private void initialize(){
		display = new Display(width, height);
		paddle = new Paddle(display);
		score = new int[2];
		startScreen();
		
		enemyPaddle = new EnemyPaddle(display);
		ball = new Ball(display, 0);
	}
	
	/* Start sequence */
	private void startScreen() {
		score[0] = 0;
		score[1] = 0;
		font = new Font("Helvetica", Font.PLAIN, 20);
		while (true) {
			tick();
			if (bs == null){
				display.getCanvas().createBufferStrategy(3);
			}
			bs = display.getCanvas().getBufferStrategy();
			g = bs.getDrawGraphics();
			
			drawStartScreen();
			bs.show();
			
			if (Paddle.triggerStart) {
				break;
			}
		}
		
	}
	
	private void drawStartScreen() {
		g = bs.getDrawGraphics();
		
		g.setColor(Color.decode("#008080"));
		g.fillRect(0, 0, width, height);
		
		g.setColor(Color.BLACK);
		g.setFont(font);
		g.drawString("PRESS ENTER TO START", width/2 - 100, height/2);
		
		g.dispose();
	}
	/* End of start sequence */
	
	private void tick() {
		try {
			Thread.sleep(10);
			time++;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void render(){
		bs = display.getCanvas().getBufferStrategy();
		if (bs == null){
			display.getCanvas().createBufferStrategy(3);
			paddle.update();
			return;
		}
		if (newBallSequence == -1) {
			checkHit();
		}else {
			if (newBallSequence == 0) {
				newBall();
				newBallSequence = -1;
			}else {
				newBallSequence--;
			}
		}
		
		ball.update();
		paddle.update();
		enemyPaddle.update();
		
		redraw();
		bs.show();
	}
	
	private void redraw() {
		g = bs.getDrawGraphics();
		
		g.setColor(Color.decode("#008080"));
		g.fillRect(0, 0, width, height);
		
		g.setColor(Color.BLACK);
		g.fillOval(ball.x, ball.y, ball.width, ball.height);
		
		g.setColor(Color.WHITE);
		g.fillRect(paddle.x, paddle.y, paddle.width, paddle.height);
		
		g.setColor(Color.BLACK);
		g.fillRect(enemyPaddle.x, enemyPaddle.y, enemyPaddle.width, enemyPaddle.height);
		
		g.setFont(font);
		g.drawString(String.format("%d",score[0]), width/3, 40);
		g.drawString(String.format("%d",score[1]), width/3*2, 40);

		
		g.dispose();
	}
	
	//function to check if the ball has hit a paddle
	private void checkHit() {
		if (ball.x > 40 && ball.x + ball.width < width - 40) {
			if (ball.y + ball.height/2 <= paddle.y + paddle.height && ball.y + ball.height/2 >= paddle.y) {
				if (ball.x + ball.velX <= 40) {
					bounceBall(paddle.velY);
				}
			}
			if (ball.y + ball.height/2 <= enemyPaddle.y + enemyPaddle.height && ball.y + ball.height/2 >= enemyPaddle.y) {
				if (ball.x + ball.velX + ball.width >= width - 40) {
					bounceBall(-enemyPaddle.velY);
				}
			}
		}else if (ball.x < 0){
			score[1]++;
			newBallSequence = 50;
			System.out.printf("%d to %d\n",score[0], score[1]);
		} else if (ball.x + ball.width > width){
			score[0]++;
			newBallSequence = 50;
			System.out.printf("%d to %d\n",score[0], score[1]);
		}
	}
	
	//function to bounce ball according to the paddle's velocity
	private void bounceBall(int paddleVel) {
		int direction = ball.velY > 0 ? 1 : -1;
		ball.velX *= -1;
		ball.x = ball.x + ball.velX;
		ball.velY = Math.abs(ball.velY + paddleVel) > 8 ? 8*direction : ball.velY + paddleVel;
		System.out.printf("paddle : %d \t ball before: %d \t ball after: %d\n", paddleVel, ball.velY - paddleVel, ball.velY);
		
		try {
			playSound("bounce.wav");
		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
			e.printStackTrace();
		}
	}

	public void playSound(String soundFile) throws MalformedURLException, UnsupportedAudioFileException, IOException, LineUnavailableException {
	    File f = new File("./" + soundFile);
	    AudioInputStream audioIn = AudioSystem.getAudioInputStream(f.toURI().toURL());  
	    Clip clip = AudioSystem.getClip();
	    clip.open(AudioSystem.getAudioInputStream(f.toURI().toURL()));
	    clip.start();
	}
	
	
	private void newBall() {
		gameCount++;
		ball = new Ball(display, gameCount % 2);
	}
	public Ball getBall() {
		return this.ball;
	}
	
	public void run(){
		initialize();
		
		while(running){
			tick();
			render();
		}
	}
	
	public void start(){
		if (running){
			return;
		}
		running = true;
		run();
	}
}
