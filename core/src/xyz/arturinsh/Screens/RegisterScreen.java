package xyz.arturinsh.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import xyz.arturinsh.GameWorld.GameWorld;
import xyz.arturinsh.Helpers.AssetsLoader;
import xyz.arturinsh.gameclient.MainGame;

public class RegisterScreen extends GameScreen {
	private Skin skin;
	private Table table;

	private TextButton registerButton, backButton;
	private Label passwordLabel, passwordLabel2, userNameLabel, dialogMessage;
	private TextField passwordTextField, passwordTextField2, userNameTextField;
	//private Dialog dialog;

	//private boolean registerSuccesful;

	public RegisterScreen(GameWorld _world) {
		super(_world);
		initUI();
	}

	private void initUI() {
		skin = AssetsLoader.getSkin();
		registerButton = new TextButton("Register", skin, "default");
		backButton = new TextButton("Return to Login", skin);

		userNameLabel = new Label("Username", skin);
		userNameTextField = new TextField("", skin);
		userNameTextField.setMaxLength(32);

		passwordLabel = new Label("Password", skin);
		passwordTextField = new TextField("", skin);
		passwordTextField.setPasswordCharacter('*');
		passwordTextField.setPasswordMode(true);
		passwordTextField.setMaxLength(32);

		passwordLabel2 = new Label("Confirm PSW", skin);
		passwordTextField2 = new TextField("", skin);
		passwordTextField2.setPasswordCharacter('*');
		passwordTextField2.setPasswordMode(true);
		passwordTextField2.setMaxLength(32);

		dialogMessage = new Label("", skin);
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
		table.add(passwordLabel2).space(10);
		table.add(passwordTextField2).minWidth(200).space(10);
		table.row();
		table.add(registerButton).space(10).colspan(2);
		table.row();
		table.add(backButton).space(30).colspan(2).right();
		stage.addActor(table);

		backButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				game.setScreen(new LoginScreen(world));
			}
		});

		registerButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				String username, psw;
				username = userNameTextField.getText();
				psw = passwordTextField.getText();

				// TODO regex check for username and psw

				if (!psw.matches(passwordTextField2.getText())) {
					showDialog("Passwords do not match!");
					passwordTextField.setText("");
					passwordTextField2.setText("");
				} else if (userNameOk(username) && pswOk(psw)) {
					world.register(username, psw);
				} else if (!userNameOk(username)) {
					showDialog("Username can contain only alphanumeric characters\n "
							+ "and must be atleast 6 characters long");
				} else if (!pswOk(psw)) {
					showDialog("Password contains illegal characters "
							+ "or is shorter than 6 characters");
				}
			}
		});

		Gdx.input.setInputProcessor(stage);
	}

	private boolean userNameOk(String username) {
		if (username.length() >= 6 && username.length() <= 32 && username.matches("[a-zA-Z0-9]*"))
			return true;
		return false;
	}

	private boolean pswOk(String psw) {
		if (psw.length() >= 6 && psw.length() <= 32 && psw.matches("[a-zA-Z0-9_.@]*"))
			return true;
		return false;
	}

	@Override
	public void show() {

	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
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
