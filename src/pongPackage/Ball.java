package pongPackage;

import java.awt.event.KeyEvent;
import java.util.Random;

public class Ball {

	protected int height;
	protected int width;
	
	protected int x;
	protected int y;
	protected int velX;
	protected int velY;
	protected int dir;
	
	private Random r;
	
	private Display display;

	
	public Ball(Display display, int dir) {
		this.display = display;
		
		this.r = new Random();
		this.y = r.nextInt(display.height/2) + display.height/4;
		this.x = display.width/2 - 10;
		this.height = 20;
		this.width = 20;
		this.dir = dir == 1 ? 1 : -1;
		
		this.velX = 6*this.dir;
		this.velY = (r.nextInt(2) + 1)*this.dir;
	}
	
	public void bounce() {
		if (y + velY <= 0 || y + velY >= display.height - this.height) {
			velY *= -1;
			y = y + velY;
		}
	}
	
	public void update(){
		bounce();
		x += velX;
		y += velY;
		//System.out.println(velY);

	}
	
	public void changeDirection() {
		this.dir *= -1;
	}
}
