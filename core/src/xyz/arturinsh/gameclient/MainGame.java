package xyz.arturinsh.gameclient;

import com.badlogic.gdx.Game;

import xyz.arturinsh.GameWorld.GameWorld;
import xyz.arturinsh.Helpers.AssetsLoader;
import xyz.arturinsh.Screens.LoginScreen;

public class MainGame extends Game {

	@Override
	public void create() {
		AssetsLoader.initUI();
		GameWorld world = new GameWorld(this);
		setScreen(new LoginScreen(world));
	}
}
