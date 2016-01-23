package xyz.arturinsh.Network;

import java.util.TimerTask;

import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.esotericsoftware.kryonet.Client;

import xyz.arturinsh.GameWorld.GameWorld;
import xyz.arturinsh.Network.Packets.PositionUpdate;

public class UDPSender extends TimerTask {

	private Client client;
	private GameWorld world;

	public UDPSender(Client _client, GameWorld _world) {
		client = _client;
		world = _world;
	}

	@Override
	public void run() {
		PositionUpdate posUpdate = new PositionUpdate();
		posUpdate.character = world.getUsersCharacterInstance().getCharacter();
		Vector3 position = world.getUsersCharacterInstance().getPosition();
		posUpdate.x = position.x;
		posUpdate.y = position.y;
		posUpdate.z = position.z;
		posUpdate.r = world.getUsersCharacterInstance().getRotation();

		client.sendUDP(posUpdate);
		// TestUDP test = new TestUDP();
		// DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		// Date date = new Date();
		// test.text = "Hello " + dateFormat.format(date);
		// client.sendTCP(test);
	}
}
