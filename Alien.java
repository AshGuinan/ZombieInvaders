import java.awt.Graphics;
import java.awt.Image;
public class Alien extends Sprite2D {
	public boolean isAlive = true;
	
	public Alien(Image i, Image i2, int windowWidth) {
		super(i,i2,windowWidth); // invoke constructor on superclass Sprite2D
	}
	
	public void paint(Graphics g) {
		if (isAlive)
			super.paint(g);
	}
	
	// To increase speed as the waves progress, 
	// pass the wavenumber to this function
	public boolean move(int speedModifier) {
		x+=xSpeed*speedModifier;
		
		// direction reversal needed?
		if (x<=0 || x>=winWidth-myImage.getWidth(null))
			return true;
		else
			return false;
	}
	
	public void reverseDirection() {
		xSpeed=-xSpeed;
		y+=20;
	}
}