import java.awt.*;

public class Sprite2D {
	// member data
	protected double x,y;
	protected double xSpeed=0;
	protected Image myImage, myImage2;
	int framesDrawn=0;
	int winWidth;
	
	// constructor
	public Sprite2D(Image i, Image i2, int windowWidth) {
		myImage = i;
		myImage2 = i2;
		winWidth = windowWidth;
	}
		
	public void setPosition(double xx, double yy) {
		x=xx;
		y=yy;
	}
		
	public void setXSpeed(double dx) {
		xSpeed=dx;
	}
	
	public void paint(Graphics g) {
		framesDrawn++;
		if ( framesDrawn%100<70 )
			g.drawImage(myImage, (int)x, (int)y, null);
		else
			g.drawImage(myImage2, (int)x, (int)y, null);
	}
}