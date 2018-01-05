package rbadia.voidspace.graphics;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import rbadia.voidspace.model.BigAsteroid;
import rbadia.voidspace.model.MegaMan;
import rbadia.voidspace.model.Moltres;
import rbadia.voidspace.model.Ship;

public class GraphicsManagerPlus extends GraphicsManager {
	
	private BufferedImage shipImg;
	private BufferedImage shipExplosionImg;
	private BufferedImage moltresImg;
	private BufferedImage moltresDefeatedImg;
	private BufferedImage bigAsteroidImg;
	private BufferedImage bigAsteroidExplosionImg;
	private BufferedImage megaManLeftImg;
	private BufferedImage megaFallLImg;
	private BufferedImage megaFireLImg;
	
	public GraphicsManagerPlus(){
		
		try {
			this.shipImg = ImageIO.read(getClass().getResource("/rbadia/voidspace/graphics/ship.png"));
			this.shipExplosionImg = ImageIO.read(getClass().getResource("/rbadia/voidspace/graphics/shipExplosion.png"));
			this.moltresImg = ImageIO.read(getClass().getResource("/rbadia/voidspace/graphics/moltres.png"));
			this.moltresDefeatedImg = ImageIO.read(getClass().getResource("/rbadia/voidspace/graphics/moltresDefeated.png"));
			this.bigAsteroidImg = ImageIO.read(getClass().getResource("/rbadia/voidspace/graphics/BigAsteroid.png"));
			this.bigAsteroidExplosionImg = ImageIO.read(getClass().getResource("/rbadia/voidspace/graphics/bigAsteroidExplosion.png"));
			this.megaManLeftImg = ImageIO.read(getClass().getResource("/rbadia/voidspace/graphics/left.png"));
			this.megaFallLImg = ImageIO.read(getClass().getResource("/rbadia/voidspace/graphics/leftjump.png"));
			this.megaFireLImg = ImageIO.read(getClass().getResource("/rbadia/voidspace/graphics/leftshoot.png"));
			
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "The graphic files are either corrupt or missing.",
					"VoidSpace - Fatal Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			System.exit(-1);
		}
	}
	public void drawMegaManL (MegaMan megaMan, Graphics2D g2d, ImageObserver observer) {
		g2d.drawImage(megaManLeftImg, megaMan.x, megaMan.y, observer);
	}
	public void drawMegaFallL (MegaMan megaMan, Graphics2D g2d, ImageObserver observer) {
		g2d.drawImage(megaFallLImg, megaMan.x, megaMan.y, observer);
	}
	public void drawMegaFireL (MegaMan megaMan, Graphics2D g2d, ImageObserver observer) {
		g2d.drawImage(megaFireLImg, megaMan.x, megaMan.y, observer);
	}
	public void drawShip(Ship ship, Graphics2D g2d, ImageObserver observer) {
		g2d.drawImage(shipImg, ship.x, ship.y, observer);
	}
	public void drawShipExplosion(Rectangle shipExplosion, Graphics2D g2d, ImageObserver observer) {
		g2d.drawImage(shipExplosionImg, shipExplosion.x, shipExplosion.y, observer);
	}
	public void drawMoltres(Moltres moltres, Graphics2D g2d, ImageObserver observer) {
		g2d.drawImage(moltresImg, moltres.x, moltres.y, observer);
	}
	public void drawMoltresExplosion(Rectangle moltresDefeated, Graphics2D g2d, ImageObserver observer) {
		g2d.drawImage(moltresDefeatedImg, moltresDefeated.x, moltresDefeated.y, observer);
	}
	public void drawBigAsteroid(BigAsteroid bigAsteroid, Graphics g2d, ImageObserver observer) {
		g2d.drawImage(bigAsteroidImg, bigAsteroid.x, bigAsteroid.y,observer);
	}
	public void drawBigAsteroidExplosion(Rectangle bigAsteroidExplosion, Graphics2D g2d, ImageObserver observer) {
		g2d.drawImage(bigAsteroidExplosionImg, bigAsteroidExplosion.x, bigAsteroidExplosion.y, observer);
	}
}
