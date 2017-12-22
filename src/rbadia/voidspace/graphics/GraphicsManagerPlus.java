package rbadia.voidspace.graphics;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import rbadia.voidspace.model.Moltres;
import rbadia.voidspace.model.Ship;

public class GraphicsManagerPlus extends GraphicsManager {
	
	private BufferedImage shipImg;
	private BufferedImage shipExplosionImg;
	private BufferedImage moltresImg;
	private BufferedImage moltresDefeatedImg;
	
	public GraphicsManagerPlus(){
		
		try {
			this.shipImg = ImageIO.read(getClass().getResource("/rbadia/voidspace/graphics/ship.png"));
			this.shipExplosionImg = ImageIO.read(getClass().getResource("/rbadia/voidspace/graphics/shipExplosion.png"));
			this.moltresImg = ImageIO.read(getClass().getResource("/rbadia/voidspace/graphics/moltres.png"));
			this.moltresDefeatedImg = ImageIO.read(getClass().getResource("/rbadia/voidspace/graphics/moltresDefeated.png"));

		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "The graphic files are either corrupt or missing.",
					"VoidSpace - Fatal Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			System.exit(-1);
		}
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

}
