package rbadia.voidspace.main;

public class GameStatusPlus extends GameStatus {

	private boolean newShip;
	private long shipDestroyed = 0;
	private long bigAsteroidsDestroyed = 0;
	private int livesLeft;
	
	public GameStatusPlus() {
	}
	
	public synchronized boolean isNewShip() {
		return newShip;
	}
	
	public synchronized void setNewShip(boolean newShip) {
		this.newShip = newShip;
	}
	
	public synchronized long getShipDestroyed() {
		return shipDestroyed;
	}

	public synchronized void setShipDestroyed(long shipDestroyed) {
		this.shipDestroyed = shipDestroyed;
	}
	
	public synchronized long getBigAsteroidsDestroyed() {
   		return bigAsteroidsDestroyed;
	}

	public synchronized void setBigAsteroidsDestroyed(long bigAsteroidsDestroyed) {
		this.bigAsteroidsDestroyed = bigAsteroidsDestroyed;
	}
	public synchronized int getLivesLeft() {
  		return livesLeft;
	}
	public synchronized void setLivesLeft(int livesLeft) {
  		this.livesLeft = livesLeft;
	} 

	
}
