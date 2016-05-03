package xyz.arturinsh.GameWorld;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Vector3;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;

import xyz.arturinsh.GameObjects.CharacterClass;
import xyz.arturinsh.GameObjects.CharacterInstance;
import xyz.arturinsh.GameObjects.MobInstance;
import xyz.arturinsh.GameObjects.MobType;
import xyz.arturinsh.Helpers.AssetsLoader;
import xyz.arturinsh.Network.NetworkListener;
import xyz.arturinsh.Network.Packets.AddPlayer;
import xyz.arturinsh.Network.Packets.Attack;
import xyz.arturinsh.Network.Packets.AttackStarted;
import xyz.arturinsh.Network.Packets.CharacterCreateFailed;
import xyz.arturinsh.Network.Packets.CharacterCreateSuccess;
import xyz.arturinsh.Network.Packets.EnterWorld;
import xyz.arturinsh.Network.Packets.LogIn;
import xyz.arturinsh.Network.Packets.LogInFailed;
import xyz.arturinsh.Network.Packets.LogInSuccess;
import xyz.arturinsh.Network.Packets.LogOut;
import xyz.arturinsh.Network.Packets.MobAttack;
import xyz.arturinsh.Network.Packets.MobUpdate;
import xyz.arturinsh.Network.Packets.PlayerPositionUpdate;
import xyz.arturinsh.Network.Packets.Register;
import xyz.arturinsh.Network.Packets.RegisterFailed;
import xyz.arturinsh.Network.Packets.RegisterSuccess;
import xyz.arturinsh.Network.Packets.RemovePlayer;
import xyz.arturinsh.Network.Packets.ServerMessage;
import xyz.arturinsh.Network.Packets.SnapShot;
import xyz.arturinsh.Network.Packets.SwitchCharacter;
import xyz.arturinsh.Network.Packets.UserCharacter;
import xyz.arturinsh.Network.UDPSender;
import xyz.arturinsh.Screens.CharacterSelectScreen;
import xyz.arturinsh.Screens.GameScreen;
import xyz.arturinsh.Screens.LoginScreen;
import xyz.arturinsh.Screens.WorldScreen;
import xyz.arturinsh.gameclient.MainGame;

public class GameWorld {
	private Client client = new Client();
	private MainGame game;
	private final String ipAddress = "192.168.1.6";
	private List<UserCharacter> characters;
	private CharacterInstance usersCharacterInstance;
	private List<CharacterInstance> otherPlayers;
	private List<MobInstance> mobs;
	private Timer timer;
	private CharacterInstance selectedPlayer = null;
	private MobInstance selectedMob = null;
	private Material originalSelectedMaterial;
	private Material selectedMaterial;
	private int ping = 0;
	private Music music;
	private Preferences preferences;

	public GameWorld(MainGame _game) {
		initPreferences();
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		game = _game;
		registerKryo();
		otherPlayers = new ArrayList<CharacterInstance>();
		mobs = new ArrayList<MobInstance>();
		initMusic();
		game.setScreen(new LoginScreen(this));
		startNetworkClient();
		timer = new Timer();
		selectedMaterial = new Material(ColorAttribute.createDiffuse(new Color(0.7f, 0.7f, 0.7f, 1)));
	}

	public void logOut() {
		client.sendTCP(new LogOut());
		otherPlayers = new ArrayList<CharacterInstance>();
		mobs = new ArrayList<MobInstance>();
		timer.cancel();
		timer = new Timer();
		characters = new ArrayList<UserCharacter>();
		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				changeScreen(new LoginScreen(GameWorld.this));
			}
		});
	}
	
	public void switchCharacter(){
		otherPlayers = new ArrayList<CharacterInstance>();
		mobs = new ArrayList<MobInstance>();
		timer.cancel();
		timer = new Timer();
		characters = new ArrayList<UserCharacter>();
		client.sendTCP(new SwitchCharacter());
	}	

	private void initPreferences() {
		preferences = AssetsLoader.getPreferences();
	}

	public MainGame getGame() {
		return game;
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
		kryo.register(EnterWorld.class);
		kryo.register(PlayerPositionUpdate.class);
		kryo.register(MobType.class);
		kryo.register(MobUpdate.class);
		kryo.register(SnapShot.class);
		kryo.register(Attack.class);
		kryo.register(AttackStarted.class);
		kryo.register(MobAttack.class);
		kryo.register(ServerMessage.class);
		kryo.register(LogOut.class);
		kryo.register(SwitchCharacter.class);
	}

	public void showDialog(String message) {
		GameScreen current = getCurrentScreen();
		current.showDialog(message);
	}

	public void showDCdialog(String message) {
		GameScreen current = getCurrentScreen();
		current.showDCDialog(message);
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

	}

	public void succesEnterWorld(EnterWorld enter) {
		usersCharacterInstance = new CharacterInstance(enter.character);

		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				changeScreen(new WorldScreen(GameWorld.this));
			}
		});
		timer.schedule(new UDPSender(client, this), 0, 50);
	}

	private void startNetworkClient() {
		client.start();
		client.addListener(new NetworkListener(this));
		try {
			client.connect(5000, ipAddress, 2300, 54777);
		} catch (IOException e) {
			System.out.print(e);
			Gdx.app.debug("Test", e.toString());
			showDCdialog("Can not connect to server!");
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

	public List<MobInstance> getMobs() {
		return mobs;
	}

	public void updateWorld(SnapShot snapShot) {
		updatePlayers(snapShot);
		updateMobs(snapShot);
	}

	private void updatePlayers(SnapShot snapShot) {
		for (PlayerPositionUpdate update : snapShot.snapshot) {
			if (usersCharacterInstance != null && usersCharacterInstance.matchesCharacter(update.character)) {
				usersCharacterInstance.checkUpdate(update);
			} else if (hasCharacter(update, otherPlayers, snapShot.time.getTime())) {
			} else {
				CharacterInstance playerInstance = new CharacterInstance(update.character);
				otherPlayers.add(playerInstance);
			}
		}
	}

	private boolean hasCharacter(PlayerPositionUpdate update, List<CharacterInstance> list, long time) {
		for (CharacterInstance player : list) {
			if (player.matchesCharacter(update.character)) {
				player.updatePlayer(update, time);
				return true;
			}
		}
		return false;
	}

	private void updateMobs(SnapShot snapShot) {
		for (MobUpdate update : snapShot.mobSnapshot) {
			if (hasMob(update, mobs, snapShot.time.getTime())) {
			} else {
				MobInstance mobInstance = new MobInstance(update.ID, update.x, update.y, update.z, update.r,
						update.type);
				mobs.add(mobInstance);
			}
		}
	}

	private boolean hasMob(MobUpdate update, List<MobInstance> list, long time) {
		for (MobInstance mob : list) {
			if (mob.getID() == update.ID) {
				mob.updateMob(update, time);
				return true;
			}
		}
		return false;
	}

	public void removePlayer(RemovePlayer player) {
		for (CharacterInstance instance : otherPlayers) {
			if (instance.matchesCharacter(player.character)) {
				int index = otherPlayers.indexOf(instance);
				if (index >= 0)
					otherPlayers.remove(index);
				break;
			}
		}
	}

	public void attack() {
		if (usersCharacterInstance.canAttack()) {
			Attack attack = new Attack();
			attack.time = new Date();
			UserCharacter tempChar = usersCharacterInstance.getCharacter();
			usersCharacterInstance.attack();

			Vector3 position = usersCharacterInstance.getPosition();
			tempChar.x = position.x;
			tempChar.y = position.y;
			tempChar.z = position.z;
			tempChar.r = usersCharacterInstance.getRotation();

			attack.character = tempChar;
			client.sendTCP(attack);
		}
	}

	public void receiveMobAttack(MobAttack attack) {
		for (MobInstance mob : mobs) {
			if (mob.getID() == attack.mob.ID) {
				mob.attack();
			}
		}
	}

	public void receiveAttack(AttackStarted attack) {
		for (CharacterInstance player : otherPlayers) {
			if (!usersCharacterInstance.matchesCharacter(attack.character)
					&& player.matchesCharacter(attack.character)) {
				player.attack();
			}
		}
	}

	public void setSelectedCharacterInstance(CharacterInstance instance) {
		if (selectedPlayer == null && instance != null) {
			ModelInstance temp = instance.getModelInstance();
			Material mat = temp.materials.get(0);
			originalSelectedMaterial = new Material();
			originalSelectedMaterial.set(mat);
			mat.set(selectedMaterial);
			selectedPlayer = instance;
			selectedPlayer.setSelected(true);
		} else if (instance == null && selectedPlayer != null) {
			ModelInstance temp = selectedPlayer.getModelInstance();
			Material mat = temp.materials.get(0);
			mat.clear();
			mat.set(originalSelectedMaterial);
			selectedPlayer.setSelected(false);
			selectedPlayer = instance;
		} else if (selectedPlayer != null && instance != null) {
			ModelInstance temp = selectedPlayer.getModelInstance();
			Material mat = temp.materials.get(0);
			mat.clear();
			mat.set(originalSelectedMaterial);
			selectedPlayer.setSelected(false);
			ModelInstance temp2 = instance.getModelInstance();
			Material mat2 = temp2.materials.get(0);
			originalSelectedMaterial = new Material();
			originalSelectedMaterial.set(mat2);
			mat2.set(selectedMaterial);

			selectedPlayer = instance;
			selectedPlayer.setSelected(true);
		}
	}

	public void setSelectedMob(MobInstance instance) {
		if (selectedMob == null && instance != null) {
			ModelInstance temp = instance.getModelInstance();
			Material mat = temp.materials.get(0);
			originalSelectedMaterial = new Material();
			originalSelectedMaterial.set(mat);
			mat.set(selectedMaterial);
			selectedMob = instance;
			selectedMob.setSelected(true);
		} else if (instance == null && selectedMob != null) {
			ModelInstance temp = selectedMob.getModelInstance();
			Material mat = temp.materials.get(0);
			mat.clear();
			mat.set(originalSelectedMaterial);
			selectedMob.setSelected(false);
			selectedMob = instance;
		} else if (selectedMob != null && instance != null) {
			ModelInstance temp = selectedMob.getModelInstance();
			Material mat = temp.materials.get(0);
			mat.clear();
			mat.set(originalSelectedMaterial);
			selectedMob.setSelected(false);
			ModelInstance temp2 = instance.getModelInstance();
			Material mat2 = temp2.materials.get(0);
			originalSelectedMaterial = new Material();
			originalSelectedMaterial.set(mat2);
			mat2.set(selectedMaterial);
			selectedMob = instance;
			selectedMob.setSelected(true);
		}
	}

	public CharacterInstance getSelectedPlayer() {
		return selectedPlayer;
	}

	public MobInstance getSelectedMob() {
		return selectedMob;
	}

	public int getPing() {
		return ping;
	}

	public void setPing(int ping) {
		this.ping = ping;
	}

	public Client getClient() {
		return client;
	}

	private void initMusic() {
		music = AssetsLoader.getMusic();

		boolean musicOn = preferences.getBoolean("musicOn", true);
		float musicVolume = preferences.getFloat("musicVolume", 1);
		music.setLooping(true);
		if (musicOn) {
			music.play();
		}
		music.setVolume(musicVolume);
	}

	public void musicPause() {
		preferences.putBoolean("musicOn", false);
		preferences.flush();
		music.pause();
	}

	public void musicResume() {
		preferences.putBoolean("musicOn", true);
		preferences.flush();
		music.play();
	}

	public float getMusicVolume() {
		return music.getVolume();
	}

	public void setMusicVolume(float volume) {
		preferences.putFloat("musicVolume", volume);
		preferences.flush();
		music.setVolume(volume);
	}

	public boolean showFPS() {
		boolean getFps = preferences.getBoolean("showFPS", false);
		return getFps;
	}

	public boolean showPing() {
		boolean getPing = preferences.getBoolean("showPing", true);
		return getPing;
	}

	public void setShowFPS(boolean show) {
		preferences.putBoolean("showFPS", show);
		preferences.flush();
	}

	public void setShowPing(boolean show) {
		preferences.putBoolean("showPing", show);
		preferences.flush();
	}

	public boolean isMusicPlaying() {
		return music.isPlaying();
	}

}
