package rbadia.voidspace.main;

import java.awt.Graphics2D;
import java.awt.Rectangle;

import rbadia.voidspace.graphics.GraphicsManager;
import rbadia.voidspace.model.Asteroid;
import rbadia.voidspace.model.BigBullet;
import rbadia.voidspace.model.Bullet;
import rbadia.voidspace.model.Platform;
import rbadia.voidspace.sounds.SoundManager;

public class Level3State extends Level1State{
	
	protected Asteroid asteroid2;
	protected Rectangle asteroid2Explosion;
	protected long lastAsteroidTime2;
	
	private static final long serialVersionUID = 1L;
	
	public Level3State(int level, MainFrame frame, GameStatus status, 
			LevelLogic gameLogic, InputHandler inputHandler, 
			GraphicsManager graphicsMan, SoundManager soundMan) {
		super(level, frame, status, gameLogic, inputHandler, graphicsMan, soundMan);
	}
	
	public Asteroid getAsteroid2() { return asteroid2; }
	
	@Override
	public void doStart() {
		super.doStart();
		setStartState(GETTING_READY);
		setCurrentState(getStartState());
		GameStatus status = this.getGameStatus();
		status.setNewAsteroid2(false);
		newAsteroid2(this);
		lastAsteroidTime2 = -NEW_ASTEROID_DELAY;
	}
	
	@Override
	public void updateScreen() {
		super.updateScreen();
		drawAsteroid2();
		checkAsteroid2FloorCollisions();
		checkMegaManAsteroid2Collisions();
		checkBigBulletAsteroid2Collisions();
		checkBullletAsteroid2Collisions();
		
	}

	@Override
	protected void drawAsteroid() {
		Graphics2D g2d = getGraphics2D();
		if((asteroid.getX() + asteroid.getPixelsWide() >  0)) {
			asteroid.translate(-asteroid.getSpeed() - 2 , asteroid.getSpeed() + 2);
			getGraphicsManager().drawAsteroid(asteroid, g2d, this);	
		}
		else {
			long currentTime = System.currentTimeMillis();
			if((currentTime - lastAsteroidTime) > NEW_ASTEROID_DELAY + 200){

				asteroid.setLocation(this.getWidth() - asteroid.getPixelsWide(),
						rand.nextInt(this.getHeight() - asteroid.getPixelsTall() - 32));
			}
			else {
				// draw explosion
				getGraphicsManager().drawAsteroidExplosion(asteroidExplosion, g2d, this);
			}
		}	
	}
	
	protected void drawShip() {
		
	}
	
	protected void drawAsteroid2() {
		Graphics2D g2d = getGraphics2D();
		if(asteroid2.getX() + asteroid2.getPixelsWide() < 500) {
			asteroid2.translate(asteroid2.getSpeed() + 2, asteroid2.getSpeed() + 2);
			getGraphicsManager().drawAsteroid(asteroid2,  g2d, this);
		}
		else {
			long currentTime2 = System.currentTimeMillis();
			if ((currentTime2 - lastAsteroidTime2) > NEW_ASTEROID_DELAY + 200) {
				asteroid2.setLocation(0, 
						rand.nextInt(this.getHeight() - asteroid2.getPixelsTall() - 32));
			}
			else {
				getGraphicsManager().drawAsteroidExplosion(asteroid2Explosion, g2d, this);
			}
		}
	}
	
	protected void checkAsteroid2FloorCollisions() {
		for(int i=0; i<9; i++){
			if(asteroid2.intersects(floor[i])){
				removeAsteroid(asteroid2);

			}
		}
	}

	protected void checkMegaManAsteroid2Collisions() {
		GameStatus status = getGameStatus();
		if(asteroid2.intersects(megaMan)){
			status.setLivesLeft(status.getLivesLeft() - 1);
			removeAsteroid(asteroid2);
		}
	}

	protected void checkBigBulletAsteroid2Collisions() {
		GameStatus status = getGameStatus();
		for(int i=0; i<bigBullets.size(); i++){
			BigBullet bigBullet = bigBullets.get(i);
			if(asteroid2.intersects(bigBullet)){
				// increase asteroids destroyed count
				status.setAsteroidsDestroyed(status.getAsteroidsDestroyed() + 100);
				removeAsteroid(asteroid2);
				damage=0;
			}
		}
	}

	protected void checkBullletAsteroid2Collisions() {
		GameStatus status = getGameStatus();
		for(int i=0; i<bullets.size(); i++){
			Bullet bullet = bullets.get(i);
			if(asteroid2.intersects(bullet)){
				// increase asteroids destroyed count
				status.setAsteroidsDestroyed(status.getAsteroidsDestroyed() + 100);
				removeAsteroid(asteroid2);
				levelAsteroidsDestroyed++;
				damage=0;
				// remove bullet
				bullets.remove(i);
				break;
			}
		}
	}
    
	public void removeAsteroid2(Asteroid asteroid){
		// "remove" asteroid
		asteroid2Explosion = new Rectangle(
				asteroid2.x,
				asteroid2.y,
				asteroid2.getPixelsWide(),
				asteroid2.getPixelsTall());
		asteroid2.setLocation(-asteroid2.getPixelsWide(), -asteroid2.getPixelsTall());
		this.getGameStatus().setNewAsteroid2(true);
		lastAsteroidTime2 = System.currentTimeMillis();
		// play asteroid explosion sound
		this.getSoundManager().playAsteroidExplosionSound();
	}
	
	public Asteroid newAsteroid2(Level3State screen){
		int xPos = (int) (Asteroid.WIDTH);
		int yPos = rand.nextInt((int)(screen.getHeight() - Asteroid.HEIGHT- 32));
		asteroid2 = new Asteroid(xPos, yPos);
		return asteroid2;
		
		
	}
	
	
	@Override
	public Platform[] newPlatforms(int n){
		platforms = new Platform[n];
		for(int i=0; i<n; i++){
			this.platforms[i] = new Platform(0,0);
			if(i<4)	platforms[i].setLocation(50+ i*50, getHeight()/2 + 140 - i*40);
			if(i==4) platforms[i].setLocation(50 +i*50, getHeight()/2 + 140 - 3*40);
			if(i>4){	
				int k=4;
				platforms[i].setLocation(50 + i*50, getHeight()/2 + 20 + (i-k)*40 );
				k=k+2;
			}
		}
		return platforms;
	}

	
}

