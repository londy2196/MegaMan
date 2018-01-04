package rbadia.voidspace.model;

public class Ship extends GameObject {
	
	private static final long serialVersionUID = 1L;
	
	public static final int DEFAULT_SPEED = 1;
	
	public static final int WIDTH = 25;
	public static final int HEIGHT = 25;
	
	public Ship(int xPos, int yPos) {
		super(xPos, yPos, Asteroid.WIDTH, Asteroid.HEIGHT);
		this.setSpeed(DEFAULT_SPEED);
	}

	public int getDefaultSpeed(){
		return DEFAULT_SPEED;
	}

}
