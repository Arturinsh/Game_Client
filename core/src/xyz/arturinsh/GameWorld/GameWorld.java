package xyz.arturinsh.GameWorld;

import java.io.IOException;

import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;

import xyz.arturinsh.NetworkListener.NetworkListener;
import xyz.arturinsh.NetworkListener.Packets.AddPlayer;
import xyz.arturinsh.NetworkListener.Packets.LogIn;
import xyz.arturinsh.NetworkListener.Packets.LogInFailed;
import xyz.arturinsh.NetworkListener.Packets.LogInSuccess;
import xyz.arturinsh.NetworkListener.Packets.Register;
import xyz.arturinsh.NetworkListener.Packets.RegisterFailed;
import xyz.arturinsh.NetworkListener.Packets.RegisterSuccess;
import xyz.arturinsh.NetworkListener.Packets.RemovePlayer;
import xyz.arturinsh.Screens.CharacterCreationScreen;
import xyz.arturinsh.Screens.GameScreen;
import xyz.arturinsh.Screens.LoginScreen;
import xyz.arturinsh.gameclient.MainGame;

public class GameWorld {
	private Client client = new Client();
	private MainGame game;
	private final String ipAddress = "127.0.0.1";
	
	public GameWorld(MainGame _game) {
		game = _game;
		registerKryo();
		startNetworkClient();
		game.setScreen(new LoginScreen(this));
	}

	public MainGame getGame() {
		return game;
	}

	public void showDialog(String message) {
		GameScreen current = getCurrentScreen();
		current.showDialog(message);
	}

	public void logIn(String username, String psw) {
		LogIn login = new LogIn();
		login.userName = username;
		login.password = psw;
		client.sendTCP(login);
	}
	
	public void logiInSucess(){
		Gdx.app.postRunnable(new Runnable(){
			@Override
			public void run() {
				changeScreen(new CharacterCreationScreen(GameWorld.this));
			}
		});
	}

	public void register(String username, String psw) {
		Register register = new Register();
		register.userName = username;
		register.password = psw;
		client.sendTCP(register);
	}

	public void registerSuccess() {
		Gdx.app.postRunnable(new Runnable(){
			@Override
			public void run() {
				changeScreen(new LoginScreen(GameWorld.this));
				showDialog("Registration successful!");
			}
		});
	}

	public void registerFailed() {
		showDialog("Registration failed!");
	}

	private void registerKryo() {
		Kryo kryo = client.getKryo();
		kryo.register(LogIn.class);
		kryo.register(Register.class);
		kryo.register(LogInSuccess.class);
		kryo.register(RegisterSuccess.class);
		kryo.register(LogInFailed.class);
		kryo.register(RegisterFailed.class);
		kryo.register(AddPlayer.class);
		kryo.register(RemovePlayer.class);
	}

	private void startNetworkClient() {
		client.start();
		client.addListener(new NetworkListener(this));
		try {
			client.connect(5000, ipAddress, 54555, 54777);
		} catch (IOException e) {
			System.out.print(e);
		}
	}

	private GameScreen getCurrentScreen() {
		GameScreen current = (GameScreen) game.getScreen();
		return current;
	}

	private void changeScreen(GameScreen screen) {
		GameScreen current = getCurrentScreen();
		current.dispose();
		current.changeScreen(screen);
	}
}
