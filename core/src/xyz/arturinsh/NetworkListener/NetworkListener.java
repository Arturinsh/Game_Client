package xyz.arturinsh.NetworkListener;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import xyz.arturinsh.GameWorld.GameWorld;
import xyz.arturinsh.NetworkListener.Packets.AddPlayer;
import xyz.arturinsh.NetworkListener.Packets.LogInFailed;
import xyz.arturinsh.NetworkListener.Packets.LogInSuccess;
import xyz.arturinsh.NetworkListener.Packets.RegisterFailed;
import xyz.arturinsh.NetworkListener.Packets.RegisterSuccess;
import xyz.arturinsh.NetworkListener.Packets.RemovePlayer;

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
			world.showDialog("LogInSucces");
		}
		if (object instanceof LogInFailed) {
			world.showDialog("LogInFailed");
		}
		if (object instanceof RegisterSuccess) {
			world.showDialog("Register Succes");
		}
		if (object instanceof RegisterFailed) {
			world.showDialog("Register Failed");
		}
		if (object instanceof AddPlayer) {
			AddPlayer player = (AddPlayer) object;
			world.showDialog(player.username + " joinded");
		}
		if (object instanceof RemovePlayer) {
			RemovePlayer player = (RemovePlayer) object;
			world.showDialog(player.username + " left");
		}
	}
}
