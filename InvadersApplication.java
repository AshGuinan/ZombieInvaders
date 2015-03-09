import java.awt.*;
import java.awt.event.*;

import javax.sound.sampled.*;
import javax.swing.*;

import java.awt.image.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.applet.*;
import java.io.File;
import java.net.*;

public class InvadersApplication extends JFrame implements Runnable, KeyListener {
	
	// member data
	private static final Dimension WindowSize = new Dimension(800,600);
	private BufferStrategy strategy;
	private static final int NUMALIENS = 30;
	private Alien[] AliensArray = new Alien[NUMALIENS];
	private Spaceship PlayerShip;
	private Image bulletImage;
	private Image shipImage;
	private Image alienImage;
	private Image alienImage2;
	private ArrayList bulletsList = new ArrayList();
	public int score = 0;
	private int waveNumber = 0;
	private GameState gameState = GameState.START_MENU;
	
	// constructor
	public InvadersApplication() {
        //Display the window, centred on the screen
        Dimension screensize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        int x = screensize.width/2 - WindowSize.width/2;
        int y = screensize.height/2 - WindowSize.height/2;
        setBounds(x, y, WindowSize.width, WindowSize.height);
        setVisible(true);
    	this.setTitle("Zombie Invaders!!");


		// load images from disk. Make sure you have the path right!
		alienImage = new ImageIcon("C:/Users/absin_000/workspace/Space_Invaders/src/zombie1.jpg").getImage();
        alienImage2 = new ImageIcon("C:/Users/absin_000/workspace/Space_Invaders/src/zombie2.jpg").getImage();
        shipImage = new ImageIcon("C:/Users/absin_000/workspace/Space_Invaders/src/sheirff.jpg").getImage();
        bulletImage = new ImageIcon("C:/Users/absin_000/workspace/Space_Invaders/src/bullet.png").getImage();
      
        // send keyboard events arriving into this JFrame back to its own event handlers
        addKeyListener(this);
        
        // initialise double-buffering
        createBufferStrategy(2);
        strategy = getBufferStrategy();

        // create and start our animation thread
        Thread t = new Thread(this);
        t.start();
	}
	
	public void startNewGame(){
        // create and initialise the player's spaceship
        PlayerShip = new Spaceship(shipImage,WindowSize.width);
        PlayerShip.setPosition(300,530);
        
        startNewWave();
	}
	
	public void startNewWave(){
		waveNumber++;
		// create and initialise some aliens, passing them each the image we have loaded
        for (int i=0; i<NUMALIENS; i++) {
        	AliensArray[i] = new Alien(alienImage,alienImage2,WindowSize.width);
        	double xx = (i%5)*80 + 70;
        	double yy = (i/5)*40 + 50;
        	AliensArray[i].setPosition(xx, yy);
        	AliensArray[i].setXSpeed(2);
        }
        
        File file = new File("C:/Users/absin_000/workspace/Space_Invaders/src/Zombie_Horde-Mike_Koenig-1926300541.wav");
             
	}
	
	public void run(){

		while ( 1==1 ) {
			
			// 1: sleep for 1/50 sec
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) { }
			switch (gameState){
			case START_MENU:
				try {
					java.applet.AudioClip clip = java.applet.Applet.newAudioClip(new java.net.URL("file://C:/Users/absin_000/workspace/Space_Invaders/src/Horror-Movie-Ambiance-SoundBible.com-2028850439.wav"));
					clip.play();
					} catch (java.net.MalformedURLException murle) {
					System.out.println(murle);
					}
				break;
			case RUNNING:
				runGame();
				break;
			case PAUSE_MENU:
				try {
					java.applet.AudioClip clip =
					java.applet.Applet.newAudioClip(new java.net.URL("file://C:/Users/absin_000/workspace/Space_Invaders/src/Horror-Movie-Ambiance-SoundBible.com-2028850439.wav"));
					clip.play();
					} catch (java.net.MalformedURLException murle) {
					System.out.println(murle);
					}
				break;
			case GAME_OVER:
				try {
					java.applet.AudioClip clip =
					java.applet.Applet.newAudioClip(new java.net.URL("file://C:/Users/absin_000/workspace/Space_Invaders/src/End_of_Game-Mike_Koenig-617089079.wav"));
					clip.play();
					} catch (java.net.MalformedURLException murle) {
					System.out.println(murle);
					}
				break;
			default:
				break;
			}
			
			//  force an application repaint
			this.repaint();
		}
	}
	
	// thread's entry point
	public void runGame() {
		
		// 2: animate game objects
		int aliensAlive = 0;
		boolean alienDirectionReversalNeeded = false;
		for (int i=0;i<NUMALIENS; i++) {
			Alien alien = AliensArray[i];
			if ( alien.isAlive ) {
				aliensAlive++;
				if (alien.move(waveNumber)){
					alienDirectionReversalNeeded=true;
				}
				//Check collision with spaceship
				double x2 = alien.x, y2 = alien.y;
				double w1 = 54, h1 = 32;
				double w2 = 50, h2 = 32;
				double x1 = PlayerShip.x;
				double y1 = PlayerShip.y;
				if (
						((x1<x2 && x1+w1>x2) || (x2<x1 && x2+w2>x1)) 
						&&
						((y1<y2 && y1+h1>y2) || (y2<y1 && y2+h2>y1)) 
					)
				{
					//Gameover
					gameState = GameState.GAME_OVER;
				}
			}
		}		
		if (alienDirectionReversalNeeded) {
			for (int i=0;i<NUMALIENS; i++) {
				if ( AliensArray[i].isAlive ) {
					AliensArray[i].reverseDirection();
				}
			}
		}
		
		PlayerShip.move();
		
		Iterator iterator = bulletsList.iterator();
		while(iterator.hasNext()){
			PlayerBullet b = (PlayerBullet) iterator.next();
			if (b.move()) {
				// true was returned by move method if bullet needs destroying due to going offscreen
				// iterator.remove is a safe way to remove from the ArrayList while iterating thru it
				iterator.remove();
			}
			else {
				// check for collision between this bullet and any alien
				double x2 = b.x, y2 = b.y;
				double w1 = 50, h1 = 32;
				double w2 = 6, h2 = 16;
				for (int i=0;i<NUMALIENS; i++) {
					if ( AliensArray[i].isAlive ) {
						double x1 = AliensArray[i].x;
						double y1 = AliensArray[i].y;
						if (
								((x1<x2 && x1+w1>x2) || (x2<x1 && x2+w2>x1)) 
								&&
								((y1<y2 && y1+h1>y2) || (y2<y1 && y2+h2>y1)) 
							)
						{
							// destroy alien and bullet
							AliensArray[i].isAlive=false;
							score = score+200;
							try {
								java.applet.AudioClip clip =
								java.applet.Applet.newAudioClip(new java.net.URL("file://C:/Users/absin_000/workspace/Space_Invaders/src/ZombieMoan-SoundBible.com-565291980.wav"));
								clip.play();
								} catch (java.net.MalformedURLException murle) {
								System.out.println(murle);
								}
			// iterator.remove is a safe way to remove from the ArrayList while iterating thru it
							iterator.remove();
							break; // no need to keep checking aliens so break out of for loop
						}
					}
				}
			}
		}

		//IF all aliens are dead, create a new wave
		if(aliensAlive == 0)
			startNewWave();
		
	}
	
	// Three Keyboard Event-Handler functions
    public void keyPressed(KeyEvent e) {
    	if (e.getKeyCode()==KeyEvent.VK_LEFT)
    		PlayerShip.setXSpeed(-4);
    	else if (e.getKeyCode()==KeyEvent.VK_RIGHT)
    		PlayerShip.setXSpeed(4);
    	else if (e.getKeyCode()==KeyEvent.VK_SPACE)
    		shootBullet();
    	else if (e.getKeyCode()==KeyEvent.VK_ENTER){
    		if (gameState.isStartMenu() || gameState.isGameOver()){
    			startNewGame();
    			gameState = GameState.RUNNING;
    		}
    		else if (gameState==GameState.PAUSE_MENU){
    			gameState = GameState.RUNNING;
    		}
    	}
    	else if (e.getKeyCode()==KeyEvent.VK_ESCAPE){
    		if (gameState.isRunning()){
    			gameState = GameState.PAUSE_MENU;
    		}
    		else
    			System.exit(0);
    	}
    }
    
    public void keyReleased(KeyEvent e) {	
    	if (e.getKeyCode()==KeyEvent.VK_LEFT || e.getKeyCode()==KeyEvent.VK_RIGHT) 
    		PlayerShip.setXSpeed(0);
    }
    
    public void keyTyped(KeyEvent e) { }
    
    // method to handle shooting
    public void shootBullet() {
    	// add a new bullet to our list
    	PlayerBullet b = new PlayerBullet(bulletImage,WindowSize.width);
    	b.setPosition(PlayerShip.x+54/2, PlayerShip.y);
    	bulletsList.add(b);
    }

    // application's paint method
    public void paint(Graphics g){
		g = strategy.getDrawGraphics(); // draw to offscreen buffer
    	switch (gameState){
		case START_MENU:
			paintStart(g);
			break;
		case RUNNING:
			paintGame(g);
			break;
		case PAUSE_MENU:
			paintPause(g);
			break;
		case GAME_OVER:
			paintGameOver(g);
			break;
		default:
			break;
		
		}
		// flip the buffers
		g.dispose();
		strategy.show();
    }

    public void paintStart(Graphics g){
    	g.setColor(Color.BLACK);
		g.fillRect(0, 0, WindowSize.width, WindowSize.height);
    	Font f = new Font("Times", Font.PLAIN, 40);
		g.setFont(f);
		Color c = Color.WHITE;
		g.setColor(c);
		Color r =Color.RED;
		
		g.drawString("ZOMBIE INVADERS" , 100, 200);
		Font a = new Font("Times", Font.PLAIN, 20);
		g.setFont(a);
		g.drawString("Use arrow keys to control your character" , 100, 260);
		g.drawString("Use space bar to shoot" , 100, 290);
		g.drawString("Esc to pause" , 100, 320);
		g.setColor(r);
		g.drawString("AND MOST IMPORTANTLY -- DON'T DIE" , 100, 350);
		
    }    
    
    public void paintGameOver(Graphics g){
    	g.setColor(Color.BLACK);
		g.fillRect(0, 0, WindowSize.width, WindowSize.height);
    	Font f = new Font("Times", Font.PLAIN, 40);
		g.setFont(f);
		Color c = Color.WHITE;
		g.setColor(c);
		
		g.drawString("Game Over!" , 100, 360);
		g.drawString("Final Score: " + score, 100, 400);
		g.drawString("Wave Number: " + waveNumber, 100, 440);
    }
    
    public void paintPause(Graphics g){
    	g.setColor(Color.BLACK);
		g.fillRect(0, 0, WindowSize.width, WindowSize.height);
    	Font f = new Font("Times", Font.PLAIN, 40);
		g.setFont(f);
		Color c = Color.WHITE;
		g.setColor(c);
		
		g.drawString("Game Paused" , 100, 360);
		g.drawString("Score: " + score, 100, 400);
		g.drawString("Wave Number: " + waveNumber, 100, 440);
    }
    
    //Game state is running
	public void paintGame(Graphics g) {
		// clear the canvas with a big black rectangle
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, WindowSize.width, WindowSize.height);
		Font f = new Font("Times", Font.PLAIN, 25);
		g.setFont(f);
		Color c = Color.WHITE;
		g.setColor(c);
		
		g.drawString("Score:" + score , 20, 60);
		
		// redraw all game objects
		for (int i=0;i<NUMALIENS; i++)
			AliensArray[i].paint(g);
		
		PlayerShip.paint(g);
		
		Iterator iterator = bulletsList.iterator();
		while(iterator.hasNext()){
			PlayerBullet b = (PlayerBullet) iterator.next();
			b.paint(g);
		}
	}
	
	// application entry point
	public static void main(String[] args) {
		InvadersApplication w = new InvadersApplication();
	}
}