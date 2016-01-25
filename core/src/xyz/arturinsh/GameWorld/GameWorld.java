package xyz.arturinsh.GameWorld;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;

import xyz.arturinsh.GameObjects.CharacterClass;
import xyz.arturinsh.GameObjects.CharacterInstance;
import xyz.arturinsh.Network.NetworkListener;
import xyz.arturinsh.Network.Packets.AddPlayer;
import xyz.arturinsh.Network.Packets.CharacterCreateFailed;
import xyz.arturinsh.Network.Packets.CharacterCreateSuccess;
import xyz.arturinsh.Network.Packets.EnterWorld;
import xyz.arturinsh.Network.Packets.LogIn;
import xyz.arturinsh.Network.Packets.LogInFailed;
import xyz.arturinsh.Network.Packets.LogInSuccess;
import xyz.arturinsh.Network.Packets.PlayersSnapShot;
import xyz.arturinsh.Network.Packets.PositionUpdate;
import xyz.arturinsh.Network.Packets.Register;
import xyz.arturinsh.Network.Packets.RegisterFailed;
import xyz.arturinsh.Network.Packets.RegisterSuccess;
import xyz.arturinsh.Network.Packets.RemovePlayer;
import xyz.arturinsh.Network.Packets.TestUDP;
import xyz.arturinsh.Network.Packets.UserCharacter;
import xyz.arturinsh.Network.UDPSender;
import xyz.arturinsh.Screens.CharacterSelectScreen;
import xyz.arturinsh.Screens.GameScreen;
import xyz.arturinsh.Screens.LoginScreen;
import xyz.arturinsh.gameclient.MainGame;

public class GameWorld {
	private Client client = new Client();
	private MainGame game;
	private final String ipAddress = "127.0.0.1";
	private List<UserCharacter> characters;
	private CharacterInstance usersCharacterInstance;
	private List<CharacterInstance> otherPlayers;
	private Timer timer;

	public GameWorld(MainGame _game) {
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		game = _game;
		registerKryo();
		startNetworkClient();
		otherPlayers = new ArrayList<CharacterInstance>();
		game.setScreen(new LoginScreen(this));
		timer = new Timer();
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

	public void createCharacterSuccess() {
		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				changeScreen(new CharacterSelectScreen(GameWorld.this));
				showDialog("Character created");
			}
		});
	}

	public void enterWorld(UserCharacter character) {
		EnterWorld enterWorld = new EnterWorld();
		enterWorld.character = character;
		client.sendTCP(enterWorld);
		timer.schedule(new UDPSender(client, this), 0, 50);
	}

	private void registerKryo() {
		Kryo kryo = client.getKryo();
		kryo.register(java.util.ArrayList.class);
		kryo.register(java.util.Date.class);
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
		kryo.register(TestUDP.class);
		kryo.register(EnterWorld.class);
		kryo.register(PositionUpdate.class);
		kryo.register(PlayersSnapShot.class);
	}

	private void startNetworkClient() {
		client.start();
		client.addListener(new NetworkListener(this));
		try {
			client.connect(5000, ipAddress, 2300, 54777);
		} catch (IOException e) {
			System.out.print(e);
			Gdx.app.debug("Test", e.toString());
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

	public CharacterInstance getUsersCharacterInstance() {
		return usersCharacterInstance;
	}

	public void setUsersCharacterInstance(CharacterInstance usersCharacterInstance) {
		this.usersCharacterInstance = usersCharacterInstance;
	}

	public void addPlayer(CharacterInstance playerInstance) {
		otherPlayers.add(playerInstance);
	}

	public List<CharacterInstance> getOtherPlayers() {
		return otherPlayers;
	}

	public void setOtherPlayers(List<CharacterInstance> otherPlayers) {
		this.otherPlayers = otherPlayers;
	}

	public void updatePlayers(PlayersSnapShot snapShot) {
		for (PositionUpdate update : snapShot.snapshot) {
			if (usersCharacterInstance.matchesCharacter(update.character)) {
//				System.out.println("Update ME");
			} else if (hasCharacter(update, otherPlayers, snapShot.time.getTime())) {
//				System.out.println("Update " + update.character.charName);
			} else {
				CharacterInstance playerInstance = new CharacterInstance(update.character);
				playerInstance.setPosition(update.x, update.y, update.z);
				playerInstance.setRotation(update.r);
				otherPlayers.add(playerInstance);
//				System.out.println("Add Player " + update.character.charName);
			}
		}
	}

	private boolean hasCharacter(PositionUpdate update, List<CharacterInstance> list, long time) {
		for (CharacterInstance player : list) {
			if (player.matchesCharacter(update.character)) {
				player.updatePlayer(update.x, update.y, update.z, update.r, time);
				//player.updatePositionOrientation(update.x, update.y, update.z, update.r);
				return true;
			}
		}
		return false;
	}
}
