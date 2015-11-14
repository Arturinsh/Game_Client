package xyz.arturinsh.GameWorld;

import com.esotericsoftware.kryonet.Client;

import xyz.arturinsh.Screens.GameScreen;
import xyz.arturinsh.gameclient.MainGame;

public class GameWorld {
	private Client client = new Client();
	private MainGame game;

	public GameWorld(MainGame _game) {
		game = _game;
	}

	public MainGame getGame() {
		return game;
	}

	public void showDialog(String message) {
		GameScreen temp = (GameScreen) game.getScreen();
		temp.showDialog(message);
	}

}
