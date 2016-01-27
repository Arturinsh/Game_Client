package xyz.arturinsh.Network;

import java.util.Date;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import xyz.arturinsh.GameObjects.CharacterInstance;
import xyz.arturinsh.GameWorld.GameWorld;
import xyz.arturinsh.Network.Packets.AddPlayer;
import xyz.arturinsh.Network.Packets.CharacterCreateFailed;
import xyz.arturinsh.Network.Packets.CharacterCreateSuccess;
import xyz.arturinsh.Network.Packets.LogInFailed;
import xyz.arturinsh.Network.Packets.LogInSuccess;
import xyz.arturinsh.Network.Packets.RegisterFailed;
import xyz.arturinsh.Network.Packets.RegisterSuccess;
import xyz.arturinsh.Network.Packets.RemovePlayer;
import xyz.arturinsh.Network.Packets.SnapShot;
import xyz.arturinsh.Network.Packets.TestUDP;

public class NetworkListener extends Listener {
	private GameWorld world;
	private Date lastSnapshotTime = null;

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

			CharacterInstance playerInstance = new CharacterInstance(player.character);
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
			// Gdx.app.debug("Test", test);
		}

		if (object instanceof SnapShot) {
			SnapShot snapShot = (SnapShot) object;
			if (lastSnapshotTime == null || lastSnapshotTime.getTime() < snapShot.time.getTime()) {
				lastSnapshotTime = snapShot.time;
				world.updateWorld(snapShot);
			}
		}

		if (object instanceof RemovePlayer) {
			RemovePlayer rmp = (RemovePlayer) object;
			world.removePlayer(rmp);
		}

	}
}
