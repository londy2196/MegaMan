package rbadia.voidspace.main;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import rbadia.voidspace.graphics.GraphicsManager;
import rbadia.voidspace.graphics.GraphicsManagerPlus;
import rbadia.voidspace.model.Asteroid;
import rbadia.voidspace.model.BigAsteroid;
import rbadia.voidspace.model.BigBullet;
import rbadia.voidspace.model.Bullet;
import rbadia.voidspace.model.Platform;
import rbadia.voidspace.model.Ship;
import rbadia.voidspace.sounds.SoundManager;

public class Level3State extends Level1State{
	
	private BufferedImage background;
	
	private GraphicsManagerPlus graphicsPlus;
	private GameStatusPlus statusPlus;
	
	protected Ship ship;
	protected BigAsteroid bigAsteroid;
	protected Rectangle bigAsteroidExplosion;
	protected Rectangle asteroid2Explosion;
	protected Rectangle shipExplosion;
	
	protected long lastAsteroidTime2;
	protected long lastShipTime;
	protected long lastBigAsteroidTime;
	
	protected static final int NEW_SHIP_DELAY = 500;
	protected static final int NEW_ASTEROID2_DELAY = 500;
	protected static final int NEW_BIG_ASTEROID_DELAY = 200;
	
	private static final long serialVersionUID = 1L;
	
	protected int levelShipsDestroyed = 0;
	protected int levelBigAsteroidsDestroyed = 0;
	
	public GraphicsManagerPlus getGraphicsManagerPlus() { return graphicsPlus; }
	protected void setGraphicsManagerPlus(GraphicsManagerPlus graphicsPlus) { this.graphicsPlus = graphicsPlus; }
	public GameStatusPlus getGameStatusPlus() { return statusPlus; }
	public void setGameStatusPlus(GameStatusPlus statusPlus) { this.statusPlus = statusPlus; }
	
	public Level3State(int level, MainFrame frame, GameStatusPlus statusPlus, 
			LevelLogic gameLogic, InputHandler inputHandler, 
			GraphicsManagerPlus graphicsPlus, SoundManager soundMan) {
		super(level, frame, statusPlus, gameLogic, inputHandler, graphicsPlus, soundMan);
		this.setGraphicsManagerPlus(graphicsPlus);
		this.setGameStatusPlus(statusPlus);
	}
	
	public BigAsteroid getBigAsteroid() { return bigAsteroid; }
	public Ship getShip() { return ship; }
	
	@Override
	public void doStart() {
		super.doStart();
		setStartState(GETTING_READY);
		setCurrentState(getStartState());
		statusPlus.setLivesLeft(3);
		statusPlus.setNewAsteroid2(false);
		statusPlus.setNewShip(false);
		newShip(this);
		newBigAsteroid(this);
		lastAsteroidTime2 = -NEW_ASTEROID_DELAY;
		lastShipTime = -NEW_SHIP_DELAY;
		lastBigAsteroidTime = -NEW_BIG_ASTEROID_DELAY;
	}
	
	@Override
	public void updateScreen() {
		super.updateScreen();
		drawBigAsteroid();
		drawShip();
		checkFloorBigAsteroidCollisions();
		checkFloorShipCollisions();
		checkMegaManBigAsteroidCollisions();
		checkMegaManShipCollisions();
		checkBigBulletBigAsteroidCollisions();
		checkBigBulletShipCollisions();
		checkBulletShipCollisions();
		checkBulletBigAsteroidCollisions();
	}

	@Override
	protected void drawAsteroid() {
		Graphics2D g2d = getGraphics2D();
		if((asteroid.getX() + asteroid.getPixelsWide() >  0)) {
			asteroid.translate(-asteroid.getSpeed() , asteroid.getSpeed());
			getGraphicsManager().drawAsteroid(asteroid, g2d, this);	
		}
		else {
			long currentTime = System.currentTimeMillis();
			if((currentTime - lastAsteroidTime) > NEW_ASTEROID_DELAY){

				asteroid.setLocation(this.getWidth() - asteroid.getPixelsWide(),
						rand.nextInt(this.getHeight() - asteroid.getPixelsTall() - 32));
			}
			else {
				// draw explosion
				getGraphicsManager().drawAsteroidExplosion(asteroidExplosion, g2d, this);
			}
		}	
	}
	
	protected void drawBigAsteroid() {
		Graphics2D g2d = getGraphics2D();
		GameStatusPlus statusPlus = getGameStatusPlus();
		if((bigAsteroid.getX() + bigAsteroid.getWidth()) < this.getWidth()) {
			bigAsteroid.translate(bigAsteroid.getSpeed(), 0);
			getGraphicsManagerPlus().drawBigAsteroid(bigAsteroid, g2d, this);
		}
		else {
			long currentTime2 = System.currentTimeMillis();
			if((currentTime2 - lastBigAsteroidTime) > NEW_BIG_ASTEROID_DELAY) {
				lastBigAsteroidTime = currentTime2;
				statusPlus.setNewBigAsteroid(false);
				bigAsteroid.setLocation((int) (0),
						(rand.nextInt((int) (this.getHeight() - bigAsteroid.getPixelsTall() - 64))));
			}
			else {
				getGraphicsManagerPlus().drawBigAsteroidExplosion(bigAsteroidExplosion, g2d, this);
			}
		}
	}
	
	protected void drawShip() {	
		Graphics2D g2d = getGraphics2D();
		GameStatusPlus statusPlus = getGameStatusPlus();
		if((ship.getX() + ship.getWidth()) > 0) {
			ship.translate(-ship.getSpeed(), 0);
			getGraphicsManagerPlus().drawShip(ship, g2d, this);
		}
		else {
			long currentTime3 = System.currentTimeMillis();
			if((currentTime3 - lastShipTime) > NEW_SHIP_DELAY) {
			// draw a new ship
				lastShipTime = currentTime3;
				statusPlus.setNewShip(false);
				ship.setLocation((int) (this.getWidth() - ship.getPixelsWide()),
					(rand.nextInt((int) (this.getHeight() - ship.getPixelsTall() - 25))));
			}
			else{
				// draw explosion
				getGraphicsManagerPlus().drawShipExplosion(shipExplosion, g2d, this);
			}
		}
	}
	
	protected void checkFloorShipCollisions() {
		for(int i=0; i<9; i++) {
			if (ship.intersects(floor[i])) {
				removeShip(ship);
			}
		}
	}
	
	protected void checkFloorBigAsteroidCollisions() {
		for(int i =0; i<9; i++) {
			if (bigAsteroid.intersects(floor[i])) {
				removeBigAsteroid(bigAsteroid);
			}
		}
	}
	
	protected void checkMegaManBigAsteroidCollisions() {
		GameStatusPlus statusPlus = getGameStatusPlus();
		if(bigAsteroid.intersects(megaMan)) {
			statusPlus.setLivesLeft(statusPlus.getLivesLeft() - 2);
			removeBigAsteroid(bigAsteroid);
		}
	}
	
	protected void checkMegaManShipCollisions() {
		GameStatusPlus statusPlus = getGameStatusPlus();
		if(ship.intersects(megaMan)) {
			statusPlus.setLivesLeft(statusPlus.getLivesLeft() - 1);
			removeShip(ship);
		}
	}
	
	protected void checkBigBulletBigAsteroidCollisions() {
		GameStatusPlus statusPlus = getGameStatusPlus();
		int hits = 0;
		for(int i = 0; i<bigBullets.size(); i++) {
			BigBullet bigBullet = bigBullets.get(i);
			if(bigAsteroid.intersects(bigBullet)) {
				hits++;
				if(hits >= 2) {
					statusPlus.setBigAsteroidsDestroyed(statusPlus.getBigAsteroidsDestroyed() + 300);
					removeBigAsteroid(bigAsteroid);
					damage = 0;
				}
			}
		}
	}
	protected void checkBigBulletShipCollisions() {
		GameStatusPlus statusPlus = getGameStatusPlus();
		for(int i=0; i<bigBullets.size(); i++) {
			BigBullet bigBullet = bigBullets.get(i);
			if(ship.intersects(bigBullet)) {
				statusPlus.setAsteroidsDestroyed(statusPlus.getAsteroidsDestroyed() + 200);
				removeShip(ship);
				damage = 0;
			}
		}
	}
	
	protected void checkBulletBigAsteroidCollisions() {
		GameStatusPlus statusPlus = getGameStatusPlus();
		int hits = 0;
		for(int i=0; i<bullets.size(); i++) {
			Bullet bullet = bullets.get(i);
			if(bigAsteroid.intersects(bullet)) {
				hits++;
				if(hits >= 3) {
					statusPlus.setBigAsteroidsDestroyed(statusPlus.getBigAsteroidsDestroyed() + 300);
					removeBigAsteroid(bigAsteroid);
					levelBigAsteroidsDestroyed++;
					damage = 0;
					bullets.remove(i);
					break;
				}
			}
		}
	}
	
	protected void checkBulletShipCollisions() {
		GameStatusPlus statusPlus = getGameStatusPlus();
		for(int i = 0; i<bullets.size(); i++) {
			Bullet bullet = bullets.get(i);
			if (ship.intersects(bullet)) {
				statusPlus.setAsteroidsDestroyed(statusPlus.getAsteroidsDestroyed() + 200);
				removeShip(ship);
				levelShipsDestroyed++;
				damage=0;
				bullets.remove(i);
				break;
			}
		}
	}
	
	public void removeShip(Ship ship) {
		shipExplosion = new Rectangle(ship.x, ship.y, ship.getPixelsWide(), ship.getPixelsTall());
		ship.setLocation(-ship.getPixelsWide(), -ship.getPixelsTall());
		this.getGameStatusPlus().setNewShip(true);
		lastShipTime = System.currentTimeMillis();
		this.getSoundManager().playAsteroidExplosionSound();
	}
	
	public void removeBigAsteroid(BigAsteroid bigAsteroid) {
		bigAsteroidExplosion = new Rectangle(bigAsteroid.x, bigAsteroid.y,
				bigAsteroid.getPixelsWide(), bigAsteroid.getPixelsTall());
		bigAsteroid.setLocation(-bigAsteroid.getPixelsWide(), -bigAsteroid.getPixelsTall());
		this.getGameStatusPlus().setNewBigAsteroid(true);
		lastBigAsteroidTime = System.currentTimeMillis();
		this.getSoundManager().playAsteroidExplosionSound();
	}
	
	public Ship newShip(Level3State screen) {
		int xPos = (int) (screen.getWidth() - Ship.WIDTH);
		int yPos = rand.nextInt((int)(screen.getHeight() - Ship.HEIGHT - 25));
		ship = new Ship(xPos, yPos);
		return ship;
	}
	
	public BigAsteroid newBigAsteroid(Level3State screen) {
		int xPos = (int) (BigAsteroid.WIDTH);
		int yPos = rand.nextInt((int)(screen.getHeight() - BigAsteroid.HEIGHT - 32));
		bigAsteroid = new BigAsteroid(xPos, yPos);
		return bigAsteroid;
	}
	
	@Override
	public boolean isLevelWon() {
		if (levelAsteroidsDestroyed >= 3 && levelShipsDestroyed >= 2 && 
				levelBigAsteroidsDestroyed >= 1) { return true; }
		return false;
	}

	
	@Override
	public Platform[] newPlatforms(int n){
		platforms = new Platform[n];
		for(int i=0; i<n; i++){
			this.platforms[i] = new Platform(230 , getHeight()/2 + 140 - i*40);
		}
		return platforms;

	}

	protected void clearScreen() {
		Graphics2D g2d = getGraphics2D();
		
		g2d.setPaint(Color.BLACK);
		g2d.fillRect(0, 0, getSize().width, getSize().height);
		
		try {
			this.background = ImageIO.read(getClass().getResource("/rbadia/voidspace/graphics/backgroundlvl3.png"));
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		g2d.drawImage(background, 0, 0, null);
	}
	
}

