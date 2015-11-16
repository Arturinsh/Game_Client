package xyz.arturinsh.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import xyz.arturinsh.GameWorld.GameWorld;
import xyz.arturinsh.Helpers.AssetsLoader;

public class LoginScreen extends GameScreen {

	private Skin skin;
	private Table table;

	private TextButton loginButton, registerButton;
	private Label passwordLabel, userNameLabel;//, errorMessage;
	private TextField passwordTextField, userNameTextField;
//	private Dialog dialog;

	public LoginScreen(GameWorld _world) {
		super(_world);
		initUI();
	}

	private void initUI() {
		skin = AssetsLoader.getSkin();
		loginButton = new TextButton("Login", skin, "default");
		registerButton = new TextButton("Register", skin, "default");

		userNameLabel = new Label("Username", skin);
		userNameTextField = new TextField("", skin);
		userNameTextField.setMaxLength(32);

		passwordLabel = new Label("Password", skin);
		passwordTextField = new TextField("", skin);
		passwordTextField.setPasswordCharacter('*');
		passwordTextField.setPasswordMode(true);
		passwordTextField.setMaxLength(32);

		//errorMessage = new Label("", skin);

		registerButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				game.setScreen(new RegisterScreen(world));
			}
		});
		loginButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				String usrName, psw;
				usrName = userNameTextField.getText();
				psw = passwordTextField.getText();
				world.logIn(usrName, psw);
			}
		});
		table = new Table();
		table.setWidth(stage.getWidth());
		table.align(Align.center | Align.top);

		table.setPosition(0, Gdx.graphics.getHeight());

		table.padTop(Gdx.graphics.getHeight() / 3);
		table.add(userNameLabel);
		table.add(userNameTextField).minWidth(200);
		table.row();
		table.add(passwordLabel).space(10);
		table.add(passwordTextField).minWidth(200).space(10);
		table.row();
		table.add(loginButton).minWidth(150).colspan(2).space(10);
		table.row();
		table.add(registerButton).space(80).colspan(2).right();
		stage.addActor(table);

		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void show() {

	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glClearColor(0, 0, 0, 0);
		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
		table.setPosition(0, Gdx.graphics.getHeight());

		table.padTop(Gdx.graphics.getHeight() / 3);
		table.setWidth(stage.getWidth());
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
