package xyz.arturinsh.NetworkListener;

import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import xyz.arturinsh.GameObjects.CharacterInstance;
import xyz.arturinsh.GameWorld.GameWorld;
import xyz.arturinsh.NetworkListener.Packets.AddPlayer;
import xyz.arturinsh.NetworkListener.Packets.CharacterCreateFailed;
import xyz.arturinsh.NetworkListener.Packets.CharacterCreateSuccess;
import xyz.arturinsh.NetworkListener.Packets.LogInFailed;
import xyz.arturinsh.NetworkListener.Packets.LogInSuccess;
import xyz.arturinsh.NetworkListener.Packets.RegisterFailed;
import xyz.arturinsh.NetworkListener.Packets.RegisterSuccess;
import xyz.arturinsh.NetworkListener.Packets.RemovePlayer;
import xyz.arturinsh.NetworkListener.Packets.TestUDP;

public class NetworkListener extends Listener {
	private GameWorld world;

	public NetworkListener(GameWorld _world) {
		world = _world;
	}

	@Override
	public void connected(Connection connection) {
	}

	@Override
	public void disconnected(Connection connection) {
	}

	@Override
	public void received(Connection connection, Object object) {
		if (object instanceof LogInSuccess) {
			LogInSuccess login = (LogInSuccess) object;
			world.setCharacters(login.characters);
			world.logiInSucess();
			Gdx.app.debug("Test", "HI");
		}

		if (object instanceof LogInFailed) {
			world.showDialog("LogInFailed");
		}
		if (object instanceof RegisterSuccess) {
			System.out.println("Reg ok");
			world.registerSuccess();
		}
		if (object instanceof RegisterFailed) {
			System.out.println("Reg fail");
			world.registerFailed();
		}
		if (object instanceof AddPlayer) {
			AddPlayer player = (AddPlayer) object;
			
			CharacterInstance playerInstance = new CharacterInstance(player.charClass);
			playerInstance.setPosition(player.x, player.y, player.z);
			
			world.addPlayer(playerInstance);
			System.out.println("AddPlayer");
		}
		if (object instanceof RemovePlayer) {
			// RemovePlayer player = (RemovePlayer) object;
			// world.showDialog(player.username + " left");
		}
		if (object instanceof CharacterCreateSuccess) {
			CharacterCreateSuccess success = (CharacterCreateSuccess) object;
			world.setCharacters(success.characters);
			world.createCharacterSuccess();
		}
		if (object instanceof CharacterCreateFailed) {
			world.showDialog("Character name already exists");
		}

		if (object instanceof TestUDP) {
			String test = ((TestUDP) object).text;
			//Gdx.app.debug("Test", test);
		}
	}
}
