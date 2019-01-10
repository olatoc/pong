package pongPackage;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Paddle implements KeyListener{

	public int x = 5;
	public int y = 200;
	
	protected int velY;
	protected int acvel;
	
	protected int height;
	protected int width;
	
	private int up;
	private int down;
	
	private Display display;
		
	public Paddle(Display display) {
		this.display = display;
		display.canvas.addKeyListener(this);
		
		this.height = 100;
		this.width = 20;
		
		this.x = 20;
		this.velY = 0;
	}
	
	public void update(){
		if (topClamp(y) && up != 0) {
			y += up;
		}
		if (bottomClamp(y) && down != 0) {
			y += down;
		}
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		if (key == KeyEvent.VK_LEFT) {
			velY = -5;
			up = velY;
		}
		if (key == KeyEvent.VK_RIGHT) {
			velY = 5;
			down = velY;
		}
	}
	
	private boolean topClamp(int y) {
		return y <= 0 ? false : true;
	}
	private boolean bottomClamp(int y) {
		return y + this.height >= display.height ? false : true;
	}
	

	@Override
	public void keyReleased(KeyEvent e) {
		up = 0;
		down = 0;
		velY = 0;
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}
	
}
