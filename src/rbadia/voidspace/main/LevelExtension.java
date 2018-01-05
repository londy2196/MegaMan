package rbadia.voidspace.main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import rbadia.voidspace.graphics.GraphicsManager;
import rbadia.voidspace.graphics.GraphicsManagerPlus;
import rbadia.voidspace.model.Asteroid;
import rbadia.voidspace.model.BigBullet;
import rbadia.voidspace.model.Bullet;
import rbadia.voidspace.model.Floor;
import rbadia.voidspace.model.MegaMan;
import rbadia.voidspace.model.Platform;
import rbadia.voidspace.sounds.SoundManager;

public class LevelExtension extends Level1State{
	
	private BufferedImage background;
	
	private GraphicsManagerPlus graphicsPlus;
	private GameStatusPlus statusPlus;
	
	private boolean goesLeft;
	
	private static final long serialVersionUID = 1L;
	//protected GraphicsManager graphicsManager;
	protected BufferedImage backBuffer;
	protected MegaMan megaMan;
	protected Asteroid asteroid;
	protected List<Bullet> bullets;
	protected List<BigBullet> bigBullets;
	protected List<Bullet> bulletsL;
	protected List<BigBullet> bigBulletsL;
	protected Floor[] floor;	
	protected int numPlatforms=8;
	protected Platform[] platforms;

	protected int damage=0;
	protected static final int NEW_MEGAMAN_DELAY = 500;
	protected static final int NEW_ASTEROID_DELAY = 500;

	protected long lastAsteroidTime;
	protected long lastLifeTime;

	protected Rectangle asteroidExplosion;

	protected Random rand;

	protected Font originalFont;
	protected Font bigFont;
	protected Font biggestFont;

	protected int levelAsteroidsDestroyed = 0;
	
	public GraphicsManagerPlus getGraphicsManagerPlus() { return graphicsPlus; }
	public GameStatusPlus getGameStatusPlus() { return statusPlus; }
	
	protected void setGraphicsManagerPlus(GraphicsManagerPlus graphicsPlus) { this.graphicsPlus = graphicsPlus; }
	public void setGameStatusPlus(GameStatusPlus statusPlus) { this.statusPlus = statusPlus; }

	// Constructors
	public LevelExtension(int level, MainFrame frame, GameStatusPlus statusPlus, 
			LevelLogic gameLogic, InputHandler inputHandler,
			GraphicsManagerPlus graphicsPlus, SoundManager soundMan) {
		super(level, frame, statusPlus, gameLogic, inputHandler, graphicsPlus, soundMan);
		this.setSize(new Dimension(500, 400));
		this.setPreferredSize(new Dimension(500, 400));
		this.setBackground(Color.BLACK);
		this.setLevel(level);
		this.setMainFrame(frame);
		this.setGameStatusPlus(statusPlus);
		this.setGameLogic(gameLogic);
		this.setInputHandler(inputHandler);
		this.setSoundManager(soundMan);
		this.setGraphicsManagerPlus(graphicsPlus);
		backBuffer = new BufferedImage(500, 400, BufferedImage.TYPE_INT_RGB);
		this.setGraphics2D(backBuffer.createGraphics());
		rand = new Random();
	}
	
	// Getters
	public MegaMan getMegaMan() 					{ return megaMan; 		}
	public Floor[] getFloor()					{ return floor; 			}
	public int getNumPlatforms()					{ return numPlatforms; 	}
	public Platform[] getPlatforms()				{ return platforms; 		}
	public Asteroid getAsteroid() 				{ return asteroid; 		}
	public List<Bullet> getBullets() 			{ return bullets; 		}
	public List<BigBullet> getBigBullets()		{ return bigBullets;   	}
	public List<Bullet> getBulletsL() { return bulletsL; }
	public List<BigBullet> getBigBulletsL() { return bigBulletsL; }

	// Level state methods
	// The method associated with the current level state will be called 
	// repeatedly during each LevelLoop iteration until the next a state 
	// transition occurs
	// To understand when each is invoked see LevelLogic.stateTransition() & LevelLoop class

	@Override
	public void doStart() {	

		setStartState(START_STATE);
		setCurrentState(getStartState());
		// init game variables
		bullets = new ArrayList<Bullet>();
		bigBullets = new ArrayList<BigBullet>();
		bulletsL = new ArrayList<Bullet>();
		bigBulletsL = new ArrayList<BigBullet>();
		//numPlatforms = new Platform[5];

		GameStatus status = this.getGameStatus();
		status.setLivesLeft(3);
		status.setAsteroidsDestroyed(0);
		status.setGameOver(false);
		status.setNewAsteroid(false);

		// init the life and the asteroid
		newMegaMan();
		newFloor(this, 9);
		newPlatforms(getNumPlatforms());
		newAsteroid(this);

		lastAsteroidTime= -NEW_ASTEROID_DELAY;
		lastLifeTime = -NEW_MEGAMAN_DELAY;

		bigFont = originalFont;
		biggestFont = null;

		// Display initial values for scores
		getMainFrame().getDestroyedValueLabel().setForeground(Color.BLACK);
		getMainFrame().getLivesValueLabel().setText(Integer.toString(status.getLivesLeft()));
		getMainFrame().getDestroyedValueLabel().setText(Long.toString(status.getAsteroidsDestroyed()));
		getMainFrame().getLevelValueLabel().setText(Long.toString(status.getLevel()));

	}

	@Override
	public void doInitialScreen() {
		setCurrentState(INITIAL_SCREEN);
		clearScreen();
		getGameLogic().drawInitialMessage();
	};

	@Override
	public void doGettingReady() {
		setCurrentState(GETTING_READY);
		getGameLogic().drawGetReady();
		repaint();
		LevelLogic.delay(2000);
		//Changes music from "menu music" to "ingame music"
		MegaManMain.audioClip.close();
		MegaManMain.audioFile = new File("audio/mainGame.wav");
		try {
			MegaManMain.audioStream = AudioSystem.getAudioInputStream(MegaManMain.audioFile);
			MegaManMain.audioClip.open(MegaManMain.audioStream);
			MegaManMain.audioClip.start();
			MegaManMain.audioClip.loop(Clip.LOOP_CONTINUOUSLY);
		} catch (UnsupportedAudioFileException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (LineUnavailableException e1) {
			e1.printStackTrace();
		}
	};

	@Override
	public void doPlaying() {
		setCurrentState(PLAYING);
		updateScreen();
	};

	@Override
	public void doNewMegaman() {
		setCurrentState(NEW_MEGAMAN);
	};

	@Override
	public void doLevelWon(){
		setCurrentState(LEVEL_WON);
		getGameLogic().drawYouWin();
		repaint();
		LevelLogic.delay(5000);
	}

	@Override
	public void doGameOverScreen(){
		setCurrentState(GAME_OVER_SCREEN);
		getGameLogic().drawGameOver();
		getMainFrame().getDestroyedValueLabel().setForeground(new Color(128, 0, 0));
		repaint();
		LevelLogic.delay(1500);
	}
	
	@Override
	public void doGameOver() {
		this.getGameStatus().setGameOver(true);
	}

	/**
	 * Update the game screen.
	 */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(backBuffer, 0, 0, this);
	}

	/**
	 * Update the game screen's backbuffer image.
	 */
	public void updateScreen(){
		Graphics2D g2d = getGraphics2D();
		GameStatus status = this.getGameStatus();

		// save original font - for later use
		if(this.originalFont == null){
			this.originalFont = g2d.getFont();
			this.bigFont = originalFont;
		}

		clearScreen();
		drawStars(50);
		drawFloor();
		drawPlatforms();
		drawMegaMan();
		drawAsteroid();
		drawBullets();
		drawBulletsL();
		drawBigBullets();
		checkBullletAsteroidCollisions();
		checkBigBulletAsteroidCollisions();
		checkMegaManAsteroidCollisions();
		checkAsteroidFloorCollisions();

		// update asteroids destroyed (score) label  
		getMainFrame().getDestroyedValueLabel().setText(Long.toString(status.getAsteroidsDestroyed()));
		// update lives left label
		getMainFrame().getLivesValueLabel().setText(Integer.toString(status.getLivesLeft()));
		//update level label
		getMainFrame().getLevelValueLabel().setText(Long.toString(status.getLevel()));
	}

	protected void checkAsteroidFloorCollisions() {
		for(int i=0; i<9; i++){
			if(asteroid.intersects(floor[i])){
				removeAsteroid(asteroid);

			}
		}
	}

	protected void checkMegaManAsteroidCollisions() {
		GameStatus status = getGameStatus();
		if(asteroid.intersects(megaMan)){
			status.setLivesLeft(status.getLivesLeft() - 1);
			removeAsteroid(asteroid);
		}
	}

	protected void checkBigBulletAsteroidCollisions() {
		GameStatus status = getGameStatus();
		for(int i=0; i<bigBullets.size(); i++){
			BigBullet bigBullet = bigBullets.get(i);
			if(asteroid.intersects(bigBullet)){
				// increase asteroids destroyed count
				status.setAsteroidsDestroyed(status.getAsteroidsDestroyed() + 100);
				removeAsteroid(asteroid);
				damage=0;
			}
		}
	}

	protected void checkBullletAsteroidCollisions() {
		GameStatus status = getGameStatus();
		for(int i=0; i<bullets.size(); i++){
			Bullet bullet = bullets.get(i);
			if(asteroid.intersects(bullet)){
				// increase asteroids destroyed count
				status.setAsteroidsDestroyed(status.getAsteroidsDestroyed() + 100);
				removeAsteroid(asteroid);
				levelAsteroidsDestroyed++;
				damage=0;
				// remove bullet
				bullets.remove(i);
				break;
			}
		}
	}

	protected void drawBigBullets() {
		Graphics2D g2d = getGraphics2D();
		for(int i=0; i<bigBullets.size(); i++){
			BigBullet bigBullet = bigBullets.get(i);
			getGraphicsManager().drawBigBullet(bigBullet, g2d, this);

			boolean remove = this.moveBigBullet(bigBullet);
			if(remove){
				bigBullets.remove(i);
				i--;
			}
		}
	}

	protected void drawBullets() {
		Graphics2D g2d = getGraphics2D();
		for(int i=0; i<bullets.size(); i++){
			Bullet bullet = bullets.get(i);
			getGraphicsManager().drawBullet(bullet, g2d, this);

			boolean remove =   this.moveBullet(bullet);
			if(remove){
				bullets.remove(i);
				i--;
			}
		}
	}
	
	protected void drawBulletsL() {
		Graphics2D g2d = getGraphics2D();
		for(int i = 0; i<bulletsL.size();i++) {
			Bullet bullet = bulletsL.get(i);
			getGraphicsManager().drawBullet(bullet, g2d, this);
			boolean remove = this.moveBulletL(bullet);
			if(remove){
				bulletsL.remove(i);
				i--;
			}
		}
	}

	protected void drawAsteroid() {
		Graphics2D g2d = getGraphics2D();
		GameStatus status = getGameStatus();
		if((asteroid.getX() + asteroid.getWidth() >  0)){
			asteroid.translate(-asteroid.getSpeed(), 0);
			getGraphicsManager().drawAsteroid(asteroid, g2d, this);	
		}
		else {
			long currentTime = System.currentTimeMillis();
			if((currentTime - lastAsteroidTime) > NEW_ASTEROID_DELAY){
				// draw a new asteroid
				lastAsteroidTime = currentTime;
				status.setNewAsteroid(false);
				asteroid.setLocation((int) (this.getWidth() - asteroid.getPixelsWide()),
						(rand.nextInt((int) (this.getHeight() - asteroid.getPixelsTall() - 32))));
			}

			else{
				// draw explosion
				getGraphicsManager().drawAsteroidExplosion(asteroidExplosion, g2d, this);
			}
		}
	}

	protected void drawMegaMan() {
		//draw one of three possible MegaMan poses according to situation
		Graphics2D g2d = getGraphics2D();
		GameStatus status = getGameStatus();
		if(!status.isNewMegaMan()){
			if((Gravity() == true) || ((Gravity() == true) && (Fire() == true || Fire2() == true) && (goesLeft==false))){
				getGraphicsManager().drawMegaFallR(megaMan, g2d, this);
			}
		}

		if((Fire() == true || Fire2()== true) && (Gravity()==false) && (goesLeft == false)){
			getGraphicsManager().drawMegaFireR(megaMan, g2d, this);
		}

		if((Gravity()==false) && (Fire()==false) && (Fire2()==false) && (goesLeft == false)){
			getGraphicsManager().drawMegaMan(megaMan, g2d, this);
		}
		if(!status.isNewMegaMan()) {
			if((Gravity() == true) || ((Gravity() == true) && (Fire() == true || Fire2() == true) && (goesLeft == true))){
				getGraphicsManagerPlus().drawMegaFallL(megaMan, g2d, this);
			}
		}

		if((FireL() == true || Fire2L()== true) && (Gravity()==false) && (goesLeft == true)){
			getGraphicsManagerPlus().drawMegaFireL(megaMan, g2d, this);
		}

		if((Gravity()==false) && (FireL()==false) && (Fire2L()==false) && (goesLeft == true)){
			getGraphicsManagerPlus().drawMegaManL(megaMan, g2d, this);
		}
	}

	protected void drawPlatforms() {
		//draw platforms
		Graphics2D g2d = getGraphics2D();
		for(int i=0; i<getNumPlatforms(); i++){
			getGraphicsManager().drawPlatform(platforms[i], g2d, this, i);
		}
	}

	protected void drawFloor() {
		//draw Floor
		Graphics2D g2d = getGraphics2D();
		for(int i=0; i<9; i++){
			getGraphicsManager().drawFloor(floor[i], g2d, this, i);	
		}
	}

	protected void clearScreen() {
		Graphics2D g2d = getGraphics2D();
		
		g2d.setPaint(Color.BLACK);
		g2d.fillRect(0, 0, getSize().width, getSize().height);
		
		try {
			this.background = ImageIO.read(getClass().getResource("/rbadia/voidspace/graphics/background.png"));
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		g2d.drawImage(background, 0, 0, null);
	}

	/**
	 * Draws the specified number of stars randomly on the game screen.
	 * @param numberOfStars the number of stars to draw
	 */
	protected void drawStars(int numberOfStars) {
		Graphics2D g2d = getGraphics2D();
		g2d.setColor(Color.WHITE);
		for(int i=0; i<numberOfStars; i++){
			int x = (int)(Math.random() * this.getWidth());
			int y = (int)(Math.random() * this.getHeight());
			g2d.drawLine(x, y, x, y);
		}
	}

	@Override
	public boolean isLevelWon() {
		return levelAsteroidsDestroyed >= 3;
	}

	protected boolean Gravity(){
		MegaMan megaMan = this.getMegaMan();
		Floor[] floor = this.getFloor();

		for(int i=0; i<9; i++){
			if((megaMan.getY() + megaMan.getHeight() -17 < this.getHeight() - floor[i].getHeight()/2) 
					&& Fall() == true){

				megaMan.translate(0 , 2);
				return true;

			}
		}
		return false;
	}

	//Bullet fire pose
	protected boolean Fire(){
		MegaMan megaMan = this.getMegaMan();
		List<Bullet> bullets = this.getBullets();
		for(int i=0; i<bullets.size(); i++){
			Bullet bullet = bullets.get(i);
			if((bullet.getX() > megaMan.getX() + megaMan.getWidth()) && 
					(bullet.getX() <= megaMan.getX() + megaMan.getWidth() + 60)){
				return true;
			}
		}
		return false;
	}
	
	protected boolean FireL() {
		MegaMan megaMan = this.getMegaMan();
		List<Bullet> bullets = this.getBulletsL();
		for(int i=0; i<bullets.size(); i++) {
			Bullet bullet = bullets.get(i);
			if((bullet.getX() < megaMan.getX() + megaMan.getWidth()) &&
					(bullet.getX() >= megaMan.getX() + megaMan.getWidth() + 60)){
				return true;
			}
		}
		return false;
	}

	//BigBullet fire pose
	protected boolean Fire2(){
		MegaMan megaMan = this.getMegaMan();
		List<BigBullet> bigBullets = this.getBigBullets();
		for(int i=0; i<bigBullets.size(); i++){
			BigBullet bigBullet = bigBullets.get(i);
			if((bigBullet.getX() > megaMan.getX() + megaMan.getWidth()) && 
					(bigBullet.getX() <= megaMan.getX() + megaMan.getWidth() + 60)){
				return true;
			}
		}
		return false;
	}

	protected boolean Fire2L() {
		MegaMan megaMan = this.getMegaMan();
		List<BigBullet> bigBullets = this.getBigBulletsL();
		for(int i=0; i<bigBullets.size(); i++){
			BigBullet bigBullet = bigBullets.get(i);
			if((bigBullet.getX() < megaMan.getX() + megaMan.getWidth()) && 
					(bigBullet.getX() >= megaMan.getX() + megaMan.getWidth() + 60)){
				return true;
			}
		}
		return false;
	}
	//Platform Gravity
	public boolean Fall(){
		MegaMan megaMan = this.getMegaMan(); 
		Platform[] platforms = this.getPlatforms();
		for(int i=0; i<getNumPlatforms(); i++){
			if((((platforms[i].getX() < megaMan.getX()) && (megaMan.getX()< platforms[i].getX() + platforms[i].getWidth()))
					|| ((platforms[i].getX() < megaMan.getX() + megaMan.getWidth()) 
							&& (megaMan.getX() + megaMan.getWidth()< platforms[i].getX() + platforms[i].getWidth())))
					&& megaMan.getY() + megaMan.getHeight() == platforms[i].getY()
					){
				return false;
			}
		}
		return true;
	}

	public void removeAsteroid(Asteroid asteroid){
		// "remove" asteroid
		asteroidExplosion = new Rectangle(
				asteroid.x,
				asteroid.y,
				asteroid.getPixelsWide(),
				asteroid.getPixelsTall());
		asteroid.setLocation(-asteroid.getPixelsWide(), -asteroid.getPixelsTall());
		this.getGameStatus().setNewAsteroid(true);
		lastAsteroidTime = System.currentTimeMillis();
		// play asteroid explosion sound
		this.getSoundManager().playAsteroidExplosionSound();
	}

	/**
	 * Fire a bullet from life.
	 */
	public void fireBullet(){
		if(goesLeft == false) {
			Bullet bullet = new Bullet(megaMan.x + megaMan.width - Bullet.WIDTH/2, 
					megaMan.y + megaMan.width/2 - Bullet.HEIGHT+2);
			bullets.add(bullet);
		}
		else {
			Bullet bullet = new Bullet(megaMan.x - Bullet.WIDTH/2,
					megaMan.y + megaMan.width/2 - Bullet.HEIGHT+2);
			bulletsL.add(bullet);
		}
		
		this.getSoundManager().playBulletSound();
	}

	/**
	 * Fire the "Power Shot" bullet
	 */
	public void fireBigBullet(){
		//BigBullet bigBullet = new BigBullet(megaMan);
		int xPos = megaMan.x + megaMan.width - BigBullet.WIDTH / 2;
		int yPos = megaMan.y + megaMan.width/2 - BigBullet.HEIGHT + 4;
		BigBullet  bigBullet = new BigBullet(xPos, yPos);
		bigBullets.add(bigBullet);
		this.getSoundManager().playBulletSound();
	}

	/**
	 * Move a bullet once fired.
	 * @param bullet the bullet to move
	 * @return if the bullet should be removed from screen
	 */
	public boolean moveBullet(Bullet bullet){
		if(bullet.getY() - bullet.getSpeed() >= 0){
			bullet.translate(bullet.getSpeed(), 0);
			return false;
		}
		else{
			return true;
		}
	}
	
	public boolean moveBulletL(Bullet bullet) {
		if(bullet.getY() - bullet.getSpeed() >= 0) {
			bullet.translate(-bullet.getSpeed(), 0);
			return false;
		}
		else { return true; }
	}

	/** Move a "Power Shot" bullet once fired.
	 * @param bigBullet the bullet to move
	 * @return if the bullet should be removed from screen
	 */
	public boolean moveBigBullet(BigBullet bigBullet){
		if(bigBullet.getY() - bigBullet.getSpeed() >= 0){
			bigBullet.translate(bigBullet.getSpeed(), 0);
			return false;
		}
		else{
			return true;
		}
	}

	/**
	 * Create a new MegaMan (and replace current one).
	 */
	public MegaMan newMegaMan(){
		this.megaMan = new MegaMan((getWidth() - MegaMan.WIDTH) / 2, (getHeight() - MegaMan.HEIGHT - MegaMan.Y_OFFSET) / 2);
		return megaMan;
	}

	public Floor[] newFloor(LevelExtension screen, int n){
		floor = new Floor[n];
		for(int i=0; i<n; i++){
			this.floor[i] = new Floor(0 + i * Floor.WIDTH, screen.getHeight()- Floor.HEIGHT/2);
		}

		return floor;
	}

	public Platform[] newPlatforms(int n){
		platforms = new Platform[n];
		for(int i=0; i<n; i++){
			this.platforms[i] = new Platform(0 , getHeight()/2 + 140 - i*40);
		}
		return platforms;
	}

	/**
	 * Create a new asteroid.
	 */
	public Asteroid newAsteroid(LevelExtension screen){
		int xPos = (int) (screen.getWidth() - Asteroid.WIDTH);
		int yPos = rand.nextInt((int)(screen.getHeight() - Asteroid.HEIGHT- 32));
		asteroid = new Asteroid(xPos, yPos);
		return asteroid;
	}

	/**
	 * Move the megaMan up
	 * @param megaMan the megaMan
	 */
	public void moveMegaManUp(){
		if(megaMan.getY() - megaMan.getSpeed() >= 0){
			megaMan.translate(0, -megaMan.getSpeed()*2);
		}
	}

	/**
	 * Move the megaMan down
	 * @param megaMan the megaMan
	 */
	public void moveMegaManDown(){
		for(int i=0; i<9; i++){
			if(megaMan.getY() + megaMan.getSpeed() + megaMan.height < getHeight() - floor[i].getHeight()/2){
				megaMan.translate(0, 2);
			}
		}
	}

	/**
	 * Move the megaMan left
	 * @param megaMan the megaMan
	 */
	public void moveMegaManLeft(){
		if(megaMan.getX() - megaMan.getSpeed() >= 0){
			megaMan.translate(-megaMan.getSpeed(), 0);
			goesLeft = true;
		}
	}

	/**
	 * Move the megaMan right
	 * @param megaMan the megaMan
	 */
	public void moveMegaManRight(){
		if(megaMan.getX() + megaMan.getSpeed() + megaMan.width < getWidth()){
			megaMan.translate(megaMan.getSpeed(), 0);
			goesLeft = false;
		}
	}

	public void speedUpMegaMan() {
		megaMan.setSpeed(megaMan.getDefaultSpeed() * 2 +1);
	}

	public void slowDownMegaMan() {
		megaMan.setSpeed(megaMan.getDefaultSpeed());
	}
}
	

