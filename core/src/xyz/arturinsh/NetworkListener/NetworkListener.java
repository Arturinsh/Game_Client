package xyz.arturinsh.NetworkListener;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import xyz.arturinsh.GameWorld.GameWorld;
import xyz.arturinsh.NetworkListener.Packets.LogInFailed;
import xyz.arturinsh.NetworkListener.Packets.LogInSuccess;

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
	}
}
