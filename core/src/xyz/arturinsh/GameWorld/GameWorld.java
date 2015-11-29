package xyz.arturinsh.GameWorld;

import java.io.IOException;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;

import xyz.arturinsh.GameObjects.CharacterClass;
import xyz.arturinsh.NetworkListener.NetworkListener;
import xyz.arturinsh.NetworkListener.Packets.AddPlayer;
import xyz.arturinsh.NetworkListener.Packets.CharacterCreateFailed;
import xyz.arturinsh.NetworkListener.Packets.CharacterCreateSuccess;
import xyz.arturinsh.NetworkListener.Packets.LogIn;
import xyz.arturinsh.NetworkListener.Packets.LogInFailed;
import xyz.arturinsh.NetworkListener.Packets.LogInSuccess;
import xyz.arturinsh.NetworkListener.Packets.Register;
import xyz.arturinsh.NetworkListener.Packets.RegisterFailed;
import xyz.arturinsh.NetworkListener.Packets.RegisterSuccess;
import xyz.arturinsh.NetworkListener.Packets.RemovePlayer;
import xyz.arturinsh.NetworkListener.Packets.UserCharacter;
import xyz.arturinsh.Screens.CharacterSelectScreen;
import xyz.arturinsh.Screens.GameScreen;
import xyz.arturinsh.Screens.LoginScreen;
import xyz.arturinsh.gameclient.MainGame;

public class GameWorld {
	private Client client = new Client();
	private MainGame game;
	private final String ipAddress = "127.0.0.1";
	private List<UserCharacter> characters;
	
	
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

	public void logiInSucess() {
		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				CharacterSelectScreen try1 = new CharacterSelectScreen(GameWorld.this);
				changeScreen(try1);
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
		Gdx.app.postRunnable(new Runnable() {
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

	public void createCharacter(String charName, CharacterClass charClass) {
		UserCharacter create = new UserCharacter();
		create.charClass = charClass;
		create.charName = charName;

		client.sendTCP(create);
	}

	private void registerKryo() {
		Kryo kryo = client.getKryo();
		kryo.register(java.util.ArrayList.class);
		kryo.register(LogIn.class);
		kryo.register(Register.class);
		kryo.register(LogInSuccess.class);
		kryo.register(RegisterSuccess.class);
		kryo.register(LogInFailed.class);
		kryo.register(RegisterFailed.class);
		kryo.register(AddPlayer.class);
		kryo.register(RemovePlayer.class);
		kryo.register(CharacterClass.class);
		kryo.register(UserCharacter.class);
		kryo.register(CharacterCreateSuccess.class);
		kryo.register(CharacterCreateFailed.class);
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

	public List<UserCharacter> getCharacters() {
		return characters;
	}

	public void setCharacters(List<UserCharacter> characters) {
		this.characters = characters;
	}
}
