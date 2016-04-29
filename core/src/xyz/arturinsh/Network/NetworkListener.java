package xyz.arturinsh.Network;

import java.util.Date;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import xyz.arturinsh.GameObjects.CharacterInstance;
import xyz.arturinsh.GameWorld.GameWorld;
import xyz.arturinsh.Network.Packets.AddPlayer;
import xyz.arturinsh.Network.Packets.AttackStarted;
import xyz.arturinsh.Network.Packets.CharacterCreateFailed;
import xyz.arturinsh.Network.Packets.CharacterCreateSuccess;
import xyz.arturinsh.Network.Packets.EnterWorld;
import xyz.arturinsh.Network.Packets.LogInFailed;
import xyz.arturinsh.Network.Packets.LogInSuccess;
import xyz.arturinsh.Network.Packets.MobAttack;
import xyz.arturinsh.Network.Packets.RegisterFailed;
import xyz.arturinsh.Network.Packets.RegisterSuccess;
import xyz.arturinsh.Network.Packets.RemovePlayer;
import xyz.arturinsh.Network.Packets.ServerMessage;
import xyz.arturinsh.Network.Packets.SnapShot;

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
		world.showDCdialog("You have been disconnected!");
	}

	@Override
	public void received(Connection connection, Object object) {
		if (object instanceof LogInSuccess) {
			LogInSuccess login = (LogInSuccess) object;
			world.setCharacters(login.characters);
			world.logiInSucess();
		}

		if (object instanceof LogInFailed) {
			world.showDialog("LogIn Failed");
		}
		if (object instanceof RegisterSuccess) {
			world.registerSuccess();
		}
		if (object instanceof RegisterFailed) {
			world.registerFailed();
		}
		if (object instanceof AddPlayer) {
			AddPlayer player = (AddPlayer) object;

			CharacterInstance playerInstance = new CharacterInstance(player.character);
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

		if (object instanceof SnapShot) {
			// TODO do only when is logged in world
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

		if (object instanceof EnterWorld) {
			EnterWorld enter = (EnterWorld) object;
			world.succesEnterWorld(enter);
		}

		if (object instanceof AttackStarted) {
			AttackStarted attack = (AttackStarted) object;
			world.receiveAttack(attack);
		}

		if (object instanceof MobAttack) {
			MobAttack attack = (MobAttack) object;
			world.receiveMobAttack(attack);
		}

		if (object instanceof ServerMessage) {
			ServerMessage message = (ServerMessage) object;
			world.showDialog(message.message);
		}
	}
}
