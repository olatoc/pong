package pongPackage;

public class Launcher {

	static Game game;
	public static void main(String[] args){
		
		game = new Game(800, 450);
		game.start();
		
	}
	
}
