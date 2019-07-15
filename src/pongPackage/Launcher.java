package pongPackage;

public class Launcher {

	static Game game;
	public static void main(String[] args){
		
		game = new Game(2000, 1500);
		game.start();
		
	}
	
}
