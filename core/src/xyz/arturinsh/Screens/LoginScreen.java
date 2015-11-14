package xyz.arturinsh.Screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

import xyz.arturinsh.GameWorld.GameWorld;
import xyz.arturinsh.gameclient.MainGame;

public class LoginScreen implements Screen {
	private MainGame game;
	private GameWorld world;

	private Stage stage;
	private Skin skin;
	private Table table;

	private TextButton loginButton, registerButton;
	private Label passwordLabel, userNameLabel, errorMessage;
	private TextField passwordTextField, userNameTextField;
	private Dialog dialog;

	public LoginScreen(GameWorld _world, MainGame _game) {
		game = _game;
		world = _world;
	}

	private void initUI() {
		
	}

	@Override
	public void show() {

	}

	@Override
	public void render(float delta) {

	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void hide() {

	}

	@Override
	public void dispose() {

	}

}
