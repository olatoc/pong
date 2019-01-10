package pongPackage;

import java.awt.Canvas;
import java.awt.Color;
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

public class Game implements Runnable{

	private Display display;

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
		
	public Game(int width, int height){
		this.width = width;
		this.height = height;
		time = 0;
	}
	
	private void initialize(){
		display = new Display(width, height);
		paddle = new Paddle(display);
		enemyPaddle = new EnemyPaddle(display);
		ball = new Ball(display, 0);
	}
	
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
		g.fillRect(paddle.x, paddle.y, paddle.width, paddle.height);
		
		g.setColor(Color.BLACK);
		g.fillRect(enemyPaddle.x, enemyPaddle.y, enemyPaddle.width, enemyPaddle.height);
		
		g.setColor(Color.BLACK);
		g.fillOval(ball.x, ball.y, ball.width, ball.height);
		
		g.dispose();
	}
	
	private void checkHit() {
		if (ball.x > 40 && ball.x + ball.width < width - 40) {
			if (ball.y + ball.height/2 <= paddle.y + paddle.height && ball.y + ball.height/2 >= paddle.y) {
				if (ball.x + ball.velX <= 40) {
					bounceBall(paddle.velY);
					try {
						playSound("bounce.wav");
					} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
						e.printStackTrace();
					}
				}
			}
			if (ball.y + ball.height/2 <= enemyPaddle.y + enemyPaddle.height && ball.y + ball.height/2 >= enemyPaddle.y) {
				if (ball.x + ball.velX + ball.width >= width - 40) {
					bounceBall(-enemyPaddle.velY);
					try {
						playSound("bounce.wav");
					} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
						e.printStackTrace();
					}
				}
			}
		}else if(ball.x < 0 || ball.x + ball.width > width){
			newBallSequence = 50;
		}
	}
	
	private void bounceBall(int paddleVel) {
		ball.velX *= -1;
		ball.x = ball.x + ball.velX;
		ball.velY = ball.velY + paddleVel/2 > 8 ? 8 : ball.velY + paddleVel/2;
		System.out.printf("paddle : %d \t ball before: %d \t ball after: %d\n", paddleVel/4, ball.velY - paddleVel/4, ball.velY);
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
