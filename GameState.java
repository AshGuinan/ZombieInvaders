import java.awt.Color;
import java.awt.Font;


public enum GameState {
	START_MENU, PAUSE_MENU, RUNNING, GAME_OVER;

	public boolean isRunning(){
		return (name() == "RUNNING");
	}
	public boolean isGameOver(){
		return (name() == "GAME_OVER");
	}
	public boolean isStartMenu(){
		return (name() == "START_MENU");
	}
	public boolean isPaused(){
		return (name() == "PAUSE_MENU");
	}
}
