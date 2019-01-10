package pongPackage;

public class EnemyPaddle{

	public int x;
	public int y;
	
	protected int velY;
	
	protected int height;
	protected int width;
	
	private boolean up;
	
	private int distance;
	
	private Display display;
	private Game game;
		
	public EnemyPaddle(Display display) {
		this.display = display;
		this.game = Launcher.game;
		
		this.height = 100;
		this.width = 20;
		
		this.x = display.width - 40;
		this.y = display.height/2 - this.height/2;
	}
	
	public void update(){
		move();
		if (bottomClamp(y) && !up) {
			y += Math.abs(velY);
		}
		if (topClamp(y) && up) {
			y -= Math.abs(velY);
		}
	}
	
	private void move() {
		distance = (y + height/2) - (game.getBall().y + game.getBall().height/2);
		velY = distance/12 > 6 ? 6 : distance/12;
		up = distance > 0;
	}
	
	private boolean topClamp(int y) {
		return y <= 0 ? false : true;
	}
	private boolean bottomClamp(int y) {
		return y + this.height >= display.height ? false : true;
	}
	
}
