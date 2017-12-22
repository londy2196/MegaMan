package rbadia.voidspace.model;

public class Moltres extends GameObject {
	private static final long serialVersionUID = 1L;

	
	public static final int DEFAULT_SPEED = 0;
	
	public static final int WIDTH = 60;
	public static final int HEIGHT = 60;
	
	public Moltres(int xPos, int yPos) {
		super(xPos, yPos, Moltres.WIDTH, Moltres.HEIGHT);
		this.setSpeed(DEFAULT_SPEED);
	}

	public int getDefaultSpeed(){
		return DEFAULT_SPEED;
	}
}

