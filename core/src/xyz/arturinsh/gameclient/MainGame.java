package xyz.arturinsh.gameclient;

import com.badlogic.gdx.Game;

import xyz.arturinsh.GameWorld.GameWorld;
import xyz.arturinsh.Screens.LoginScreen;

public class MainGame extends Game {

	@Override
	public void create() {
		GameWorld world = new GameWorld();
		setScreen(new LoginScreen(world, this));
	}
}
