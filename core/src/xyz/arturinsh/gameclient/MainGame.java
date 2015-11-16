package xyz.arturinsh.gameclient;

import com.badlogic.gdx.Game;

import xyz.arturinsh.GameWorld.GameWorld;
import xyz.arturinsh.Helpers.AssetsLoader;
import xyz.arturinsh.Screens.CharacterCreationScreen;
import xyz.arturinsh.Screens.LoginScreen;

public class MainGame extends Game {

	@Override
	public void create() {
		AssetsLoader.initUI();
		//TODO Add loading screen
		
		GameWorld world = new GameWorld(this);
		//this.setScreen(new CharacterCreationScreen(world));
	}
}
